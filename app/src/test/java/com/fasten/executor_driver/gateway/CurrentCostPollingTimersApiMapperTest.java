package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.utils.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingTimersApiMapperTest {

  @Rule
  public final ApiOrderTimersRule rule = new ApiOrderTimersRule();

  private Mapper<String, Pair<Long, Long>> mapper;

  @Before
  public void setUp() {
    mapper = new CurrentCostPollingTimersApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getApiOrderTimers(423_401L, 93_212L));

    // Результат:
    assertEquals(pair.first, new Long(423_401L));
    assertEquals(pair.second, new Long(93_212L));
  }

  /**
   * Должен успешно преобразовать JSON без таймера пакета в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithoutOrderTimerToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getApiOrderTimers(null, 93_212L));

    // Результат:
    assertEquals(pair.first, new Long(0L));
    assertEquals(pair.second, new Long(93_212L));
  }

  /**
   * Должен дать ошибку, если JSON без периода.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutOrderPeriodFail() throws Exception {
    // Дано и Действие:
    mapper.map(rule.getApiOrderTimers(423_401L, null));
  }

  /**
   * Должен дать ошибку, если JSON с малым периодом пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithSmallOrderPeriodFail() throws Exception {
    // Дано и Действие:
    mapper.map(rule.getApiOrderTimers(423_401L, 14_999L));
  }

  /**
   * Должен дать ошибку, если JSON с отрицательным таймером пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithNegativeOrderTimerFail() throws Exception {
    // Дано и Действие:
    mapper.map(rule.getApiOrderTimers(-1L, 93_212L));
  }

  /**
   * Должен дать ошибку, если JSON с отрицательным периодом пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithNegativeOrderPeriodFail() throws Exception {
    // Дано и Действие:
    mapper.map(rule.getApiOrderTimers(423_401L, -1L));
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано и Действие:
    mapper.map("");
  }

  /**
   * Должен дать ошибку, если пришла просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано и Действие:
    mapper.map("dasie");
  }

  /**
   * Должен дать ошибку, если пришло просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано и Действие:
    mapper.map("12");
  }

  /**
   * Должен дать ошибку, если пришел массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано и Действие:
    mapper.map("[]");
  }
}