package com.cargopull.executor_driver.backend.vibro;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.gateway.Mapper;
import java.util.List;
import javax.inject.Inject;

/**
 * Преобразуем список битов вибры в списки таймингов и амплитуд.
 */
public class NewPatternMapper implements Mapper<List<VibeBeat>, VibeBeats> {

  @Inject
  public NewPatternMapper() {
  }

  @NonNull
  @Override
  public VibeBeats map(@NonNull List<VibeBeat> from) {
    VibeBeats vibeBeats = new VibeBeats(from.size());
    for (int i = 0; i < from.size(); i++) {
      VibeBeat vibeBeat = from.get(i);
      vibeBeats.durations[i] = vibeBeat.duration;
      vibeBeats.volumes[i] = vibeBeat.volume;
    }
    return vibeBeats;
  }
}
