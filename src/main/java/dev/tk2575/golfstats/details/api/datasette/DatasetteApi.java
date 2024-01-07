package dev.tk2575.golfstats.details.api.datasette;

import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class DatasetteApi {

    private final DatasettePayload payload = getDetails();

    private DatasettePayload getDetails() {
        var rounds = 
                GolfRoundResourceManager.getInstance()
                        .getRoundsByGolfer()
                        .values().stream()
                        .flatMap(List::stream)
                        .toList();
        return new DatasettePayload(rounds);
    }
    
    public List<DatasetteRound> rounds() {
        return this.payload.getRounds();
    }
    
    public List<DatasetteHole> holes() {
        return this.payload.getHoles();
    }
    
    public List<DatasetteShot> shots() {
        return this.payload.getShots();
    }
}
