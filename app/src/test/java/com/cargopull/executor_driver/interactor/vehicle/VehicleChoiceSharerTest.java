package com.cargopull.executor_driver.interactor.vehicle;

import com.cargopull.executor_driver.entity.Vehicle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleChoiceSharerTest {

  private VehicleChoiceSharer vehicleChoiceSharer;

  @Before
  public void setUp() {
    vehicleChoiceSharer = new VehicleChoiceSharer();
  }

  /**
   * Должен получить значение без изменений.
   */
  @Test
  public void valueUnchangedForRead() {
    // Дано:
    vehicleChoiceSharer.updateWith(new Vehicle(1, "man", "mod", "col", "pla", false));

    // Действие и Результат:
    vehicleChoiceSharer.get().test().assertValue(new Vehicle(1, "man", "mod", "col", "pla", false));
  }
}