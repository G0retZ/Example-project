package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.ExecutorBalance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBalanceApiMapperTest {

  private Mapper<StompMessage, ExecutorBalance> mapper;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    mapper = new ExecutorBalanceApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщенияв баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToBalanceSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{\"mainAccount\":1,\"bonusAccount\":2,\"nonCashAccount\":3}");

    // Действие:
    ExecutorBalance executorBalance = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 1);
    assertEquals(executorBalance.getBonusAccount(), 2);
    assertEquals(executorBalance.getCashlessAccount(), 3);
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщениябез основного счета в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutMainAccountToBalanceSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("{\"bonusAccount\":2,\"nonCashAccount\":3}");

    // Действие:
    ExecutorBalance executorBalance = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 0);
    assertEquals(executorBalance.getBonusAccount(), 2);
    assertEquals(executorBalance.getCashlessAccount(), 3);
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщениябез бонусного счета в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutBonusAccountToBalanceSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("{\"mainAccount\":1,\"nonCashAccount\":3}");

    // Действие:
    ExecutorBalance executorBalance = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 1);
    assertEquals(executorBalance.getBonusAccount(), 0);
    assertEquals(executorBalance.getCashlessAccount(), 3);
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщениябез безналичного счета в баланс.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutCashlessAccountToBalanceSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("{\"mainAccount\":1,\"bonusAccount\":2}");

    // Действие:
    ExecutorBalance executorBalance = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorBalance.getMainAccount(), 1);
    assertEquals(executorBalance.getBonusAccount(), 2);
    assertEquals(executorBalance.getCashlessAccount(), 0);
  }

  /**
   * Должен дать ошибку, если пришел JSON в пейлоаде сообщенияс текстом в основном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithMainAccountStringToBalanceFail() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{\"mainAccount\":\"a\",\"bonusAccount\":2,\"nonCashAccount\":3}");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если пришел JSON в пейлоаде сообщенияс текстом в бонусном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithBonusAccountStringToBalanceFail() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{\"mainAccount\":1,\"bonusAccount\":\"b\",\"nonCashAccount\":3}");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если пришел JSON в пейлоаде сообщенияс текстом в безналичном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithCashlessAccountStringToBalanceFail() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{\"mainAccount\":1,\"bonusAccount\":2,\"nonCashAccount\":\"c\"}");

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