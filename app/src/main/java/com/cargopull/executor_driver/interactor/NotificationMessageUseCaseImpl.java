package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class NotificationMessageUseCaseImpl implements NotificationMessageUseCase {

  @NonNull
  private final CommonGateway<String> gateway;
  @Nullable
  private Flowable<String> messagesFlowable;

  @Inject
  public NotificationMessageUseCaseImpl(@NonNull CommonGateway<String> gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Flowable<String> getNotificationMessages() {
    if (messagesFlowable == null) {
      messagesFlowable = gateway.getData()
          .observeOn(Schedulers.single())
          .replay(1)
          .refCount();
    }
    return messagesFlowable;
  }
}
