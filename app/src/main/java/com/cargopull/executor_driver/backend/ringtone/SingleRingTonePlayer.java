package com.cargopull.executor_driver.backend.ringtone;

import android.content.ContentResolver;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import javax.inject.Inject;

/**
 * Проигрывает только 1 звук за раз.
 */
public class SingleRingTonePlayer implements RingTonePlayer {

  @NonNull
  private final Context context;
  @Nullable
  private Ringtone ringtone;
  @Nullable
  private Integer soundRes;

  @Inject
  public SingleRingTonePlayer(@NonNull Context context) {
    this.context = context;
  }

  @Override
  public void playRingTone(@NonNull @RawRes Integer soundRes) {
    this.soundRes = soundRes;
    Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
        + context.getPackageName() + "/" + soundRes);
    if (ringtone != null && ringtone.isPlaying()) {
      ringtone.stop();
    }
    ringtone = RingtoneManager.getRingtone(context, uri);
    ringtone.play();
  }

  @Override
  public void stopRingTone(@NonNull @RawRes Integer soundRes) {
    if (soundRes.equals(this.soundRes) && ringtone != null && ringtone.isPlaying()) {
      this.soundRes = null;
      new Handler(context.getMainLooper()).postDelayed(() -> ringtone.stop(), 2000);
    }
  }
}
