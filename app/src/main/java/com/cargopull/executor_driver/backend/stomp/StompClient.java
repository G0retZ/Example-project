package com.cargopull.executor_driver.backend.stomp;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface StompClient {

  Flowable<Boolean> getConnectionState();

  Flowable<StompFrame> listenToDestination(String destination, int ms, float fraction);

  Completable send(String destination, String data);
}
