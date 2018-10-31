package com.cargopull.executor_driver.backend.vibro;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import org.junit.Test;

public class NewPatternMapperTest {

  @Test
  public void testComplex() {
    // Дано:
    NewPatternMapper newPatternMapper = new NewPatternMapper();

    // Действие:
    VibeBeats vibeBeats = newPatternMapper.map(
        Arrays.asList(
            new VibeBeat(193L, -1),
            new VibeBeat(384L, 0),
            new VibeBeat(929L, 126),
            new VibeBeat(3094L, 0),
            new VibeBeat(8572L, 0),
            new VibeBeat(4830L, 39),
            new VibeBeat(198L, -1),
            new VibeBeat(283L, 0),
            new VibeBeat(790L, -1),
            new VibeBeat(939L, 0),
            new VibeBeat(120L, 29),
            new VibeBeat(399L, 84),
            new VibeBeat(781L, 0),
            new VibeBeat(920L, 255),
            new VibeBeat(495L, 0)
        )
    );

    // Результат:
    assertArrayEquals(
        new long[]{193, 384, 929, 3094, 8572, 4830, 198, 283, 790, 939, 120, 399, 781, 920, 495},
        vibeBeats.durations
    );
    assertArrayEquals(
        new int[]{-1, 0, 126, 0, 0, 39, -1, 0, -1, 0, 29, 84, 0, 255, 0},
        vibeBeats.volumes
    );
  }
}