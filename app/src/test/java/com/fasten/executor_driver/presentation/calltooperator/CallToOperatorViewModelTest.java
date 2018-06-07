package com.fasten.executor_driver.presentation.calltooperator;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CallToOperatorViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private CallToOperatorViewModel callToOperatorViewModel;
  private TestScheduler testScheduler;

  @Mock
  private Observer<ViewState<CallToOperatorViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    callToOperatorViewModel = new CallToOperatorViewModelImpl();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Не должен возвращать никаких состояний вида.
   */
  @Test
  public void setNoViewStateToLiveDataEver() {
    // Действие:
    callToOperatorViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    callToOperatorViewModel.callToOperator();
    testScheduler.advanceTimeBy(20, TimeUnit.MINUTES);

    // Результат:
    verifyZeroInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить изначально.
   */
  @Test
  public void doNotSetNavigateInitially() {
    // Действие:
    callToOperatorViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
   */
  @Test
  public void doNotSetNavigate() {
    // Дано:
    callToOperatorViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToOperatorViewModel.callToOperator();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void doNotSetNavigateAfter9999ms() {
    // Дано:
    callToOperatorViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToOperatorViewModel.callToOperator();
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
    callToOperatorViewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    callToOperatorViewModel.callToOperator();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(CallToOperatorNavigate.FINISHED);
  }
}