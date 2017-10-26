package org.vitrivr.cineast.core.features;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig.Distance;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;
import org.vitrivr.cineast.core.segmenter.FuzzyColorHistogramCalculator;
import org.vitrivr.cineast.core.segmenter.SubdividedFuzzyColorHistogram;

public class SubDivAverageFuzzyColor extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  public SubDivAverageFuzzyColor() {
    super("features_SubDivAverageFuzzyColor", 2f / 4f);
  }

  @Override
  public void processSegment(SegmentContainer shot) {
    LOGGER.traceEntry();
    if (!phandler.idExists(shot.getId())) {
      SubdividedFuzzyColorHistogram fch = FuzzyColorHistogramCalculator
          .getSubdividedHistogramNormalized(shot.getAvgImg().getBufferedImage(), 2);
      persist(shot.getId(), fch);
    }
    LOGGER.traceExit();
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    SubdividedFuzzyColorHistogram query = FuzzyColorHistogramCalculator
        .getSubdividedHistogramNormalized(sc.getAvgImg().getBufferedImage(), 2);
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  @Override
  protected QueryConfig setQueryConfig(ReadableQueryConfig qc) {
    return QueryConfig.clone(qc).setDistanceIfEmpty(Distance.chisquared);
  }

}
