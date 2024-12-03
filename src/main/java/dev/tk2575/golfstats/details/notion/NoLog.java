package dev.tk2575.golfstats.details.notion;

import notion.api.v1.logging.NotionLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoLog implements NotionLogger {

	@Override
	public void debug(@NotNull String s) {

	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void debug(@NotNull String s, @Nullable Throwable throwable) {

	}

	@Override
	public void info(@NotNull String s, @Nullable Throwable throwable) {

	}

	@Override
	public void warn(@NotNull String s, @Nullable Throwable throwable) {

	}

	@Override
	public void error(@NotNull String s, @Nullable Throwable throwable) {

	}

	@Override
	public void info(@NotNull String s) {

	}

	@Override
	public void warn(@NotNull String s) {

	}

	@Override
	public void error(@NotNull String s) {

	}
}
