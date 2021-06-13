package org.vitrivr.cineast.core.iiif.imageapi.v2_1_1;

import static org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilderImpl.isImageDimenValidFloat;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_REGION_BY_PCT;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_REGION_SQUARE;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_ABOVE_FULL;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_BY_CONFINED_WH;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_BY_DISTORTED_WH;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_BY_H;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_BY_PCT;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_BY_W;
import static org.vitrivr.cineast.core.iiif.imageapi.v2_1_1.ImageInformation_v2_1_1.ProfileItem.SUPPORTS_SIZE_BY_WH;

import javax.naming.OperationNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilder;
import org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestBuilderImpl;
import org.vitrivr.cineast.core.iiif.imageapi.BaseImageRequestValidators;
import org.vitrivr.cineast.core.iiif.imageapi.ImageRequest;

/**
 * @author singaltanmay
 * @version 1.0
 * @created 09.06.21
 */
public class ImageRequestBuilder_v2_1_1_Impl implements ImageRequestBuilder_v2_1_1 {

  private static final Logger LOGGER = LogManager.getLogger();
  private final BaseImageRequestBuilder baseBuilder;
  private String size;
  private Validators validators;

  public ImageRequestBuilder_v2_1_1_Impl(String baseUrl) {
    this.baseBuilder = new BaseImageRequestBuilderImpl(baseUrl);
  }

  public ImageRequestBuilder_v2_1_1_Impl(ImageInformation_v2_1_1 imageInformation) throws IllegalArgumentException {
    this(imageInformation.getAtId());
    this.validators = new Validators(imageInformation);
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setRegionFull() {
    baseBuilder.setRegionFull();
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setRegionSquare() throws IllegalArgumentException, OperationNotSupportedException {
    if (validators != null) {
      validators.validateServerSupportsFeature(SUPPORTS_REGION_SQUARE, "Server does not support explicitly requesting square regions of images");
    }
    baseBuilder.setRegionSquare();
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setRegionAbsolute(float x, float y, float w, float h) throws IllegalArgumentException, OperationNotSupportedException {
    if (w <= 0 || h <= 0) {
      throw new IllegalArgumentException("Width and height must be greater than 0");
    }
    if (validators != null) {
      validators.validateServerSupportsRegionAbsolute(w, h);
    }
    baseBuilder.setRegionAbsolute(x, y, w, h);
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setRegionPercentage(float x, float y, float w, float h) throws IllegalArgumentException, OperationNotSupportedException {
    if (x < 0 || x > 100 || y < 0 || y > 100) {
      throw new IllegalArgumentException("Value should lie between 0 and 100");
    }
    if (x == 100 || y == 100) {
      throw new IllegalArgumentException("Request region is entirely outside the image's reported dimensional bounds");
    }
    if (w <= 0 || w > 100 || h <= 0 || h > 100) {
      throw new IllegalArgumentException("Height and width of the image must belong in the range (0, 100]");
    }
    if (validators != null) {
      validators.validateServerSupportsFeature(SUPPORTS_REGION_BY_PCT, "Server does not support requests for regions of images by percentage.");
    }
    baseBuilder.setRegionPercentage(x, y, w, h);
    return this;
  }

  public ImageRequestBuilder_v2_1_1_Impl setSizeFull() {
    this.size = SIZE_FULL;
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setSizeMax() {
    baseBuilder.setSizeMax();
    return this;
  }

  public ImageRequestBuilder_v2_1_1_Impl setSizeScaledExact(Float width, Float height) throws IllegalArgumentException, OperationNotSupportedException {
    boolean isWidthValid = isImageDimenValidFloat(width);
    boolean isHeightValid = isImageDimenValidFloat(height);
    // Behaviour of server when neither width or height are provided is undefined. Thus, user should be forced to some other method such as setSizeMax.
    if (!isWidthValid && !isHeightValid) {
      throw new IllegalArgumentException("Either width or height must be a valid float value!");
    }
    if (validators != null) {
      validators.validateSizeScaledExact(width, height, isWidthValid, isHeightValid);
    }
    baseBuilder.setSizeScaledExact(width, height);
    return this;
  }

  public ImageRequestBuilder_v2_1_1_Impl setSizeScaledBestFit(float width, float height, boolean isWidthOverridable, boolean isHeightOverridable) throws IllegalArgumentException, OperationNotSupportedException {
    // Behaviour of server when both width and height are overridable is undefined. Thus, user should be forced to some other method such as setSizeMax.
    if (isWidthOverridable && isHeightOverridable) {
      throw new IllegalArgumentException("Both width and height cannot be overridable!");
    }
    if (validators != null) {
      validators.validateSizeScaledBestFit(width, height, isWidthOverridable, isHeightOverridable);
    }
    // If both width and height cannot be overridden by the server then it is the same case as exact scaling.
    if (!isWidthOverridable && !isHeightOverridable) {
      return this.setSizeScaledExact(width, height);
    }
    baseBuilder.setSizeScaledBestFit(width, height, isWidthOverridable, isHeightOverridable);
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setSizePercentage(float n) throws IllegalArgumentException, OperationNotSupportedException {
    if (n <= 0) {
      throw new IllegalArgumentException("Percentage value has to be greater than 0");
    }
    if (validators != null) {
      validators.validateSizePercentage(n);
    }
    baseBuilder.setSizePercentage(n);
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setRotation(float degree, boolean mirror) throws IllegalArgumentException, OperationNotSupportedException {
    if (degree < 0 || degree > 360) {
      throw new IllegalArgumentException("Rotation value can only be between 0° and 360°!");
    }
    if (validators != null) {
      validators.validateSetRotation(degree, mirror);
    }
    baseBuilder.setRotation(degree, mirror);
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setQuality(String quality) throws IllegalArgumentException, OperationNotSupportedException {
    if (validators != null) {
      validators.validateServerSupportsQuality(quality);
    }
    baseBuilder.setQuality(quality);
    return this;
  }

  @Override
  public ImageRequestBuilder_v2_1_1_Impl setFormat(String format) throws IllegalArgumentException, OperationNotSupportedException {
    if (validators != null) {
      validators.validateServerSupportsFormat(format);
    }
    baseBuilder.setFormat(format);
    return this;
  }

  @Override
  public ImageRequest build() {
    ImageRequest imageRequest = baseBuilder.build();
    if (this.size != null && !this.size.isEmpty()) {
      imageRequest.setSize(this.size);
    }
    return imageRequest;
  }

  private static class Validators extends BaseImageRequestValidators {

    private final ImageInformation_v2_1_1 imageInformation;

    public Validators(ImageInformation_v2_1_1 imageInformation) throws IllegalArgumentException {
      super(imageInformation);
      this.imageInformation = imageInformation;
    }

    private void validateSizeScaledExact(Float width, Float height, boolean isWidthValid, boolean isHeightValid) throws OperationNotSupportedException {
      if (!isWidthValid) {
        validateServerSupportsFeature(SUPPORTS_SIZE_BY_H, "Server does not support requesting for image sizes by height alone");
      }
      if (!isHeightValid) {
        validateServerSupportsFeature(SUPPORTS_SIZE_BY_W, "Server does not support requesting for image sizes by width alone");
      }
      if (width > imageInformation.getWidth() || height > imageInformation.getHeight()) {
        validateServerSupportsFeature(SUPPORTS_SIZE_ABOVE_FULL, "Server does not support requesting for image sizes about the image's full size");
      }
      float requestAspectRatio = width / height;
      float originalAspectRatio = (float) imageInformation.getWidth() / imageInformation.getHeight();
      if (requestAspectRatio != originalAspectRatio) {
        validateServerSupportsFeature(SUPPORTS_SIZE_BY_DISTORTED_WH, "Server does not support requesting for image sizes that would distort the image");
      } else {
        validateServerSupportsFeature(SUPPORTS_SIZE_BY_WH, "Server does not support requesting for image sizes using width and height parameters");
      }
    }

    public boolean validateSizeScaledBestFit(float width, float height, boolean isWidthOverridable, boolean isHeightOverridable) throws OperationNotSupportedException {
      if (width > imageInformation.getWidth() || height > imageInformation.getHeight()) {
        validateServerSupportsFeature(SUPPORTS_SIZE_ABOVE_FULL, "Server does not support requesting for image sizes about the image's full size");
      }
      if (isWidthOverridable || isHeightOverridable) {
        validateServerSupportsFeature(SUPPORTS_SIZE_BY_CONFINED_WH, "Server does not support requesting images with overridable width or height parameters");
      }
      return false;
    }

    public void validateSizePercentage(float n) throws OperationNotSupportedException {
      validateServerSupportsFeature(SUPPORTS_SIZE_BY_PCT, "Server does not support requesting for image size using percentages");
      if (n > 100) {
        validateServerSupportsFeature(SUPPORTS_SIZE_ABOVE_FULL, "Server does not support requesting for image sizes about the image's full size");
      }
    }
  }
}
