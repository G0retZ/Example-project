package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
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
  private Mapper<ApiVehicleOptionItem, VehicleOption> vehicleOptionMapper;

  @Before
  public void setUp() throws Exception {
    when(vehicleOptionMapper.map(any(ApiVehicleOptionItem.class)))
        .thenReturn(new VehicleOptionBoolean(0, "n", false, false));
    mapper = new VehicleApiMapper(vehicleOptionMapper);
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
            new ApiVehicleOptionItem(324, "option0", true, false, "345", -5, 123),
            new ApiVehicleOptionItem(32, "option1", true, false, "34", null, null),
            new ApiVehicleOptionItem(31, "option2", true, true, "1", 50, 2100),
            new ApiVehicleOptionItem(523, "option3", false, true, "false", null, null),
            new ApiVehicleOptionItem(42, "option4", false, false, "true", null, null)
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
    assertEquals(vehicle.getVehicleOptions(), Arrays.<VehicleOption>asList(
        new VehicleOptionBoolean(0, "n", false, false),
        new VehicleOptionBoolean(0, "n", false, false),
        new VehicleOptionBoolean(0, "n", false, false),
        new VehicleOptionBoolean(0, "n", false, false),
        new VehicleOptionBoolean(0, "n", false, false)
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
    assertEquals(vehicle.getVehicleOptions(), new ArrayList<>());
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