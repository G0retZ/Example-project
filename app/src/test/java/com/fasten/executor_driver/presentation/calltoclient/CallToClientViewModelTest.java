package com.fasten.executor_driver.presentation.calltoclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.interactor.CallToClientUseCase;
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
public class CallToClientViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CallToClientViewModel callToClientViewModel;
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
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(callToClientUseCase.callToClient()).thenReturn(Completable.never());
    callToClientViewModel = new CallToClientViewModelImpl(callToClientUseCase);
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
    callToClientViewModel.callToClient();

    // Результат:
    verify(callToClientUseCase, only()).callToClient();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос звонка еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    callToClientViewModel.callToClient();
    callToClientViewModel.callToClient();
    callToClientViewModel.callToClient();

    // Результат:
    verify(callToClientUseCase, only()).callToClient();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    callToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWhenCallToClient() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    callToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    callToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CallToClientViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида бездействия при ошибке сети.
   */
  @Test
  public void setIdleViewState() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    callToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    callToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CallToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    callToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    callToClientViewModel.callToClient();

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CallToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида по истечении 20 секунд.
   */
  @Test
  public void setNoViewStateToLiveData20SecondsAfterCall() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    callToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    callToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver, times(2)).onChanged(any(CallToClientViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToClientViewStateIdle.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить изначально.
   */
  @Test
  public void doNotSetNavigateInitially() {
    // Действие:
    callToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
   */
  @Test
  public void doNotSetNavigateForPending() {
    // Дано:
    callToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToClientViewModel.callToClient();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "Ошибка" сети.
   */
  @Test
  public void doNotSetNavigateForNoNetworkError() {
    // Дано:
    when(callToClientUseCase.callToClient())
        .thenReturn(Completable.error(NoNetworkException::new));
    callToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(1, TimeUnit.NANOSECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(CallToClientNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void doNotSetNavigateForIdle() {
    // Дано:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    callToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(9999, TimeUnit.MILLISECONDS);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к финишу" через 10 секунд после успеха запроса дозвона.
   */
  @Test
  public void setNavigateToFinishAfter10SecondsLater() {
    // Дано:
    when(callToClientUseCase.callToClient()).thenReturn(Completable.complete());
    callToClientViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToClientViewModel.callToClient();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(CallToClientNavigate.FINISHED);
  }
}