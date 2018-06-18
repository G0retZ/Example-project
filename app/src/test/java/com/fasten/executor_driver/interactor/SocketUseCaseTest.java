package com.fasten.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SocketUseCaseTest {

  private SocketUseCase socketUseCase;

  @Mock
  private SocketGateway socketGateway;

  @Before
  public void setUp() {
    when(socketGateway.openSocket()).thenReturn(Flowable.never());
    socketUseCase = new SocketUseCaseImpl(socketGateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея открытие сокета.
   */
  @Test
  public void askGatewayToOpenSocket() {
    // Действие:
    socketUseCase.connect().test();

    // Результат:
    verify(socketGateway, only()).openSocket();
  }

  /* Проверяем ответы на запрос соединения */

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Flowable.error(new NoNetworkException()));

    // Действие:
    TestSubscriber<Boolean> testSubscriber = socketUseCase.connect().test();

    // Результат:
    testSubscriber.assertError(NoNetworkException.class);
    testSubscriber.assertNoValues();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerConnectSuccessful() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Flowable.just(true).concatWith(Flowable.never()));

    // Действие:
    TestSubscriber<Boolean> testSubscriber = socketUseCase.connect().test();

    // Результат:
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoErrors();
    testSubscriber.assertValue(true);
  }

  /**
   * Должен ответить закрытием сокета.
   */
  @Test
  public void answerConnectionClosed() {
    // Дано:
    when(socketGateway.openSocket()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<Boolean> testSubscriber = socketUseCase.connect().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNoValues();
  }
}