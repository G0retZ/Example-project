package com.cargopull.executor_driver.backend.vibro;

import static org.junit.Assert.assertArrayEquals;

import com.cargopull.executor_driver.utils.Pair;
import java.util.Arrays;
import org.junit.Test;

public class NewPatternMapperTest {

  @Test
  public void testComplex() {
    // Дано:
    NewPatternMapper newPatternMapper = new NewPatternMapper();

    // Действие:
    Pair<long[], int[]> pair = newPatternMapper.map(
        Arrays.asList(
            new Pair<>(193L, -1),
            new Pair<>(384L, 0),
            new Pair<>(929L, 126),
            new Pair<>(3094L, 0),
            new Pair<>(8572L, 0),
            new Pair<>(4830L, 39),
            new Pair<>(198L, -1),
            new Pair<>(283L, 0),
            new Pair<>(790L, -1),
            new Pair<>(939L, 0),
            new Pair<>(120L, 29),
            new Pair<>(399L, 84),
            new Pair<>(781L, 0),
            new Pair<>(920L, 255),
            new Pair<>(495L, 0)
        )
    );

    // Результат:
    assertArrayEquals(
        new long[]{193, 384, 929, 3094, 8572, 4830, 198, 283, 790, 939, 120, 399, 781, 920, 495},
        pair.first
    );
    assertArrayEquals(
        new int[]{-1, 0, 126, 0, 0, 39, -1, 0, -1, 0, 29, 84, 0, 255, 0},
        pair.second
    );
  }
}