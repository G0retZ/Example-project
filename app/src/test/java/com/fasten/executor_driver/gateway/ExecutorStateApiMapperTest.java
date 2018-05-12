package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasten.executor_driver.entity.ExecutorState;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompMessage;

public class ExecutorStateApiMapperTest {

  private Mapper<StompMessage, ExecutorState> mapper;

  @Before
  public void setUp() {
    mapper = new ExecutorStateApiMapper();
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "смена закрыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToShiftClosed() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "SHIFT_CLOSED")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "смена закрыта" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToShiftClosedWithData() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "SHIFT_CLOSED")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "смена открыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToShiftOpened() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "SHIFT_OPENED")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "смена открыта" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToShiftOpenedWithData() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "SHIFT_OPENED")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "онлайн".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToOnline() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "ONLINE")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "онлайн" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToOnlineWithData() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "ONLINE")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "подтверждение заказа".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToDriverOrderConfirmation() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "DRIVER_ORDER_CONFIRMATION")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.DRIVER_ORDER_CONFIRMATION);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "подтверждение заказа" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToDriverOrderConfirmationWithData() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "DRIVER_ORDER_CONFIRMATION")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.DRIVER_ORDER_CONFIRMATION);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "ожидание подтверждения клиента".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToClientOrderConfirmation() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "CLIENT_ORDER_CONFIRMATION")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.CLIENT_ORDER_CONFIRMATION);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "ожидание подтверждения клиента" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToClientOrderConfirmation() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "CLIENT_ORDER_CONFIRMATION")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.CLIENT_ORDER_CONFIRMATION);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен дать ошибку, если нужных хедеров нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", null, null));
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "на пути к клиенту".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToMovingToClient() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "MOVING_TO_CLIENT")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.MOVING_TO_CLIENT);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "на пути к клиенту" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToMovingToClient() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "MOVING_TO_CLIENT")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.MOVING_TO_CLIENT);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен дать ошибку, если заголовок Status null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullStatusHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", null)
        ),
        null
    ));
  }

  /**
   * Должен дать ошибку, если заголовок Status пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyStatusHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "")
        ),
        ""
    ));
  }

  /**
   * Должен дать ошибку, если значение заголовка Status неверное.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNonexistentForStatusHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "SHIFT")
        ),
        null
    ));
  }
}