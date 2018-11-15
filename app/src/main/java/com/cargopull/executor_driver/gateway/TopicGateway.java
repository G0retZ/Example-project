package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

public class TopicGateway<D> implements CommonGateway<D> {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Predicate<StompMessage> filter;
  @NonNull
  private final Mapper<StompMessage, D> mapper;
  @Nullable
  private final D defaultValue;

  @Inject
  public TopicGateway(@NonNull TopicListener topicListener,
      @NonNull Predicate<StompMessage> filter,
      @NonNull Mapper<StompMessage, D> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
    this.filter = filter;
    defaultValue = null;
  }

  @Inject
  public TopicGateway(@NonNull TopicListener topicListener,
      @NonNull Predicate<StompMessage> filter,
      @NonNull Mapper<StompMessage, D> mapper,
      @NonNull D defaultValue) {
    this.topicListener = topicListener;
    this.mapper = mapper;
    this.filter = filter;
    this.defaultValue = defaultValue;
  }

  @NonNull
  @Override
  public Flowable<D> getData() {
    Flowable<D> flowable = topicListener.getAcknowledgedMessages()
        .subscribeOn(Schedulers.io())
        .filter(filter)
        .map(mapper::map);
    return defaultValue == null ? flowable : flowable.startWith(defaultValue);
  }
}
