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
  private OrderCostViewModel viewModel;
  @Mock
  private OrderCurrentCostUseCase orderCurrentCostUseCase;
  @Mock
  private Observer<ViewState<OrderCostViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private PublishSubject<Integer> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderCurrentCostUseCase.getOrderCurrentCost())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OrderCostViewModelImpl(orderCurrentCostUseCase);
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
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderCostNavigate.SERVER_DATA_ERROR);
  }
}