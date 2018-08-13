package com.cargopull.executor_driver.presentation.calltooperator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.presentation.ViewState;
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
public class CallToOperatorViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CallToOperatorViewModel viewModel;
  private TestScheduler testScheduler;

  @Mock
  private Observer<ViewState<CallToOperatorViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    viewModel = new CallToOperatorViewModelImpl();
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
    verify(viewStateObserver, only()).onChanged(any(CallToOperatorViewStateNotCalling.class));
  }

  /**
   * Должен вернуть состояние вида "звоним".
   */
  @Test
  public void setCallingViewStateToLiveDataForComplete() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToOperator();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToOperatorViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToOperatorViewStateCalling.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "не звоним" по истечении 10 секунд.
   */
  @Test
  public void setNotCallingViewStateToLiveData10SecondsAfterCall() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.callToOperator();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(CallToOperatorViewStateNotCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToOperatorViewStateCalling.class));
    inOrder.verify(viewStateObserver).onChanged(any(CallToOperatorViewStateNotCalling.class));
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
   * Не должен никуда переходить при запросе.
   */
  @Test
  public void doNotSetNavigate() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToOperator();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить и через 10 минут.
   */
  @Test
  public void doNotSetNavigateAfter9999ms() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToOperator();
    testScheduler.advanceTimeBy(10, TimeUnit.MINUTES);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}