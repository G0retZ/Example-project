package com.cargopull.executor_driver.presentation.calltoclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.interactor.CallToClientUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToClientViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CallToClientViewModel viewModel;
  @Mock
  private CallToClientUseCase callToClientUseCase;
  private TestScheduler testScheduler;

  @Mock
  private Observer<ViewState<CallToClientViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.never());
    viewModel = new CallToClientViewModelImpl(callToClientUseCase);
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить юзкейс позвонить клиенту.
   */
  @Test
  public void askUseCaseToCallToClient() {
    // Дано:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());

    // Действие:
    viewModel.callToClient();

    // Результат:
    verify(callToClientUseCase, only()).callToClient();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос звонка еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    viewModel.callToClient();
    viewModel.callToClient();
    viewModel.callToClient();

    // Результат:
    verify(callToClientUseCase, only()).callToClient();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "не звоним" изначально.
   */
  @Test
  public void setNotCallingViewStateToLiveDataInitially() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(CallToClientViewStateNotCalling.class));
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWhenCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "не звоним" при ошибке сети.
   */
  @Test
  public void setNotCallingViewStateToLiveDataForError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "звоним".
   */
  @Test
  public void setCallingViewStateToLiveDataForComplete() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateCalling.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "не звоним" по истечении 10 секунд.
   */
  @Test
  public void setNotCallingViewStateToLiveData10SecondsAfterCall() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
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
    viewModel.callToClient();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения".
   */
  @Test
  public void setNavigateForNoNetworkError() {
    // Дано:
    when(callToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void doNotSetNavigateForIdle() {
    // Дано:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(9999, TimeUnit.MILLISECONDS);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить через 10 секунд после успеха запроса дозвона.
   */
  @Test
  public void setNavigateToFinishAfter10SecondsLater() {
    // Дано:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}