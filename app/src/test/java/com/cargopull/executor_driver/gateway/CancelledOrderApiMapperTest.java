package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.entity.Order;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompMessage;

public class CancelledOrderApiMapperTest {

  private Mapper<StompMessage, Order> mapper;

  @Before
  public void setUp() {
    mapper = new CancelledOrderApiMapper();
  }

  /**
   * Должен успешно преобразовать строку из хедера в число.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingHeaderToOrder() throws Exception {
    // Действие:
    Order cancelledOrder = mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("PreliminaryCancelled", "1234567890")), "\n"));

    // Результат:
    assertEquals(cancelledOrder,
        new Order(1234567890L, "a", "b", 1, "c", 2, 3, 4, 5, 6, 7, 8, 9, 10)
    );
  }

  /**
   * Должен дать ошибку, если хедера нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("PreliminaryCancelled", null)), "\n"));
  }

  /**
   * Должен дать ошибку, если пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("PreliminaryCancelled", "\n")), "\n"));
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
        Collections.singletonList(new StompHeader("PreliminaryCancelled", "123.345")), "\n"));
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
        Collections.singletonList(new StompHeader("PreliminaryCancelled", "a9876543210")), "\n"));
  }
}