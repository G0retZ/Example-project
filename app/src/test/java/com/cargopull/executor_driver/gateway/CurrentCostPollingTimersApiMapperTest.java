package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.utils.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingTimersApiMapperTest {

  @Rule
  public final ApiOrderTimersRule rule = new ApiOrderTimersRule();

  private Mapper<StompFrame, Pair<Long, Long>> mapper;
  @Mock
  private StompFrame stompFrame;

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
    when(stompFrame.getBody()).thenReturn(rule.getApiOrderTimers(423_401L, 93_212L));

    // Действие:
    Pair<Long, Long> pair = mapper.map(stompFrame);

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
    when(stompFrame.getBody()).thenReturn(rule.getApiOrderTimers(null, 93_212L));

    // Действие:
    Pair<Long, Long> pair = mapper.map(stompFrame);

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
    when(stompFrame.getBody()).thenReturn(rule.getApiOrderTimers(423_401L, null));

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде с малым периодом пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithSmallOrderPeriodFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn(rule.getApiOrderTimers(423_401L, 14_999L));

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде с отрицательным таймером пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithNegativeOrderTimerFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn(rule.getApiOrderTimers(-1L, 93_212L));

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде с отрицательным периодом пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithNegativeOrderPeriodFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn(rule.getApiOrderTimers(423_401L, -1L));

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде пустая строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn("\n");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn("dasie");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn("12");

    // Действие:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано
    when(stompFrame.getBody()).thenReturn("[]");

    // Действие:
    mapper.map(stompFrame);
  }
}