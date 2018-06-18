package com.fasten.executor_driver.presentation.serverconnection;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.accounts.AuthenticatorException;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.ServerConnectionUseCase;
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
public class ServerConnectionViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServerConnectionViewModel executorStateViewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ServerConnectionViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ServerConnectionViewActions>> viewStateCaptor;
  @Mock
  private ServerConnectionViewActions executorStateViewActions;

  @Mock
  private ServerConnectionUseCase executorStateUseCase;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(executorStateUseCase.connect()).thenReturn(Flowable.never());
    executorStateViewModel = new ServerConnectionViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы подключение к серверу.
   */
  @Test
  public void askUseCaseToConnectServer() {
    // Действие:
    executorStateViewModel.connectServer();

    // Результат:
    verify(executorStateUseCase, only()).connect();
  }

  /**
   * Не должен просить у юзкейса повторного подключения.
   */
  @Test
  public void doNotAskUseCaseToConnectServerIfAlreadyConnected() {
    // Действие:
    executorStateViewModel.connectServer();
    executorStateViewModel.connectServer();
    executorStateViewModel.connectServer();

    // Результат:
    verify(executorStateUseCase, only()).connect();
  }

  /* Тетсируем сообщение. */

  /**
   * Должен отобразить состояния подключения к серверу.
   */
  @Test
  public void showServerConnectionStatus() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateViewActions);
    when(executorStateUseCase.connect())
        .thenReturn(Flowable.just(true, false, false, true).concatWith(Flowable.never()));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.connectServer();

    // Результат:
    verify(viewStateObserver, times(4)).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(executorStateViewActions);
    }
    inOrder.verify(executorStateViewActions).showConnectionReady(true);
    inOrder.verify(executorStateViewActions, times(2)).showConnectionReady(false);
    inOrder.verify(executorStateViewActions).showConnectionReady(true);
    verifyNoMoreInteractions(viewStateObserver, executorStateViewActions);
  }

  /**
   * Должен отобразить отсустсвие подключения, если оно было завершено.
   */
  @Test
  public void showNoServerConnectionOnCompletion() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateViewActions);
    when(executorStateUseCase.connect()).thenReturn(Flowable.just(true));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.connectServer();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(executorStateViewActions);
    }
    inOrder.verify(executorStateViewActions).showConnectionReady(true);
    inOrder.verify(executorStateViewActions).showConnectionReady(false);
    verifyNoMoreInteractions(viewStateObserver, executorStateViewActions);
  }

  /**
   * Должен показать отсустсвие подключения, если при ошибке сети.
   */
  @Test
  public void showNoServerConnectionOnNetworkError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateViewActions);
    when(executorStateUseCase.connect()).thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.connectServer();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(executorStateViewActions);
    }
    inOrder.verify(executorStateViewActions).showConnectionReady(false);
    verifyNoMoreInteractions(executorStateViewActions);
  }

  /**
   * Должен показать показать отсустсвие подключения, если при ошибке авторизации.
   */
  @Test
  public void showNoServerConnectionOnAuthError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(executorStateViewActions);
    when(executorStateUseCase.connect()).thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    executorStateViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    executorStateViewModel.connectServer();

    // Результат:
    verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(executorStateViewActions);
    }
    inOrder.verify(executorStateViewActions).showConnectionReady(false);
    verifyNoMoreInteractions(executorStateViewActions);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к отсутствию сети".
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    when(executorStateUseCase.connect()).thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.connectServer();

    // Результат:
    verify(navigationObserver, only()).onChanged(ServerConnectionNavigate.NO_NETWORK);
  }

  /**
   * Должен вернуть "перейти к авторизации".
   */
  @Test
  public void navigateToAuthorize() {
    // Дано:
    when(executorStateUseCase.connect()).thenReturn(Flowable.error(AuthenticatorException::new));

    // Действие:
    executorStateViewModel.getNavigationLiveData().observeForever(navigationObserver);
    executorStateViewModel.connectServer();

    // Результат:
    verify(navigationObserver, only()).onChanged(ServerConnectionNavigate.NO_NETWORK);
  }
}