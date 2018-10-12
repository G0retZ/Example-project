package com.cargopull.executor_driver.presentation.ordercost;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.ClassRule;
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

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderCostViewModel viewModel;
  @Mock
  private OrderCurrentCostUseCase orderCurrentCostUseCase;
  @Mock
  private Observer<ViewState<OrderCostViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private PublishSubject<Long> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(orderCurrentCostUseCase.getOrderCurrentCost())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OrderCostViewModelImpl(orderCurrentCostUseCase);
  }


  /* Тетсируем работу с юзкейсом текущей цены выполнения заказа. */

  /**
   * Должен просить юзкейс получить актуальную цену, при создании.
   */
  @Test
  public void askUseCaseForOrderCostInitially() {
    // Результат:
    verify(orderCurrentCostUseCase, only()).getOrderCurrentCost();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void DoNotTouchUseCaseOnSubscriptions() {
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
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);

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
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);
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
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}