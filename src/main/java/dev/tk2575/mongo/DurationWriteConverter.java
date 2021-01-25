package dev.tk2575.mongo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DurationWriteConverter implements Converter<Duration, Long> {

	@Override
	public Long convert(Duration duration) {
		return duration.getSeconds();
	}
}
