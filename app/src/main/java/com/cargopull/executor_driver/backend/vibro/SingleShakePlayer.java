package com.cargopull.executor_driver.backend.vibro;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import com.cargopull.executor_driver.gateway.Mapper;
import com.cargopull.executor_driver.utils.Pair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;
import javax.inject.Inject;

public class SingleShakePlayer implements ShakeItPlayer {

  @NonNull
  private final Gson gson;
  @Nullable
  private final Vibrator vibrator;
  @NonNull
  private final Context context;
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
    gson = new Gson();
    this.context = context;
    this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    this.newPatternMapper = newPatternMapper;
    this.oldPatternMapper = oldPatternMapper;
  }

  @Override
  public void shakeIt(@RawRes int patternId) {
    InputStream is = context.getResources().openRawResource(patternId);
    Writer writer = new StringWriter();
    char[] buffer = new char[1024];
    try {
      Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      int n;
      while ((n = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, n);
      }
      String jsonString = writer.toString();
      Type type = new TypeToken<List<Pair<Long, Integer>>>() {
      }.getType();
      shakeIt(gson.fromJson(jsonString, type));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void shakeIt(@NonNull List<Pair<Long, Integer>> patternItems) {
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
