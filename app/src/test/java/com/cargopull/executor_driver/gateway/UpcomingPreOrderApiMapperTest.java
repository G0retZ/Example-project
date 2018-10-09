package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.entity.Order;
import java.util.ArrayList;
import java.util.Arrays;
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
            Arrays.asList(
                new StompHeader("OrderId", "1234567890"),
                new StompHeader("ETA", "0987654321")
            ),
            "\n"
        )
    );

    // Результат:
    assertEquals(order.getId(), 1234567890L);
    assertEquals(order.getEtaToStartPoint(), 987654321L);
  }

  /**
   * Должен дать ошибку, если хедера OrderId нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullOrderIdHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "\n"));
  }

  /**
   * Должен дать ошибку, если хедер OrderId пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyOrderIdHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "")), "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере OrderId дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFloatOrderIdHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "123.345")), "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере OrderId число больше чем long.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingLongNumberOrderIdHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "9999999999999999999999")),
        "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере OrderId не число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotANumberOrderIdHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
        Collections.singletonList(new StompHeader("OrderId", "a9876543210")),
        "\n"));
  }

  /**
   * Должен дать ошибку, если хедера ETA нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullEtaHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
            Collections.singletonList(new StompHeader("OrderId", "1234567890")),
            "\n"
        )
    );
  }

  /**
   * Должен дать ошибку, если хедер ETA пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyEtaHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
            Arrays.asList(
                new StompHeader("OrderId", "1234567890"),
                new StompHeader("ETA", "")
            ),
            "\n"
        )
    );
  }

  /**
   * Должен дать ошибку, если в хедере ETA дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFloatEtaHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
            Arrays.asList(
                new StompHeader("OrderId", "1234567890"),
                new StompHeader("ETA", "123.345")
            ),
            "\n"
        )
    );
  }

  /**
   * Должен дать ошибку, если в хедере ETA число больше чем long.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingLongNumberEtaHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
            Arrays.asList(
                new StompHeader("OrderId", "1234567890"),
                new StompHeader("ETA", "9999999999999999999999")
            ),
            "\n"
        )
    );
  }

  /**
   * Должен дать ошибку, если в хедере ETA не число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotANumberEtaHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE",
            Arrays.asList(
                new StompHeader("OrderId", "1234567890"),
                new StompHeader("ETA", "a9876543210")
            ),
            "\n"
        )
    );
  }
}