package com.cargopull.executor_driver.application;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.BigTextStyle;
import com.cargopull.executor_driver.R;

/**
 * Сервис для удержания приложения в активном состоянии. Позволяет процессам в памяти выполнять свои
 * задачи максимально защищенными от прибивания процесса платформой.
 */
public class PersistenceService extends Service {

  private static final String TAG = PersistenceService.class.getSimpleName();

  private static final String CHANNEL_ID = "state_channel";
  private static final String QUIET_CHANNEL_ID = "state_channel_quiet";
  /**
   * The identifier for the notification displayed for the foreground service.
   */
  private static final int NOTIFICATION_ID = 18372713;
  private boolean onscreen;
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
        // Create the channel for the notification
        NotificationChannel mChannel =
            new NotificationChannel(CHANNEL_ID, getString(R.string.server_connection),
                NotificationManager.IMPORTANCE_HIGH);
        mChannel.setSound(null, null);
        mChannel.enableVibration(false);

        // Set the Notification Channel for the Notification Manager.
        notificationManager.createNotificationChannel(mChannel);

        mChannel = new NotificationChannel(QUIET_CHANNEL_ID, getString(R.string.server_connection),
            NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.setSound(null, null);
        mChannel.enableVibration(false);

        // Set the Notification Channel for the Notification Manager.
        notificationManager.createNotificationChannel(mChannel);
      }
    }
    getApplication().registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

      }

      @Override
      public void onActivityStarted(Activity activity) {

      }

      @Override
      public void onActivityResumed(Activity activity) {
        onscreen = true;
      }

      @Override
      public void onActivityPaused(Activity activity) {
        onscreen = false;
      }

      @Override
      public void onActivityStopped(Activity activity) {

      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

      }

      @Override
      public void onActivityDestroyed(Activity activity) {

      }
    });
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
        new NotificationCompat.Builder(this, onscreen ? QUIET_CHANNEL_ID : CHANNEL_ID);
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