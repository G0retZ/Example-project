package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOption;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItemLimits;
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "true",
        new ApiVehicleOptionItemLimits(-5, 123),
        new ApiVehicleOption("name", false, false));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 324);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), true);
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(454, "false",
        null,
        new ApiVehicleOption("name", false, false));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 454);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), false);
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(1, "false",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", true, false));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 1);
    assertEquals(vehicleOption.getName(), "name");
    assertTrue(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), false);
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(1, "true",
        null,
        new ApiVehicleOption("name", true, false));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionBoolean);
    assertEquals(vehicleOption.getId(), 1);
    assertEquals(vehicleOption.getName(), "name");
    assertTrue(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), true);
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "34",
        new ApiVehicleOptionItemLimits(-5, 123),
        new ApiVehicleOption("name", false, true));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionNumeric);
    assertEquals(vehicleOption.getId(), 324);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), 34);
    assertEquals(((VehicleOptionNumeric) vehicleOption).getMinValue(), -5);
    assertEquals(((VehicleOptionNumeric) vehicleOption).getMaxValue(), 123);
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(1, "54",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", true, true));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionNumeric);
    assertEquals(vehicleOption.getId(), 1);
    assertEquals(vehicleOption.getName(), "name");
    assertTrue(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), 54);
    assertEquals(((VehicleOptionNumeric) vehicleOption).getMinValue(), 50);
    assertEquals(((VehicleOptionNumeric) vehicleOption).getMaxValue(), 300);
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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(454, "-345",
        null,
        new ApiVehicleOption("name", false, true));

    // Действие:
    VehicleOption vehicleOption = mapper.map(apiVehicleOptionItem);

    // Результат:
    assertTrue(vehicleOption instanceof VehicleOptionNumeric);
    assertEquals(vehicleOption.getId(), 454);
    assertEquals(vehicleOption.getName(), "name");
    assertFalse(vehicleOption.isVariable());
    assertEquals(vehicleOption.getValue(), -345);
    assertEquals(((VehicleOptionNumeric) vehicleOption).getMaxValue(), 0);
    assertEquals(((VehicleOptionNumeric) vehicleOption).getMinValue(), 0);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект без пределов
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingDynamicNumericWithWrongLimitsFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "-345",
        new ApiVehicleOptionItemLimits(50, -300),
        new ApiVehicleOption("name", true, true));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "-345",
        null,
        new ApiVehicleOption("name", true, true));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "3k2i",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", false, false));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "3k2i",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", true, false));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "3k2i",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", false, true));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "3k2i",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", true, true));

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если изменяемый числовой входной объект содержит дробное число
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStaticNumericWithFloatFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "3.2",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", false, true));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "3,2",
        new ApiVehicleOptionItemLimits(50, 300),
        new ApiVehicleOption("name", true, true));

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если тип опции - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionTypeFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "34s5",
        new ApiVehicleOptionItemLimits(-5, 123),
        null);

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }

  /**
   * Должен дать ошибку, если тип опции - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionTypeNameFail() throws Exception {
    // Дано:
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, "34s5",
        new ApiVehicleOptionItemLimits(-5, 123),
        new ApiVehicleOption(null, false, true));

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
    ApiVehicleOptionItem apiVehicleOptionItem = new ApiVehicleOptionItem(324, null,
        new ApiVehicleOptionItemLimits(-5, 123),
        new ApiVehicleOption("name", false, true));

    // Действие:
    mapper.map(apiVehicleOptionItem);
  }
}