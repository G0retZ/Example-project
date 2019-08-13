package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.TopicListener;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class TopicGateway<D> implements CommonGateway<D> {

  @NonNull
  private final TopicListener topicListener;
  @NonNull
  private final Predicate<StompFrame> filter;
  @NonNull
  private final Mapper<StompFrame, D> mapper;
  @Nullable
  private final D defaultValue;

  @Inject
  public TopicGateway(@NonNull TopicListener topicListener,
      @NonNull Predicate<StompFrame> filter,
      @NonNull Mapper<StompFrame, D> mapper) {
    this.topicListener = topicListener;
    this.mapper = mapper;
    this.filter = filter;
    defaultValue = null;
  }

  @Inject
  public TopicGateway(@NonNull TopicListener topicListener,
      @NonNull Predicate<StompFrame> filter,
      @NonNull Mapper<StompFrame, D> mapper,
      @NonNull D defaultValue) {
    this.topicListener = topicListener;
    this.mapper = mapper;
    this.filter = filter;
    this.defaultValue = defaultValue;
  }

  @NonNull
  @Override
  public Flowable<D> getData() {
    Flowable<D> flowable = topicListener.getMessages()
        .subscribeOn(Schedulers.io())
        .filter(filter)
        .map(mapper::map);
    return defaultValue == null ? flowable : flowable.startWith(defaultValue);
  }
}
