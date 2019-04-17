package com.cargopull.executor_driver.utils;

import io.reactivex.Emitter;

public class EmptyEmitter<T> implements Emitter<T> {

  @Override
  public void onNext(T value) {

  }

  @Override
  public void onError(Throwable error) {

  }

  @Override
  public void onComplete() {

  }
}
