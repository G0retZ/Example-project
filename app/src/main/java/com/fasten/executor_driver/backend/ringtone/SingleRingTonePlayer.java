package com.fasten.executor_driver.backend.ringtone;

import android.content.ContentResolver;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import javax.inject.Inject;

/**
 * Проигрывает только 1 звук за раз.
 */
public class SingleRingTonePlayer implements RingTonePlayer {

  @NonNull
  private final Context context;
  @Nullable
  private Ringtone ringtone;

  @Inject
  public SingleRingTonePlayer(@NonNull Context context) {
    this.context = context;
  }

  @Override
  public void playRingTone(@RawRes int soundRes) {
    Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
        + context.getPackageName() + "/" + soundRes);
    if (ringtone != null && ringtone.isPlaying()) {
      ringtone.stop();
    }
    ringtone = RingtoneManager.getRingtone(context, uri);
    ringtone.play();
  }
}
