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
public class ApproachShot {

  private final ApproachBin bin;
  private final long roundCount;
  private final BigDecimal strokesGained;

  public ApproachShot(Shot shot) {
    this(ApproachBin.shotBinFunction.apply(shot), 1L, shot.getStrokesGained());
  }

  public ApproachShot merge(ApproachShot approachShot) {
    return new ApproachShot(this.bin, this.roundCount + approachShot.roundCount, this.strokesGained.add(approachShot.strokesGained));
  }

  public static List<ApproachShot> compile(ShotStream shots) {
    return shots.map(ApproachShot::new).toList();
  }

  public static ApproachShot merge(@NonNull List<ApproachShot> shots) {
    var bin = shots.isEmpty() ? ApproachBin.OTHER : shots.getFirst().bin;
    return shots.stream().reduce(new ApproachShot(bin, 0L, BigDecimal.ZERO), ApproachShot::merge);
  }

  public BigDecimal getMeanStrokesGainedPerRound() {
    return Utils.divide(this.strokesGained, this.roundCount, 2);
  }
}
