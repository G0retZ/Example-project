package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.utils.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(Parameterized.class)
public class ExecutorStateApiMapperTest {

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();
  @Rule
  public final ExpectedException thrown = ExpectedException.none();
  private Mapper<StompMessage, ExecutorState> mapper;
  @Mock
  private Mapper<StompMessage, String> payloadMapper;
  private final StompMessage conditionStompMessage;
  private final Class<? extends Exception> expectedException;
  private final ExecutorState expectedExecutorState;
  private final String expectedMessage;
  private final long expectedTimer;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public ExecutorStateApiMapperTest(Pair<StompMessage, Expectations> conditions) {
    conditionStompMessage = conditions.first;
    expectedException = conditions.second.exception;
    expectedExecutorState = conditions.second.executorState;
    expectedMessage = conditions.second.message;
    expectedTimer = conditions.second.timer;
  }

  @Parameterized.Parameters
  public static Iterable primeNumbers() {
    ArrayList<Pair<StompMessage, Expectations>> conditions = new ArrayList<>();
    // Соответствия значений хедера статуса эксепшенам
    HashMap<String, Class<? extends Exception>> statusHeadersToExceptions = new HashMap<>();
    statusHeadersToExceptions.put(null, DataMappingException.class);
    statusHeadersToExceptions.put("", DataMappingException.class);
    statusHeadersToExceptions.put("SHIFT", DataMappingException.class);
    // Соответствия значений хедера статуса статусам исполнителя
    HashMap<String, ExecutorState> statusHeadersToStates = new HashMap<>();
    statusHeadersToStates.put(null, null);
    statusHeadersToStates.put("", null);
    statusHeadersToStates.put("SHIFT", null);
    statusHeadersToStates.put("BLOCKED", ExecutorState.BLOCKED);
    statusHeadersToStates.put("SHIFT_CLOSED", ExecutorState.SHIFT_CLOSED);
    statusHeadersToStates.put("SHIFT_OPENED", ExecutorState.SHIFT_OPENED);
    statusHeadersToStates.put("ONLINE", ExecutorState.ONLINE);
    statusHeadersToStates.put("DRIVER_ORDER_CONFIRMATION", ExecutorState.DRIVER_ORDER_CONFIRMATION);
    statusHeadersToStates.put("CLIENT_ORDER_CONFIRMATION", ExecutorState.CLIENT_ORDER_CONFIRMATION);
    statusHeadersToStates.put("MOVING_TO_CLIENT", ExecutorState.MOVING_TO_CLIENT);
    statusHeadersToStates.put("WAITING_FOR_CLIENT", ExecutorState.WAITING_FOR_CLIENT);
    statusHeadersToStates.put("ORDER_FULFILLMENT", ExecutorState.ORDER_FULFILLMENT);
    statusHeadersToStates.put("PAYMENT_CONFIRMATION", ExecutorState.PAYMENT_CONFIRMATION);
    // Соответствия значений хедера пейлоада сообщениям
    HashMap<String, String> payloadsToMessages = new HashMap<>();
    payloadsToMessages.put(null, null);
    payloadsToMessages.put("\npayload", "payload");
    // Соответствия значений хедера таймера эксепшенам
    HashMap<String, Class<? extends Exception>> timeHeadersToExceptions = new HashMap<>();
    timeHeadersToExceptions.put("", DataMappingException.class);
    timeHeadersToExceptions.put("jdi1293", DataMappingException.class);
    // Соответствия значений хедера таймера значениям таймера
    HashMap<String, Long> timerHeadersToTimers = new HashMap<>();
    timerHeadersToTimers.put(null, null);
    timerHeadersToTimers.put("", null);
    timerHeadersToTimers.put("jdi1293", null);
    timerHeadersToTimers.put("1345", 1345L);
    // Соответствия значений хедера статуса эксепшенам
    HashMap<String, ExecutorState> blockedHeadersToStates = new HashMap<>();
    blockedHeadersToStates.put(null, null);
    blockedHeadersToStates.put("", null);
    blockedHeadersToStates.put("true", ExecutorState.BLOCKED);
    for (Entry<String, ExecutorState> executorStateEntry : statusHeadersToStates.entrySet()) {
      for (Entry<String, String> payloadEntry : payloadsToMessages.entrySet()) {
        for (Entry<String, Long> timerEntry : timerHeadersToTimers.entrySet()) {
          for (Entry<String, ExecutorState> block : blockedHeadersToStates.entrySet()) {
            Class<? extends Exception> exceptionClass = null;
            // Если нет соответствия хедеров статуса и блокировки
            if (executorStateEntry.getValue() == null && block.getValue() == null) {
              // Берем ошибку соответствующую хедеру статуса
              exceptionClass = statusHeadersToExceptions.get(executorStateEntry.getKey());
            }
            // Если нет ошибки соответствующей хедеру статуса
            if (exceptionClass == null) {
              // Берем ошибку от соответствующую хедеру таймера
              exceptionClass = timeHeadersToExceptions.get(timerEntry.getKey());
            }
            List<StompHeader> headers = new ArrayList<>();
            // Если есть статус соответствующий хедеру
            if (executorStateEntry.getKey() != null) {
              // Добавляем в хедер его ключ
              headers.add(new StompHeader("Status", executorStateEntry.getKey()));
            }
            // Если есть таймер соответствующий хедеру
            if (timerEntry.getKey() != null) {
              // Добавляем в хедер его ключ
              headers.add(new StompHeader("CustomerConfirmationTimer", timerEntry.getKey()));
            }
            // Если есть флаг соответствующий хедеру
            if (block.getKey() != null) {
              // Добавляем в хедер его ключ
              headers.add(new StompHeader("Blocked", block.getKey()));
            }
            conditions.add(new Pair<>(
                    new StompMessage("MESSAGE", headers, payloadEntry.getKey()),
                    new Expectations(
                        exceptionClass,
                        block.getValue() != null ? block.getValue() : executorStateEntry.getValue(),
                        payloadEntry.getValue(),
                        timerEntry.getValue() == null ? 0 : timerEntry.getValue()
                    )
                )
            );
          }
        }
      }
    }
    return conditions;
  }

  @Before
  public void setUp() throws Exception {
    // Дано:
    when(payloadMapper.map(conditionStompMessage)).thenReturn(expectedMessage);
    if (expectedException != null) {
      thrown.expect(expectedException);
    }
    mapper = new ExecutorStateApiMapper(payloadMapper);
  }

  /**
   * Должен запросить у маппера сообщения из пейлоада STOMP фрейма извлечь текст сообщения, если не было ошибок маппинга.
   *
   * @throws Exception ошибка
   */
  @Test
  public void shouldAskPayloadMapperToMapStompMessageToMessage() throws Exception {
    // Действие:
    mapper.map(conditionStompMessage);

    // Результат:
    if (expectedException == null) {
      verify(payloadMapper, only()).map(conditionStompMessage);
    } else {
      verifyZeroInteractions(payloadMapper);
    }
  }

  /**
   * Должен преобразовать STOMP сообщение в соответствующий результат.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingConditionsStompMessageToExpectedState() throws Exception {
    // Действие:
    ExecutorState executorState = mapper.map(conditionStompMessage);

    // Результат:
    assertEquals(expectedExecutorState, executorState);
    assertEquals(expectedMessage, executorState.getData());
    assertEquals(expectedTimer, executorState.getCustomerTimer());
  }

  private static class Expectations {

    final Class<? extends Exception> exception;
    final ExecutorState executorState;
    final String message;
    final long timer;

    Expectations(Class<? extends Exception> exception, ExecutorState executorState, String message,
        long timer) {
      this.exception = exception;
      this.executorState = executorState;
      this.message = message;
      this.timer = timer;
    }
  }
}