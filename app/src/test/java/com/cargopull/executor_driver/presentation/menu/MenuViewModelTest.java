package com.cargopull.executor_driver.presentation.menu;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class MenuViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private MenuViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateUseCase executorStateUseCase;
  @Mock
  private Observer<ViewState<FragmentViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  @Mock
  private FragmentViewActions fragmentViewActions;
  @Captor
  private ArgumentCaptor<ViewState<FragmentViewActions>> viewStateCaptor;
  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;
  private PublishSubject<ExecutorState> publishSubject;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(executorStateUseCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new MenuViewModelImpl(errorReporter, executorStateUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку получения данных.
   */
  @Test
  public void reportIllegalArgumentErrorIfBlocked() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен просить у юзкейса источник для подписки изначально.
   */
  @Test
  public void askExecutorStateUseCaseForSubscribeInitially() {
    // Effect:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра изначально.
   */
  @Test
  public void setUnAvailablePendingViewStateInitially() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(MenuViewStateFilterUnAvailable.class));
  }

  /**
   * Не должен давать иных состояний вида если была ошибка в подписке на статусы исполнителя.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForExecutorStateError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(MenuViewStateFilterUnAvailable.class));
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "заблокирован".
   */
  @Test
  public void setUnAvailableViewStateForBlocked() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "смена закрыта".
   */
  @Test
  public void setUnAvailableViewStateForShiftClosed() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с активной кнопкой фильтра для "смена открыта".
   */
  @Test
  public void setAvailableViewStateForShiftOpened() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(MenuViewStateFilterUnAvailable.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new MenuViewStateFilterAvailable(runnableCaptor.capture()));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с активной кнопкой фильтра для "онлайн".
   */
  @Test
  public void setAvailableViewStateForOnline() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(MenuViewStateFilterUnAvailable.class));
    inOrder.verify(viewStateObserver)
        .onChanged(new MenuViewStateFilterAvailable(runnableCaptor.capture()));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Подтверждение срочного заказа".
   */
  @Test
  public void setUnAvailableViewStateForDriverOrderConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Подтверждение предварительного
   * заказа".
   */
  @Test
  public void setUnAvailableViewStateForDriverPreOrderConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Подтверждение заказа клиентом".
   */
  @Test
  public void setUnAvailableViewStateForClientOrderConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Движении к клиенту".
   */
  @Test
  public void setUnAvailableViewStateForMovingToClient() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Ожидании клиента".
   */
  @Test
  public void setUnAvailableViewStateForWaitingForClient() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Выполнения заказа".
   */
  @Test
  public void setUnAvailableViewStateForOrderFulfillment() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Выполнения заказа".
   */
  @Test
  public void setUnAvailableViewStateForPaymentConfirmation() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.PAYMENT_CONFIRMATION);

    // Effect:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorForExecutorStateError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен никуда ходить.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    for (ExecutorState executorState : ExecutorState.values()) {
      publishSubject.onNext(executorState);
    }

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к настройке фильтра заказов для открытой смены.
   */
  @Test
  public void navigateToOrdersFilterForShiftOpened() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(fragmentViewActions);
    verify(fragmentViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();

    // Effect:
    verify(navigateObserver, only()).onChanged(MenuNavigate.ORDERS_FILTER);
  }

  /**
   * Должен вернуть перейти к настройке фильтра заказов для онлайн.
   */
  @Test
  public void navigateToOrdersFilterForOnline() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onNext(ExecutorState.ONLINE);
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(fragmentViewActions);
    verify(fragmentViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();

    // Effect:
    verify(navigateObserver, only()).onChanged(MenuNavigate.ORDERS_FILTER);
  }
}