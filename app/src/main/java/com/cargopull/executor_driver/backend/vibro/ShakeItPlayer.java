package com.cargopull.executor_driver.backend.vibro;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.utils.Pair;
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
