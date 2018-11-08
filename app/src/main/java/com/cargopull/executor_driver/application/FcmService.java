package com.cargopull.executor_driver.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import java.util.Map;
import javax.inject.Inject;

public class FcmService extends FirebaseMessagingService {

  private Observer<Map<String, String>> fcmObserver;
  @Nullable
  private ApiService apiService;
  @NonNull
  private Disposable disposable = Disposables.empty();

  @Inject
  public void setFcmObserver(@NonNull Observer<Map<String, String>> fcmObserver) {
    this.fcmObserver = fcmObserver;
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
    if (remoteMessage != null) {
      Map<String, String> dataMap = remoteMessage.getData();
      Notification notification = remoteMessage.getNotification();
      if (notification != null) {
        String title = notification.getTitle();
        if (title != null) {
          dataMap.put("title", title);
        }
        String body = notification.getBody();
        if (body != null) {
          dataMap.put("body", body);
        }
      }
      fcmObserver.onNext(dataMap);
    }
  }
}
