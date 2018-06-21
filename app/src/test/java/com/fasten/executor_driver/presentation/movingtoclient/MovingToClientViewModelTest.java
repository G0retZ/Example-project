package com.fasten.executor_driver.presentation.movingtoclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
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
  private TestScheduler testScheduler;

  @Mock
  private Observer<ViewState<MovingToClientViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
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
  public void setPendingViewStateToLiveDataForReportArrival() {
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
   * Должен вернуть состояние вида бездействия при ошибке сети.
   */
  @Test
  public void setIdleViewStateToLiveDataForError() {
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
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForReportArrival() {
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

  /**
   * Должен вернуть состояние вида звонка.
   */
  @Test
  public void setCallingViewStateToLiveDataForCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateCalling.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида бездействия по истечении 20 секунд.
   */
  @Test
  public void setIdleViewStateToLiveData20SecondsAfterCall() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(MovingToClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить изначально.
   */
  @Test
  public void doNotSetNavigateInitially() {
    // Действие:
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
   */
  @Test
  public void doNotSetNavigateForPending() {
    // Дано:
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения".
   */
  @Test
  public void setNavigateToNoConnectionForNoNetworkError() {
    // Дано:
    when(movingToClientUseCase.reportArrival())
        .thenReturn(Completable.error(IllegalStateException::new));
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    verify(navigateObserver, only()).onChanged(MovingToClientNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить после сообщения о прибытии.
   */
  @Test
  public void doNotSetNavigateForReportArrivalSuccess() {
    // Дано:
    when(movingToClientUseCase.reportArrival()).thenReturn(Completable.complete());
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    movingToClientViewModel.reportArrival();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к звонку клиенту".
   */
  @Test
  public void setNavigateToCallToClientForCallRequest() {
    // Дано:
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    movingToClientViewModel.callToClient();

    // Результат:
    verify(navigateObserver, only()).onChanged(MovingToClientNavigate.CALL_TO_CLIENT);
  }

  /**
   * Не должен никуда переходить для вида "В процессе звонка".
   */
  @Test
  public void doNotSetNavigateForCalling() {
    // Дано:
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    movingToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(9999, TimeUnit.MILLISECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(MovingToClientNavigate.CALL_TO_CLIENT);
  }

  /**
   * Не должен никуда переходить через 10 секунд после успеха запроса дозвона.
   */
  @Test
  public void doNotSetNavigateAfter10SecondsOfCalling() {
    // Дано:
    movingToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    movingToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(MovingToClientNavigate.CALL_TO_CLIENT);
  }
}