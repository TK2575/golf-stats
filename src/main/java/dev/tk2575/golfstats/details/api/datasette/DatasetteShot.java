package dev.tk2575.golfstats.details.api.datasette;

import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
class DatasetteShot {
    
    private final int roundId;
    private final int hole;
    private final int sequence;
    private final String lie;
    private final String category;
    private final long distanceValue;
    private final String distanceUnit;
    private final BigDecimal strokesGained;
    private final String resultLie;
    private final String missType;
    private final long missDistnaceValue;
    private final String missDistanceUnit;
    private final int missAngle;
    private final String missDescription;
    private final int strokes;

    private DatasetteShot(int roundId, int holeNumber, int sequence, Shot shot) {
        this.roundId = roundId;
        this.hole = holeNumber;
        this.sequence = sequence;
        this.lie = shot.getLie().getLabel();
        this.category = shot.getShotCategory().getLabel();
        this.distanceValue = shot.getDistanceFromTarget().getValue();
        this.distanceUnit = shot.getDistanceFromTarget().getLengthUnit();
        this.strokesGained = shot.getStrokesGained();
        this.resultLie = shot.getResultLie().getLabel();
        this.missType = shot.getMissAngle().getMissType();
        this.missDistnaceValue = shot.getMissDistance().getValue();
        this.missDistanceUnit = shot.getMissDistance().getLengthUnit();
        this.missAngle = shot.getMissAngle().getAngleDegrees();
        this.missDescription = shot.getMissAngle().getDescription();
        this.strokes = shot.getCount();
    }
    
    static List<DatasetteShot> compile(int roundId, int holeNumber, ShotStream shots) {
        int i = 0;
        List<DatasetteShot> results = new ArrayList<>();
        for (Shot shot : shots.getStream().toList()) {
            i++;
            results.add(new DatasetteShot(roundId, holeNumber, i, shot));
        }
        return results;
    }
}
