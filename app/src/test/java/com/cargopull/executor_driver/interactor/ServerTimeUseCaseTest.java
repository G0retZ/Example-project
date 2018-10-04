package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
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
  private CommonGateway<Long> gateway;
  @Mock
  private TimeUtils timeUtils;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new ServerTimeUseCaseImpl(errorReporter, gateway, timeUtils);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея текущие временные метки сервера.
   */
  @Test
  public void askGatewayForServerTime() {
    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

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
  public void doNotSetServerTimeIfNoData() {
    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verifyZeroInteractions(timeUtils);
  }

  /**
   * Не должен запрпрашивать у гейтвея текущие временные метки сервера.
   */
  @Test
  public void doNotSetServerTimeIfError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
    verifyZeroInteractions(timeUtils);
  }

  /**
   * Не должен задавать текущее время сервера.
   */
  @Test
  public void doNotSetServerTime() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(timeUtils);
    when(gateway.getData()).thenReturn(Flowable.just(1L, 2L, 3L));

    // Действие:
    useCase.getServerTime().test();

    // Результат:
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
    when(gateway.getData()).thenReturn(Flowable.just(1L, 2L, 3L));

    // Действие:
    TestObserver testObserver = useCase.getServerTime().test();

    // Результат:
    testObserver.assertNoErrors();
    testObserver.assertComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerWithError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

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
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Действие:
    TestObserver testObserver = useCase.getServerTime().test();

    // Результат:
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }
}