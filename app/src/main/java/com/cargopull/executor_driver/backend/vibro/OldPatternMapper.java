package com.cargopull.executor_driver.backend.vibro;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.gateway.Mapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OldPatternMapper implements Mapper<List<VibeBeat>, VibeBeats> {

  @NonNull
  @Override
  public VibeBeats map(@NonNull List<VibeBeat> from) {
    List<Long> list = new ArrayList<>();
    Iterator<VibeBeat> patternItemIterator = from.iterator();
    VibeBeat previousVibeBeat = null;
    while (patternItemIterator.hasNext()) {
      VibeBeat vibeBeat = patternItemIterator.next();
      if (previousVibeBeat == null) {
        if (vibeBeat.volume != 0) {
          list.add(0L);
        }
        list.add(0, vibeBeat.duration);
      } else {
        if ((vibeBeat.volume == 0 && previousVibeBeat.volume == 0)
            || (vibeBeat.volume != 0 && previousVibeBeat.volume != 0)) {
          list.set(0, list.get(0) + vibeBeat.duration);
        } else {
          list.add(0, vibeBeat.duration);
        }
      }
      previousVibeBeat = vibeBeat;
    }
    VibeBeats vibeBeats = new VibeBeats(list.size());
    int size = list.size();
    for (int i = 0; i < size; i++) {
      vibeBeats.durations[size - i - 1] = list.get(i);
    }
    return vibeBeats;
  }
}
