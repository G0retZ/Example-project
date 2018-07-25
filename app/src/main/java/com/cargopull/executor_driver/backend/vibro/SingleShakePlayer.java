package com.cargopull.executor_driver.backend.vibro;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

public class SingleShakePlayer implements ShakeItPlayer {

  @Nullable
  private final Vibrator vibrator;
  @NonNull
  private final Mapper<List<Pair<Long, Integer>>, Pair<long[], int[]>> newPatternMapper;
  @NonNull
  private final Mapper<List<Pair<Long, Integer>>, long[]> oldPatternMapper;
  @NonNull
  private Disposable disposable = Disposables.empty();

  @Inject
  public SingleShakePlayer(@NonNull Context context,
      @NonNull Mapper<List<Pair<Long, Integer>>, Pair<long[], int[]>> newPatternMapper,
      @NonNull Mapper<List<Pair<Long, Integer>>, long[]> oldPatternMapper) {
    this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    this.newPatternMapper = newPatternMapper;
    this.oldPatternMapper = oldPatternMapper;
  }

  @Override
  public void shakeIt(@NonNull List<Pair<Long, Integer>> patternItems) {
    disposable.dispose();
    if (vibrator == null || !vibrator.hasVibrator()) {
      return;
    }
    vibrator.cancel();
    if (VERSION.SDK_INT >= 26) {
      disposable = Single.just(patternItems)
          .subscribeOn(Schedulers.io())
          .map(newPatternMapper::map)
          .map(pair -> {
            if (!vibrator.hasAmplitudeControl()) {
              for (int i = 0; i < pair.second.length; i++) {
                if (pair.second[i] != 0) {
                  pair.second[i] = -1;
                }
              }
            }
            return pair;
          })
          .observeOn(AndroidSchedulers.mainThread())
          .flatMapCompletable(
              pair -> Completable.fromAction(
                  () -> vibrator.vibrate(
                      VibrationEffect.createWaveform(pair.first, pair.second, -1)
                  )
              )
          ).subscribe(
              () -> {
              }, Throwable::printStackTrace
          );
    } else {
      disposable = Single.just(patternItems)
          .subscribeOn(Schedulers.io())
          .map(oldPatternMapper::map)
          .observeOn(AndroidSchedulers.mainThread())
          .flatMapCompletable(
              longs -> Completable.fromAction(() -> vibrator.vibrate(longs, -1))
          ).subscribe(
              () -> {
              }, Throwable::printStackTrace
          );
    }
  }
}
