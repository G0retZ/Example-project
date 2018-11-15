package com.cargopull.executor_driver.application;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.BigTextStyle;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.R;

/**
 * Сервис для удержания приложения в активном состоянии. Позволяет процессам в памяти выполнять свои
 * задачи максимально защищенными от прибивания процесса платформой.
 */
public class PersistenceService extends Service {

  private static final String TAG = PersistenceService.class.getSimpleName();
  /**
   * The identifier for the notification displayed for the foreground service.
   */
  private static final int NOTIFICATION_ID = 18372713;
  private Handler mServiceHandler;

  @Override
  public void onCreate() {
    HandlerThread handlerThread = new HandlerThread(TAG);
    handlerThread.start();
    mServiceHandler = new Handler(handlerThread.getLooper());
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null || !intent.hasExtra(Intent.EXTRA_TITLE)
        || !intent.hasExtra(Intent.EXTRA_TEXT)) {
      return START_NOT_STICKY;
    }
    startForeground(NOTIFICATION_ID, getNotification(
        intent.getIntExtra(Intent.EXTRA_TITLE, 0),
        intent.getIntExtra(Intent.EXTRA_TEXT, 0),
        intent.getParcelableExtra(Intent.EXTRA_INTENT)
    ));
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    mServiceHandler.removeCallbacksAndMessages(null);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * Returns the {@link NotificationCompat} used as part of the foreground service.
   */
  private Notification getNotification(@StringRes int title, @StringRes int text,
      @Nullable PendingIntent activityPendingIntent) {
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, BuildConfig.QUIET_CHANNEL_ID);
    if (activityPendingIntent != null) {
      builder.setContentIntent(activityPendingIntent);
    }
    builder.setContentText(getString(text))
        .setStyle(new BigTextStyle().bigText(getString(text)))
        .setContentTitle(getString(title))
        .setOngoing(true)
        .setSound(null)
        .setVibrate(new long[0])
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(getString(text))
        .setWhen(System.currentTimeMillis());
    return builder.build();
  }
}