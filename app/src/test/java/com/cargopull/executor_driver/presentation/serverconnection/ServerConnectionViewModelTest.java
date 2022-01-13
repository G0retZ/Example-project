package com.cargopull.executor_driver.presentation.serverconnection;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.interactor.ServerConnectionUseCase;
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

import io.reactivex.Flowable;
import io.reactivex.functions.Action;

@RunWith(MockitoJUnitRunner.class)
public class ServerConnectionViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServerConnectionViewModel viewModel;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ServerConnectionViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ServerConnectionViewActions>> viewStateCaptor;
  @Mock
  private ServerConnectionViewActions viewActions;
  @Mock
  private ServerConnectionUseCase executorStateUseCase;
  @Mock
  private Action disposableAction;

  @Before
  public void setUp() {
    when(executorStateUseCase.connect()).thenReturn(Flowable.never());
    viewModel = new ServerConnectionViewModelImpl(executorStateUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы подключение к серверу.
   */
  @Test
  public void askUseCaseToConnectServer() {
    // Действие:
    viewModel.connectServer();

    // Результат:
    verify(executorStateUseCase, only()).connect();
  }

  /**
   * Не должен просить у юзкейса повторного подключения.
   */
  @Test
  public void doNotAskUseCaseToConnectServerIfAlreadyConnected() {
    // Действие:
    viewModel.connectServer();
    viewModel.connectServer();
    viewModel.connectServer();

    // Результат:
    verify(executorStateUseCase, only()).connect();
  }

  /**
   * Должен отписаться на запрос отключения от сервера.
   *
   * @throws Exception error
   */
  @Test
  public void disposeConnectionToServer() throws Exception {
    // Дано:
    when(executorStateUseCase.connect())
        .thenReturn(Flowable.<Boolean>never().doOnCancel(disposableAction));

    // Действие:
    viewModel.connectServer();

    // Результат:
    verify(executorStateUseCase, only()).connect();
    verifyNoInteractions(disposableAction);
    viewModel.disconnectServer();
    verify(disposableAction, only()).run();
  }

  /* Тетсируем сообщение. */

  /**
   * Должен отобразить состояния подключения к серверу.
   */
  @Test
  public void showServerConnectionStatus() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(executorStateUseCase.connect())
        .thenReturn(Flowable.just(true, false, false, true).concatWith(Flowable.never()));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.connectServer();

    // Результат:
    verify(viewStateObserver, times(4)).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(viewActions);
    }
    inOrder.verify(viewActions).showConnectionReady(true);
    inOrder.verify(viewActions, times(2)).showConnectionReady(false);
    inOrder.verify(viewActions).showConnectionReady(true);
    verifyNoMoreInteractions(viewStateObserver, viewActions);
  }

  /**
   * Должен отобразить отсутствие подключения к серверу при отписке.
   */
  @Test
  public void showNoServerConnectionForDisconnect() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(executorStateUseCase.connect())
        .thenReturn(Flowable.just(true, false, false, true).concatWith(Flowable.never()));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.connectServer();
    viewModel.disconnectServer();

    // Результат:
    verify(viewStateObserver, times(5)).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(viewActions);
    }
    inOrder.verify(viewActions).showConnectionReady(true);
    inOrder.verify(viewActions, times(2)).showConnectionReady(false);
    inOrder.verify(viewActions).showConnectionReady(true);
    inOrder.verify(viewActions).showConnectionReady(false);
    verifyNoMoreInteractions(viewStateObserver, viewActions);
  }

  /**
   * Не должен ничего показывать при ошибке.
   */
  @Test
  public void showNothingOnError() {
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(executorStateUseCase.connect())
        .thenReturn(Flowable.just(true, false).concatWith(Flowable.error(NoNetworkException::new)));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.connectServer();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(viewActions);
    }
    inOrder.verify(viewActions).showConnectionReady(true);
    inOrder.verify(viewActions).showConnectionReady(false);
    verifyNoMoreInteractions(viewStateObserver, viewActions);
  }

  /**
   * Не должен ничего показывать при завершении.
   */
  @Test
  public void showNothingOnCompletion() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(executorStateUseCase.connect()).thenReturn(Flowable.just(true, false));

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.connectServer();

    // Результат:
    verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
    for (ViewState<ServerConnectionViewActions> value : viewStateCaptor.getAllValues()) {
      value.apply(viewActions);
    }
    inOrder.verify(viewActions).showConnectionReady(true);
    inOrder.verify(viewActions).showConnectionReady(false);
    verifyNoMoreInteractions(viewStateObserver, viewActions);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к авторизации".
   */
  @Test
  public void navigateToAuthorize() {
    // Дано:
    when(executorStateUseCase.connect()).thenReturn(Flowable.error(AuthorizationException::new));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.connectServer();

    // Результат:
    verify(navigationObserver, only()).onChanged(ServerConnectionNavigate.AUTHORIZE);
  }

  /**
   * Должен игноррировать прочие ошибки.
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    when(executorStateUseCase.connect()).thenReturn(Flowable.error(Exception::new));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.connectServer();

    // Результат:
    verifyNoInteractions(navigationObserver);
  }
}