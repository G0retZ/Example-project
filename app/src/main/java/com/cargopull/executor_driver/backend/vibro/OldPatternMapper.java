package com.cargopull.executor_driver.backend.vibro;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.utils.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OldPatternMapper implements Mapper<List<Pair<Long, Integer>>, long[]> {

  @NonNull
  @Override
  public long[] map(@NonNull List<Pair<Long, Integer>> from) {
    List<Long> list = new ArrayList<>();
    Iterator<Pair<Long, Integer>> patternItemIterator = from.iterator();
    Pair<Long, Integer> previousPair = null;
    while (patternItemIterator.hasNext()) {
      Pair<Long, Integer> pair = patternItemIterator.next();
      if (previousPair == null) {
        if (pair.second != 0) {
          list.add(0L);
        }
        list.add(0, pair.first);
      } else {
        if ((pair.second == 0 && previousPair.second == 0)
            || (pair.second != 0 && previousPair.second != 0)) {
          list.set(0, list.get(0) + pair.first);
        } else {
          list.add(0, pair.first);
        }
      }
      previousPair = pair;
    }
    long[] range = new long[list.size()];
    Iterator<Long> iterator = list.iterator();
    int size = list.size();
    for (int i = 0; i < size; i++) {
      range[size - i - 1] = iterator.next();
    }
    return range;
  }
}
