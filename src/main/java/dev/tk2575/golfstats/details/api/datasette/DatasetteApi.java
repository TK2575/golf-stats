package dev.tk2575.golfstats.details.api.datasette;

import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("datasette")
@Log4j2
public class DatasetteApi {

    private final DatasettePayload payload = getDetails();

    private DatasettePayload getDetails() {
        var rounds = 
                GolfRoundResourceManager.getInstance()
                        .getRoundsByGolfer().values().stream().flatMap(List::stream).toList();
        return new DatasettePayload(rounds);
    }

    @GetMapping(value = "rounds", produces = "application/json")
    public List<DatasetteRound> rounds() {
        return this.payload.getRounds();
    }

    @GetMapping(value = "holes", produces = "application/json")
    public List<DatasetteHole> holes() {
        return this.payload.getHoles();
    }

    @GetMapping(value = "shots", produces = "application/json")
    public List<DatasetteShot> shots() {
        return this.payload.getShots();
    }
}
