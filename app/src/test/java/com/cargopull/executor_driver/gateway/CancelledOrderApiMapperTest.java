package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.stomp.Command;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RouteType;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class CancelledOrderApiMapperTest {

  private Mapper<StompFrame, Order> mapper;

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
    // Action:
    Order cancelledOrder = mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("PreliminaryCancelled", "1234567890"), "\n"));

    // Effect:
    assertEquals(cancelledOrder,
        new Order(1234567890L, PaymentType.CASH, "a", "b", 1, "c", 2, 3, 4, 5, 6, 7, 8, 9, 10,
            RouteType.POLYGON)
    );
  }

  /**
   * Должен дать ошибку, если хедера нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullHeaderFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("PreliminaryCancelled", null), "\n"));
  }

  /**
   * Должен дать ошибку, если пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyHeaderFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("PreliminaryCancelled", "\n"), "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFloatHeaderFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("PreliminaryCancelled", "123.345"), "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере не число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotANumberHeaderFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("PreliminaryCancelled", "a9876543210"), "\n"));
  }
}