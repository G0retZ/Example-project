package com.cargopull.executor_driver.backend.stomp;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StompClient {

  private final String socketUrl;
  private final WebSocketConnection webSocketConnection;
  private volatile FlowableEmitter<Boolean> connectionState;
  private volatile boolean lastState;

  public StompClient(String socketUrl, WebSocketConnection webSocketConnection) {
    this.socketUrl = socketUrl;
    this.webSocketConnection = webSocketConnection;
  }

  public Flowable<Boolean> getConnectionState() {
    return Flowable.<Boolean>create(
        emitter -> connectionState = emitter,
        BackpressureStrategy.BUFFER
    ).startWith(lastState);
  }

  public Flowable<StompFrame> listenToDestination(String destination, int ms, float fraction) {
    final String topicId = UUID.randomUUID().toString();
    return webSocketConnection.connect(socketUrl)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .switchMap(message -> {
              if ("CONN".equals(message)) {
                StompFrame connect = new StompFrame(Command.CONNECT, "");
                connect.addHeader("version", "1.0");
                connect.addHeader("heart-beat", "0," + ms);
                return webSocketConnection.sendMessage(connect.toString()).toFlowable();
              } else {
                return Flowable.just(message);
              }
            }
        ).map(StompFrame::fromString)
        .switchMap(stompFrame -> {
          if (stompFrame.getCommand() == Command.CONNECTED) {
            connectionState.onNext(lastState = true);
            StompFrame subscribe = new StompFrame(Command.SUBSCRIBE, "");
            subscribe.addHeader("id", topicId);
            subscribe.addHeader("destination", destination);
            subscribe.addHeader("ack", "client-individual");
            return webSocketConnection.sendMessage(subscribe.toString())
                .onErrorComplete()
                .toSingleDefault(stompFrame)
                .toFlowable().concatWith(Flowable.never())
                .timeout((long) (ms * fraction), TimeUnit.MILLISECONDS);
          } else if (stompFrame.getCommand() == Command.ERROR) {
            return Flowable.error(new Exception(
                stompFrame.getBody().isEmpty() ?
                    stompFrame.getHeaders().get("message") :
                    stompFrame.getBody()
            ));
          } else {
            return Flowable.just(stompFrame).concatWith(Flowable.never())
                .timeout((long) (ms * fraction), TimeUnit.MILLISECONDS);
          }
        }).doOnCancel(() -> {
          StompFrame unsubscribe = new StompFrame(Command.UNSUBSCRIBE, "");
          unsubscribe.addHeader("id", topicId);
          StompFrame disconnect = new StompFrame(Command.DISCONNECT, "");
          lastState = false;
          webSocketConnection.sendMessage(unsubscribe.toString())
              .andThen(webSocketConnection.sendMessage(disconnect.toString()))
              .subscribe(() -> {
              }, Throwable::printStackTrace)
              .isDisposed();
        }).filter(stompFrame -> stompFrame.getCommand() == Command.MESSAGE)
        .filter(stompFrame ->
            destination.equals(stompFrame.getHeaders().get("destination"))
                && topicId.equals(stompFrame.getHeaders().get("subscription"))
        ).switchMap(
            stompFrame -> {
              StompFrame ack = new StompFrame(Command.ACK, "");
              ack.addHeader("subscription", stompFrame.getHeaders().get("subscription"));
              ack.addHeader("message-id", stompFrame.getHeaders().get("message-id"));
              return webSocketConnection.sendMessage(ack.toString())
                  .onErrorComplete()
                  .toSingleDefault(stompFrame)
                  .toFlowable();
            }
        ).doOnError(throwable -> {
          lastState = false;
          connectionState.tryOnError(throwable);
        });
  }

  public Completable send(String destination, String data) {
    return webSocketConnection.sendMessage(
        new StompFrame(
            Command.SEND,
            Collections.singletonMap("destination", destination),
            data
        ).toString()
    );
  }
}