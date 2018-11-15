package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.interactor.CommonGateway;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import java.util.Map;
import javax.inject.Inject;

public class FcmGateway<D> implements CommonGateway<D> {

  @NonNull
  private final Observable<Map<String, String>> fcmListener;
  @NonNull
  private final Predicate<Map<String, String>> filter;
  @NonNull
  private final Mapper<Map<String, String>, D> mapper;

  @Inject
  public FcmGateway(@NonNull Observable<Map<String, String>> fcmListener,
      @NonNull Predicate<Map<String, String>> filter,
      @NonNull Mapper<Map<String, String>, D> mapper) {
    this.fcmListener = fcmListener;
    this.mapper = mapper;
    this.filter = filter;
  }

  @NonNull
  @Override
  public Flowable<D> getData() {
    return fcmListener
        .toFlowable(BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io())
        .filter(filter)
        .map(mapper::map);
  }
}
