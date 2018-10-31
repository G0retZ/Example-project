package com.cargopull.executor_driver.backend.vibro;

import android.content.Context;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import com.cargopull.executor_driver.gateway.Mapper;
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

public class OldSingleShakePlayer implements ShakeItPlayer {

  @NonNull
  private final Gson gson;
  @Nullable
  private final Vibrator vibrator;
  @NonNull
  private final Context context;
  @NonNull
  private final Mapper<List<VibeBeat>, VibeBeats> patternMapper;
  @NonNull
  private Disposable disposable = Disposables.empty();

  @Inject
  public OldSingleShakePlayer(@NonNull Context context,
      @NonNull Mapper<List<VibeBeat>, VibeBeats> patternMapper) {
    gson = new Gson();
    this.context = context;
    this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    this.patternMapper = patternMapper;
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
      Type type = new TypeToken<List<VibeBeat>>() {
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

  private void shakeIt(@NonNull List<VibeBeat> patternItems) {
    disposable.dispose();
    if (vibrator == null || !vibrator.hasVibrator()) {
      return;
    }
    vibrator.cancel();
    disposable = Single.just(patternItems)
        .subscribeOn(Schedulers.io())
        .map(patternMapper::map)
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable(
            vibeBeats -> Completable.fromAction(() -> vibrator.vibrate(vibeBeats.durations, -1))
        ).subscribe(
            () -> {
            }, Throwable::printStackTrace
        );
  }
}
