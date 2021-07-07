package org.vitrivr.cineast.core.iiif.imageapi.v2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder.EXTENSION_JPG;
import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder.EXTENSION_PNG;
import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder.QUALITY_BITONAL;
import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder.QUALITY_COLOR;
import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder.QUALITY_DEFAULT;
import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder.QUALITY_GRAY;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageApiCompliance_v2.LEVEL_0;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageApiCompliance_v2.LEVEL_1;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageApiCompliance_v2.LEVEL_2;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_BASE_URI_REDIRECT;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_CORS;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_JSONLD_MEDIA_TYPE;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_REGION_BY_PCT;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_REGION_BY_PX;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_ROTATION_BY_90s;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_SIZE_BY_CONFINED_WH;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_SIZE_BY_DISTORTED_WH;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_SIZE_BY_H;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_SIZE_BY_PCT;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_SIZE_BY_W;
import static org.vitrivr.cineast.core.iiif.imageapi.v2.ImageInformation_v2.ProfileItem.SUPPORTS_SIZE_BY_WH;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for methods in {@link ImageApiCompliance_v2}
 */
public class ImageApiCompliance_v2_Test {

  @Nested
  class Level0Tests {

    /**
     * Tests that {@link ImageApiCompliance_v2#isQualitySupported} returns true for all the qualities that Level 0 compliant servers are required to support
     */
    @Test
    void isQualitySupported() {
      assertTrue(ImageApiCompliance_v2.isQualitySupported(QUALITY_DEFAULT, LEVEL_0));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isFormatSupported} returns true for all the formats that Level 0 compliant servers are required to support
     */
    @Test
    void isFormatSupported() {
      assertTrue(ImageApiCompliance_v2.isFormatSupported(EXTENSION_JPG, LEVEL_0));
    }
  }

  @Nested
  class Level1Tests {

    /**
     * Tests that {@link ImageApiCompliance_v2#isFeatureSupported} returns true for all the features that Level 1 compliant servers are required to support
     */
    @Test
    void isFeatureSupported() {
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_REGION_BY_PX, LEVEL_1));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_W, LEVEL_1));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_H, LEVEL_1));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_PCT, LEVEL_1));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isQualitySupported} returns true for all the qualities that Level 1 compliant servers are required to support
     */
    @Test
    void isQualitySupported() {
      assertTrue(ImageApiCompliance_v2.isQualitySupported(QUALITY_DEFAULT, LEVEL_1));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isFormatSupported} returns true for all the formats that Level 1 compliant servers are required to support
     */
    @Test
    void isFormatSupported() {
      assertTrue(ImageApiCompliance_v2.isFormatSupported(EXTENSION_JPG, LEVEL_1));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isHttpFeatureSupported} returns true for all the HTTP features that Level 1 compliant servers are required to support
     */
    @Test
    void isHttpFeatureSupported() {
      assertTrue(ImageApiCompliance_v2.isHttpFeatureSupported(SUPPORTS_BASE_URI_REDIRECT, LEVEL_1));
      assertTrue(ImageApiCompliance_v2.isHttpFeatureSupported(SUPPORTS_CORS, LEVEL_1));
      assertTrue(ImageApiCompliance_v2.isHttpFeatureSupported(SUPPORTS_JSONLD_MEDIA_TYPE, LEVEL_1));
    }
  }

  @Nested
  class Level2Tests {

    /**
     * Tests that {@link ImageApiCompliance_v2#isFeatureSupported} returns true for all the features that Level 2 compliant servers are required to support
     */
    @Test
    void isFeatureSupported() {
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_REGION_BY_PX, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_REGION_BY_PCT, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_W, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_H, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_PCT, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_CONFINED_WH, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_DISTORTED_WH, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_SIZE_BY_WH, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFeatureSupported(SUPPORTS_ROTATION_BY_90s, LEVEL_2));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isQualitySupported} returns true for all the qualities that Level 2 compliant servers are required to support
     */
    @Test
    void isQualitySupported() {
      assertTrue(ImageApiCompliance_v2.isQualitySupported(QUALITY_DEFAULT, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isQualitySupported(QUALITY_COLOR, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isQualitySupported(QUALITY_GRAY, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isQualitySupported(QUALITY_BITONAL, LEVEL_2));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isFormatSupported} returns true for all the formats that Level 2 compliant servers are required to support
     */
    @Test
    void isFormatSupported() {
      assertTrue(ImageApiCompliance_v2.isFormatSupported(EXTENSION_JPG, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isFormatSupported(EXTENSION_PNG, LEVEL_2));
    }

    /**
     * Tests that {@link ImageApiCompliance_v2#isHttpFeatureSupported} returns true for all the HTTP features that Level 2 compliant servers are required to support
     */
    @Test
    void isHttpFeatureSupported() {
      assertTrue(ImageApiCompliance_v2.isHttpFeatureSupported(SUPPORTS_BASE_URI_REDIRECT, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isHttpFeatureSupported(SUPPORTS_CORS, LEVEL_2));
      assertTrue(ImageApiCompliance_v2.isHttpFeatureSupported(SUPPORTS_JSONLD_MEDIA_TYPE, LEVEL_2));
    }
  }
}
