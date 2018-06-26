package com.fasten.executor_driver.presentation.calltooperator;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.presentation.ViewState;
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
import org.mockito.Mock;
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
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    viewModel = new CallToOperatorViewModelImpl();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Не должен возвращать никаких состояний вида.
   */
  @Test
  public void setNoViewStateToLiveDataEver() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.callToOperator();
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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе".
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
   * Не должен никуда переходить.
   */
  @Test
  public void doNotSetNavigateAfter9999ms() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToOperator();
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
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.callToOperator();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    verify(navigateObserver, only()).onChanged(CallToOperatorNavigate.FINISHED);
  }
}