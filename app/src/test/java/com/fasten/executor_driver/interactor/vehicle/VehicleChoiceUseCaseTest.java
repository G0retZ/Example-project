package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Observable;
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
  private DataSharer<List<Vehicle>> vehiclesSharer;

  @Mock
  private DataSharer<Vehicle> vehicleChoiceSharer;

  @Before
  public void setUp() throws Exception {
    vehicleChoiceUseCase = new VehicleChoiceUseCaseImpl(vehiclesSharer, vehicleChoiceSharer);
    when(vehiclesSharer.get()).thenReturn(Observable.never());
  }

  /* Проверяем работу с публикатором списка ТС */

  /**
   * Должен запросить у публикатора список ТС.
   *
   * @throws Exception error
   */
  @Test
  public void askGatewayForVehicles() throws Exception {
    // Действие:
    vehicleChoiceUseCase.getVehicles().test();

    // Результат:
    verify(vehiclesSharer, only()).get();
  }

  /* Проверяем ответы на запрос списка ТС */

  /**
   * Должен ответить ошибкой недостаточности средств.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoNetworkError() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.error(new InsufficientCreditsException()));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test().assertError(InsufficientCreditsException.class);
  }

  /**
   * Должен ответить успехом и без искажений.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void answerWithVehiclesList() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test().assertValue(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    );
  }

  /**
   * Должен ответить ошибкой об отсутствия доступных ТС.
   *
   * @throws Exception error
   */
  @Test
  public void answerNoVehiclesAvailableError() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.just(new ArrayList<>()));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test().assertError(NoVehiclesAvailableException.class);
  }

  /* Проверяем работу с публикатором ТС */

  /**
   * Не должен трогать публикатор.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void doNotTouchDataSharer() throws Exception {
    // Действие:
    vehicleChoiceUseCase.getVehicles().test();
    when(vehiclesSharer.get()).thenReturn(Observable.error(new NoNetworkException()));
    vehicleChoiceUseCase.getVehicles().test();
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    ));
    vehicleChoiceUseCase.getVehicles().test();
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Collections.singletonList(
            new Vehicle(12, "manufacturer", "model", "color", "license", true)
        ))
    ));
    vehicleChoiceUseCase.getVehicles().test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceSharer);
  }

  /**
   * Должен опубликовать выбранную ТС.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void askDataSharerToShareTheSelectedVehicle() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.setSelectedVehicle(2).test();

    // Результат:
    verify(vehicleChoiceSharer, only())
        .share(new Vehicle(14, "manufactur", "modelsa", "color", "licens", false));
  }

  /**
   * Не должен трогать публикатор, если выбор неверный.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void doNotTouchDataSharerIfSelectionInvalid() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    ));

    // Действие:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.setSelectedVehicle(-1).test();
    vehicleChoiceUseCase.setSelectedVehicle(5).test();

    // Результат:
    verifyZeroInteractions(vehicleChoiceSharer);
  }

  /* Проверяем ответы на публикацию */

  /**
   * Должен ответить ошибкой, если выбрана позиция за пределами текущего дипазона.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void answerArgumentError() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.setSelectedVehicle(-1).test().assertError(IndexOutOfBoundsException.class);
    vehicleChoiceUseCase.setSelectedVehicle(1).test().assertError(IndexOutOfBoundsException.class);
    vehicleChoiceUseCase.setSelectedVehicle(3).test().assertError(IndexOutOfBoundsException.class);
    vehicleChoiceUseCase.setSelectedVehicle(5).test().assertError(IndexOutOfBoundsException.class);
  }

  /**
   * Должен ответить успехом.
   *
   * @throws Exception error
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void answerSuccess() throws Exception {
    // Дано:
    when(vehiclesSharer.get()).thenReturn(Observable.just(
        new ArrayList<>(Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactur", "modelsa", "color", "licens", false),
            new Vehicle(15, "manufactu", "modelsan", "colo", "licensee", true)
        ))
    ));

    // Действие и Результат:
    vehicleChoiceUseCase.getVehicles().test();
    vehicleChoiceUseCase.setSelectedVehicle(0).test().assertComplete();
    vehicleChoiceUseCase.setSelectedVehicle(2).test().assertComplete();
  }
}