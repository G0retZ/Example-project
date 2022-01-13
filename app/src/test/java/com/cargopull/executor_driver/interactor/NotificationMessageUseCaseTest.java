package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class NotificationMessageUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();
  private NotificationMessageUseCase useCase;
  @Mock
  private CommonGateway<String> gateway;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new NotificationMessageUseCaseImpl(gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея сообщения о заказах упущенных исполнителем только раз.
   */
  @Test
  public void askGatewayForMissedOrdersMessages() {
    // Action:
    useCase.getNotificationMessages().test().isDisposed();
    useCase.getNotificationMessages().test().isDisposed();
    useCase.getNotificationMessages().test().isDisposed();
    useCase.getNotificationMessages().test().isDisposed();

    // Effect:
    verify(gateway, only()).getData();
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть сообщения о заказах упущенных исполнителем.
   */
  @Test
  public void answerWithMissedOrdersMessages() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.just("1", "2", "3"));

    // Action:
    TestSubscriber<String> testSubscriber = useCase.getNotificationMessages().test();

    // Effect:
    testSubscriber.assertValues("1", "2", "3");
    testSubscriber.assertNoErrors();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    TestSubscriber<String> testSubscriber = useCase.getNotificationMessages().test();

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен завершить получение сообщений о заказах упущенных исполнителем.
   */
  @Test
  public void answerComplete() {
    // Given:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Action:
    TestSubscriber<String> testSubscriber = useCase.getNotificationMessages().test();

    // Effect:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}