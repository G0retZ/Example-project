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
  private MovingToClientViewModel viewModel;
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
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(movingToClientUseCase.reportArrival()).thenReturn(Completable.never());
    viewModel = new MovingToClientViewModelImpl(movingToClientUseCase);
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
    viewModel.reportArrival();

    // Результат:
    verify(movingToClientUseCase, only()).reportArrival();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос прибытия еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    viewModel.reportArrival();
    viewModel.reportArrival();
    viewModel.reportArrival();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.reportArrival();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.reportArrival();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.reportArrival();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToClient();

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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToClient();
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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
   */
  @Test
  public void doNotSetNavigateForPending() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.reportArrival();

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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.reportArrival();

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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.reportArrival();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к звонку клиенту".
   */
  @Test
  public void setNavigateToCallToClientForCallRequest() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToClient();

    // Результат:
    verify(navigateObserver, only()).onChanged(MovingToClientNavigate.CALL_TO_CLIENT);
  }

  /**
   * Не должен никуда переходить для вида "В процессе звонка".
   */
  @Test
  public void doNotSetNavigateForCalling() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToClient();
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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(MovingToClientNavigate.CALL_TO_CLIENT);
  }
}