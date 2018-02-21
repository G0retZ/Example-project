package com.fasten.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SelectedVehicleUseCaseTest {

  private SelectedVehicleUseCase selectedVehicleUseCase;

  @Mock
  private DataSharer<Vehicle> vehicleChoiceSharer;

  @Before
  public void setUp() throws Exception {
    selectedVehicleUseCase = new SelectedVehicleUseCaseImpl(vehicleChoiceSharer);
    when(vehicleChoiceSharer.get()).thenReturn(Observable.never());
  }

  /* Проверяем работу с публикатором выбранного ТС */

  /**
   * Должен запросить у публикатора выбранное ТС.
   *
   * @throws Exception error
   */
  @Test
  public void askVehicleDataSharerForVehicles() throws Exception {
    // Действие:
    selectedVehicleUseCase.getSelectedVehicle().test();

    // Результат:
    verify(vehicleChoiceSharer, only()).get();
  }

  /* Проверяем ответы на запрос выбранного ТС */

  /**
   * Должен ответить успехом и без искажений.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithVehiclesList() throws Exception {
    // Дано:
    when(vehicleChoiceSharer.get()).thenReturn(Observable.fromArray(
        new Vehicle(12, "manufacturer", "model", "color", "license", false),
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    ));

    // Действие и Результат:
    selectedVehicleUseCase.getSelectedVehicle().test().assertValues(
        new Vehicle(12, "manufacturer", "model", "color", "license", false),
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    );
  }

  /**
   * Должен ответить ошибкой.
   *
   * @throws Exception error
   */
  @Test
  public void answerWithError() throws Exception {
    // Дано:
    when(vehicleChoiceSharer.get()).thenReturn(Observable.error(new NoFreeVehiclesException()));

    // Действие и Результат:
    selectedVehicleUseCase.getSelectedVehicle().test().assertError(NoFreeVehiclesException.class);
  }
}