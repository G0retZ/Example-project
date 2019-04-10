package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import javax.inject.Inject;

/**
 * Для обмена данными типа {@link D} в слое ЮзКейсов в течение жизни процесса.
 *
 * @param <D> - тип данных
 */
public abstract class MemoryDataSharer<D> implements DataReceiver<D>, DataUpdateAndResetUseCase<D> {

  @NonNull
  private BehaviorSubject<D> subject;

  @Inject
  protected MemoryDataSharer() {
    this.subject = BehaviorSubject.create();
  }

  @NonNull
  @Override
  public Observable<D> get() {
    return subject;
  }

  @Override
  public void updateWith(@NonNull D data) {
    subject.onNext(data);
  }

  @Override
  public void reset() {
    Subject<D> tmp = subject;
    subject = BehaviorSubject.create();
    tmp.onComplete();
  }
}
