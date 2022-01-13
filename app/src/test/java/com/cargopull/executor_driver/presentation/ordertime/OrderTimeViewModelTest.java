package com.cargopull.executor_driver.presentation.ordertime;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderFulfillmentTimeUseCase;
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
public class OrderTimeViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderTimeViewModel viewModel;
  @Mock
  private OrderFulfillmentTimeUseCase orderCurrentTimeUseCase;
  @Mock
  private Observer<ViewState<OrderTimeViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private PublishSubject<Long> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(orderCurrentTimeUseCase.getOrderElapsedTime())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OrderTimeViewModelImpl(orderCurrentTimeUseCase);
  }


  /* Тетсируем работу с юзкейсом текущего времени выполнения заказа. */

  /**
   * Должен просить юзкейс получить актуальное время выполнения заказа, при создании.
   */
  @Test
  public void askUseCaseForOrderTimeInitially() {
    // Effect:
    verify(orderCurrentTimeUseCase, only()).getOrderElapsedTime();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(orderCurrentTimeUseCase, only()).getOrderElapsedTime();
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
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(0));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида с временем заказа.
   */
  @Test
  public void setViewStateWithTimesToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(123L);
    publishSubject.onNext(873L);
    publishSubject.onNext(4728L);
    publishSubject.onNext(32L);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(0));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(123));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(873));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(4728));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(32));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида при ошибке.
   */
  @Test
  public void setNoNewViewStateViewStateToLiveDataOnError() {
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
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(0));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(123));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(873));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(4728));
    inOrder.verify(viewStateObserver).onChanged(new OrderTimeViewState(32));
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