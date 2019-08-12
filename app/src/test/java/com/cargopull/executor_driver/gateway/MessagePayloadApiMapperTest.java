package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.stomp.Command;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class MessagePayloadApiMapperTest {

  private Mapper<StompFrame, String> mapper;

  @Before
  public void setUp() {
    mapper = new MessagePayloadApiMapper();
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringSuccess() throws Exception {
    // Действие:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), ""));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь строку в 1 символ из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractOneCharStringSuccess() throws Exception {
    // Действие:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\""));

    // Результат:
    assertEquals("\"", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из двух кавычек.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutQuotesSuccess() throws Exception {
    // Действие:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\"\""));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из 1 пробела.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutSpaceSuccess() throws Exception {
    // Действие:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), " "));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из 1 переноса строки.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutNewLineSuccess() throws Exception {
    // Действие:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\n"));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "Message test super nova"));

    // Результат:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних пробелов и переносов строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "  \n Message test super nova  \n "));

    // Результат:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних кавычек.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringFromQuotesSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(
            new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\"Message test super nova\""));

    // Результат:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних кавычек пробелов и переносов строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringFromQuotesSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "\"  \n Message test super nova  \n \""));

    // Результат:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringWithQuotesSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(
            new StompFrame(Command.MESSAGE, Collections.emptyMap(), "Message \"test super\" nova"));

    // Результат:
    assertEquals("Message \"test super\" nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте, но удалив крайние.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringWithQuotesWithoutLeadingAndTrailingOnesSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "\"Message \"test super\" nova\""));

    // Результат:
    assertEquals("Message \"test super\" nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте, но удалив крайние,
   * пробелы и переносы строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringWithQuotesWithoutLeadingAndTrailingOnesSuccess()
      throws Exception {
    // Действие:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "\"  \n Message \"test super\" nova  \n \""));

    // Результат:
    assertEquals("Message \"test super\" nova", message);
  }
}