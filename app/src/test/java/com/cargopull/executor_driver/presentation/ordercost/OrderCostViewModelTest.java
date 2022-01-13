package com.cargopull.executor_driver.presentation.ordercost;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

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

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderCostViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
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
    viewModel = new OrderCostViewModelImpl(errorReporter, orderCurrentCostUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку маппинга.
   */
  @Test
  public void reportDataMappingError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом текущей цены выполнения заказа. */

  /**
   * Должен просить юзкейс получить актуальную цену, при создании.
   */
  @Test
  public void askUseCaseForOrderCostInitially() {
    // Effect:
    verify(orderCurrentCostUseCase, only()).getOrderCurrentCost();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void DoNotTouchUseCaseOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(orderCurrentCostUseCase, only()).getOrderCurrentCost();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида с 0 изначально.
   */
  @Test
  public void setViewStateWithZeroToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OrderCostViewState(0));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с суммой.
   */
  @Test
  public void setViewStateWithCostsToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);
    publishSubject.onError(new Exception());

    // Effect:
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
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}