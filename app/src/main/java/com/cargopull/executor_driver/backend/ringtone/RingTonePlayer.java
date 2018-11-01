package com.cargopull.executor_driver.backend.ringtone;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

/**
 * Проигрыватель звуков.
 */
public interface RingTonePlayer {

  /**
   * Проиграть звук.
   *
   * @param soundRes ИД ресурса звука.
   */
  void playRingTone(@NonNull @RawRes Integer soundRes);

  /**
   * Остановить звук с затуханием в 2 с.
   *
   * @param soundRes ИД ресурса звука.
   */
  void stopRingTone(@NonNull @RawRes Integer soundRes);
}
