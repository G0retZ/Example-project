package com.cargopull.executor_driver.backend.web;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.AppConfigKt;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;
import java.util.concurrent.TimeUnit;

public class PersonalQueueListener implements TopicListener, TopicStarter {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final CommonGateway<Boolean> networkConnectionGateway;
  @NonNull
  private final AppSettingsService appSettingsService;
  @Nullable
  private Flowable<StompFrame> stompFrameFlowable;
  @Nullable
  private SingleSubject<Long> resetSubject;

  public PersonalQueueListener(@NonNull StompClient stompClient,
      @NonNull CommonGateway<Boolean> networkConnectionGateway,
      @NonNull AppSettingsService appSettingsService) {
    this.stompClient = stompClient;
    this.networkConnectionGateway = networkConnectionGateway;
    this.appSettingsService = appSettingsService;
  }

  @NonNull
  @Override
  public Flowable<StompFrame> getMessages() {
    if (stompFrameFlowable == null) {
      stompFrameFlowable = networkConnectionGateway.getData()
          .distinctUntilChanged()
          .switchMap(state -> {
                if (state) {
                  String login = appSettingsService.getData("authorizationLogin");
                  return stompClient.subscribe(
                      AppConfigKt.STATUS_DESTINATION(login == null ? "" : login), 2_000, 2F
                  ).subscribeOn(Schedulers.io())
                      .doOnComplete(() -> {
                        throw new ConnectionClosedException();
                      });
                } else {
                  return Flowable.never();
                }
              }
          ).retryWhen(failed ->
              failed.concatMap(throwable -> {
                if (throwable instanceof AuthorizationException) {
                  return (resetSubject = SingleSubject.create()).toFlowable();
                } else if (throwable instanceof DeprecatedVersionException) {
                  return Flowable.<StompFrame>error(throwable);
                } else {
                  throwable.printStackTrace();
                  return Flowable.timer(1, TimeUnit.SECONDS);
                }
              })
          ).share();
    }
    return stompFrameFlowable;
  }

  @Override
  public void restart() {
    SingleSubject<Long> rs = resetSubject;
    resetSubject = null;
    if (rs != null) {
      rs.onSuccess(0L);
    }
  }
}
