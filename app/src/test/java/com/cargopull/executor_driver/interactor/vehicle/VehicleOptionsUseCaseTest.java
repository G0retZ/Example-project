package com.cargopull.executor_driver.interactor.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataReceiver;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class VehicleOptionsUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private VehicleOptionsUseCase useCase;

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
    useCase = new VehicleOptionsUseCaseImpl(gateway, vehicleChoiceReceiver, lastUsedVehicleGateway,
        vehiclesAndOptionsGateway);
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
    // Action:
    useCase.getVehicleOptions().test().isDisposed();

    // Effect:
    verify(vehicleChoiceReceiver, only()).get();
  }

  /**
   * Не должен трогать публикатор выбранного ТС.
   */
  @Test
  public void doNotTouchDataSharer() {
    // Action:
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )), new ArrayList<>()
    ).test().isDisposed();

    // Effect:
    verifyNoInteractions(vehicleChoiceReceiver);
  }

  /* Проверяем работу с гейтвеем ТС и поций */

  /**
   * Должен запросить у гейтвея ТС и опций опции исполнителя.
   */
  @Test
  public void askVehiclesAndOptionsGatewayForVehicles() {
    // Action:
    useCase.getDriverOptions().test().isDisposed();

    // Effect:
    verify(vehiclesAndOptionsGateway, only()).getExecutorOptions();
  }

  /**
   * Не должен трогать гейтвеем ТС и опций.
   */
  @Test
  public void doNotTouchVehiclesAndOptionsGateway() {
    // Action:
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )), new ArrayList<>()
    ).test().isDisposed();

    // Effect:
    verifyNoInteractions(vehiclesAndOptionsGateway);
  }

  /* Проверяем ответы на запрос выбранного ТС */

  /**
   * Должен ответить успехом и опциями по первому варианту сначала.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithSelectedVehicleOptionsList() {
    // Given:
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    vehicles.add(0, new Vehicle(13, "manufacturers", "model4", "carrots", "licensee", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false)
    );
    vehicles.add(0, new Vehicle(11, "manufacturer2", "models", "colors", "lic", true));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    vehicles.add(0, new Vehicle(12, "manufacturer", "model", "color", "license", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.fromIterable(vehicles));

    // Action и Effect:
    useCase.getVehicleOptions().test().assertValues(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
            new OptionBoolean(3, "name3", "desc3", true)
        )),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false),
            new OptionBoolean(3, "name3", "desc3", true)
        )),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        ))
    );
  }

  /**
   * Должен ответить пустым списком доступных для изменений опций ТС сначала.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerNoVehicleOptionsAvailableError() {
    // Given:
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    vehicles.add(0, new Vehicle(11, "manufacturer2", "models", "colors", "lic", true));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    vehicles.add(0, new Vehicle(12, "manufacturer", "model", "color", "license", false));
    vehicles.get(0).addVehicleOptions(
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false)
    );
    vehicles.add(0, new Vehicle(13, "manufacturers", "model4", "carrots", "licensee", false));
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.fromIterable(vehicles));

    // Action
    TestObserver<List<Option>> testObserver = useCase.getVehicleOptions().test();

    // Effect:
    testObserver.assertValues(
        new ArrayList<>(),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )),
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false),
            new OptionBoolean(3, "name3", "desc3", true)
        ))
    );
  }

  /* Проверяем ответы на запрос опций исполнителя */

  /**
   * Должен ответить успехом и опциями.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerWithExecutorOptionsList() {
    // Given:
    when(vehiclesAndOptionsGateway.getExecutorOptions()).thenReturn(Single.just(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false),
            new OptionBoolean(3, "name3", "desc3", true)
        )
    ));

    // Action и Effect:
    useCase.getDriverOptions().test().assertValues(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false),
            new OptionBoolean(3, "name3", "desc3", true)
        ))
    );
  }

  /**
   * Должен ответить пустым списком доступных для изменений опций ТС.
   */
  @SuppressWarnings({"unchecked"})
  @Test
  public void answerNoExecutorOptionsAvailableError() {
    // Given:
    when(vehiclesAndOptionsGateway.getExecutorOptions()).thenReturn(Single.just(new ArrayList<>()));

    // Action
    TestObserver<List<Option>> testObserver = useCase.getDriverOptions().test();

    // Effect:
    testObserver.assertValues(new ArrayList<>());
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Не должен трогать гейтвей.
   */
  @Test
  public void doNotTouchGateway() {
    // Action:
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )), new ArrayList<>()
    ).test().isDisposed();

    // Effect:
    verifyNoInteractions(gateway);
  }

  /**
   * Должен передать гейтвею опции исполнителя и ТС с установленными опциями.
   */
  @Test
  public void askGatewayToSetVehicleAndDriverOptions() {
    // Given:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));

    // Action:
    useCase.getVehicleOptions().test().isDisposed();
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", false)
        )), Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )
    ).test().isDisposed();

    // Effect:
    vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.setOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", false)
        )
    );
    verify(gateway, only()).sendVehicleOptions(vehicle, Arrays.asList(
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false)
    ));
  }

  /* Проверяем работу с гейтвеем последней использованной ТС */

  /**
   * Должен запросить гейтвей сохраненить последнее использование ТС после успеха сохранения опций.
   */
  @Test
  public void askLastUseVehicleGatewayToSaveLastUsedVehicleIdIfSuccess() {
    // Given:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));

    // Action:
    useCase.getVehicleOptions().test().isDisposed();
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", false)
        )), Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )
    ).test().isDisposed();

    // Effect:
    verify(lastUsedVehicleGateway, only()).saveLastUsedVehicleId(vehicle);
  }

  /* Проверяем ответы на передачу опций ТС для выхода на линию */

  /**
   * Должет ответить ошибкой преобразования данных.
   */
  @Test
  public void answerDataMappingError() {
    // Action и Effect:
    useCase.setSelectedVehicleAndOptions(
        new ArrayList<>(Arrays.asList(
            new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
            new OptionBoolean(2, "name2", "desc2", false)
        )), new ArrayList<>()
    ).test().assertError(IllegalStateException.class);
  }

  /**
   * Должен ответить ошибкой сети.
   */
  @Test
  public void answerNoNetworkError() {
    // Given:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.error(NoNetworkException::new));

    // Action:
    useCase.getVehicleOptions().test().isDisposed();

    // Effect:
    useCase.setSelectedVehicleAndOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", false)
        ), new ArrayList<>()
    ).test().assertError(NoNetworkException.class);
  }

  /**
   * Должен ответить ошибкой аргумента.
   */
  @Test
  public void answerArgumentError() {
    // Given:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.complete());
    when(lastUsedVehicleGateway.saveLastUsedVehicleId(any()))
        .thenReturn(Completable.error(new IllegalArgumentException()));

    // Action:
    useCase.getVehicleOptions().test().isDisposed();

    // Effect:
    useCase.setSelectedVehicleAndOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", false)
        ), new ArrayList<>()
    ).test().assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSetSelectedVehicleOptionsSuccessful() {
    // Given:
    Vehicle vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "desc0", 10, 0, 20),
        new OptionNumeric(1, "name1", "desc1", -5, -18, 0),
        new OptionBoolean(2, "name2", "desc2", false),
        new OptionBoolean(3, "name3", "desc3", true)
    );
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.just(vehicle));
    when(gateway.sendVehicleOptions(any(Vehicle.class), anyList()))
        .thenReturn(Completable.complete());
    when(lastUsedVehicleGateway.saveLastUsedVehicleId(any())).thenReturn(Completable.complete());

    // Action:
    useCase.getVehicleOptions().test().isDisposed();

    // Effect:
    useCase.setSelectedVehicleAndOptions(
        Arrays.asList(
            new OptionNumeric(0, "name0", "desc0", 40, 0, 120),
            new OptionNumeric(1, "name1", "desc1", -50, 20, 30),
            new OptionBoolean(2, "name2", "desc2", false)
        ), new ArrayList<>()
    ).test().assertComplete();
  }
}