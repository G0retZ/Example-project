package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.cargopull.executor_driver.entity.ExecutorState;
import java.util.Arrays;
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
    assertEquals(executorState.getCustomerTimer(), 0);
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
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "смена закрыта" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToShiftClosedWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "SHIFT_CLOSED"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "смена закрыта" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToShiftClosedWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "SHIFT_CLOSED"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
    assertEquals(executorState.getCustomerTimer(), 1345);
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
    assertEquals(executorState.getCustomerTimer(), 0);
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
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "смена открыта" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToShiftOpenedWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "SHIFT_OPENED"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "смена открыта" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToShiftOpenedWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "SHIFT_OPENED"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
    assertEquals(executorState.getCustomerTimer(), 1345);
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
    assertEquals(executorState.getCustomerTimer(), 0);
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
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "онлайн" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToOnlineWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "ONLINE"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "онлайн" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToOnlineWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "ONLINE"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
    assertEquals(executorState.getCustomerTimer(), 1345);
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
    assertEquals(executorState.getCustomerTimer(), 0);
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
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "подтверждение заказа" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToDriverOrderConfirmationWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "DRIVER_ORDER_CONFIRMATION"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.DRIVER_ORDER_CONFIRMATION);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "подтверждение заказа" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToDriverOrderConfirmationWithDataAndTimer()
      throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "DRIVER_ORDER_CONFIRMATION"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.DRIVER_ORDER_CONFIRMATION);
    assertEquals(executorState.getCustomerTimer(), 1345);
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
    assertEquals(executorState.getCustomerTimer(), 0);
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
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "ожидание подтверждения клиента" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToClientOrderConfirmationWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "CLIENT_ORDER_CONFIRMATION"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.CLIENT_ORDER_CONFIRMATION);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "ожидание подтверждения клиента" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToClientOrderConfirmationWithDataAndTimer()
      throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "CLIENT_ORDER_CONFIRMATION"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.CLIENT_ORDER_CONFIRMATION);
    assertEquals(executorState.getCustomerTimer(), 1345);
    assertEquals(executorState.getData(), "\npayload");
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
    assertEquals(executorState.getCustomerTimer(), 0);
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
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "на пути к клиенту" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToMovingToClientWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "MOVING_TO_CLIENT"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.MOVING_TO_CLIENT);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "на пути к клиенту" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToMovingToClientWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "MOVING_TO_CLIENT"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.MOVING_TO_CLIENT);
    assertEquals(executorState.getCustomerTimer(), 1345);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "ожидание клиента".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToWaitingForClient() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "WAITING_FOR_CLIENT")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.WAITING_FOR_CLIENT);
    assertEquals(executorState.getCustomerTimer(), 0);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "ожидание клиента" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToWaitingForClient() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "WAITING_FOR_CLIENT")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.WAITING_FOR_CLIENT);
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "ожидание клиента" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToWaitingForClientWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "WAITING_FOR_CLIENT"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.WAITING_FOR_CLIENT);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "ожидание клиента" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToWaitingForClientWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "WAITING_FOR_CLIENT"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.WAITING_FOR_CLIENT);
    assertEquals(executorState.getCustomerTimer(), 1345);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "выполнение заказа".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToOrderFulfillment() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "ORDER_FULFILLMENT")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_FULFILLMENT);
    assertEquals(executorState.getCustomerTimer(), 0);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "выполнение заказа" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToOrderFulfillment() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "ORDER_FULFILLMENT")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_FULFILLMENT);
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "выполнение заказа" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToOrderFulfillmentWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "ORDER_FULFILLMENT"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_FULFILLMENT);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "выполнение заказа" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToOrderFulfillmentWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "ORDER_FULFILLMENT"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.ORDER_FULFILLMENT);
    assertEquals(executorState.getCustomerTimer(), 1345);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строку из хедера в статус "прием оплаты".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToPaymentAcceptance() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "PAYMENT_ACCEPTANCE")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.PAYMENT_ACCEPTANCE);
    assertEquals(executorState.getCustomerTimer(), 0);
    assertNull(executorState.getData());
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "прием оплаты" с данными.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderWithPayloadToPaymentAcceptance() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Collections.singletonList(
            new StompHeader("Status", "PAYMENT_ACCEPTANCE")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.PAYMENT_ACCEPTANCE);
    assertEquals(executorState.getCustomerTimer(), 0);
    assertEquals(executorState.getData(), "\npayload");
  }

  /**
   * Должен успешно преобразовать строки из хедеров в статус "прием оплаты" с не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersToOrderPaymentAcceptanceWithTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "PAYMENT_ACCEPTANCE"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        null
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.PAYMENT_ACCEPTANCE);
    assertNull(executorState.getData());
    assertEquals(executorState.getCustomerTimer(), 1345);
  }

  /**
   * Должен успешно преобразовать строку из хедера и тело в статус "прием оплаты" с данными и не-нулевым таймером.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeadersWithPayloadToPaymentAcceptanceWithDataAndTimer() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "PAYMENT_ACCEPTANCE"),
            new StompHeader("CustomerConfirmationTimer", "1345")
        ),
        "\npayload"
    ));

    // Результат:
    assertEquals(executorState, ExecutorState.PAYMENT_ACCEPTANCE);
    assertEquals(executorState.getCustomerTimer(), 1345);
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

  /**
   * Должен дать ошибку, если значение заголовка CustomerConfirmationTimer пустое.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyStringForCustomerConfirmationTimerHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "WAITING_FOR_CLIENT"),
            new StompHeader("CustomerConfirmationTimer", "")
        ),
        null
    ));
  }

  /**
   * Должен дать ошибку, если значение заголовка CustomerConfirmationTimer не числовое.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotNumberStringForCustomerConfirmationTimerHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage(
        "MESSAGE",
        Arrays.asList(
            new StompHeader("Status", "ORDER_FULFILLMENT"),
            new StompHeader("CustomerConfirmationTimer", "jdi1293")
        ),
        null
    ));
  }
}