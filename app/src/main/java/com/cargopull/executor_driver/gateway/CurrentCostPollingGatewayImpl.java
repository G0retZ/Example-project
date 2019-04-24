package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.web.TopicListener;
import com.cargopull.executor_driver.interactor.CurrentCostPollingGateway;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class CurrentCostPollingGatewayImpl implements CurrentCostPollingGateway {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final Mapper<StompMessage, Pair<Long, Long>> mapper;

  @Inject
  public CurrentCostPollingGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull StompClient stompClient,
      @NonNull Mapper<StompMessage, Pair<Long, Long>> mapper) {
    this.topicListener = topicListener;
    this.stompClient = stompClient;
    this.mapper = mapper;
  }

  @NonNull
  @Override
  public Completable startPolling() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(stompMessage -> stompMessage.findHeader("OverPackage") != null)
        .switchMap(this::pollingChooser)
        .flatMapCompletable(
            b -> stompClient.send(BuildConfig.POLLING_DESTINATION, "\"\"").onErrorComplete()
        );
  }

  private Flowable<Long> pollingChooser(@NonNull StompMessage stompMessage) {
    if (stompMessage.findHeader("OverPackage").equals("1")) {
      return Flowable.just(stompMessage)
          .map(mapper::map)
          .switchMap(pair -> Flowable.interval(pair.first, pair.second, TimeUnit.MILLISECONDS));
    } else {
      return Flowable.empty();
    }
  }
}
