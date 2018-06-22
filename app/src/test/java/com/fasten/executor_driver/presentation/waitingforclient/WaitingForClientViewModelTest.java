package com.fasten.executor_driver.presentation.waitingforclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.WaitingForClientUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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
public class WaitingForClientViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private WaitingForClientViewModel viewModel;
  @Mock
  private WaitingForClientUseCase waitingForClientUseCase;

  @Mock
  private Observer<ViewState<WaitingForClientViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(waitingForClientUseCase.startTheOrder()).thenReturn(Completable.never());
    viewModel = new WaitingForClientViewModelImpl(
        waitingForClientUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить юзкейс сообщить о начале погрузки.
   */
  @Test
  public void askUseCaseToStartLoading() {
    // Дано:
    when(waitingForClientUseCase.startTheOrder()).thenReturn(Completable.complete());

    // Действие:
    viewModel.startLoading();

    // Результат:
    verify(waitingForClientUseCase, only()).startTheOrder();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос звонка/начала еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    viewModel.startLoading();
    viewModel.startLoading();
    viewModel.startLoading();

    // Результат:
    verify(waitingForClientUseCase, only()).startTheOrder();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида бездействия изначально.
   */
  @Test
  public void setIdleViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForStartLoading() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида бездействия при ошибке сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForStartLoading() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(waitingForClientUseCase.startTheOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForStartLoadingWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(waitingForClientUseCase.startTheOrder())
        .thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Не должен никуда ходить при начале погрузки.
   */
  @Test
  public void doNotTouchNavigationObserver() {
    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.startLoading();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к ошибке сети при начале погрузки.
   */
  @Test
  public void navigateToNoConnectionForNoNetworkError() {
    // Дано:
    when(waitingForClientUseCase.startTheOrder())
        .thenReturn(Completable.error(new IllegalStateException()));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.startLoading();

    // Результат:
    verify(navigateObserver, only()).onChanged(WaitingForClientNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить при успешном начале погрузки.
   */
  @Test
  public void doNotTouchNavigationObserverForSuccess() {
    // Дано:
    when(waitingForClientUseCase.startTheOrder()).thenReturn(Completable.complete());

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.startLoading();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}