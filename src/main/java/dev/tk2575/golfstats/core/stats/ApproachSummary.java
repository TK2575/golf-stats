package dev.tk2575.golfstats.core.stats;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Getter
@RequiredArgsConstructor
public class ApproachSummary {

  private final ApproachBin bin;
  private final long roundCount;
  private final BigDecimal strokesGained;

  public ApproachSummary(Shot shot) {
    this(ApproachBin.shotBinFunction.apply(shot), 1L, shot.getStrokesGained());
  }

  public ApproachSummary merge(ApproachSummary approachSummary) {
    return new ApproachSummary(this.bin, this.roundCount + approachSummary.roundCount, this.strokesGained.add(approachSummary.strokesGained));
  }

  public static List<ApproachSummary> compile(ShotStream shots) {
    return shots.map(ApproachSummary::new).toList();
  }

  public static ApproachSummary merge(@NonNull List<ApproachSummary> shots) {
    var bin = shots.isEmpty() ? ApproachBin.OTHER : shots.getFirst().bin;
    return shots.stream().reduce(new ApproachSummary(bin, 0L, BigDecimal.ZERO), ApproachSummary::merge);
  }

  public BigDecimal getMeanStrokesGainedPerRound() {
    return Utils.divide(this.strokesGained, this.roundCount, 2);
  }
}
