package dev.tk2575.golfstats;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.RoundingMode.HALF_UP;

@NoArgsConstructor(access = AccessLevel.NONE)
public class Utils {

	public static boolean isNullOrBlank(String string) {
		return (string == null || string.isBlank());
	}

	public static <T> boolean isNullOrEmpty(Collection<T> collection) {
		return (collection == null || collection.isEmpty());
	}

	public static BigDecimal divideInts(Integer value, Integer divisor) {
		return BigDecimal.valueOf((float) value / divisor).setScale(2, HALF_UP);
	}

	public static Long mean(Long int1, Long int2) {
		if (int1.equals(int2)) return int1;
		return mean(BigDecimal.valueOf(int1), BigDecimal.valueOf(int2)).longValueExact();
	}

	public static BigDecimal mean(BigDecimal int1, BigDecimal int2) {
		if (int1.equals(int2)) return int1;
		return (int1.add(int2)).divide(new BigDecimal("2"), HALF_UP);
	}

	public static BigDecimal median(List<BigDecimal> list) {
		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException("cannot compute median of null or empty list");
		}

		list.sort(Comparator.comparing(BigDecimal::doubleValue));
		int size = list.size();
		int midPoint = size / 2;
		BigDecimal midValue = list.get(midPoint);
		if (size % 2 == 1) {
			return midValue;
		}
		else {
			return (list.get(midPoint - 1)
			            .add(midValue)).divide(new BigDecimal("2.0"), HALF_UP);
		}
	}

	public static BigDecimal roundToTwoDecimalPlaces(BigDecimal value) {
		return value.setScale(2, HALF_UP);
	}

	public static BigDecimal roundToOneDecimalPlace(BigDecimal value) {
		return value.setScale(1, HALF_UP);
	}

	public static String convertToTSV(String[] data) {
		return printAsDelimitedValues(data, "\t");
	}

	public static String convertToCSV(String[] data) {
		return printAsDelimitedValues(data, ",");
	}

	public static <T> String joinByHyphenIfUnequal(T o1, T o2) {
		return joinIfUnequal(" - ", o1, o2);
	}

	public static <T> String joinIfUnequal(String delimiter, T o1, T o2) {
		if (o1.equals(o2)) {
			return o1.toString();
		}
		return String.join(delimiter, o1.toString(), o2.toString());
	}

	private static String printAsDelimitedValues(String[] data, String delimiter) {
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
