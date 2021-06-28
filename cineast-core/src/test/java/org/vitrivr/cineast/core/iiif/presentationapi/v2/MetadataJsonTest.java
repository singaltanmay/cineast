package org.vitrivr.cineast.core.iiif.presentationapi.v2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Manifest;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Metadata;

/**
 * @author singaltanmay
 * @version 1.0
 * @created 27.06.21
 */
public class MetadataJsonTest {

  private final String DESCRIPTION = "bb699753-8d03-4366-8139-a68ed087f837";
  private final String ATTRIBUTION = "2de3ce77-6818-4ebd-af48-a7f86ee8b7d4";
  private final String METADATA_LABEL = "4e40d999-78f2-4c58-941d-146bc03238c1";
  private final String METADATA_VALUE = "3ff41a2f-0dac-472b-af62-b1e7e5970e04";
  private Manifest manifest;

  @BeforeEach
  void setup() {
    manifest = mock(Manifest.class);
    when(manifest.getDescription()).thenReturn(DESCRIPTION);
    when(manifest.getAttribution()).thenReturn(ATTRIBUTION);
    Metadata metadata = mock(Metadata.class);
    when(metadata.getLabel()).thenReturn(METADATA_LABEL);
    when(metadata.getValue()).thenReturn(METADATA_VALUE);
    when(manifest.getMetadata()).thenReturn(Collections.singletonList(metadata));
  }

  @Test
  void toJsonStringTest() throws JsonProcessingException {
    MetadataJson metadataJson = new MetadataJson(manifest);
    String jsonString = metadataJson.toJsonString();
    assertNotNull(jsonString);
    assertFalse(jsonString.isEmpty());
    System.out.println(jsonString);
    assertTrue(jsonString.contains("\"description\":\"" + DESCRIPTION + "\""));
    assertTrue(jsonString.contains("\"attribution\":\"" + ATTRIBUTION + "\""));
    assertTrue(jsonString.contains("\"metadata\":[{\"label\":\"" + METADATA_LABEL + "\",\"value\":\"" + METADATA_VALUE + "\"}]"));
  }

}
