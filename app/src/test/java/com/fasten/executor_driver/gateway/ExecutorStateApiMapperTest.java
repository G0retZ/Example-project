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
   * Должен успешно преобразовать тело в статус "смена закрыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayloadToShiftClosed() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\nSHIFT_CLOSED"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать тело с кавычками в статус "смена закрыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedPayloadToShiftClosed() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\n\"SHIFT_CLOSED\""
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
    assertNull(executorState.getData());
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
   * Должен успешно преобразовать тело в статус "смена открыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayloadToShiftOpened() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\nSHIFT_OPENED"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать тело с кавычками в статус "смена открыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedPayloadToShiftOpened() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\n\"SHIFT_OPENED\""
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
    assertNull(executorState.getData());
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
   * Должен успешно преобразовать тело в статус "онлайн".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayloadToOnline() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\nONLINE"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать тело с кавычками в статус "онлайн".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedPayloadToOnline() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\n\"ONLINE\""
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
    assertNull(executorState.getData());
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
   * Должен успешно преобразовать тело в статус "подтверждение заказа".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayloadToOrderConfirmation() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\nORDER_CONFIRMATION"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_CONFIRMATION);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать тело с кавычками в статус "подтверждение заказа".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedPayloadToOrderConfirmation() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\n\"ORDER_CONFIRMATION\""
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_CONFIRMATION);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "подтверждение заказа".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToOrderConfirmation() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "ORDER_CONFIRMATION")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_CONFIRMATION);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "подтверждение заказа" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToOrderConfirmationWithData() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "ORDER_CONFIRMATION")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_CONFIRMATION);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать тело в статус "на пути к точке погрузки".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayloadToApproachingLoadPoint() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\nIN_PROGRESS"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.IN_PROGRESS);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать тело с кавычками в статус "на пути к точке погрузки".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedPayloadToApproachingLoadPoint() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "\n\"IN_PROGRESS\""
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.IN_PROGRESS);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "на пути к точке погрузки".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToApproachingLoadPoint() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "IN_PROGRESS")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.IN_PROGRESS);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "на пути к точке погрузки" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToApproachingLoadPointWithData() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "IN_PROGRESS")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.IN_PROGRESS);
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
   * Должен дать ошибку, если сообщение null при заголовке Type.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullMessageForTypeHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        null
    ));
  }

  /**
   * Должен дать ошибку, если сообщение пустое при заголовке Type.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyMessageForTypeHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        ""
    ));
  }

  /**
   * Должен дать ошибку, если значение неверное при заголовке Type.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNonexistentForTypeHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Type", "Status")
        ),
        "SHIFT"
    ));
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