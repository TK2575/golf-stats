package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class RoundMeta {

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final String transport;

	public RoundMeta(@NonNull GolfRound round) {
		this.date = round.getDate();
		this.duration = round.getDuration();
		this.golfer = round.getGolfer();
		this.course = round.getCourse();
		this.tee = round.getTee();
		this.transport = round.getTransport();
	}

	public RoundMeta(@NonNull Golfer golfer, @NonNull LocalDateTime started, @NonNull LocalDateTime ended, @NonNull Course course, @NonNull Tee tee) {
		this.date = started.toLocalDate();
		this.duration = Duration.between(started, ended);
		this.golfer = golfer;
		this.course = course;
		this.tee = tee;
		this.transport = "Unknown";
	}
	
	public RoundMeta(@NonNull Golfer golfer, LocalDate date, Duration duration, Course course, Tee tee, String transport) {
		this.date = date;
		this.duration = duration;
		this.golfer = golfer;
		this.course = course;
		this.tee = tee;
		this.transport = transport;
	}

	static RoundMeta compositeOf(GolfRound round1, GolfRound round2) {
		String transport = round1.getTransport().equalsIgnoreCase(round2.getTransport())
				? round1.getTransport()
				: "Various";

		return RoundMeta.builder()
				.date(round2.getDate())
				.duration(round1.getDuration().plus(round2.getDuration()))
				.golfer(round2.getGolfer())
				.course(Course.compositeOf(round1.getCourse(), round2.getCourse()))
				.tee(Tee.compositeOf(round1.getTee(), round2.getTee()))
				.transport(transport)
				.build();
	}
}
