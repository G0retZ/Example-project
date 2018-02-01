package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Для обмена данными типа {@link D} в слое ЮзКейсов в течение жизни процесса.
 *
 * @param <D> - тип данных
 */
public abstract class MemoryDataSharer<D> implements DataSharer<D> {

  @NonNull
  private final Subject<D> subject;

  MemoryDataSharer() {
    subject = BehaviorSubject.create();
  }

  @Override
  public void share(@Nullable D data) {
    if (data != null) {
      subject.onNext(data);
    }
  }

  @NonNull
  @Override
  public Observable<D> get() {
    return subject;
  }
}
