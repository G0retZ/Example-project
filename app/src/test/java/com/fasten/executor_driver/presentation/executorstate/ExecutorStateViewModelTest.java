package com.fasten.executor_driver.presentation.executorstate;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.accounts.AuthenticatorException;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
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
public class ExecutorStateViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ExecutorStateViewModel executorStateViewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ExecutorStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ExecutorStateViewActions>> viewStateCaptor;
  @Mock
  private ExecutorStateViewActions executorStateViewActions;

  @Mock
  private ExecutorStateUseCase executorStateUseCase;

  @Before
  public void setUp() {
    ExecutorState.ONLINE.setData(null);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateUseCase.getExecutorStates(anyBoolean())).thenReturn(Flowable.never());
    executorStateViewModel = new ExecutorStateViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы исполнителя без сброса кеша.
   */
  @Test
  public void askDataReceiverToSubscribeToExecutorStateUpdates() {
    // Действие:
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(false);
  }

  /**
   * Должен попросить у юзкейса статусы исполнителя со сбросом кеша.
   */
  @Test
  public void askDataReceiverToSubscribeToExecutorStateUpdatesWithCacheReset() {
    // Действие:
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(executorStateUseCase, only()).getExecutorStates(true);
  }

  /**
   * Должен просить у юзкейса загрузить статусы исполнителя, без сброса кеша, даже если уже
   * подписан.
   */
  @Test
  public void doNotTouchUseCaseBeforeFirstRequestComplete() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateUseCase);

    // Действие:
    executorStateViewModel.initializeExecutorState(false);
    executorStateViewModel.initializeExecutorState(true);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    inOrder.verify(executorStateUseCase).getExecutorStates(false);
    inOrder.verify(executorStateUseCase).getExecutorStates(true);
    inOrder.verify(executorStateUseCase).getExecutorStates(false);
    verifyNoMoreInteractions(executorStateUseCase);
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сопутствующее сообщение.
   */
  @Test
  public void showOnlineMessage() {
    // Дано:
    ExecutorState.ONLINE.setData("Message");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(executorStateViewActions);
    verify(executorStateViewActions, only()).showMessage("Message");
  }

  /**
   * Не должен показывать null сообщение.
   */
  @Test
  public void doNotShowNullMessage() {
    // Дано:
    ExecutorState.ONLINE.setData(null);
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать пустое сообщение.
   */
  @Test
  public void doNotShowEmptyMessage() {
    // Дано:
    ExecutorState.ONLINE.setData("");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /**
   * Не должен показывать сообщение из пробелов.
   */
  @Test
  public void doNotShowSpaceMessage() {
    // Дано:
    ExecutorState.ONLINE.setData("\n");
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к отсутствию сети".
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к авторизации".
   */
  @Test
  public void navigateToAuthorize() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к карте".
   */
  @Test
  public void navigateToShiftClosed() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_CLOSED));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_CLOSED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToShiftOpened() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.SHIFT_OPENED));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_SHIFT_OPENED);
  }

  /**
   * Должен вернуть "перейти к получению заказа".
   */
  @Test
  public void navigateToOnline() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ONLINE));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MAP_ONLINE);
  }

  /**
   * Должен вернуть "перейти к подтверждению заказа".
   */
  @Test
  public void navigateToDriverOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.DRIVER_ORDER_CONFIRMATION));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(true);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к ожиданию подтверждения клиента".
   */
  @Test
  public void navigateToClientOrderConfirmation() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.CLIENT_ORDER_CONFIRMATION));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION);
  }

  /**
   * Должен вернуть "перейти к движению к клиенту".
   */
  @Test
  public void navigateToMovingToClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.MOVING_TO_CLIENT));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.MOVING_TO_CLIENT);
  }

  /**
   * Должен вернуть "перейти к ожиданию клиента".
   */
  @Test
  public void navigateToWaitingForClient() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.WAITING_FOR_CLIENT));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.WAITING_FOR_CLIENT);
  }

  /**
   * Должен вернуть "перейти к выполнению заказа".
   */
  @Test
  public void navigateToOrderFulfillment() {
    // Дано:
    when(executorStateUseCase.getExecutorStates(anyBoolean()))
        .thenReturn(Flowable.just(ExecutorState.ORDER_FULFILLMENT));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.initializeExecutorState(false);

    // Результат:
    verify(navigationObserver, only()).onChanged(ExecutorStateNavigate.ORDER_FULFILLMENT);
  }
}