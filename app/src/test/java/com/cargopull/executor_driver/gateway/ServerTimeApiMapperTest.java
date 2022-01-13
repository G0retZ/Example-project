package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.stomp.Command;
import com.cargopull.executor_driver.backend.stomp.StompFrame;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class ServerTimeApiMapperTest {

  private Mapper<StompFrame, Long> mapper;

  @Before
  public void setUp() {
    mapper = new ServerTimeApiMapper();
  }

  /**
   * Должен успешно преобразовать строку из хедера в число.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingPayLoadToInteger() throws Exception {
    // Given и Action:
    long excessiveCost = mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("ServerTimeStamp", "1234567890"), "\n"));

    // Effect:
    assertEquals(excessiveCost, 1234567890L);
  }

  /**
   * Должен дать ошибку, если хедера нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullPayloadFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\n"));
  }

  /**
   * Должен дать ошибку, если хедер пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyPayloadFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("ServerTimeStamp", ""), "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFloatPayloadFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("ServerTimeStamp", "123.345"), "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере число больше чем long.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingLongNumberPayloadFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("ServerTimeStamp", "9999999999999999999999"),
        "\n"));
  }

  /**
   * Должен дать ошибку, если в хедере не число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNotANumberPayloadFail() throws Exception {
    // Given и Action:
    mapper.map(new StompFrame(Command.MESSAGE,
        Collections.singletonMap("ServerTimeStamp", "a9876543210"), "\n"));
  }
}