package com.fasten.executor_driver.interactor.vehicle;

import com.fasten.executor_driver.entity.Vehicle;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VehiclesSharerTest {

  private VehiclesSharer vehiclesSharer;

  @Before
  public void setUp() {
    vehiclesSharer = new VehiclesSharer();
  }

  /**
   * Должен получить значение без изменений.
   */
  @Test
  public void valueUnchangedForRead() {
    // Дано:
    vehiclesSharer.onNext(
        Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactures", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufacturers", "modelers", "colo", "licensee", true)
        )
    );

    // Действие и Результат:
    vehiclesSharer.get().test().assertValue(
        Arrays.asList(
            new Vehicle(12, "manufacturer", "model", "color", "license", false),
            new Vehicle(13, "manufacture", "models", "colo", "licenses", true),
            new Vehicle(14, "manufactures", "modeler", "color", "licensees", false),
            new Vehicle(15, "manufacturers", "modelers", "colo", "licensee", true)
        )
    );
  }
}