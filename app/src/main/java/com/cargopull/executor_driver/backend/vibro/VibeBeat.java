package com.cargopull.executor_driver.backend.vibro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class VibeBeat {

  @SerializedName("duration")
  @Expose
  final long duration;
  @SerializedName("volume")
  @Expose
  final int volume;

  /**
   * Constructor for a VibeBeat.
   *
   * @param duration the duration of a beat
   * @param volume the volume of a beat
   */
  VibeBeat(long duration, int volume) {
    this.duration = duration;
    this.volume = volume;
  }
}
