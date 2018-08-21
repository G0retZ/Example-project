package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import java.io.IOException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServerTimeUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private ServerTimeUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ServerTimeGateway gateway;
  @Mock
  private DataReceiver<String> loginReceiver;
  @Mock
  private TimeUtils timeUtils;
  @Mock
  private Action action;

  @Before
  public void setUp() {
    when(gateway.loadServerTime(anyString())).thenReturn(Flowable.never());
    when(loginReceiver.get()).thenReturn(Observable.never());
    useCase = new ServerTimeUseCaseImpl(errorReporter, gateway, loginReceiver, timeUtils);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея текущие временные метки сервера.
   */
  @Test
  public void askGatewayForServerTime() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(gateway);
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    inOrder.verify(gateway).loadServerTime("1234567890");
    inOrder.verify(gateway).loadServerTime("0987654321");
    inOrder.verify(gateway).loadServerTime("123454321");
    inOrder.verify(gateway).loadServerTime("09876567890");
    verifyNoMoreInteractions(gateway);
  }

  /**
   * Должен отписаться от предыдущих запросов текущих временных меток сервера.
   *
   * @throws Exception error
   */
  @Test
  public void ubSubscribeFromPreviousRequestsToGateway() throws Exception {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));
    when(gateway.loadServerTime(anyString()))
        .thenReturn(Flowable.<Long>never().doOnCancel(action));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verify(action, times(3)).run();
  }

  /**
   * Не должен запрпрашивать у гейтвея текущие временные метки сервера.
   */
  @Test
  public void doNotAskGatewayForServerTimeIfNoLogin() {
    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея текущие временные метки сервера.
   */
  @Test
  public void doNotAskGatewayForServerTimeIfLoginError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку, если была ошибка получения логина.
   */
  @Test
  public void reportGetLoginFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(IOException.class));
  }

  /**
   * Должен отправить ошибку, если подписка обломалась.
   */
  @Test
  public void reportSubscriptionFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadServerTime("1234567890"))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем работу с временем */

  /**
   * Не должен задавать текущее время сервера.
   */
  @Test
  public void doNotSetServerTimeIfNoLogin() {
    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен запрпрашивать у гейтвея текущие временные метки сервера.
   */
  @Test
  public void doNotSetServerTimeIfLoginError() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Не должен задавать текущее время сервера.
   */
  @Test
  public void doNotSetServerTime() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(timeUtils);
    when(gateway.loadServerTime(anyString())).thenReturn(Flowable.just(
        1L, 2L, 3L
    ));
    when(loginReceiver.get()).thenReturn(Observable.just(
        "1234567890", "0987654321", "123454321", "09876567890"
    ));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    inOrder.verify(timeUtils).setServerCurrentTime(1L);
    inOrder.verify(timeUtils).setServerCurrentTime(2L);
    inOrder.verify(timeUtils).setServerCurrentTime(3L);
    inOrder.verify(timeUtils).setServerCurrentTime(1L);
    inOrder.verify(timeUtils).setServerCurrentTime(2L);
    inOrder.verify(timeUtils).setServerCurrentTime(3L);
    inOrder.verify(timeUtils).setServerCurrentTime(1L);
    inOrder.verify(timeUtils).setServerCurrentTime(2L);
    inOrder.verify(timeUtils).setServerCurrentTime(3L);
    inOrder.verify(timeUtils).setServerCurrentTime(1L);
    inOrder.verify(timeUtils).setServerCurrentTime(2L);
    inOrder.verify(timeUtils).setServerCurrentTime(3L);
    verifyNoMoreInteractions(timeUtils);
  }

  /* Проверяем ответы */

  /**
   * Должен вернуть текущие временные метки сервера.
   */
  @Test
  public void answerWithServerTimes() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadServerTime("1234567890"))
        .thenReturn(Flowable.just(1L, 2L, 3L));

    // Действие:
    TestObserver testObserver = useCase.getServerTime().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен вернуть ошибку, если была ошибка получения логина.
   */
  @Test
  public void answerWithErrorIfGetLoginFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.error(IOException::new));

    // Действие:
    TestObserver testObserver = useCase.getServerTime().test();

    // Результат:
    testObserver.assertError(IOException.class);
    testObserver.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку, если подписка обломалась.
   */
  @Test
  public void answerWithErrorIfSubscriptionFailed() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadServerTime("1234567890"))
        .thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestObserver testObserver = useCase.getServerTime().test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNotComplete();
  }

  /**
   * Должен завершить получение текущих временных меток сервера.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(loginReceiver.get()).thenReturn(Observable.just("1234567890"));
    when(gateway.loadServerTime("1234567890")).thenReturn(Flowable.empty());

    // Действие:
    TestObserver testObserver = useCase.getServerTime().test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }
}