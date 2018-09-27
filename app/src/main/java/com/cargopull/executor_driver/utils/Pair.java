package com.cargopull.executor_driver.utils;

import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */
public class Pair<F, S> {

  @SerializedName("duration")
  @Expose
  @NonNull
  public final F first;
  @SerializedName("volume")
  @Expose
  @NonNull
  public final S second;

  /**
   * Constructor for a Pair.
   *
   * @param first the first object in the Pair
   * @param second the second object in the pair
   */
  public Pair(@NonNull F first, @NonNull S second) {
    this.first = first;
    this.second = second;
  }
}
