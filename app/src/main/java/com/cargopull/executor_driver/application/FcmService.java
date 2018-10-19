package com.cargopull.executor_driver.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class FcmService extends FirebaseMessagingService {

  @Nullable
  private AnnouncementViewModel announcementViewModel;
  @Nullable
  private ApiService apiService;
  @NonNull
  private Disposable disposable = Disposables.empty();

  @Inject
  public void setAnnouncementViewModel(@NonNull AnnouncementViewModel announcementViewModel) {
    this.announcementViewModel = announcementViewModel;
  }

  @Inject
  public void setApiService(@NonNull ApiService apiService) {
    this.apiService = apiService;
  }

  @Override
  public void onNewToken(String s) {
    super.onNewToken(s);
    if (apiService == null) {
      return;
    }
    disposable = apiService.sendFcmInstanceID(s)
        .subscribeOn(Schedulers.io())
        .subscribe(() -> {
        }, Throwable::printStackTrace);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    ((MainApplication) getApplication()).getAppComponent().inject(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    disposable.dispose();
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
