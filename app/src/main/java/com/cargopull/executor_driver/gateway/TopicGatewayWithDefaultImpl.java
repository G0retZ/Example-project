package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class TopicGatewayWithDefaultImpl<D> implements CommonGateway<D> {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, D> mapper;
  @NonNull
  private final Predicate<StompMessage> filter;
  @NonNull
  private final D defaultValue;

  @Inject
  public TopicGatewayWithDefaultImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, D> mapper,
      @NonNull Predicate<StompMessage> filter,
      @NonNull D defaultValue) {
    this.topicListener = topicListener;
    this.mapper = mapper;
    this.filter = filter;
    this.defaultValue = defaultValue;
  }

  @NonNull
  @Override
  public Flowable<D> getData() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(filter)
        .map(mapper::map)
        .startWith(defaultValue);
  }
}
