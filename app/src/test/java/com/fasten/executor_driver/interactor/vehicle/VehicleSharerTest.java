package com.fasten.executor_driver.interactor.vehicle;

import com.fasten.executor_driver.entity.Vehicle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehicleSharerTest {

  private VehicleSharer vehicleSharer;

  @Before
  public void setUp() throws Exception {
    vehicleSharer = new VehicleSharer();
  }

  /**
   * Должен получить значение без изменений
   *
   * @throws Exception error
   */
  @Test
  public void valueUnchangedForRead() throws Exception {
    // Дано:
    vehicleSharer.share(new Vehicle(1, "man", "mod", "col", "pla", false));

    // Действие и Результат:
    vehicleSharer.get().test().assertValue(new Vehicle(1, "man", "mod", "col", "pla", false));
  }
}