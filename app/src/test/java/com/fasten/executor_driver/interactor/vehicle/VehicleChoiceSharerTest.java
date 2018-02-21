package com.fasten.executor_driver.interactor.vehicle;

import com.fasten.executor_driver.entity.Vehicle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleChoiceSharerTest {

  private VehicleChoiceSharer vehicleChoiceSharer;

  @Before
  public void setUp() throws Exception {
    vehicleChoiceSharer = new VehicleChoiceSharer();
  }

  /**
   * Должен получить значение без изменений.
   *
   * @throws Exception error
   */
  @Test
  public void valueUnchangedForRead() throws Exception {
    // Дано:
    vehicleChoiceSharer.share(new Vehicle(1, "man", "mod", "col", "pla", false));

    // Действие и Результат:
    vehicleChoiceSharer.get().test().assertValue(new Vehicle(1, "man", "mod", "col", "pla", false));
  }
}