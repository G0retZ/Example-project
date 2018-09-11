package com.cargopull.executor_driver.backend.vibro;

import android.support.annotation.RawRes;

/**
 * Проигрыватель вибро.
 */
public interface ShakeItPlayer {

  /**
   * Вибрировать.
   *
   * @param patternId - id RAW ресурса с JSON с набором таймингов с аплитудами
   */
  void shakeIt(@RawRes int patternId);
}
