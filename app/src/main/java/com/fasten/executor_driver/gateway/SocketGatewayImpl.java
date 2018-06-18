package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.SocketGateway;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import org.reactivestreams.Publisher;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.client.StompClient;

public class SocketGatewayImpl implements SocketGateway {

  @NonNull
  private final StompClient stompClient;

  @Inject
  public SocketGatewayImpl(@NonNull StompClient stompClient) {
    this.stompClient = stompClient;
  }

  @Override
  public Flowable<Boolean> openSocket() {
    return stompClient.lifecycle()
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io())
        .takeWhile(lifecycleEvent -> lifecycleEvent.getType() != LifecycleEvent.Type.CLOSED)
        .switchMap((Function<LifecycleEvent, Publisher<Boolean>>) lifecycleEvent -> {
          switch (lifecycleEvent.getType()) {
            case OPENED:
              return Flowable.just(true);
            case ERROR:
              return Flowable.error(lifecycleEvent.getException());
            default:
              return Flowable.error(new Exception());
          }
        })
        .startWith(Flowable.create(emitter -> {
          if (stompClient.isConnected()) {
            emitter.onNext(true);
          } else if (!stompClient.isConnecting()) {
            stompClient.connect();
          }
          emitter.onComplete();
        }, BackpressureStrategy.BUFFER))
        .doOnCancel(stompClient::disconnect)
        .observeOn(Schedulers.single());
  }
}
