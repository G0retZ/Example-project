package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private DataReceiver<Vehicle> vehicleChoiceReceiver;

  @Mock
  private LastUsedVehicleGateway lastUsedVehicleGateway;

  @Mock
  private VehiclesAndOptionsGateway vehiclesAndOptionsGateway;

  @Before
  public void setUp() {
    vehicleOptionsUseCase = new VehicleOptionsUseCaseImpl(gateway, vehicleChoiceReceiver,
        lastUsedVehicleGateway, vehiclesAndOptionsGateway);
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList())).thenReturn(Completable.never());
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.never());
    when(lastUsedVehicleGateway.saveLastUsedVehicleId(any())).thenReturn(Completable.never());
    when(vehiclesAndOptionsGateway.getExecutorOptions()).thenReturn(Single.never());
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Должен запросить у публикатора выбранную ТС.
   */
  @Test
  public void askDataSharerForSelectedVehicle() {
    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    verify(vehicleChoiceReceiver, only()).get();
  }

  /**
   * Не должен трогать публикатор выбранного ТС.
   */
  @Test
  public void doNotTouchDataSharer() {
    // Действие:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), new ArrayList<>()
    ).test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceReceiver);
  }

  /* Проверяем работу с гейтвеем ТС и поций */

  /**
   * Должен запросить у гейтвея ТС и опций опции исполнителя.
   */
  @Test
  public void askVehiclesAndOptionsGatewayForVehicles() {
    // Действие:
    vehicleOptionsUseCase.getDriverOptions().test();

    // Результат:
    verify(vehiclesAndOptionsGateway, only()).getExecutorOptions();
  }

  /**
   * Не должен трогать гейтвеем ТС и опций.
   */
  @Test
  public void doNotTouchVehiclesAndOptionsGateway() {
    // Действие:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), new ArrayList<>()
    ).test();

    // Результат:
    verifyZeroInteractions(vehiclesAndOptionsGateway);
  }

  /* Проверяем ответы на запрос выбранного ТС */

  /**
   * Должен ответить успехом и только динамическими опциями по первому варианту сначала.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithSelectedVehicleOptionsList() {
    // Дано:
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    vehicles.add(0, new Vehicle(13, "manufacturers", "model4", "carrots", "licensee", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", false, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false, false),
        new OptionBoolean(3, "name3", "desc3", true, true)
    );
    vehicles.add(0, new Vehicle(11, "manufacturer2", "models", "colors", "lic", true));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", true, true)
    );
    vehicles.add(0, new Vehicle(12, "manufacturer", "model", "color", "license", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.fromIterable(vehicles));

    // Действие и Результат:
    vehicleOptionsUseCase.getVehicleOptions().test().assertValues(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false),
            new OptionBoolean(3, "name3", "desc3", true, true)
        )),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
            new OptionBoolean(3, "name3", "desc3", true, true)
        ))
    );
  }

  /**
   * Должен ответить пустым списком доступных для изменений опций ТС сначала.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerNoVehicleOptionsAvailableError() {
    // Дано:
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    vehicles.add(0, new Vehicle(11, "manufacturer2", "models", "colors", "lic", true));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", true, true)
    );
    vehicles.add(0, new Vehicle(12, "manufacturer", "model", "color", "license", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    vehicles.add(0, new Vehicle(13, "manufacturers", "model4", "carrots", "licensee", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", false, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.fromIterable(vehicles));

    // Действие
    TestObserver<List<Option>> testObserver =
        vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    testObserver.assertValues(
        new ArrayList<>(),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false),
            new OptionBoolean(3, "name3", "desc3", true, true)
        ))
    );
  }

  /* Проверяем ответы на запрос опций исполнителя */

  /**
   * Должен ответить успехом и только динамическими опциями.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithExecutorOptionsList() {
    // Дано:
    when(vehiclesAndOptionsGateway.getExecutorOptions()).thenReturn(Single.just(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false),
            new OptionBoolean(3, "name3", "desc3", false, true)
        )
    ));

    // Действие и Результат:
    vehicleOptionsUseCase.getDriverOptions().test().assertValues(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        ))
    );
  }

  /**
   * Должен ответить пустым списком доступных для изменений опций ТС.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerNoExecutorOptionsAvailableError() {
    // Дано:
    when(vehiclesAndOptionsGateway.getExecutorOptions()).thenReturn(Single.just(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", false, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false, false),
            new OptionBoolean(3, "name3", "desc3", false, true)
        )
    ));

    // Действие
    TestObserver<List<Option>> testObserver = vehicleOptionsUseCase.getDriverOptions().test();

    // Результат:
    testObserver.assertValues(
        new ArrayList<>()
    );
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей.
   */
  @Test
  public void doNotTouchGateway() {
    // Действие:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), new ArrayList<>()
    ).test();

    // Результат:
    verifyZeroInteractions(gateway);
  }

  /**
   * Должен передать гейтвею опции исполнителя и ТС с установленными опциями.
   */
  @Test
  public void askGatewayToSetVehicleAndDriverOptions() {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )
    ).test();

    // Результат:
    vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.setOptions(
        new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
        new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
        new OptionBoolean(2, "name2", "desc2", true, false)
    );
    verify(gateway, only()).sendVehicleOptions(vehicle, Arrays.asList(
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false)
    ));
  }

  /* Проверяем работу с гейтвеем последней использованной ТС */

  /**
   * Должен запросить гейтвей сохраненить последнее использование ТС после успеха сохранения опций.
   */
  @Test
  public void askLastUseVehicleGatewayToSaveLastUsedVehicleIdIfSuccess() {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )
    ).test();

    // Результат:
    verify(lastUsedVehicleGateway, only()).saveLastUsedVehicleId(vehicle);
  }

  /* Проверяем ответы на передачу опций ТС для выхода на линию */

  /**
   * Должет ответить ошибкой преобразования данных.
   */
  @Test
  public void answerDataMappingError() {
    // Действие и Результат:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", true, false)
        )), new ArrayList<>()
    ).test().assertError(DataMappingException.class);
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", true, false)
        ), new ArrayList<>()
    ).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой аргумента.
   */
  @Test
  public void answerArgumentError() {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.complete());
    when(lastUsedVehicleGateway.saveLastUsedVehicleId(any()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", true, false)
        ), new ArrayList<>()
    ).test().assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSetSelectedVehicleOptionsSuccessful() {
    // Дано:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", true, false),
        new OptionBoolean(3, "name3", "desc3", false, true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.complete());
    when(lastUsedVehicleGateway.saveLastUsedVehicleId(any())).thenReturn(Completable.complete());

    // Действие:
    vehicleOptionsUseCase.getVehicleOptions().test();

    // Результат:
    vehicleOptionsUseCase.setSelectedVehicleAndOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", true, 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", true, -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", true, false)
        ), new ArrayList<>()
    ).test().assertComplete();
  }
}