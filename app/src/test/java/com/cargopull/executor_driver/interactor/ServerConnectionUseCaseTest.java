package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.AuthorizationException;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Subscription;

@RunWith(MockitoJUnitRunner.class)
public class ServerConnectionUseCaseTest {

  private ServerConnectionUseCase useCase;

  @Mock
  private ServerConnectionGateway gateway;
  @Mock
  private Consumer<Subscription> consumer;
  private TestScheduler testScheduler;

  @Before
  public void setUp() {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(gateway.openSocket()).thenReturn(Flowable.never());
    useCase = new ServerConnectionUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея открытие сокета.
   */
  @Test
  public void askGatewayToOpenSocket() {
    // Действие:
    useCase.connect().test();

    // Результат:
    verify(gateway, only()).openSocket();
  }

  /**
   * Не должен просить у гейтвея повторных открытий сокета.
   */
  @Test
  public void doNotAskGatewayToOpenSocketAgain() {
    // Действие:
    useCase.connect().test();
    useCase.connect().test();
    useCase.connect().test();

    // Результат:
    verify(gateway, only()).openSocket();
  }

  /**
   * Должен просить у гейтвея повторные открытия сокета после завершений.
   */
  @Test
  public void askGatewayToOpenSocketAgainAfterComplete() {
    // Дано:
    when(gateway.openSocket()).thenReturn(Flowable.empty());

    // Действие:
    useCase.connect().test();
    useCase.connect().test();
    useCase.connect().test();

    // Результат:
    verify(gateway, times(3)).openSocket();
  }

  /**
   * Должен просить у гейтвея повторные открытия сокета после ошибок авторизации.
   */
  @Test
  public void askGatewayToOpenSocketAgainAfterAuthorizationErrors() {
    // Дано:
    when(gateway.openSocket()).thenReturn(Flowable.error(new AuthorizationException()));

    // Действие:
    useCase.connect().test();
    useCase.connect().test();
    useCase.connect().test();

    // Результат:
    verify(gateway, times(3)).openSocket();
  }

  /**
   * Не должен просить у гейтвея повторных открытий сокета после прочих ошибок.
   */
  @Test
  public void doNotAskGatewayToOpenSocketAgainAfterOtherErrors() {
    // Дано:
    when(gateway.openSocket()).thenReturn(Flowable.error(new Exception()));

    // Действие:
    useCase.connect().test();
    useCase.connect().test();
    useCase.connect().test();

    // Результат:
    verify(gateway, only()).openSocket();
  }

  /**
   * Должен повторить попытку открытия сокета.
   *
   * @throws Exception error
   */
  @Test
  public void retryOpenSocket() throws Exception {
    // Дано:
    when(gateway.openSocket()).thenReturn(
        Flowable.<Boolean>error(Exception::new).doOnSubscribe(consumer)
    );

    // Действие:
    useCase.connect().test();
    testScheduler.advanceTimeBy(60, TimeUnit.MINUTES);

    // Результат:
    verify(consumer, times(241)).accept(any());
  }

  /* Проверяем ответы на запрос соединения */

  /**
   * Должен ответить ошибкой авторизации.
   */
  @Test
  public void answerAuthorizationError() {
    // Дано:
    when(gateway.openSocket()).thenReturn(Flowable.error(new AuthorizationException()));

    // Действие:
    TestSubscriber<Boolean> testSubscriber = useCase.connect().test();

    // Результат:
    testSubscriber.assertError(AuthorizationException.class);
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен игнорировать иные ошибки, и для новых подписчиков тоже.
   */
  @Test
  public void ignoreOtherError() {
    // Дано:
    when(gateway.openSocket()).thenReturn(Flowable.error(new Exception()));

    // Действие:
    TestSubscriber<Boolean> testSubscriber = useCase.connect().test();
    testScheduler.advanceTimeBy(45, TimeUnit.MINUTES);
    TestSubscriber<Boolean> testSubscriber2 = useCase.connect().test();
    testScheduler.advanceTimeBy(45, TimeUnit.MINUTES);

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
    testSubscriber2.assertNoErrors();
    testSubscriber2.assertNoValues();
    testSubscriber2.assertNotComplete();
  }

  /**
   * Должен ответить состояниями подключения сети. При этом должен поделить результат между подписчиками.
   */
  @Test
  public void answerConnectionStates() {
    // Дано:
    when(gateway.openSocket()).thenReturn(
        Flowable.intervalRange(0, 6, 0, 30, TimeUnit.SECONDS, testScheduler)
            .switchMap(i -> Flowable.just(i % 2 == 0))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Boolean> testSubscriber = useCase.connect().test();
    testScheduler.advanceTimeBy(89, TimeUnit.SECONDS);
    TestSubscriber<Boolean> testSubscriber2 = useCase.connect().test();
    testScheduler.advanceTimeBy(900, TimeUnit.SECONDS);

    // Результат:
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoErrors();
    testSubscriber.assertValues(true, false, true, false, true, false);
    testSubscriber2.assertNotComplete();
    testSubscriber2.assertNoErrors();
    testSubscriber2.assertValues(false, true, false);
  }

  /**
   * Должен ответить закрытием сокета.
   */
  @Test
  public void answerConnectionClosed() {
    // Дано:
    when(gateway.openSocket()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<Boolean> testSubscriber = useCase.connect().test();
    testScheduler.advanceTimeBy(45, TimeUnit.MINUTES);

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
  }
}