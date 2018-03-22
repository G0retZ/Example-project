package com.fasten.executor_driver.application;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import com.fasten.executor_driver.R;

/**
 * Сервис для удержания приложения в активном состоянии. Позволяет процессам в памяти выполнять свои
 * задачи максимально защищенными от прибивания процесса платформой.
 */
public class PersistenceService extends Service {

  private static final String TAG = PersistenceService.class.getSimpleName();

  private static final String CHANNEL_ID = "state_channel";

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

    // Android O requires a Notification Channel.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager =
          (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      if (notificationManager != null) {
        CharSequence name = getString(R.string.app_name);
        // Create the channel for the notification
        NotificationChannel mChannel =
            new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

        // Set the Notification Channel for the Notification Manager.
        notificationManager.createNotificationChannel(mChannel);
      }
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null || !intent.hasExtra(Intent.EXTRA_TITLE)
        || !intent.hasExtra(Intent.EXTRA_TEXT)) {
      return START_NOT_STICKY;
    }
    startForeground(NOTIFICATION_ID, getNotification(
        intent.getIntExtra(Intent.EXTRA_TITLE, 0),
        intent.getIntExtra(Intent.EXTRA_TEXT, 0)
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
  private Notification getNotification(@StringRes int title, @StringRes int text) {
    // PendingIntent для запуска Activity.
    PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, OnlineActivity.class), 0);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        .addAction(R.mipmap.ic_launcher, getString(R.string.to_app), activityPendingIntent)
        .setContentText(getString(text))
        .setContentTitle(getString(title))
        .setOngoing(true)
        .setPriority(Notification.PRIORITY_HIGH)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(getString(text))
        .setWhen(System.currentTimeMillis());
    return builder.build();
  }
}