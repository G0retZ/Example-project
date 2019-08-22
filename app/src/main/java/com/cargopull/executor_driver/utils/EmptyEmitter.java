package com.cargopull.executor_driver.utils;

import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;

public class EmptyEmitter<T> implements FlowableEmitter<T> {

  @Override
  public void onNext(T value) {

  }

  @Override
  public void onError(Throwable error) {

  }

  @Override
  public void onComplete() {

  }

  @Override
  public void setDisposable(Disposable d) {

  }

  @Override
  public void setCancellable(Cancellable c) {

  }

  @Override
  public long requested() {
    return 0;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public FlowableEmitter<T> serialize() {
    return null;
  }

  @Override
  public boolean tryOnError(Throwable t) {
    return false;
  }
}
