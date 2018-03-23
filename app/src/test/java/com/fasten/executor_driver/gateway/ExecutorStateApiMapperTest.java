package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.entity.ExecutorState;
import org.junit.Before;
import org.junit.Test;

public class ExecutorStateApiMapperTest {

  private Mapper<String, ExecutorState> mapper;

  @Before
  public void setUp() throws Exception {
    mapper = new ExecutorStateApiMapper();
  }

  /**
   * Должен успешно преобразовать строку в статус "смена закрыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStringToShiftClosed() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map("SHIFT_CLOSED");

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
  }

  /**
   * Должен успешно преобразовать строку с кавычками в статус "смена закрыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedStringToShiftOpened() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map("\"SHIFT_CLOSED\"");

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_CLOSED);
  }

  /**
   * Должен успешно преобразовать строку в статус "смена открыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStringToShiftOpened() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map("SHIFT_OPENED");

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен успешно преобразовать строку с кавычками в статус "смена открыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedStringToShiftClosed() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map("\"SHIFT_OPENED\"");

    // Результат:
    assertEquals(executorState, ExecutorState.SHIFT_OPENED);
  }

  /**
   * Должен успешно преобразовать строку в статус "онлайн".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStringToOnline() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map("ONLINE");

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
  }

  /**
   * Должен успешно преобразовать строку с кавычками в статус "онлайн".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQuotedStringToOnline() throws Exception {
    // Дано и Действие:
    ExecutorState executorState = mapper.map("\"ONLINE\"");

    // Результат:
    assertEquals(executorState, ExecutorState.ONLINE);
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyStringFail() throws Exception {
    // Дано и Действие:
    mapper.map("");
  }

  /**
   * Должен дать ошибку, если значение неверное.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNonexistentFail() throws Exception {
    // Дано и Действие:
    mapper.map("SHIFT");
  }

  /**
   * Должен дать ошибку, если значение с кавычками неверное.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingQuotedNonexistentFail() throws Exception {
    // Дано и Действие:
    mapper.map("\"SHIFT\"");
  }
}