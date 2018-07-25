package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import org.junit.Before;
import org.junit.Test;

public class OptionApiMapperTest {

  private Mapper<ApiOptionItem, Option> mapper;

  @Before
  public void setUp() {
    mapper = new VehicleOptionApiMapper();
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект с пределами в неизменяемый
   * двоичный параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", false, false,
        "true", -5, 123);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertFalse(option.isVariable());
    assertEquals(option.getValue(), true);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект без опсания в неизменяемый
   * двоичный параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithoutDescriptionSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", null, false, false,
        "true", -5, 123);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertFalse(option.isVariable());
    assertEquals(option.getValue(), true);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект без пределов в неизменяемый
   * двоичный параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithoutLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(454, "name", "description", false, false,
        "false", null, null);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 454);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertFalse(option.isVariable());
    assertEquals(option.getValue(), false);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект с пределами в изменяемый
   * двоичный параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", "description", false, true,
        "false", 50, 300);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertTrue(option.isVariable());
    assertEquals(option.getValue(), false);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект без описания в изменяемый
   * двоичный параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithoutDescriptionSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", null, false, true,
        "false", 50, 300);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertTrue(option.isVariable());
    assertEquals(option.getValue(), false);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект без пределов в изменяемый
   * двоичный параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithoutLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", "description", false, true,
        "true", null, null);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertTrue(option.isVariable());
    assertEquals(option.getValue(), true);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект с пределами в неизменяемый
   * числовой параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        "34", -5, 123);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertFalse(option.isVariable());
    assertEquals(option.getValue(), 34);
    assertEquals(option.getMinValue(), -5);
    assertEquals(option.getMaxValue(), 123);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект без описания в неизменяемый
   * числовой параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithoutDescriptionSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", null, true, false,
        "34", -5, 123);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertFalse(option.isVariable());
    assertEquals(option.getValue(), 34);
    assertEquals(option.getMinValue(), -5);
    assertEquals(option.getMaxValue(), 123);
  }

  /**
   * Должен успешно преобразовать изменяемый числовой входной объект с пределами в изменяемый
   * числовой параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicNumericWithLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", "description", true, true,
        "54", 50, 300);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertTrue(option.isVariable());
    assertEquals(option.getValue(), 54);
    assertEquals(option.getMinValue(), 50);
    assertEquals(option.getMaxValue(), 300);
  }

  /**
   * Должен успешно преобразовать изменяемый числовой входной объект без описания в изменяемый
   * числовой параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicNumericWithoutDescriptionSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", null, true, true,
        "54", 50, 300);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertTrue(option.isVariable());
    assertEquals(option.getValue(), 54);
    assertEquals(option.getMinValue(), 50);
    assertEquals(option.getMaxValue(), 300);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект без пределов в неизменяемый
   * числовой параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithoutLimitsSuccess() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(454, "name", "description", true, false,
        "-345", null, null);

    // Действие:
    Option option = mapper.map(apiOptionItem);

    // Результат:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 454);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertFalse(option.isVariable());
    assertEquals(option.getValue(), -345);
    assertEquals(option.getMaxValue(), 0);
    assertEquals(option.getMinValue(), 0);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект имеет некорректные пределы.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithWrongLimitsFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", 50, -300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без  минимального предела.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutMinLimitFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", null, 200);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без максимального предела.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutMaxLimitFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", 5, null);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без пределов.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutLimitsFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", null, null);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый двоичный входной объект содержит не распознаваемые
   * данные.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticBooleanWithJunkFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", false, false,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый двоичный входной объект содержит не распознаваемые данные.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicBooleanWithJunkFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", false, true,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый числовой входной объект содержит не распознаваемые
   * данные.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticNumericWithJunkFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит не распознаваемые данные.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithJunkFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый числовой входной объект содержит дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticNumericWithFloatFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        "3.2", 50, 300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithFloatFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "3.2", 50, 300);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если имя опции - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionNameFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, null, "description", true, false,
        "34s5", -5, 123);

    // Действие:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если значение - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutValueFail() throws Exception {
    // Дано:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        null, -5, 123);

    // Действие:
    mapper.map(apiOptionItem);
  }
}