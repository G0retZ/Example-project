package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.incoming.ApiParam;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOption;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItemLimits;
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
   * Должен успешно преобразовать входной объект
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingSuccess() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        "plate",
        new ApiParam("color"),
        false,
        Arrays.asList(
            new ApiVehicleOptionItem(324, "345",
                new ApiVehicleOptionItemLimits(-5, 123),
                new ApiVehicleOption("option0", false, true)),
            new ApiVehicleOptionItem(32, "34",
                null,
                new ApiVehicleOption("option1", false, true)),
            new ApiVehicleOptionItem(31, "1",
                new ApiVehicleOptionItemLimits(50, 2100),
                new ApiVehicleOption("option2", true, true)),
            new ApiVehicleOptionItem(523, "false",
                null,
                new ApiVehicleOption("option3", true, false)),
            new ApiVehicleOptionItem(42, "true",
                null,
                new ApiVehicleOption("option4", false, false))
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
   * Должен успешно преобразовать входной объект без опций
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingWithEmptyOptionsSuccess() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        "plate",
        new ApiParam("color"),
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
   * Должен дать ошибку, если марка - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutMarkFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        null,
        new ApiParam("model"),
        "plate",
        new ApiParam("color"),
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если имя марки - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutMarkNameFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam(null),
        new ApiParam("model"),
        "plate",
        new ApiParam("color"),
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если модель - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutModelFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        null,
        "plate",
        new ApiParam("color"),
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если имя модели - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutModelNameFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam(null),
        "plate",
        new ApiParam("color"),
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если гос номер - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutPlateFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        null,
        new ApiParam("color"),
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если цвет - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutColorFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        "plate",
        null,
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если имя цвета - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutColorNameFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        "plate",
        new ApiParam(null),
        false,
        new ArrayList<>()
    );

    // Действие:
    mapper.map(apiVehicle);
  }

  /**
   * Должен дать ошибку, если опции - нуль
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutOptionsFail() throws Exception {
    // Дано:
    ApiVehicle apiVehicle = new ApiVehicle(
        2190,
        new ApiParam("mark"),
        new ApiParam("model"),
        "plate",
        new ApiParam("color"),
        false,
        null
    );

    // Действие:
    mapper.map(apiVehicle);
  }
}