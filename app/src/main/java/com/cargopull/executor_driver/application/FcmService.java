package com.cargopull.executor_driver.application;

import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import javax.inject.Inject;

public class FcmService extends FirebaseMessagingService {

  @Nullable
  private AnnouncementViewModel announcementViewModel;

  @Inject
  public void setAnnouncementViewModel(@Nullable AnnouncementViewModel announcementViewModel) {
    this.announcementViewModel = announcementViewModel;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    ((MainApplication) getApplication()).getAppComponent().inject(this);
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    if (announcementViewModel == null) {
      return;
    }
    if (remoteMessage != null && remoteMessage.getNotification() != null
        && remoteMessage.getNotification().getBody() != null) {
      announcementViewModel.postMessage(remoteMessage.getNotification().getBody());
    }
  }
}
