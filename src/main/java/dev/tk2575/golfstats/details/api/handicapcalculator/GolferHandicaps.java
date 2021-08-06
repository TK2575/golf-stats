package dev.tk2575.golfstats.details.api.handicapcalculator;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class GolferHandicaps {
	private final @NonNull String name;
	private final @NonNull Map<String, BigDecimal> handicapIndexes;
}
