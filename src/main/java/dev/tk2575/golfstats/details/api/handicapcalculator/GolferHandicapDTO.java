package dev.tk2575.golfstats.details.api.handicapcalculator;

import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GolferHandicapDTO {
	private final @NonNull List<TeeDTO> tees;
	private final @NonNull List<GolferHandicaps> handicaps;
}
