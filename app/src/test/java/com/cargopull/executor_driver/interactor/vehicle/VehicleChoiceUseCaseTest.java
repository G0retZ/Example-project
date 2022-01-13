package com.cargopull.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataUpdateUseCase;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Single;

@RunWith(MockitoJUnitRunner.class)
public class VehicleChoiceUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private VehicleChoiceUseCase useCase;

  @Mock
  private VehiclesAndOptionsGateway gateway;
  @Mock
  private DataUpdateUseCase<Vehicle> vehicleChoiceObserver;

  @Before
  public void setUp() {
    useCase = new VehicleChoiceUseCaseImpl(gateway, vehicleChoiceObserver);
    when(gateway.getExecutorVehicles()).thenReturn(Single.never());
  }

  /* Проверяем работу с публикатором списка ТС */

  /**
   * Должен запросить у публикатора список ТС.
   */
  @Test
  public void askVehiclesDataSharerForVehicles() {
    // Действие:
    useCase.getVehicles().test().isDisposed();

    // Результат:
    verify(gateway, only()).getExecutorVehicles();
  }

  /* Проверяем ответы на запрос списка ТС */

  /**
   * Должен ответить ошибкой блокировки водителя.
   */
  @Test
  public void answerNoNetworkError() {
    // Дано:
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new DriverBlockedException()));

    // Действие и Результат:
    useCase.getVehicles().test().assertError(DriverBlockedException.class);
  }

  /**
   * Должен ответить успехом и без искажений.
   */
  @Test
  public void answerWithVehiclesList() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    useCase.getVehicles().test().assertValue(
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
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.just(new ArrayList<>()));

    // Действие и Результат:
    useCase.getVehicles().test().assertError(EmptyListException.class);
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Не должен трогать публикатор.
   */
  @Test
  public void doNotTouchVehicleChoiceDataSharer() {
    // Действие:
    useCase.getVehicles().test().isDisposed();
    when(gateway.getExecutorVehicles())
        .thenReturn(Single.error(new NoNetworkException()));
    useCase.getVehicles().test().isDisposed();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));
    useCase.getVehicles().test().isDisposed();
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true)
        ))
    ));
    useCase.getVehicles().test().isDisposed();

    // Результат:
    verifyNoInteractions(vehicleChoiceObserver);
  }

  /**
   * Должен опубликовать выбранную ТС.
   */
  @Test
  public void askVehicleChoiceDataSharerToShareTheSelectedVehicle() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    useCase.getVehicles().test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false)
    ).test().isDisposed();

    // Результат:
    verify(vehicleChoiceObserver, only())
        .updateWith(new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false));
  }

  /**
   * Не должен трогать публикатор, если выбор неверный.
   */
  @Test
  public void doNotTouchVehicleChoiceDataSharerIfSelectionInvalid() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие:
    useCase.getVehicles().test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(11, "manufacturer", "model", "color", "license", false)
    ).test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true)
    ).test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    ).test().isDisposed();

    // Результат:
    verifyNoInteractions(vehicleChoiceObserver);
  }

  /* Проверяем ответы на публикацию */

  /**
   * Должен ответить ошибкой, если выбраного ТС нет в списке.
   */
  @Test
  public void answerOutOfBoundsError() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    useCase.getVehicles().test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(11, "manufacturer", "model", "color", "license", false)
    ).test().assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен ответить ошибкой, если выбрано занятое ТС или его нет в списке.
   */
  @Test
  public void answerArgumentError() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    useCase.getVehicles().test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true)
    ).test().assertError(IllegalArgumentException.class);
    useCase.selectVehicle(
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    ).test().assertError(IllegalArgumentException.class);
  }

  /**
   * Должен ответить успехом.
   */
  @Test
  public void answerSuccess() {
    // Дано:
    when(gateway.getExecutorVehicles()).thenReturn(Single.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    useCase.getVehicles().test().isDisposed();
    useCase.selectVehicle(
        new Vehicle(12, "manufacturer", "model", "color", "license", false)
    ).test().assertComplete();
    useCase.selectVehicle(
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false)
    ).test().assertComplete();
  }
}