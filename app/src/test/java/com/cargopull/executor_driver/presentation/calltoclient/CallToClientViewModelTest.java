package com.cargopull.executor_driver.presentation.calltoclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.interactor.CallToClientUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

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

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;

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
    // Given:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());

    // Action:
    viewModel.callToClient();

    // Effect:
    verify(callToClientUseCase, only()).callToClient();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос звонка еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Action:
    viewModel.callToClient();
    viewModel.callToClient();
    viewModel.callToClient();

    // Effect:
    verify(callToClientUseCase, only()).callToClient();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида "не звоним" изначально.
   */
  @Test
  public void setNotCallingViewStateToLiveDataInitially() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(CallToClientViewStateNotCalling.class));
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWhenCallToClient() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.callToClient();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "не звоним" при ошибке сети.
   */
  @Test
  public void setNotCallingViewStateToLiveDataForError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.callToClient();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Effect:
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
    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
   */
  @Test
  public void doNotSetNavigateForPending() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.callToClient();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения".
   */
  @Test
  public void setNavigateForNoNetworkError() {
    // Given:
    when(callToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void doNotSetNavigateForIdle() {
    // Given:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(9999, TimeUnit.MILLISECONDS);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить через 10 секунд после успеха запроса дозвона.
   */
  @Test
  public void setNavigateToFinishAfter10SecondsLater() {
    // Given:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }
}