package dev.tk2575;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
  
  @Test
  void testDivide() {
    long numerator = 4L;
    long denominator = 9L;
    assertEquals(new BigDecimal(".44"), Utils.divide(numerator, denominator));
  }

}