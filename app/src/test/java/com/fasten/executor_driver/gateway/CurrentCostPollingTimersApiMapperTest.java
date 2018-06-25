package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.support.v4.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CurrentCostPollingTimersApiMapperTest {

  @Rule
  public final ApiOrderRule rule = new ApiOrderRule();

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
    Pair<Long, Long> pair = mapper.map(rule.getFullOrder());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без ИД в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutIdToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutId());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без комментария в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCommentToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutComment());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без описания цены в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimationToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutEstimation());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без рассчетной стоимости в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCostToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutCost());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без таймаута в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutTimeoutToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutTimeout());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без ETA в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithoutEtaToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutEta());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без времени подтверждения в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutConfirmationTimeToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutConfirmationTime());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без времени начала в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutStartTimeToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutStartTime());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без времени таймера до начала поллинга в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithouOverPackageTimerSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutOverPackageTimer());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 0L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без времени периода поллинга в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOverPackagePeriodSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutOverPackagePeriod());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 0L);
  }

  /**
   * Должен успешно преобразовать JSON без ИД в дистанции в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutDistanceIdToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutDistanceId());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без значения дистанции в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithoutDistanceValueToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutDistanceValue());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без дистанции в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithoutDistanceToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutDistance());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON с пустым маршрутом в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithEmptyRouteToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithEmptyRoute());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без маршрута в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  public void mappingJsonStringWithoutRouteToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutRoute());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON с пустым списком опций в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithEmptyOptionsToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithEmptyOptions());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
  }

  /**
   * Должен успешно преобразовать JSON без опций в пару значений таймеров.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOptionsToOrderSuccess() throws Exception {
    // Дано и Действие:
    Pair<Long, Long> pair = mapper.map(rule.getOrderWithoutOptions());

    // Результат:
    assertNotNull(pair.first);
    assertNotNull(pair.second);
    assertEquals((long) pair.first, 423401L);
    assertEquals((long) pair.second, 93212L);
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