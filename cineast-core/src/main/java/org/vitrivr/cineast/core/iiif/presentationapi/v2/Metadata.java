package org.vitrivr.cineast.core.iiif.presentationapi.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *   @author singaltanmay
 *   @version 1.0
 *   @created 23.06.21
 */
class Metadata {

  @JsonProperty
  private String label;
  @JsonProperty
  private String value;

  public Metadata() {
  }

  @Override
  public String toString() {
    return "MetadataItem{" +
        "label='" + label + '\'' +
        ", value='" + value + '\'' +
        '}';
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
