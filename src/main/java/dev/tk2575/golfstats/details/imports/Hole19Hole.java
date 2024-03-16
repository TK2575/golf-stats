package dev.tk2575.golfstats.details.parsers;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@ToString
public class Hole19Hole {
	private Integer sequence;
	@SerializedName("si") private Integer strokeIndex;
	private Integer par;
}
