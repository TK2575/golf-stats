package dev.tk2575;

import dev.tk2575.golfstats.details.CSVFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.NONE)
public class Utils {

	public static boolean isNullOrBlank(String string) {
		return (string == null || string.isBlank());
	}

	public static <T> boolean isNullOrEmpty(Collection<T> collection) {
		return (collection == null || collection.isEmpty());
	}

	public static BigDecimal divide(Number value, Number divisor) {
		return divide(value, divisor, 2);
	}
	
	public static BigDecimal divide(Number numerator, Number divisor, int scale) {
		return new BigDecimal(numerator.toString()).divide(new BigDecimal(divisor.toString()), scale, HALF_UP);
	}
	
	public static Long percentile(@NonNull Collection<Long> samples, long percentile) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("cannot compute percentile of empty list");
		}
		
		var items = new ArrayList<>(samples);
		items.sort(Comparator.naturalOrder());
		int index = (int) Math.ceil(percentile / 100.0 * items.size());
		return items.get(index - 1);
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

	public static String convertToTSV(Collection<String> data) {
		return printAsDelimitedValues("\t", data);
	}

	public static String convertToCSV(Collection<String> data) {
		return printAsDelimitedValues(",", data);
	}

	public static String toTitleCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		StringBuilder converted = new StringBuilder();

		boolean convertNext = true;
		for (char ch : text.toCharArray()) {
			if (Character.isSpaceChar(ch)) {
				convertNext = true;
			}
			else if (convertNext) {
				ch = Character.toTitleCase(ch);
				convertNext = false;
			}
			else {
				ch = Character.toLowerCase(ch);
			}
			converted.append(ch);
		}

		return converted.toString();
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

	public static LocalTime parseTime(List<DateTimeFormatter> timeFormats, String raw) {
		LocalTime result;

		for (DateTimeFormatter timeFormat : timeFormats) {
			try {
				result = LocalTime.parse(raw, timeFormat);
				if (result != null) return result;
			}
			catch (Exception ignored) { }
		}

		throw new IllegalArgumentException(raw + " cannot be parsed with any of the supplied formats");
	}

	public static List<CSVFile> readCSVFilesInDirectory(@NonNull String directory) {
		List<CSVFile> files = new ArrayList<>();
		ClassLoader classLoader = Utils.class.getClassLoader();
		InputStream directoryStream = classLoader.getResourceAsStream(directory);

		if (directoryStream != null) {
			List<String> fileNames =
					new BufferedReader(new InputStreamReader(directoryStream))
							.lines()
							.filter(each -> !each.contains(" "))
							.filter(each -> each.endsWith(".csv"))
							.toList();


			InputStream fileStream;
			for (String each : fileNames) {
				fileStream = classLoader.getResourceAsStream(String.join("/", directory, each));
				if (fileStream != null) {
					files.add(new CSVFile(each, new BufferedReader(new InputStreamReader(fileStream)).lines().collect(joining("\n"))));
				}
			}
		}

		return files;
	}

	private static String printAsDelimitedValues(String delimiter, Collection<String> data) {
		return data.stream()
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
