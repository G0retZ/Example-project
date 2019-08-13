package com.cargopull.executor_driver.presentation.menu;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
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

  /* Тетсируем работу с юзкейсом. */

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку получения данных.
   */
  @Test
  public void reportIllegalArgumentErrorIfBlocked() {
    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен просить у юзкейса источник для подписки изначально.
   */
  @Test
  public void askExecutorStateUseCaseForSubscribeInitially() {
    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра изначально.
   */
  @Test
  public void setUnAvailablePendingViewStateInitially() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(MenuViewStateFilterUnAvailable.class));
  }

  /**
   * Не должен давать иных состояний вида если была ошибка в подписке на статусы исполнителя.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForExecutorStateError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(MenuViewStateFilterUnAvailable.class));
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "заблокирован".
   */
  @Test
  public void setUnAvailableViewStateForBlocked() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.BLOCKED);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "смена закрыта".
   */
  @Test
  public void setUnAvailableViewStateForShiftClosed() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_CLOSED);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с активной кнопкой фильтра для "смена открыта".
   */
  @Test
  public void setAvailableViewStateForShiftOpened() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.DRIVER_ORDER_CONFIRMATION);

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Подтверждение заказа клиентом".
   */
  @Test
  public void setUnAvailableViewStateForClientOrderConfirmation() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.CLIENT_ORDER_CONFIRMATION);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Движении к клиенту".
   */
  @Test
  public void setUnAvailableViewStateForMovingToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.MOVING_TO_CLIENT);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Ожидании клиента".
   */
  @Test
  public void setUnAvailableViewStateForWaitingForClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.WAITING_FOR_CLIENT);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Выполнения заказа".
   */
  @Test
  public void setUnAvailableViewStateForOrderFulfillment() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ORDER_FULFILLMENT);

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(MenuViewStateFilterUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние с неактивной кнопкой фильтра для "Выполнения заказа".
   */
  @Test
  public void setUnAvailableViewStateForPaymentConfirmation() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.PAYMENT_CONFIRMATION);

    // Результат:
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
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен никуда ходить.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    for (ExecutorState executorState : ExecutorState.values()) {
      publishSubject.onNext(executorState);
    }

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к настройке фильтра заказов для открытой смены.
   */
  @Test
  public void navigateToOrdersFilterForShiftOpened() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.SHIFT_OPENED);
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(fragmentViewActions);
    verify(fragmentViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();

    // Результат:
    verify(navigateObserver, only()).onChanged(MenuNavigate.ORDERS_FILTER);
  }

  /**
   * Должен вернуть перейти к настройке фильтра заказов для онлайн.
   */
  @Test
  public void navigateToOrdersFilterForOnline() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(ExecutorState.ONLINE);
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(fragmentViewActions);
    verify(fragmentViewActions).setClickAction(anyInt(), runnableCaptor.capture());
    runnableCaptor.getValue().run();

    // Результат:
    verify(navigateObserver, only()).onChanged(MenuNavigate.ORDERS_FILTER);
  }
}