package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotCategory;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RoundDetailTableColumn {
  private final String header; 
  private final Integer par;
  private final Integer strokes;
  private final BigDecimal strokesGainedTotal;
  private final BigDecimal strokesGainedTee;
  private final BigDecimal strokesGainedApproach;
  private final BigDecimal strokesGainedAroundGreen;
  private final BigDecimal strokesGainedPutting;
  private final String greenInRegulation;
  private final String fairwayHit;
  private final BigDecimal drivingDistance;
  
  public String getStrokesGainedTotal() {
    return strokesGainedTotal == null ? "" : strokesGainedTotal.toPlainString();
  }
  
  public String getStrokesGainedTee() {
    return strokesGainedTee == null ? "" : strokesGainedTee.toPlainString();
  }
  
  public String getStrokesGainedApproach() {
    return strokesGainedApproach == null ? "" : strokesGainedApproach.toPlainString();
  }
  
  public String getStrokesGainedAroundGreen() {
    return strokesGainedAroundGreen == null ? "" : strokesGainedAroundGreen.toPlainString();
  }
  
  public String getStrokesGainedPutting() {
    return strokesGainedPutting == null ? "" : strokesGainedPutting.toPlainString();
  }
  
  public String getDrivingDistance() {
    return drivingDistance == null ? "-" : drivingDistance.toPlainString();
  }
  
  public static List<RoundDetailTableColumn> compile(GolfRound source) {
    List<RoundDetailTableColumn> results = new ArrayList<>();
    List<Hole> holes = source.getHoles().toList();
    List<Hole> front = new HoleStream(holes).frontNine().toList();
    List<Hole> back = new HoleStream(holes).backNine().toList();
    
    front.stream().map(RoundDetailTableColumn::new).forEach(results::add);
    results.add(new RoundDetailTableColumn(front, "Out"));
    back.stream().map(RoundDetailTableColumn::new).forEach(results::add);
    results.add(new RoundDetailTableColumn(back, "In"));
    results.add(new RoundDetailTableColumn(holes, "Total"));

    return results;
  }
  
  private RoundDetailTableColumn(Hole hole) {
    this.header = hole.getNumber().toString();
    this.par = hole.getPar();
    this.strokes = hole.getStrokes();
    this.strokesGainedTotal = hole.getStrokesGained();
    var sgMap = hole.getShots().strokesGainedByShotType();
    this.strokesGainedTee = sgMap.getOrDefault(ShotCategory.tee().getLabel(), null);
    this.strokesGainedApproach = sgMap.getOrDefault(ShotCategory.approach().getLabel(), null);
    this.strokesGainedAroundGreen = sgMap.getOrDefault(ShotCategory.aroundGreen().getLabel(), null);
    this.strokesGainedPutting = sgMap.getOrDefault(ShotCategory.green().getLabel(), null);
    this.greenInRegulation = hole.isGreenInRegulation() ? "'+" : "x";
    this.fairwayHit = hole.isFairwayPresent() ? hole.isFairwayInRegulation() ? "'+" : "x" : "-";
    this.drivingDistance = hole.getShots().teeShots()
        .map(shot -> shot.getDistance().getLengthInYards())
        .findFirst()
        .map(BigDecimal::new)
        .orElse(null);
  }
  
  private RoundDetailTableColumn(List<Hole> holes, String header) {
    this.header = header;
    this.par = new HoleStream(holes).getPar();
    this.strokes = new HoleStream(holes).totalStrokes();
    this.strokesGainedTotal = new HoleStream(holes).totalStrokesGained();
    
    var sgMap = new HoleStream(holes).strokesGainedByShotType();
    this.strokesGainedTee = sgMap.getOrDefault(ShotCategory.tee().getLabel(), null);
    this.strokesGainedApproach = sgMap.getOrDefault(ShotCategory.approach().getLabel(), null);
    this.strokesGainedAroundGreen = sgMap.getOrDefault(ShotCategory.aroundGreen().getLabel(), null);
    this.strokesGainedPutting = sgMap.getOrDefault(ShotCategory.green().getLabel(), null);
    
    this.greenInRegulation = new HoleStream(holes).totalGreensInRegulation() + "/" + holes.size();
    this.fairwayHit = new HoleStream(holes).totalFairwaysInRegulation() + "/" + new HoleStream(holes).totalFairways();
    this.drivingDistance = BigDecimal.valueOf(new HoleStream(holes).allShots().p75DrivingDistance());
  }
  
  public List<String> toValues() {
    return List.of(
        header,
        par.toString(),
        strokes.toString(),
        getStrokesGainedTotal(),
        getStrokesGainedTee(),
        getStrokesGainedApproach(),
        getStrokesGainedAroundGreen(),
        getStrokesGainedPutting(),
        greenInRegulation,
        fairwayHit,
        getDrivingDistance()
    );
  }
  
  public static final List<String> HEADERS = List.of(
      "Hole",
      "Par",
      "Strokes",
      "Strokes Gained",
      "Strokes Gained - Tee",
      "Strokes Gained - Approach",
      "Strokes Gained - Around Green",
      "Strokes Gained - Putting",
      "GIR",
      "Fairway Hit",
      "Driving Distance");
  
}
