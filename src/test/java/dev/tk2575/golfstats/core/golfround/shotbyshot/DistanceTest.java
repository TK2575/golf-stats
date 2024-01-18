package dev.tk2575.golfstats.core.golfround.shotbyshot;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class DistanceTest {
  
  @Test
  void testCalculateShotDistance() {
    var target = Distance.yards(100);
    var miss = Distance.yards(12);
    var missAngle = spy(MissAngle.class);

    // no angle degrees
    when(missAngle.getAngleDegrees()).thenReturn(Optional.empty());
    var shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(88, shotDistance.getLengthInYards());

    // long (clock face 12)
    when(missAngle.getAngleDegrees()).thenReturn(Optional.of(0));
    shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(112, shotDistance.getLengthInYards());
    
    // short (clock face 6)
    when(missAngle.getAngleDegrees()).thenReturn(Optional.of(180));
    shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(88, shotDistance.getLengthInYards());
    
    // clock face 2
    when(missAngle.getAngleDegrees()).thenReturn(Optional.of(60));
    shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(107, shotDistance.getLengthInYards());
    
    // clock face 5
    when(missAngle.getAngleDegrees()).thenReturn(Optional.of(150));
    shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(90, shotDistance.getLengthInYards());
    
    // clock face 8
    when(missAngle.getAngleDegrees()).thenReturn(Optional.of(240));
    shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(95, shotDistance.getLengthInYards());
    
    // clock face 11
    when(missAngle.getAngleDegrees()).thenReturn(Optional.of(330));
    shotDistance = Distance.shotDistance(target, miss, missAngle);
    assertEquals(111, shotDistance.getLengthInYards());
    
  }

}