package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import ua.naiksoftware.stomp.client.StompMessage;

public class ExcessiveCostApiMapperTest {

  private Mapper<StompMessage, Integer> mapper;

  @Before
  public void setUp() {
    mapper = new ExcessiveCostApiMapper();
  }

  /**
   * Должен успешно преобразовать строку из сообщения в число.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayLoadToInteger() throws Exception {
    // Дано и Действие:
    int excessiveCost = mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "\n1234567890"));

    // Результат:
    assertEquals(excessiveCost, 1234567890);
  }

  /**
   * Должен дать ошибку, если сообщения нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), null));
  }

  /**
   * Должен дать ошибку, если сообщение пустое.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "\n"));
  }

  /**
   * Должен дать ошибку, если в сообщении дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFloatPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "\n123.345"));
  }

  /**
   * Должен дать ошибку, если в сообщении число больше чем int.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingLongNumberPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "\n9876543210"));
  }

  /**
   * Должен дать ошибку, если в сообщении не число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotANumberPayloadFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("MESSAGE", new ArrayList<>(), "\na9876543210"));
  }
}