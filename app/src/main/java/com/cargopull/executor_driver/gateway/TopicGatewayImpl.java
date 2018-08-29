package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class TopicGatewayImpl<D> implements CommonGateway<D> {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Mapper<StompMessage, D> mapper;
  @NonNull
  private final Predicate<StompMessage> filter;

  @Inject
  public TopicGatewayImpl(@NonNull TopicListener topicListener,
      @NonNull Mapper<StompMessage, D> mapper,
      @NonNull Predicate<StompMessage> filter) {
    this.topicListener = topicListener;
    this.mapper = mapper;
    this.filter = filter;
  }

  @NonNull
  @Override
  public Flowable<D> getData() {
    return topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(filter)
        .map(mapper::map);
  }
}
