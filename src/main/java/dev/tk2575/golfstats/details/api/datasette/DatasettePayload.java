package dev.tk2575.golfstats.details.api.datasette;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
class DatasettePayload {
    private final List<DatasetteRound> rounds = new ArrayList<>();
    private final List<DatasetteHole> holes = new ArrayList<>();
    private final List<DatasetteShot> shots = new ArrayList<>();
    
    DatasettePayload(Collection<GolfRound> source) {
        int i = 0;
        for (GolfRound round : source) {
            i++;
            this.rounds.add(new DatasetteRound(i, round));
            List<Hole> roundHoles = round.getHoles().toList();
            this.holes.addAll(DatasetteHole.compile(i, roundHoles));
            for (Hole hole : roundHoles) {
                this.shots.addAll(DatasetteShot.compile(i, hole.getNumber(), hole.getShots()));
            }
        }
    }
}
