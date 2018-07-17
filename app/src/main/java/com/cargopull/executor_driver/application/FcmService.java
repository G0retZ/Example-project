package com.cargopull.executor_driver.application;

import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewActions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import javax.inject.Inject;

public class FcmService extends FirebaseMessagingService {

  @Nullable
  private ExecutorStateViewActions stateViewActions;

  @Inject
  public void setStateViewActions(@Nullable ExecutorStateViewActions stateViewActions) {
    this.stateViewActions = stateViewActions;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    ((MainApplication) getApplication()).getAppComponent().inject(this);
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    if (stateViewActions == null) {
      return;
    }
    if (remoteMessage != null && remoteMessage.getNotification() != null
        && remoteMessage.getNotification().getBody() != null) {
      stateViewActions.showOnlineMessage(remoteMessage.getNotification().getBody());
    }
  }
}
