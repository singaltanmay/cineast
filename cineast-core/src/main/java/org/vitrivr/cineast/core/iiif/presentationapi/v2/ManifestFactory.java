package org.vitrivr.cineast.core.iiif.presentationapi.v2;

import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.iiif.imageapi.ImageRequest;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Canvas;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Image;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Manifest;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Sequence;

/**
 * Takes a URL String to a manifest in the constructor and downloads all the images, image information and simplified metadata to the filesystem.
 *
 * @author singaltanmay
 * @version 1.0
 * @created 30.06.21
 */
public class ManifestFactory {

  private static final Logger LOGGER = LogManager.getLogger();
  private final Manifest manifest;

  public ManifestFactory(String manifestUrl) throws Exception {
    ManifestRequest manifestRequest = new ManifestRequest(manifestUrl);
    this.manifest = manifestRequest.parseManifest();
    if (manifest == null) {
      throw new Exception("Error occurred in parsing the manifest!");
    }
  }

  /**
   * Writes the {@link MetadataJson} generated from this {@link Manifest} to the filesystem
   *
   * @param jobDirectoryString The directory where the file has to be written to
   * @param filename The name of the file with extension
   */
  public void saveMetadataJson(String jobDirectoryString, String filename) {
    MetadataJson metadataJson = new MetadataJson(manifest);
    try {
      metadataJson.saveToFile(jobDirectoryString, filename);
    } catch (IOException e) {
      LOGGER.error("Failed to save manifest metadata JSON to filesystem");
      e.printStackTrace();
    }
  }

  public void saveAllCanvasImages(String jobDirectoryString, String filenamePrefix) {
    List<Sequence> sequences = manifest.getSequences();
    if (sequences != null && sequences.size() != 0) {
      for (Sequence sequence : sequences) {
        List<Canvas> canvases = sequence.getCanvases();
        if (canvases != null && canvases.size() != 0) {
          // TODO loop restricted to 2 images during development
          for (int i = 0; i < Math.min(2, canvases.size()); i++) {
            final Canvas canvas = canvases.get(i);
            List<Image> images = canvas.getImages();
            if (images != null && images.size() != 0) {
              final int canvasIndex = i;
              // Download all images in the canvas
              images.forEach(image -> {
                String imageApiUrl = image.getResource().getAtId();
                // Make image request to remote server
                ImageRequest imageRequest = ImageRequest.fromUrl(imageApiUrl);
                // Write the downloaded image to the filesystem
                LOGGER.info("Trying to save image to file system: " + image);
                try {
                  imageRequest.saveToFile(jobDirectoryString, filenamePrefix + canvasIndex, imageApiUrl);
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
