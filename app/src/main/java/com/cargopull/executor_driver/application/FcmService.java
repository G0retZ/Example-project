package com.cargopull.executor_driver.application;

import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementStateViewActions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import javax.inject.Inject;

public class FcmService extends FirebaseMessagingService {

  @Nullable
  private AnnouncementStateViewActions announcementStateViewActions;

  @Inject
  public void setAnnouncementStateViewActions(
      @Nullable AnnouncementStateViewActions announcementStateViewActions) {
    this.announcementStateViewActions = announcementStateViewActions;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    ((MainApplication) getApplication()).getAppComponent().inject(this);
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    if (announcementStateViewActions == null) {
      return;
    }
    if (remoteMessage != null && remoteMessage.getNotification() != null
        && remoteMessage.getNotification().getBody() != null) {
      announcementStateViewActions.showMessage(remoteMessage.getNotification().getBody());
    }
  }
}
