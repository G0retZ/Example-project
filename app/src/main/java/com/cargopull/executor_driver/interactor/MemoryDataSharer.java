package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import javax.inject.Inject;

/**
 * Для обмена данными типа {@link D} в слое ЮзКейсов в течение жизни процесса.
 *
 * @param <D> - тип данных
 */
public abstract class MemoryDataSharer<D> implements DataReceiver<D>, Observer<D> {

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
  public void onSubscribe(Disposable d) {
    subject.onSubscribe(d);
  }

  @Override
  public void onNext(@NonNull D d) {
    subject.onNext(d);
  }

  @Override
  public void onError(@NonNull Throwable e) {
    Subject<D> tmp = subject;
    subject = BehaviorSubject.create();
    tmp.onError(e);
  }

  @Override
  public void onComplete() {
    Subject<D> tmp = subject;
    subject = BehaviorSubject.create();
    tmp.onComplete();
  }
}
