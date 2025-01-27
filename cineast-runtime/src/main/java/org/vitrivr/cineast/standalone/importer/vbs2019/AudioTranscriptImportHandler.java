package org.vitrivr.cineast.standalone.importer.vbs2019;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.features.AudioTranscriptionSearch;
import org.vitrivr.cineast.core.util.LogHelper;
import org.vitrivr.cineast.standalone.importer.handlers.DataImportHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioTranscriptImportHandler extends DataImportHandler {

  private static final Logger LOGGER = LogManager.getLogger();

  public AudioTranscriptImportHandler(int threads, int batchsize) {
    super(threads, batchsize);
  }

  @Override
  public void doImport(Path root) {
    try {
      LOGGER.info("Starting data import for caption files in: {}", root.toString());
      Files.walk(root, 2).filter(p -> p.toString().toLowerCase().endsWith(".json")).forEach(p -> {
        try {
          this.futures.add(this.service.submit(new DataImportRunner(new AudioTranscriptionImporter(p), AudioTranscriptionSearch.AUDIO_TRANSCRIPTION_TABLE_NAME, "audio file")));
        } catch (IOException e) {
          LOGGER.fatal("Failed to open path at {} ", p);
          throw new RuntimeException(e);
        }
      });
    } catch (IOException e) {
      LOGGER.error("Could not start data import process with path '{}' due to an IOException: {}. Aborting...", root.toString(), LogHelper.getStackTrace(e));
    }
  }
}
