package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.entity.Order;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompMessage;

public class UpcomingPreOrderApiMapperTest {

  private Mapper<StompMessage, Order> mapper;

  @Before
  public void setUp() {
    mapper = new UpcomingPreOrderApiMapper();
  }

  /**
   * Должен успешно преобразовать строку из хедера и пейлоада в заказ.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderAndPayLoadToOrder() throws Exception {
    // Дано и Действие:
    Order order = mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "1234567890")), "khd\ns13:40i\nhq\n"));

    // Результат:
    assertEquals(order.getId(), 1234567890L);
    assertEquals(order.getEtaToStartPoint(), 49200000L);
  }

  /**
   * Должен дать ошибку, если формат времени неверный.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingPayLoadWithWrongTimeFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "1234567890")), "khd\ns33:40i\nhq\n"));
  }

  /**
   * Должен дать ошибку, если пэйлоад пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "1234567890")), ""));
  }

  /**
   * Должен дать ошибку, если в пэйлоаде нет времени.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWrongPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "1234567890")),
        "super\npower\n"));
  }

  /**
   * Должен дать ошибку, если хедера нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "khd\ns13:40i\nhq\n"));
  }

  /**
   * Должен дать ошибку, если хедер пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "")), "khd\ns13:40i\nhq\n"));
  }

  /**
   * Должен дать ошибку, если в хедере дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFloatHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "123.345")), "khd\ns13:40i\nhq\n"));
  }

  /**
   * Должен дать ошибку, если в хедере число больше чем long.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingLongNumberHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "9999999999999999999999")),
        "khd\ns13:40i\nhq\n"));
  }

  /**
   * Должен дать ошибку, если в хедере не число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotANumberHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "a9876543210")),
        "khd\ns13:40i\nhq\n"));
  }

}