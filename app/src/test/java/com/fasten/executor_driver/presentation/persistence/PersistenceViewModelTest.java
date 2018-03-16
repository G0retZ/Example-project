package com.fasten.executor_driver.presentation.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.DataReceiver;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private PersistenceViewModel persistenceViewModel;
  @Mock
  private DataReceiver<ExecutorState> executorStateReceiver;

  @Mock
  private Observer<ViewState<PersistenceViewActions>> viewStateObserver;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateReceiver.get()).thenReturn(PublishSubject.never());
    persistenceViewModel = new PersistenceViewModelImpl(executorStateReceiver);
  }

  /* Тетсируем работу с публикатором номера телефона. */

  /**
   * Должен просить юзкейс получить список ТС, при первой и только при первой подписке.
   *
   * @throws Exception error
   */
  @Test
  public void askSelectedDataSharerForLoginInitially() throws Exception {
    // Действие:
    persistenceViewModel.getViewStateLiveData();
    persistenceViewModel.getViewStateLiveData();
    persistenceViewModel.getViewStateLiveData();

    // Результат:
    verify(executorStateReceiver, only()).get();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Не должен давать никакого состояния вида изначально.
   *
   * @throws Exception error
   */
  @Test
  public void setNoViewStateToLiveData() throws Exception {
    // Действие:
    persistenceViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние запущенного сервиса с текстами.
   *
   * @throws Exception error
   */
  @Test
  public void setStartedViewStateToLiveData() throws Exception {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateReceiver.get()).thenReturn(publishSubject);
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    persistenceViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.OPENED_SHIFT);
    publishSubject.onNext(ExecutorState.READY_FOR_ORDERS);
    publishSubject.onNext(ExecutorState.APPROACHING_LOADING_POINT);
    publishSubject.onNext(ExecutorState.LOADING);
    publishSubject.onNext(ExecutorState.APPROACHING_UNLOADING_POINT);
    publishSubject.onNext(ExecutorState.UNLOADING);
    publishSubject.onNext(ExecutorState.READY_FOR_ORDERS);

    // Результат:
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.online, R.string.no_orders));
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.online, R.string.wait_for_orders));
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.executing, R.string.to_loading_point));
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.executing, R.string.loading));
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.executing, R.string.to_unloading_point));
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.executing, R.string.unloading));
    inOrder.verify(viewStateObserver)
        .onChanged(new PersistenceViewStateStart(R.string.online, R.string.wait_for_orders));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние не запущенного сервиса.
   *
   * @throws Exception error
   */
  @Test
  public void setStoppedViewStateToLiveData() throws Exception {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateReceiver.get()).thenReturn(publishSubject);
    persistenceViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.CLOSED_SHIFT);
    publishSubject.onNext(ExecutorState.UNAUTHORIZED);

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(any(PersistenceViewStateStop.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен игнорировать ошибки.
   *
   * @throws Exception error
   */
  @SuppressWarnings("unchecked")
  @Test
  public void ignoreErrors() throws Exception {
    // Дано:
    PublishSubject<ExecutorState> publishSubject = PublishSubject.create();
    when(executorStateReceiver.get()).thenReturn(publishSubject, PublishSubject.never());
    persistenceViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new IllegalArgumentException());

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }
}