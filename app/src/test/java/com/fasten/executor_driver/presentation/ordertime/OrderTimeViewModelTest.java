package com.fasten.executor_driver.presentation.ordertime;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.websocket.ConnectionClosedException;
import com.fasten.executor_driver.interactor.OrderFulfillmentTimeUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
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
public class OrderTimeViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderTimeViewModel orderTimeViewModel;
  @Mock
  private Observer<ViewState<OrderTimeViewActions>> viewStateObserver;
  @Mock
  private OrderFulfillmentTimeUseCase orderCurrentTimeUseCase;
  private PublishSubject<Long> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderCurrentTimeUseCase.getOrderElapsedTime())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderTimeViewModel = new OrderTimeViewModelImpl(orderCurrentTimeUseCase);
  }


  /* Тетсируем работу с юзкейсом текущего времени выполнения заказа. */

  /**
   * Должен просить юзкейс получить актуальную цену, при создании.
   */
  @Test
  public void askSelectedVehicleUseCaseForVehiclesInitially() {
    // Результат:
    verify(orderCurrentTimeUseCase, only()).getOrderElapsedTime();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void DoNotTouchSelectedVehicleUseCaseDuringVehicleChoosing() {
    // Действие:
    orderTimeViewModel.getViewStateLiveData();
    orderTimeViewModel.getNavigationLiveData();
    orderTimeViewModel.getViewStateLiveData();
    orderTimeViewModel.getNavigationLiveData();

    // Результат:
    verify(orderCurrentTimeUseCase, only()).getOrderElapsedTime();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида с 0 изначально.
   */
  @Test
  public void setViewStateWithZeroToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    orderTimeViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(0));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с временем заказа.
   */
  @Test
  public void setViewStateWithTimesToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderTimeViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(0));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(123));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(873));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(4728));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(32));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида ошибки.
   */
  @Test
  public void setErrorViewStateToLiveDataOnError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderTimeViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);
    publishSubject.onError(new ConnectionClosedException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(0));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(123));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(873));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(4728));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateIdle(32));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewStateError(0));
    verifyNoMoreInteractions(viewStateObserver);
  }
}