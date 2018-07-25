package com.cargopull.executor_driver.backend.ringtone;

import android.support.annotation.RawRes;

/**
 * Проигрыватель звуков.
 */
public interface RingTonePlayer {

  /**
   * Проиграть звук.
   *
   * @param soundRes ИД ресурса звука.
   */
  void playRingTone(@RawRes int soundRes);
}
