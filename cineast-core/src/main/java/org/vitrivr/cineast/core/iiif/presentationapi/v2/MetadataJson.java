package org.vitrivr.cineast.core.iiif.presentationapi.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Manifest;
import org.vitrivr.cineast.core.iiif.presentationapi.v2.models.Metadata;

/**
 * @author singaltanmay
 * @version 1.0
 * @created 27.06.21
 */
public class MetadataJson {

  public final String description;
  public final String attribution;
  public final List<Metadata> metadata;

  public MetadataJson(Manifest manifest) {
    this.description = manifest.getDescription();
    this.attribution = manifest.getAttribution();
    this.metadata = manifest.getMetadata();
  }

  public String toJsonString() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(this);
  }

}
