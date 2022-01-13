package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
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
   * Должен успешно преобразовать неизменяемый двоичный входной объект с пределами в двоичный
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithLimitsSuccess() throws Exception {
    // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", false, false,
        "true", -5, 123);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertEquals(option.getValue(), true);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект без описания в двоичный
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithoutDescriptionSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", null, false, false,
        "true", -5, 123);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertEquals(option.getValue(), true);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект без пределов в двоичный
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithoutLimitsSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(454, "name", "description", false, false,
        "false", null, null);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 454);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertEquals(option.getValue(), false);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект с пределами в двоичный
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithLimitsSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", "description", false, true,
        "false", 50, 300);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertEquals(option.getValue(), false);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект без описания в двоичный
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithoutDescriptionSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", null, false, true,
        "false", 50, 300);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertEquals(option.getValue(), false);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект без пределов в двоичный
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithoutLimitsSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", "description", false, true,
        "true", null, null);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionBoolean);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertEquals(option.getValue(), true);
    assertEquals(option.getMinValue(), false);
    assertEquals(option.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект с пределами в числовой
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithLimitsSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        "34", -5, 123);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertEquals(option.getValue(), 34);
    assertEquals(option.getMinValue(), -5);
    assertEquals(option.getMaxValue(), 123);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект без описания в числовой
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithoutDescriptionSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", null, true, false,
        "34", -5, 123);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 324);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertEquals(option.getValue(), 34);
    assertEquals(option.getMinValue(), -5);
    assertEquals(option.getMaxValue(), 123);
  }

  /**
   * Должен успешно преобразовать изменяемый числовой входной объект с пределами в числовой
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicNumericWithLimitsSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", "description", true, true,
        "54", 50, 300);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
    assertEquals(option.getValue(), 54);
    assertEquals(option.getMinValue(), 50);
    assertEquals(option.getMaxValue(), 300);
  }

  /**
   * Должен успешно преобразовать изменяемый числовой входной объект без описания в числовой
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicNumericWithoutDescriptionSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(1, "name", null, true, true,
        "54", 50, 300);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 1);
    assertEquals(option.getName(), "name");
    assertNull(option.getDescription());
    assertEquals(option.getValue(), 54);
    assertEquals(option.getMinValue(), 50);
    assertEquals(option.getMaxValue(), 300);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект без пределов в числовой
   * параметр.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithoutLimitsSuccess() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(454, "name", "description", true, false,
        "-345", null, null);

      // Action:
    Option option = mapper.map(apiOptionItem);

      // Effect:
    assertTrue(option instanceof OptionNumeric);
    assertEquals(option.getId(), 454);
    assertEquals(option.getName(), "name");
    assertEquals(option.getDescription(), "description");
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
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", 50, -300);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без  минимального предела.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutMinLimitFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", null, 200);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без максимального предела.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutMaxLimitFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", 5, null);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без пределов.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutLimitsFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "-345", null, null);

      // Action:
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
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", false, false,
        "3k2i", 50, 300);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый двоичный входной объект содержит не распознаваемые данные.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicBooleanWithJunkFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", false, true,
        "3k2i", 50, 300);

      // Action:
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
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        "3k2i", 50, 300);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит не распознаваемые данные.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithJunkFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "3k2i", 50, 300);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый числовой входной объект содержит дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticNumericWithFloatFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        "3.2", 50, 300);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит дробное число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithFloatFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, true,
        "3.2", 50, 300);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если имя опции - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionNameFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, null, "description", true, false,
        "34s5", -5, 123);

      // Action:
    mapper.map(apiOptionItem);
  }

  /**
   * Должен дать ошибку, если значение - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutValueFail() throws Exception {
      // Given:
    ApiOptionItem apiOptionItem = new ApiOptionItem(324, "name", "description", true, false,
        null, -5, 123);

      // Action:
    mapper.map(apiOptionItem);
  }
}