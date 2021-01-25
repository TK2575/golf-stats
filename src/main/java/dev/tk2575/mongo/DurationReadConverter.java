package dev.tk2575.mongo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class DurationReadConverter implements Converter<Long, Duration> {

	@Override
	public Duration convert(Long seconds) {
		return Duration.of(seconds, ChronoUnit.SECONDS);
	}
}
