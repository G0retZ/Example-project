package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.interactor.UpdateMessageGateway;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import javax.inject.Inject;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class UpdateMessageGatewayImpl implements UpdateMessageGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public UpdateMessageGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @NonNull
  @Override
  public Flowable<String> loadUpdateMessages(@NonNull String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(
          String.format(BuildConfig.STATUS_DESTINATION, channelId),
          StompClient.ACK_CLIENT_INDIVIDUAL
      ).subscribeOn(Schedulers.io())
          .onErrorResumeNext(Flowable.empty())
          .filter(stompMessage -> "UpdateVersion".equals(stompMessage.findHeader("message")))
          .doOnNext(
              stompMessage -> stompClient.sendAfterConnection(
                  new StompMessage("ACK",
                      Arrays.asList(
                          new StompHeader("subscription", stompMessage.findHeader("subscription")),
                          new StompHeader("message-id", stompMessage.findHeader("message-id"))
                      ),
                      ""
                  )
              ).subscribe(() -> {
              }, Throwable::printStackTrace)
          )
          .map(stompMessage -> stompMessage.getPayload().replace("\"", "").trim())
          .observeOn(Schedulers.single());
    }
    return Flowable.error(ConnectionClosedException::new);
  }
}