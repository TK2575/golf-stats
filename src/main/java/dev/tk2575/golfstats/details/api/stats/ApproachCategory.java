package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Lie;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApproachCategory {
  
  @Getter
  public enum Bin {
    FAIRWAY_100("Fairway < 100"),
    FAIRWAY_100_150("Fairway 100-150"),
    FAIRWAY_150_200("Fairway 150-200"),
    FAIRWAY_200("Fairway 200+"),
    ROUGH_150("Rough < 150"),
    ROUGH_150_PLUS("Rough 150+"),
    OTHER("Other");
    
    private final String label;
    
    Bin(String label) {
      this.label = label;
    }

  }
  
  public static Function<Shot, ApproachCategory.Bin> shotBinFunction = shot -> {
    var lie = shot.getLie();
    var dist = shot.getDistanceFromTarget().getLengthInYards();
    var cat = shot.getShotCategory();
    var other = Bin.OTHER;

    if (!cat.is(ShotCategory.approach())) {
      return other;
    }

    if (lie.is(Lie.fairway())) {
      if (dist <= 100) {
        return Bin.FAIRWAY_100;
      }
      if (dist < 150) {
        return Bin.FAIRWAY_100_150;
      }
      if (dist < 200) {
        return Bin.FAIRWAY_150_200;
      }
      return Bin.FAIRWAY_200;
    }

    if (lie.is(Lie.rough())) {
      return dist < 150 ? Bin.ROUGH_150 : Bin.ROUGH_150_PLUS;
    }

    return other;
  };
}
