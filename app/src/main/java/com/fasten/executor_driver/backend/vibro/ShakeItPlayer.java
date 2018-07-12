package com.fasten.executor_driver.backend.vibro;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.utils.Pair;
import java.util.List;

/**
 * Проигрыватель вибро.
 */
public interface ShakeItPlayer {

  /**
   * Вибрировать.
   *
   * @param patternItems - набор таймингов с аплитудами
   */
  void shakeIt(@NonNull List<Pair<Long, Integer>> patternItems);
}
