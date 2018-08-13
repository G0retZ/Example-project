package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiVehicle;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.Vehicle;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleApiMapperTest {

  private Mapper<ApiVehicle, Vehicle> mapper;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;

  @Before
  public void setUp() throws Exception {
    when(apiOptionMapper.map(any(ApiOptionItem.class)))
        .thenReturn(new OptionBoolean(0, "n", "d", false, false));
    mapper = new VehicleApiMapper(apiOptionMapper);
  }

  /**
   * Должен успешно преобразовать входной объект.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingSuccess() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        "mark",
        "model",
        "plate",
        "color",
        false,
        Arrays.asList(
            new ApiOptionItem(324, "option0", "description0", true, false, "345", -5, 123),
            new ApiOptionItem(32, "option1", "description1", true, false, "34", null, null),
            new ApiOptionItem(31, "option2", "description2", true, true, "1", 50, 2100),
            new ApiOptionItem(523, "option3", "description3", false, true, "false", null, null),
            new ApiOptionItem(42, "option4", "description4", false, false, "true", null, null)
        )
    );

    // Действие:
    Vehicle vehicle = mapper.map(apiVehicle);

    // Результат:
    assertEquals(vehicle.getId(), 2190);
    assertEquals(vehicle.getManufacturer(), "mark");
    assertEquals(vehicle.getModel(), "model");
    assertEquals(vehicle.getLicensePlate(), "plate");
    assertEquals(vehicle.getColor(), "color");
    assertFalse(vehicle.isBusy());
    assertEquals(vehicle.getOptions(), Arrays.<Option>asList(
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false)
    ));
  }

  /**
   * Должен успешно преобразовать входной объект без опций.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingWithEmptyOptionsSuccess() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        "mark",
        "model",
        "plate",
        "color",
        true,
        new ArrayList<>()
    );

    // Действие:
    Vehicle vehicle = mapper.map(apiVehicle);

    // Результат:
    assertEquals(vehicle.getId(), 2190);
    assertEquals(vehicle.getManufacturer(), "mark");
    assertEquals(vehicle.getModel(), "model");
    assertEquals(vehicle.getLicensePlate(), "plate");
    assertEquals(vehicle.getColor(), "color");
    assertTrue(vehicle.isBusy());
    assertEquals(vehicle.getOptions(), new ArrayList<>());
  }

  /**
   * Должен дать ошибку, если имя марки - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutMarkNameFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        null,
        "model",
        "plate",
        "color",
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если имя модели - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutModelNameFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        "mark",
        null,
        "plate",
        "color",
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если гос номер - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutPlateFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        "mark",
        "model",
        null,
        "color",
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если цвет - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutColorFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        "mark",
        "model",
        "plate",
        null,
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если опции - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionsFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        "mark",
        "model",
        "plate",
        "color",
        false,
        null
    );

    // Действие:
    mapper.map(apiVehicle);
  }
}