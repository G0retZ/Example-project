package com.fasten.executor_driver.presentation.movingtoclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.MovingToClientUseCase;
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
public class MovingToClientViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private MovingToClientViewModel movingToClientViewModel;
  @Mock
  private MovingToClientUseCase movingToClientUseCase;

  @Mock
  private Observer<ViewState<MovingToClientViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(movingToClientUseCase.reportArrival()).thenReturn(Completable.never());
    movingToClientViewModel = new MovingToClientViewModelImpl(movingToClientUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен попросить юзкейс сообщить о прибытии на место встречи.
   */
  @Test
  public void askUseCaseToReportArrived() {
    // Дано:
    when(movingToClientUseCase.reportArrival()).thenReturn(Completable.complete());

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    verify(movingToClientUseCase, only()).reportArrival();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос прибытия еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    movingToClientViewModel.reportArrival();
    movingToClientViewModel.reportArrival();
    movingToClientViewModel.reportArrival();

    // Результат:
    verify(movingToClientUseCase, only()).reportArrival();
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
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForReportArrival() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForReportArrival() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.reportArrival())
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForReportArrivalWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(movingToClientUseCase.reportArrival()).thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }
}