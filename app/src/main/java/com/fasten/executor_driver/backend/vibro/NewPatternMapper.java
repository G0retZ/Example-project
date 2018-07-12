package com.fasten.executor_driver.backend.vibro;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.utils.Pair;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

/**
 * Преобразуем список элементов вибры в списки таймингов и амплитуд.
 */
public class NewPatternMapper implements Mapper<List<Pair<Long, Integer>>, Pair<long[], int[]>> {

  @Inject
  public NewPatternMapper() {
  }

  @NonNull
  @Override
  public Pair<long[], int[]> map(@NonNull List<Pair<Long, Integer>> from) {
    long[] timings = new long[from.size()];
    int[] amplitudes = new int[from.size()];
    Iterator<Pair<Long, Integer>> pairIterator = from.iterator();
    for (int i = 0; i < from.size(); i++) {
      Pair<Long, Integer> pair = pairIterator.next();
      timings[i] = pair.first;
      amplitudes[i] = pair.second;
    }
    return new Pair<>(timings, amplitudes);
  }
}
