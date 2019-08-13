package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.AppConfigKt;
import com.cargopull.executor_driver.backend.stomp.StompClient;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.TopicListener;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class CurrentCostPollingGatewayImpl implements CurrentCostPollingGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<StompFrame, Pair<Long, Long>> mapper;

  @Inject
  public CurrentCostPollingGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull StompClient stompClient,
      @NonNull Mapper<StompFrame, Pair<Long, Long>> mapper) {
    this.topicListener = topicListener;
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Completable startPolling() {
    return topicListener.getMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompFrame -> stompFrame.getHeaders().get("OverPackage") != null)
        .switchMap(this::pollingChooser)
        .flatMapCompletable(
            b -> stompClient.send(AppConfigKt.POLLING_DESTINATION, "\"\"").onErrorComplete()
        );
  }

  private Flowable<Long> pollingChooser(@NonNull StompFrame stompFrame) {
    if (stompFrame.getHeaders().get("OverPackage").equals("1")) {
      return Flowable.just(stompFrame)
          .map(mapper::map)
          .switchMap(pair -> Flowable.interval(pair.first, pair.second, TimeUnit.MILLISECONDS));
    } else {
      return Flowable.empty();
    }
  }
}
