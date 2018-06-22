package com.fasten.executor_driver.presentation.ordercost;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.interactor.OrderCurrentCostUseCase;
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
public class OrderCostViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderCostViewModel orderCostViewModel;
  @Mock
  private OrderCurrentCostUseCase orderCurrentCostUseCase;
  @Mock
  private Observer<ViewState<OrderCostViewActions>> viewStateObserver;
  @Mock
  private PublishSubject<Integer> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderCurrentCostUseCase.getOrderCurrentCost())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderCostViewModel = new OrderCostViewModelImpl(orderCurrentCostUseCase);
  }


  /* Тетсируем работу с юзкейсом текущей цены выполнения заказа. */

  /**
   * Должен просить юзкейс получить актуальную цену, при создании.
   */
  @Test
  public void askSelectedVehicleUseCaseForVehiclesInitially() {
    // Результат:
    verify(orderCurrentCostUseCase, only()).getOrderCurrentCost();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void DoNotTouchSelectedVehicleUseCaseDuringVehicleChoosing() {
    // Действие:
    orderCostViewModel.getViewStateLiveData();
    orderCostViewModel.getNavigationLiveData();
    orderCostViewModel.getViewStateLiveData();
    orderCostViewModel.getNavigationLiveData();

    // Результат:
    verify(orderCurrentCostUseCase, only()).getOrderCurrentCost();
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
    orderCostViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(0));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с суммой.
   */
  @Test
  public void setViewStateWithCostsToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderCostViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(123);
    publishSubject.onNext(873);
    publishSubject.onNext(4728);
    publishSubject.onNext(32);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(0));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(123));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(873));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(4728));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(32));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида ошибки данных сервера.
   */
  @Test
  public void setErrorViewStateToLiveDataOnError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderCostViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(123);
    publishSubject.onNext(873);
    publishSubject.onNext(4728);
    publishSubject.onNext(32);
    publishSubject.onError(new Exception());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(0));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(123));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(873));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(4728));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(32));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewStateServerDataError(0));
    verifyNoMoreInteractions(viewStateObserver);
  }
}