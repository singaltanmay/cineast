package org.vitrivr.cineast.standalone.cli;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.DatabaseConfig;
import org.vitrivr.cineast.core.iiif.IIIFConfig;
import org.vitrivr.cineast.core.iiif.imageapi.ImageRequest;
import org.vitrivr.cineast.core.iiif.imageapi.ImageRequestFactory;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.ManifestRequest;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Canvas;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Image;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Manifest;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Sequence;
import org.vitrivr.cineast.core.util.json.JacksonJsonProvider;
import org.vitrivr.cineast.standalone.config.IngestConfig;
import org.vitrivr.cineast.standalone.config.InputConfig;
import org.vitrivr.cineast.standalone.run.ExtractionCompleteListener;
import org.vitrivr.cineast.standalone.run.ExtractionContainerProvider;
import org.vitrivr.cineast.standalone.run.ExtractionDispatcher;
import org.vitrivr.cineast.standalone.run.path.ExtractionContainerProviderFactory;

/**
 * A CLI command that can be used to start a media extraction based on an extraction definition file.
 *
 * @author Ralph Gasser
 * @version 1.0
 */
@Command(name = "extract", description = "Starts a media extracting using the specified settings.")
public class ExtractionCommand implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger();

  @Option(name = {"--no-finalize"}, title = "Do Not Finalize", description = "If this flag is not set, automatically rebuilds indices & optimizes all entities when writing to cottontail after the extraction. Set this flag when you want more performance with external parallelism.")
  private final boolean doNotFinalize = false;

  @Required
  @Option(name = {"-e", "--extraction"}, title = "Extraction config", description = "Path that points to a valid Cineast extraction config file.")
  private String extractionConfig;

  /** Helper method to detect if the path in InputConfig is that of an IIIF job or the local filesystem */
  public static boolean isURL(String url) {
    try {
      new URI(url);
      return true;
    } catch (URISyntaxException e) {
      return false;
    }
  }

  @Override
  public void run() {
    final ExtractionDispatcher dispatcher = new ExtractionDispatcher();
    final File file = new File(this.extractionConfig);
    if (file.exists()) {
      try {
        final JacksonJsonProvider reader = new JacksonJsonProvider();
        final IngestConfig context = reader.toObject(file, IngestConfig.class);
        if (context != null) {
          InputConfig inputConfig = context.getInput();
          IIIFConfig iiifConfig = inputConfig.getIiif();
          if (iiifConfig != null) {
            String directoryPath;
            String iiifConfigPath = inputConfig.getPath();
            if (iiifConfigPath == null || iiifConfigPath.isEmpty()) {
              directoryPath = file.getAbsoluteFile().toPath().getParent() + "/iiif-media";
              inputConfig.setPath(directoryPath);
            } else {
              directoryPath = iiifConfigPath;
            }
            configureIIIFExtractionJob(iiifConfig, directoryPath);
          }
        }
        final ExtractionContainerProvider provider = ExtractionContainerProviderFactory.tryCreatingTreeWalkPathProvider(file, context);
        if (dispatcher.initialize(provider, context)) {
          /* Only attempt to optimize Cottontail entities if we were extracting into Cottontail, otherwise an unavoidable error message would be displayed when extracting elsewhere. */
          if (!doNotFinalize && context != null && context.getDatabase().getSelector() == DatabaseConfig.Selector.COTTONTAIL && context.getDatabase().getWriter() == DatabaseConfig.Writer.COTTONTAIL) {
            dispatcher.registerListener(new ExtractionCompleteListener() {
              @Override
              public void extractionComplete() {
                OptimizeEntitiesCommand.optimizeAllCottontailEntities();
              }
            });
          }
          dispatcher.registerListener((ExtractionCompleteListener) provider);
          dispatcher.start();
          dispatcher.block();
        } else {
          System.err.printf("Could not start handleExtraction with configuration file '%s'. Does the file exist?%n", file);
        }
      } catch (IOException e) {
        System.err.printf("Could not start handleExtraction with configuration file '%s' due to a IO error.%n", file);
        e.printStackTrace();
      } catch (ClassCastException e) {
        System.err.println("Could not register completion listener for extraction.");
      }
    } else {
      System.err.printf("Could not start handleExtraction with configuration file '%s'; the file does not exist!%n", file);
    }
  }

  /**
   * Configures an IIIF extraction job by downloading all specified images from the server onto to the filesystem and pointing the {@link ExtractionContainerProvider} to that directory.
   *
   * @param iiifConfig The IIIF config parsed as an {@link IIIFConfig}
   * @param directoryPath The path where the downloaded IIIF content should be stored
   * @throws IOException Thrown if downloading or writing an image or it's associated information encounters an IOException
   */
  private void configureIIIFExtractionJob(IIIFConfig iiifConfig, String directoryPath) throws IOException {
    final Path jobDirectory = Paths.get(directoryPath);
    if (!Files.exists(jobDirectory)) {
      Files.createDirectories(jobDirectory);
    }
    final String jobDirectoryString = jobDirectory.toString();
    String itemPrefixString = "iiif_image_";
    String imageApiBaseUrl = iiifConfig.getBaseUrl();
    if (imageApiBaseUrl != null && !imageApiBaseUrl.isEmpty()) {
      ImageRequestFactory imageRequestFactory = new ImageRequestFactory(iiifConfig);
      imageRequestFactory.createImageRequests(jobDirectoryString, itemPrefixString);
    }
    String manifestUrl = iiifConfig.getManifestUrl();
    if (imageApiBaseUrl != null && !imageApiBaseUrl.isEmpty()) {
      ManifestRequest manifestRequest = new ManifestRequest(manifestUrl);
      Manifest manifest = manifestRequest.parseManifest(null);
      List<Sequence> sequences = manifest != null ? manifest.getSequences() : null;
      if (sequences != null && sequences.size() != 0) {
        for (Sequence sequence : sequences) {
          List<Canvas> canvases = sequence.getCanvases();
          if (canvases != null && canvases.size() != 0) {
            for (int i = 0; i < Math.min(5, canvases.size()); i++) {
              final Canvas canvas = canvases.get(i);
              List<Image> images = canvas.getImages();
              if (images != null && images.size() != 0) {
                final int canvasIndex = i;
                images.forEach(image -> {
                  String imageApiUrl = image.getResource().getAtId();
                  ImageRequest imageRequest = ImageRequest.fromUrl(imageApiUrl);
                  LOGGER.info("Trying to save image to file system: " + image);
                  try {
                    imageRequest.saveToFile(itemPrefixString, "manifest_image" + canvasIndex, imageApiUrl);
                  } catch (IOException e) {
                    LOGGER.error("Failed to save image to file system: " + image);
                    e.printStackTrace();
                  }
                });
              }
            }
          }
        }
      }
    }
  }
}
