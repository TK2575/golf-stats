package dev.tk2575.golfstats.details.notion;

import dev.tk2575.Utils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@ToString
public class NotionGolfRound {
  private Optional<String> name;
  private String id;
  private Optional<ZonedDateTime> start = Optional.empty();
  private Optional<ZonedDateTime> end = Optional.empty();
  private Optional<String> course;
  private Optional<String> city;
  private Optional<String> state;
  private Optional<String> tees;
  private Optional<BigDecimal> rating;
  private Optional<BigDecimal> slope;
  private Optional<String> transport;
  private Optional<String> shotsShorthand;
  private boolean triggerValidation;
  private Optional<String> validationFailure;

  private Optional<Integer> strokes;
  private Optional<Integer> score;
  private Optional<BigDecimal> tot;
  private Optional<BigDecimal> ott;
  private Optional<BigDecimal> app;
  private Optional<BigDecimal> arg;
  private Optional<BigDecimal> putt;
  private Optional<BigDecimal> rcvr;
  private Optional<BigDecimal> p75Drive;
  private Optional<Integer> greatShots;
  private Optional<Integer> poorShots;
  private Optional<BigDecimal> differential;
  private Optional<BigDecimal> handicapIndex;
  private Optional<Long> yards;
  private Optional<Integer> holesPlayed;

  @ToString.Exclude
  private final Map<String, PageProperty> props;

  public NotionGolfRound(Page page) {
    this(page.getProperties());

    this.id = Utils.getNullSafe(() -> {
      PageProperty.UniqueId uId = props.get("ID").getUniqueId();
      return String.join("-", uId.getPrefix(), uId.getNumber().toString());
    }).orElseThrow(() -> new IllegalArgumentException("No ID found in the page"));
    
    this.slope = getBigDecimal("Slope");
    this.validationFailure = getString("Validation Failure");
    this.differential = getBigDecimal("Differential");
    this.score = getInt("Score");
    this.rating = getBigDecimal("Rating");
    this.tot = getBigDecimal("TOT");
    this.putt = getBigDecimal("PUTT");
    this.triggerValidation = Utils.getNullSafe(() ->
        props.get("Trigger Validation").getCheckbox()).orElse(false
    );
    this.shotsShorthand = getString("Shots Shorthand");
    this.yards = getBigDecimal("Yards").map(BigDecimal::longValue);
    this.city = getString("City");
    this.tees = getString("Tees");
    this.state = getString("State");
    this.app = getBigDecimal("APP");
    this.holesPlayed = getInt("Holes Played");
    this.handicapIndex = getBigDecimal("Handicap Index");
    this.ott = getBigDecimal("OTT");
    this.poorShots = getInt("Poor Shots");
    this.course = getString("Course");
    this.transport = getString("Transport");
    this.p75Drive = getBigDecimal("p75 Drive");
    this.rcvr = getBigDecimal("RCVR");
    this.strokes = getInt("Strokes");
    this.arg = getBigDecimal("ARG");
    this.greatShots = getInt("Great Shots");
    this.name = getString("Name");


    var date = Utils.getNullSafe(() -> props.get("Date").getDate());
    if (date.isPresent()) {
      var theDate = date.get();
      this.start = theDate.getStart() == null
          ? Optional.empty()
          : Optional.of(ZonedDateTime.parse(theDate.getStart()));
      this.end = theDate.getEnd() == null
          ? Optional.empty()
          : Optional.of(ZonedDateTime.parse(theDate.getEnd()));
    }
  }

  private Optional<Integer> getInt(String key) {
    return Utils.getNullSafe(() -> this.props.get(key).getNumber().intValue());
  }
  
  private Optional<BigDecimal> getBigDecimal(String key) {
    return Utils.getNullSafe(() -> {
        var val = this.props.get(key).getNumber().floatValue();
        return Utils.roundToTwoDecimalPlaces(BigDecimal.valueOf(val));
    });
  }

  private Optional<String> getString(String key) {
    return Utils.getNullSafe(() -> 
        this.props.get(key).getRichText().getFirst().getText().getContent()
    );
  }

  public boolean isValidationReady() {
    return this.start.isPresent()
        && this.rating.isPresent() 
        && this.slope.isPresent() 
        && this.shotsShorthand.isPresent();
  }
}
