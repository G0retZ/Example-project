package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsUseCaseTest {

  private VehicleOptionsUseCase vehicleOptionsUseCase;

  @Mock
  private VehicleOptionsGateway gateway;

  @Mock
  private DataSharer<Vehicle> vehicleSharer;

  @Before
  public void setUp() throws Exception {
    vehicleOptionsUseCase = new VehicleOptionsUseCaseImpl(gateway, vehicleSharer);
    when(gateway.sendVehicleOptions(any(Vehicle.class))).thenReturn(Completable.never());
    when(vehicleSharer.get()).thenReturn(Observable.never());
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Должен запросить у публикатора выбранную ТС.
   *
   * @throws Exception error.
   */
  @Test
  public void askDataSharerForSelectedVehicle() throws Exception {
    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    verify(vehicleSharer, only()).get();
  }

  /* Проверяем ответы на запрос выбранного ТС */

  /**
   * Должен отвечать успехом и только динамическими опциями.
   *
   * @throws Exception error.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithVehiclesList() throws Exception {
    // Дано:
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    vehicles.add(0, new Vehicle(13, "manufacturers", "model4", "carrots", "licensee", false));
    vehicles.get(0).addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", false, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", false, false),
        new VehicleOptionBoolean(3, "name3", true, true)
    );
    vehicles.add(0, new Vehicle(11, "manufacturer2", "models", "colors", "lic", true));
    vehicles.get(0).addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", true, true)
    );
    vehicles.add(0, new Vehicle(12, "manufacturer", "model", "color", "license", false));
    vehicles.get(0).addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", false, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", false, true)
    );
    when(vehicleSharer.get()).thenReturn(Observable.fromIterable(vehicles));

    // Действие и Результат:
    vehicleOptionsUseCase.getVehicleOptions().test().assertValues(
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
            new VehicleOptionBoolean(2, "name2", true, false)
        )),
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
            new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
            new VehicleOptionBoolean(2, "name2", true, false),
            new VehicleOptionBoolean(3, "name3", true, true)
        )),
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(0, "name0", true, 10, 0, 20),
            new VehicleOptionBoolean(3, "name3", true, true)
        ))
    );
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей.
   *
   * @throws Exception error
   */
  @Test
  public void doNotTouchGateway() throws Exception {
    // Действие:
    vehicleOptionsUseCase.setSelectedVehicleOptions(
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
            new VehicleOptionBoolean(2, "name2", true, false)
        ))
    ).test();

    // Результат:
    verifyZeroInteractions(vehicleSharer);
  }

  /**
   * Должен передать гейтвею ТС с установленными опциями.
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayToSetVehicleOptions() throws Exception {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", false, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", false, true)
    );
    when(vehicleSharer.get()).thenReturn(Observable.just(vehicle));

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();
    vehicleOptionsUseCase.setSelectedVehicleOptions(
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(0, "name0", true, 40, 0, 120),
            new VehicleOptionNumeric(1, "name1", true, -50, 20, 30),
            new VehicleOptionBoolean(2, "name2", true, false)
        ))
    ).test();

    // Результат:
    vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.setVehicleOptions(
        new VehicleOptionNumeric(0, "name0", true, 40, 0, 120),
        new VehicleOptionNumeric(1, "name1", true, -50, 20, 30),
        new VehicleOptionBoolean(2, "name2", true, false)
    );
    verify(gateway, only()).sendVehicleOptions(vehicle);
  }

  /* Проверяем ответы на передачу опций ТС для выхода на линию */

  /**
   * Должет ответить ошибкой преобразования данных.
   *
   * @throws Exception error
   */
  @Test
  public void answerDataMappingError() throws Exception {
    // Действие и Результат:
    vehicleOptionsUseCase.setSelectedVehicleOptions(
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
            new VehicleOptionBoolean(2, "name2", true, false)
        ))
    ).test().assertError(DataMappingException.class);
  }

  /**
   * Должен ответить ошибкой сети.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", false, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", false, true)
    );
    when(vehicleSharer.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class)))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    vehicleOptionsUseCase.setSelectedVehicleOptions(
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(0, "name0", true, 40, 0, 120),
            new VehicleOptionNumeric(1, "name1", true, -50, 20, 30),
            new VehicleOptionBoolean(2, "name2", true, false)
        ))
    ).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить успехом.
   *
   * @throws Exception error
   */
  @Test
  public void answerSetSelectedVehicleOptionsSuccessful() throws Exception {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", false, 10, 0, 20),
        new VehicleOptionNumeric(1, "name1", true, -5, -18, 0),
        new VehicleOptionBoolean(2, "name2", true, false),
        new VehicleOptionBoolean(3, "name3", false, true)
    );
    when(vehicleSharer.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class))).thenReturn(Completable.complete());

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    vehicleOptionsUseCase.setSelectedVehicleOptions(
        new ArrayList<>(Arrays.asList(
            new VehicleOptionNumeric(0, "name0", true, 40, 0, 120),
            new VehicleOptionNumeric(1, "name1", true, -50, 20, 30),
            new VehicleOptionBoolean(2, "name2", true, false)
        ))
    ).test().assertComplete();
  }
}