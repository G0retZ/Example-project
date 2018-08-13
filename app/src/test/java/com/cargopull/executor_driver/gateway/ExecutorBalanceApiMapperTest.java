package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.entity.ExecutorBalance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBalanceApiMapperTest {

  private Mapper<String, ExecutorBalance> mapper;

  @Before
  public void setUp() {
    mapper = new ExecutorBalanceApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToBalanceSuccess() throws Exception {
    // Дано и Действие:
    ExecutorBalance executorBalance = mapper.map(
        "{\"mainAccount\":1,\"bonusAccount\":2,\"nonCashAccount\":3}"
    );

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 1);
    assertEquals(executorBalance.getBonusAccount(), 2);
    assertEquals(executorBalance.getCashlessAccount(), 3);
  }

  /**
   * Должен успешно преобразовать JSON без основного счета в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutMainAccountToBalanceSuccess() throws Exception {
    // Дано и Действие:
    ExecutorBalance executorBalance = mapper.map(
        "{\"bonusAccount\":2,\"nonCashAccount\":3}"
    );

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 0);
    assertEquals(executorBalance.getBonusAccount(), 2);
    assertEquals(executorBalance.getCashlessAccount(), 3);
  }

  /**
   * Должен успешно преобразовать JSON без бонусного счета в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutBonusAccountToBalanceSuccess() throws Exception {
    // Дано и Действие:
    ExecutorBalance executorBalance = mapper.map(
        "{\"mainAccount\":1,\"nonCashAccount\":3}"
    );

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 1);
    assertEquals(executorBalance.getBonusAccount(), 0);
    assertEquals(executorBalance.getCashlessAccount(), 3);
  }

  /**
   * Должен успешно преобразовать JSON без безналичного счета в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCashlessAccountToBalanceSuccess() throws Exception {
    // Дано и Действие:
    ExecutorBalance executorBalance = mapper.map(
        "{\"mainAccount\":1,\"bonusAccount\":2}"
    );

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 1);
    assertEquals(executorBalance.getBonusAccount(), 2);
    assertEquals(executorBalance.getCashlessAccount(), 0);
  }

  /**
   * Должен дать ошибку, если пришел JSON с текстом в основном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithMainAccountStringToBalanceFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\"mainAccount\":\"a\",\"bonusAccount\":2,\"nonCashAccount\":3}");
  }

  /**
   * Должен дать ошибку, если пришел JSON с текстом в бонусном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithBonusAccountStringToBalanceFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\"mainAccount\":1,\"bonusAccount\":\"b\",\"nonCashAccount\":3}");
  }

  /**
   * Должен дать ошибку, если пришел JSON с текстом в безналичном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithCashlessAccountStringToBalanceFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\"mainAccount\":1,\"bonusAccount\":2,\"nonCashAccount\":\"c\"}");
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