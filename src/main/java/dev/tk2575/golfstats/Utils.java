package dev.tk2575.golfstats;

import lombok.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;

@NoArgsConstructor(access = AccessLevel.NONE)
public class Utils {

	public static BigDecimal divideInts(Integer value, Integer divisor) {
		return BigDecimal.valueOf((float) value / divisor).setScale(2, HALF_UP);
	}

	public static BigDecimal mean(BigDecimal int1, BigDecimal int2) {
		return (int1.add(int2)).divide(new BigDecimal("2"), HALF_UP);
	}

	public static BigDecimal median(List<BigDecimal> list) {
		if (list == null || list.isEmpty()) throw new IllegalArgumentException("cannot compute median of null or empty list");

		list.sort(Comparator.comparing(BigDecimal::doubleValue));
		int size = list.size();
		int midPoint = size / 2;
		BigDecimal midValue = list.get(midPoint);
		if (size % 2 == 1) {
			return midValue;
		}
		else {
			return (list.get(midPoint-1).add(midValue)).divide(new BigDecimal("2.0"), HALF_UP);
		}
	}

	public static String convertToTSV(String[] data) {
		return printAsDelimitedValues(data, "\t");
	}

	public static String convertToCSV(String[] data) {
		return printAsDelimitedValues(data, ",");
	}

	private static String printAsDelimitedValues(String[] data,
	                                             String delimiter) {
		return Stream.of(data)
		             .map(Utils::escapeSpecialCharacters)
		             .collect(Collectors.joining(delimiter));
	}

	private static String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}


}
