package com.cargopull.executor_driver.interactor.map;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HeatMapUseCaseTest {

  private final static int REQUIRED_INTERVAL = 5;

  private HeatMapUseCase useCase;

  private TestScheduler testScheduler;

  @Mock
  private HeatMapGateway gateway;

  @Mock
  private Callable<String> testCallable;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(gateway.getHeatMap()).thenReturn(Single.never());
    useCase = new HeatMapUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея тепловую карту.
   */
  @Test
  public void askGatewayForHeatMap() {
    // Действие:
    useCase.loadHeatMap().test();

    // Результат:
    verify(gateway, only()).getHeatMap();
  }

  /**
   * Не должен запрашивать у гейтвея тепловую карту при повторном запросе.
   */
  @Test
  public void doNotAskGatewayForHeatMap() {
    // Действие:
    useCase.loadHeatMap().test();
    useCase.loadHeatMap().test();

    // Результат:
    verify(gateway, only()).getHeatMap();
  }

  /* Проверяем ответы на запрос тепловой карты */

  /**
   * Должен пропустить ошибки сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(gateway.getHeatMap()).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    TestSubscriber<String> testObserver = useCase.loadHeatMap().test();
    testScheduler.advanceTimeBy(REQUIRED_INTERVAL, TimeUnit.MINUTES);
    testScheduler.advanceTimeBy(REQUIRED_INTERVAL, TimeUnit.MINUTES);
    testScheduler.advanceTimeBy(REQUIRED_INTERVAL, TimeUnit.MINUTES);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    testObserver.assertNotTerminated();
  }

  /**
   * Должен вернуть полученные мапы.
   */
  @Test
  public void answerWithValues() {
    // Дано:
    when(gateway.getHeatMap()).thenReturn(Single.fromCallable(new Callable<String>() {
      private int count;

      @Override
      public String call() throws Exception {
        if (count < 2) {
          count++;
          throw new NoNetworkException();
        } else if (count < 5) {
          count++;
          return "12";
        } else if (count < 7) {
          count++;
          throw new NoNetworkException();
        }
        return "123";
      }
    }));

    // Действие:
    TestSubscriber<String> testObserver = useCase.loadHeatMap().test();
    testScheduler.advanceTimeBy(REQUIRED_INTERVAL * 9, TimeUnit.MINUTES);

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertNotComplete();
    testObserver.assertNotTerminated();
    testObserver.assertValues("12", "12", "12", "123", "123", "123");
  }

  /**
   * Не должен ничего запрашивать у гейтвея после отписки всех подписчиков.
   *
   * @throws Exception error
   */
  @Test
  public void doNotAskGatewayForHeatMapAfterDispose() throws Exception {
    // Дано:
    when(testCallable.call()).thenReturn("test mess");
    when(gateway.getHeatMap()).thenReturn(Single.fromCallable(testCallable));

    // Действие:
    Disposable disposable1 = useCase.loadHeatMap()
        .subscribe(System.out::println); // +1 вызов
    Disposable disposable2 = useCase.loadHeatMap()
        .subscribe(System.out::println); // +1 вызов
    Disposable disposable3 = useCase.loadHeatMap()
        .subscribe(System.out::println); // +1 вызов
    testScheduler
        .advanceTimeBy(REQUIRED_INTERVAL, TimeUnit.MINUTES); // +3 вызова на всех подписчиков
    disposable1.dispose();
    testScheduler
        .advanceTimeBy(REQUIRED_INTERVAL, TimeUnit.MINUTES); // +2 вызова на оставшихся подписчиков
    disposable2.dispose();
    testScheduler
        .advanceTimeBy(REQUIRED_INTERVAL, TimeUnit.MINUTES); // +1 вызов на последнего подписчика
    disposable3.dispose();

    // Результат:
    verify(testCallable, times(4)).call();
    testScheduler.advanceTimeBy(REQUIRED_INTERVAL * 10, TimeUnit.MINUTES);
    verifyNoMoreInteractions(testCallable);
  }
}