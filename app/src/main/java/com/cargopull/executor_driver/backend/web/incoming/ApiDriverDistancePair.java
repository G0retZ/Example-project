package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;

public class ApiDriverDistancePair {

  @SerializedName("distance")
  private int distance;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiDriverDistancePair() {
  }

  @SuppressWarnings("SameParameterValue")
  ApiDriverDistancePair(int distance) {
    this.distance = distance;
  }

  public int getDistance() {
    return distance;
  }
}
