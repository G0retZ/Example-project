package com.cargopull.executor_driver.backend.vibro;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class OldPatternMapperTest {

  private OldPatternMapper oldPatternMapper;

  @Before
  public void setUp() {
    oldPatternMapper = new OldPatternMapper();
  }

  @Test
  public void testQuietFirst() {
    assertArrayEquals(
        new long[]{200, 500, 300, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(200L, 0),
                new VibeBeat(500L, -1),
                new VibeBeat(300L, 0),
                new VibeBeat(800L, 255)
            )
        ).durations
    );
  }

  @Test
  public void testLoudFirst() {
    assertArrayEquals(
        new long[]{0, 200, 500, 300, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(200L, -1),
                new VibeBeat(500L, 0),
                new VibeBeat(300L, 255),
                new VibeBeat(800L, 0)
            )
        ).durations
    );
  }

  @Test
  public void testQuietFirstRepeatQuiet() {
    assertArrayEquals(
        new long[]{200, 500, 500, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(200L, 0),
                new VibeBeat(500L, -1),
                new VibeBeat(300L, 0),
                new VibeBeat(200L, 0),
                new VibeBeat(800L, 255)
            )
        ).durations
    );
  }

  @Test
  public void testLoudFirstRepeatQuiet() {
    assertArrayEquals(
        new long[]{0, 200, 700, 300, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(200L, -1),
                new VibeBeat(500L, 0),
                new VibeBeat(200L, 0),
                new VibeBeat(300L, 255),
                new VibeBeat(800L, 0)
            )
        ).durations
    );
  }

  @Test
  public void testQuietFirstRepeatLoud() {
    assertArrayEquals(
        new long[]{200, 500, 300, 1000},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(200L, 0),
                new VibeBeat(500L, -1),
                new VibeBeat(300L, 0),
                new VibeBeat(200L, 126),
                new VibeBeat(800L, 255)
            )
        ).durations
    );
  }

  @Test
  public void testLoudFirstRepeatLoud() {
    assertArrayEquals(
        new long[]{0, 200, 500, 500, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(200L, -1),
                new VibeBeat(500L, 0),
                new VibeBeat(200L, 126),
                new VibeBeat(300L, 255),
                new VibeBeat(800L, 0)
            )
        ).durations
    );
  }

  @Test
  public void testComplex() {
    assertArrayEquals(
        new long[]{0, 193, 384, 929, 11666, 5028, 283, 9390, 939, 519, 781, 920, 495},
        oldPatternMapper.map(
            Arrays.asList(
                new VibeBeat(193L, -1),
                new VibeBeat(384L, 0),
                new VibeBeat(929L, 126),
                new VibeBeat(3094L, 0),
                new VibeBeat(8572L, 0),
                new VibeBeat(4830L, 39),
                new VibeBeat(198L, -1),
                new VibeBeat(283L, 0),
                new VibeBeat(9390L, -1),
                new VibeBeat(939L, 0),
                new VibeBeat(120L, 29),
                new VibeBeat(399L, 84),
                new VibeBeat(781L, 0),
                new VibeBeat(920L, 255),
                new VibeBeat(495L, 0)
            )
        ).durations
    );
  }
}