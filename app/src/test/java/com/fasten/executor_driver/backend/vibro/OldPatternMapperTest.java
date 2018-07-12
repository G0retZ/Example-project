package com.fasten.executor_driver.backend.vibro;

import static org.junit.Assert.assertArrayEquals;

import com.fasten.executor_driver.utils.Pair;
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
                new Pair<>(200L, 0),
                new Pair<>(500L, -1),
                new Pair<>(300L, 0),
                new Pair<>(800L, 255)
            )
        )
    );
  }

  @Test
  public void testLoudFirst() {
    assertArrayEquals(
        new long[]{0, 200, 500, 300, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new Pair<>(200L, -1),
                new Pair<>(500L, 0),
                new Pair<>(300L, 255),
                new Pair<>(800L, 0)
            )
        )
    );
  }

  @Test
  public void testQuietFirstRepeatQuiet() {
    assertArrayEquals(
        new long[]{200, 500, 500, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new Pair<>(200L, 0),
                new Pair<>(500L, -1),
                new Pair<>(300L, 0),
                new Pair<>(200L, 0),
                new Pair<>(800L, 255)
            )
        )
    );
  }

  @Test
  public void testLoudFirstRepeatQuiet() {
    assertArrayEquals(
        new long[]{0, 200, 700, 300, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new Pair<>(200L, -1),
                new Pair<>(500L, 0),
                new Pair<>(200L, 0),
                new Pair<>(300L, 255),
                new Pair<>(800L, 0)
            )
        )
    );
  }

  @Test
  public void testQuietFirstRepeatLoud() {
    assertArrayEquals(
        new long[]{200, 500, 300, 1000},
        oldPatternMapper.map(
            Arrays.asList(
                new Pair<>(200L, 0),
                new Pair<>(500L, -1),
                new Pair<>(300L, 0),
                new Pair<>(200L, 126),
                new Pair<>(800L, 255)
            )
        )
    );
  }

  @Test
  public void testLoudFirstRepeatLoud() {
    assertArrayEquals(
        new long[]{0, 200, 500, 500, 800},
        oldPatternMapper.map(
            Arrays.asList(
                new Pair<>(200L, -1),
                new Pair<>(500L, 0),
                new Pair<>(200L, 126),
                new Pair<>(300L, 255),
                new Pair<>(800L, 0)
            )
        )
    );
  }

  @Test
  public void testComplex() {
    assertArrayEquals(
        new long[]{0, 193, 384, 929, 11666, 5028, 283, 9390, 939, 519, 781, 920, 495},
        oldPatternMapper.map(
            Arrays.asList(
                new Pair<>(193L, -1),
                new Pair<>(384L, 0),
                new Pair<>(929L, 126),
                new Pair<>(3094L, 0),
                new Pair<>(8572L, 0),
                new Pair<>(4830L, 39),
                new Pair<>(198L, -1),
                new Pair<>(283L, 0),
                new Pair<>(9390L, -1),
                new Pair<>(939L, 0),
                new Pair<>(120L, 29),
                new Pair<>(399L, 84),
                new Pair<>(781L, 0),
                new Pair<>(920L, 255),
                new Pair<>(495L, 0)
            )
        )
    );
  }
}