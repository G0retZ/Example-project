package com.cargopull.executor_driver.backend.vibro;

import androidx.annotation.NonNull;

class VibeBeats {

  @NonNull
  final long[] durations;
  @NonNull
  final int[] volumes;

  /**
   * Constructor for a VibeBeats.
   *
   * @param length the number of beats
   */
  VibeBeats(int length) {
    this.durations = new long[length];
    this.volumes = new int[length];
  }
}
