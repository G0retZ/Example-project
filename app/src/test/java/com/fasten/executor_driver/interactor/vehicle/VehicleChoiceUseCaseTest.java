package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataReceiver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleChoiceUseCaseTest {

  private VehicleChoiceUseCase vehicleChoiceUseCase;

  @Mock
  private DataReceiver<List<Vehicle>> listDataReceiver;

  @Mock
  private Observer<Vehicle> vehicleChoiceObserver;

  @Before
  public void setUp() {
    vehicleChoiceUseCase = new VehicleChoiceUseCaseImpl(listDataReceiver, vehicleChoiceObserver);
    when(listDataReceiver.get()).thenReturn(Observable.never());
  }

  /* Проверяем работу с публикатором списка ТС */

  /**
   * Должен запросить у публикатора список ТС.
   */
  @Test
  public void askVehiclesDataSharerForVehicles() {
    // Действие:
    vehicleChoiceUseCase.getVehicles().test();

    // Результат:
    verify(listDataReceiver, only()).get();
  }

  /* Проверяем ответы на запрос списка ТС */

  /**
   * Должен ответить ошибкой недостаточности средств.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.error(new InsufficientCreditsException()));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test().assertError(InsufficientCreditsException.class);
  }

  /**
   * Должен ответить успехом и без искажений.
   */
  @Test
  public void answerWithVehiclesList() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test().assertValue(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    );
  }

  /**
   * Должен ответить ошибкой об отсутствия доступных ТС.
   */
  @Test
  public void answerNoVehiclesAvailableError() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(new ArrayList<>()));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test().assertError(NoVehiclesAvailableException.class);
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Не должен трогать публикатор.
   */
  @Test
  public void doNotTouchVehicleChoiceDataSharer() {
    // Действие:
    vehicleChoiceUseCase.getVehicles().test();
    when(listDataReceiver.get()).thenReturn(Observable.error(new NoNetworkException()));
    vehicleChoiceUseCase.getVehicles().test();
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));
    vehicleChoiceUseCase.getVehicles().test();
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true)
        ))
    ));
    vehicleChoiceUseCase.getVehicles().test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceObserver);
  }

  /**
   * Должен опубликовать выбранную ТС.
   */
  @Test
  public void askVehicleChoiceDataSharerToShareTheSelectedVehicle() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false)
    ).test();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .onNext(new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false));
  }

  /**
   * Не должен трогать публикатор, если выбор неверный.
   */
  @Test
  public void doNotTouchVehicleChoiceDataSharerIfSelectionInvalid() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(11, "manufacturer", "model", "color", "license", false)
    ).test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true)
    ).test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    ).test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceObserver);
  }

  /* Проверяем ответы на публикацию */

  /**
   * Должен ответить ошибкой, если выбраного ТС нет в списке.
   */
  @Test
  public void answerOutOfBoundsError() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(11, "manufacturer", "model", "color", "license", false)
    ).test().assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен ответить ошибкой, если выбрано занятое ТС или его нет в списке.
   */
  @Test
  public void answerArgumentError() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true)
    ).test().assertError(IllegalArgumentException.class);
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    ).test().assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccess() {
    // Дано:
    when(listDataReceiver.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(12, "manufacturer", "model", "color", "license", false)
    ).test().assertComplete();
    vehicleChoiceUseCase.selectVehicle(
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false)
    ).test().assertComplete();
  }
}