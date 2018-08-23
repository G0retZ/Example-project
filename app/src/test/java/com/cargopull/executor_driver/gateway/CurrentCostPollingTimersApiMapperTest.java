package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.utils.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingTimersApiMapperTest {

  @Rule
  public final ApiOrderTimersRule rule = new ApiOrderTimersRule();

  private Mapper<StompMessage, Pair<Long, Long>> mapper;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    mapper = new CurrentCostPollingTimersApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn(rule.getApiOrderTimers(423_401L, 93_212L));

    // Действие:
    Pair<Long, Long> pair = mapper.map(stompMessage);

    // Результат:
    assertEquals(pair.first, new Long(423_401L));
    assertEquals(pair.second, new Long(93_212L));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде без таймера пакета в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithoutOrderTimerToOrderSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn(rule.getApiOrderTimers(null, 93_212L));

    // Действие:
    Pair<Long, Long> pair = mapper.map(stompMessage);

    // Результат:
    assertEquals(pair.first, new Long(0L));
    assertEquals(pair.second, new Long(93_212L));
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде без периода.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutOrderPeriodFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn(rule.getApiOrderTimers(423_401L, null));

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде с малым периодом пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithSmallOrderPeriodFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn(rule.getApiOrderTimers(423_401L, 14_999L));

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде с отрицательным таймером пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithNegativeOrderTimerFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn(rule.getApiOrderTimers(-1L, 93_212L));

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде с отрицательным периодом пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithNegativeOrderPeriodFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn(rule.getApiOrderTimers(423_401L, -1L));

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде пустая строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("dasie");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("12");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("[]");

    // Действие:
    mapper.map(stompMessage);
  }
}