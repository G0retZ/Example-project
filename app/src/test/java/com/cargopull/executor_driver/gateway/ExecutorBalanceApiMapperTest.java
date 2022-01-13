package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.ExecutorBalance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBalanceApiMapperTest {

  private Mapper<StompFrame, ExecutorBalance> mapper;
  @Mock
  private StompFrame stompFrame;

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
    // Given
    when(stompFrame.getBody())
        .thenReturn("{\"mainAccount\":1,\"bonusAccount\":2,\"nonCashAccount\":3}");

    // Action:
    ExecutorBalance executorBalance = mapper.map(stompFrame);

    // Effect:
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
    // Given
    when(stompFrame.getBody()).thenReturn("{\"bonusAccount\":2,\"nonCashAccount\":3}");

    // Action:
    ExecutorBalance executorBalance = mapper.map(stompFrame);

    // Effect:
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
    // Given
    when(stompFrame.getBody()).thenReturn("{\"mainAccount\":1,\"nonCashAccount\":3}");

    // Action:
    ExecutorBalance executorBalance = mapper.map(stompFrame);

    // Effect:
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
    // Given
    when(stompFrame.getBody()).thenReturn("{\"mainAccount\":1,\"bonusAccount\":2}");

    // Action:
    ExecutorBalance executorBalance = mapper.map(stompFrame);

    // Effect:
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
    // Given
    when(stompFrame.getBody())
        .thenReturn("{\"mainAccount\":\"a\",\"bonusAccount\":2,\"nonCashAccount\":3}");

    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришел JSON в пейлоаде сообщенияс текстом в бонусном счете в баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithBonusAccountStringToBalanceFail() throws Exception {
    // Given
    when(stompFrame.getBody())
        .thenReturn("{\"mainAccount\":1,\"bonusAccount\":\"b\",\"nonCashAccount\":3}");

    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если пришел JSON в пейлоаде сообщенияс текстом в безналичном счете в
   * баланс.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithCashlessAccountStringToBalanceFail() throws Exception {
    // Given
    when(stompFrame.getBody())
        .thenReturn("{\"mainAccount\":1,\"bonusAccount\":2,\"nonCashAccount\":\"c\"}");

    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде пустая строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Given
    when(stompFrame.getBody()).thenReturn("\n");

    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Given
    when(stompFrame.getBody()).thenReturn("dasie");

    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Given
    when(stompFrame.getBody()).thenReturn("12");

    // Action:
    mapper.map(stompFrame);
  }

  /**
   * Должен дать ошибку, если в пейлоаде массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Given
    when(stompFrame.getBody()).thenReturn("[]");

    // Action:
    mapper.map(stompFrame);
  }
}