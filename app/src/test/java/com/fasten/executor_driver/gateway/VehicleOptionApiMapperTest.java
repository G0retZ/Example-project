package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import org.junit.Before;
import org.junit.Test;

public class VehicleOptionApiMapperTest {

  private Mapper<ApiVehicleOptionItem, VehicleOption> mapper;

  @Before
  public void setUp() throws Exception {
    mapper = new VehicleOptionApiMapper();
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект с пределами в неизменяемый
   * двоичный параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", false, false,
        "true", -5, 123);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 324);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), true);
    assertEquals(vehicleOption.getMinValue(), false);
    assertEquals(vehicleOption.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый двоичный входной объект без пределов в неизменяемый
   * двоичный параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticBooleanWithoutLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(454, "name", false, false,
        "false", null, null);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 454);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), false);
    assertEquals(vehicleOption.getMinValue(), false);
    assertEquals(vehicleOption.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект с пределами в изменяемый
   * двоичный параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(1, "name", false, true,
        "false", 50, 300);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 1);
    assertEquals(vehicleOption.getName(), "name");
    assertTrue(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), false);
    assertEquals(vehicleOption.getMinValue(), false);
    assertEquals(vehicleOption.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать изменяемый двоичный входной объект без пределов в изменяемый
   * двоичный параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicBooleanWithoutLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(1, "name", false, true,
        "true", null, null);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 1);
    assertEquals(vehicleOption.getName(), "name");
    assertTrue(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), true);
    assertEquals(vehicleOption.getMinValue(), false);
    assertEquals(vehicleOption.getMaxValue(), true);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект с пределами в неизменяемый
   * числовой параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, false,
        "34", -5, 123);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionNumeric);
    assertEquals(vehicleOption.getId(), 324);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), 34);
    assertEquals(vehicleOption.getMinValue(), -5);
    assertEquals(vehicleOption.getMaxValue(), 123);
  }

  /**
   * Должен успешно преобразовать изменяемый числовой входной объект с пределами в изменяемый
   * числовой параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingDynamicNumericWithLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(1, "name", true, true,
        "54", 50, 300);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionNumeric);
    assertEquals(vehicleOption.getId(), 1);
    assertEquals(vehicleOption.getName(), "name");
    assertTrue(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), 54);
    assertEquals(vehicleOption.getMinValue(), 50);
    assertEquals(vehicleOption.getMaxValue(), 300);
  }

  /**
   * Должен успешно преобразовать неизменяемый числовой входной объект без пределов в неизменяемый
   * числовой параметр
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStaticNumericWithoutLimitsSuccess() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(454, "name", true, false,
        "-345", null, null);

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionNumeric);
    assertEquals(vehicleOption.getId(), 454);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), -345);
    assertEquals(vehicleOption.getMaxValue(), 0);
    assertEquals(vehicleOption.getMinValue(), 0);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект имеет некорректные пределы
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithWrongLimitsFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, true,
        "-345", 50, -300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без  минимального предела
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutMinLimitFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, true,
        "-345", null, 200);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без максимального предела
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutMaxLimitFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, true,
        "-345", 5, null);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без пределов
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithoutLimitsFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, true,
        "-345", null, null);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый двоичный входной объект содержит не распознаваемые данные
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticBooleanWithJunkFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", false, false,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый двоичный входной объект содержит не распознаваемые данные
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicBooleanWithJunkFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", false, true,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый числовой входной объект содержит не распознаваемые данные
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticNumericWithJunkFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, false,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит не распознаваемые данные
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithJunkFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, true,
        "3k2i", 50, 300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если неизменяемый числовой входной объект содержит дробное число
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticNumericWithFloatFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, false,
        "3.2", 50, 300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит дробное число
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithFloatFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, true,
        "3.2", 50, 300);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если имя опции - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionNameFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, null, true, false,
        "34s5", -5, 123);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если значение - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutValueFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "name", true, false,
        null, -5, 123);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }
}