package com.fasten.executor_driver.application;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.ExecutorState;

/**
 * Сервис для удержания приложения в активном состоянии. Позволяет процессам в памяти выполнять свои
 * задачи максимально защищенными от прибивания процесса платформой.
 */
public class PersistenceService extends Service {

  private static final String PACKAGE_NAME =
      "com.fasten.executor_driver.application.persistenceService";
  private static final String TAG = PersistenceService.class.getSimpleName();

  private static final String CHANNEL_ID = "state_channel";
  private static final String EXTRA_EXECUTOR_STATE = PACKAGE_NAME +
      ".ExecutorState";

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
    if (intent == null || !intent.hasExtra(EXTRA_EXECUTOR_STATE)) {
      return START_NOT_STICKY;
    }
    ExecutorState executorState;
    try {
      executorState = ExecutorState
          .valueOf(intent.getStringExtra(EXTRA_EXECUTOR_STATE));
    } catch (IllegalArgumentException ie) {
      return START_NOT_STICKY;
    }
    switch (executorState) {
      case APPROACHING_LOADING_POINT:
        startForeground(NOTIFICATION_ID,
            getNotification(R.string.executing, R.string.to_loading_point));
        break;
      case APPROACHING_UNLOADING_POINT:
        startForeground(NOTIFICATION_ID,
            getNotification(R.string.executing, R.string.to_unloading_point));
        break;
      case UNAUTHORIZED:
        stopSelf();
        return START_NOT_STICKY;
      case CLOSED_SHIFT:
        stopSelf();
        return START_NOT_STICKY;
      case OPENED_SHIFT:
        startForeground(NOTIFICATION_ID,
            getNotification(R.string.online, R.string.no_orders));
        break;
      case READY_FOR_ORDERS:
        startForeground(NOTIFICATION_ID,
            getNotification(R.string.online, R.string.wait_for_orders));
        break;
      case LOADING:
        startForeground(NOTIFICATION_ID,
            getNotification(R.string.executing, R.string.loading));
        break;
      case UNLOADING:
        startForeground(NOTIFICATION_ID,
            getNotification(R.string.executing, R.string.unloading));
        break;
    }
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
        new Intent(this, MapActivity.class), 0);

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