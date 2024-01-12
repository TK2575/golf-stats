package dev.tk2575.golfstats.core.golfround.shotbyshot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeetDistanceTest {
  
  @Test
  void testDistanceInYards() {
    assertEquals(33, Distance.feet(100).getLengthInYards());
  }

}