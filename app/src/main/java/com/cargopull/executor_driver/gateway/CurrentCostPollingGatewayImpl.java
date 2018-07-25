package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class CurrentCostPollingGatewayImpl implements CurrentCostPollingGateway {

  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<String, Pair<Long, Long>> mapper;

  @Inject
  public CurrentCostPollingGatewayImpl(@NonNull StompClient stompClient,
      @NonNull Mapper<String, Pair<Long, Long>> mapper) {
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Completable startPolling(@NonNull String channelId) {
    if (stompClient.isConnected() || stompClient.isConnecting()) {
      return stompClient.topic(
          String.format(BuildConfig.STATUS_DESTINATION, channelId),
          StompClient.ACK_CLIENT_INDIVIDUAL
      ).subscribeOn(Schedulers.io())
          .observeOn(Schedulers.computation())
          .filter(stompMessage -> stompMessage.findHeader("OverPackage") != null)
          .takeWhile(stompMessage -> stompMessage.findHeader("OverPackage").equals("1"))
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
          .map(stompMessage -> mapper.map(stompMessage.getPayload()))
          .switchMap(pair -> Flowable.interval(pair.first, pair.second, TimeUnit.MILLISECONDS))
          .flatMapCompletable(
              b -> stompClient.send(BuildConfig.POLLING_DESTINATION, "\"\"")
          ).observeOn(Schedulers.single());
    }
    return Completable.error(ConnectionClosedException::new);
  }
}
