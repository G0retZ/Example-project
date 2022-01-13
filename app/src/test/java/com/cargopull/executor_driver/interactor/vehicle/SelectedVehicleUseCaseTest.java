package com.cargopull.executor_driver.interactor.vehicle;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataReceiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.NoSuchElementException;

import io.reactivex.Observable;

@RunWith(MockitoJUnitRunner.class)
public class SelectedVehicleUseCaseTest {

  private SelectedVehicleUseCase useCase;

  @Mock
  private DataReceiver<Vehicle> vehicleChoiceReceiver;

  @Before
  public void setUp() {
    useCase = new SelectedVehicleUseCaseImpl(vehicleChoiceReceiver);
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.never());
  }

  /* Проверяем работу с публикатором выбранного ТС */

  /**
   * Должен запросить у публикатора выбранное ТС.
   */
  @Test
  public void askVehicleDataSharerForVehicles() {
    // Action:
    useCase.getSelectedVehicle().test().isDisposed();

    // Effect:
    verify(vehicleChoiceReceiver, only()).get();
  }

  /* Проверяем ответы на запрос выбранного ТС */

  /**
   * Должен ответить успехом и без искажений.
   */
  @Test
  public void answerWithVehicleSelections() {
    // Given:
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.fromArray(
        new Vehicle(12, "manufacturer", "model", "color", "license", false),
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    ));

    // Action и Effect:
    useCase.getSelectedVehicle().test().assertValues(
        new Vehicle(12, "manufacturer", "model", "color", "license", false),
        new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
        new Vehicle(14, "manufacturers", "modeler", "color", "licensees", false),
        new Vehicle(15, "manufactures", "modelers", "colo", "licensee", true)
    );
  }

  /**
   * Должен ответить ошибкой.
   */
  @Test
  public void answerWithError() {
    // Given:
    when(vehicleChoiceReceiver.get()).thenReturn(Observable.error(new NoSuchElementException()));

    // Action и Effect:
    useCase.getSelectedVehicle().test().assertError(NoSuchElementException.class);
  }
}