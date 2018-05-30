package com.fasten.executor_driver.presentation.waitingforclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
  private WaitingForClientViewModel movingToClientViewModel;
  @Mock
  private WaitingForClientUseCase movingToClientUseCase;

  @Mock
  private Observer<ViewState<WaitingForClientViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(movingToClientUseCase.startTheOrder()).thenReturn(Completable.never());
    movingToClientViewModel = new WaitingForClientViewModelImpl(
        movingToClientUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить юзкейс сообщить о начале погрузки.
   */
  @Test
  public void askUseCaseToStartLoading() {
    // Дано:
    when(movingToClientUseCase.startTheOrder()).thenReturn(Completable.complete());

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    verify(movingToClientUseCase, only()).startTheOrder();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос звонка/начала еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    movingToClientViewModel.startLoading();
    movingToClientViewModel.startLoading();
    movingToClientViewModel.startLoading();

    // Результат:
    verify(movingToClientUseCase, only()).startTheOrder();
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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForStartLoading() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.startTheOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForStartLoadingWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.startTheOrder())
        .thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.startLoading();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(WaitingForClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }
}