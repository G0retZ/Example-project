package com.cargopull.executor_driver.backend.websocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

class PersonalQueueListener implements WebSocketListener {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final DataReceiver<String> loginReceiver;
  @Nullable
  private Flowable<StompMessage> stompMessageFlowable;

  PersonalQueueListener(@NonNull StompClient stompClient,
      @NonNull DataReceiver<String> loginReceiver) {
    this.stompClient = stompClient;
    this.loginReceiver = loginReceiver;
  }

  @NonNull
  @Override
  public Flowable<StompMessage> getAcknowledgedMessages() {
    if (stompMessageFlowable == null) {
      stompMessageFlowable = loginReceiver.get()
          .toFlowable(BackpressureStrategy.BUFFER)
          .switchMap(
              login -> stompClient.topic(
                  String.format(BuildConfig.STATUS_DESTINATION, login),
                  StompClient.ACK_CLIENT_INDIVIDUAL
              ).subscribeOn(Schedulers.io())
          ).retry()
          .switchMap(
              stompMessage -> stompClient.sendAfterConnection(
                  new StompMessage("ACK",
                      Arrays.asList(
                          new StompHeader("subscription", stompMessage.findHeader("subscription")),
                          new StompHeader("message-id", stompMessage.findHeader("message-id"))
                      ),
                      ""
                  )
              ).onErrorComplete()
                  .toSingleDefault(stompMessage)
                  .toFlowable()
          ).share();
    }
    return stompMessageFlowable;
  }
}
