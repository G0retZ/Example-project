package com.fasten.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class VehicleTest {

  private Vehicle vehicle;

  @Before
  public void setUp() throws Exception {
    vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(vehicle.getId(), 12);
    assertEquals(vehicle.getManufacturer(), "manufacturer");
    assertEquals(vehicle.getModel(), "model");
    assertEquals(vehicle.getColor(), "color");
    assertEquals(vehicle.getLicensePlate(), "license");
    assertFalse(vehicle.isBusy());
    assertEquals(vehicle.getOptions(), new ArrayList<Option>(
        Arrays.asList(
            new OptionNumeric(0, "name0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", true, false),
            new OptionBoolean(3, "name3", false, true)
        )
    ));
  }

  @Test
  public void testEquals() throws Exception {
    Vehicle vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.setOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(11, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturers", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "models", "colors", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "licenses", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", true);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", false, 10, 0, 19),
        new OptionNumeric(1, "name1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", true, false),
        new OptionBoolean(3, "name3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
  }
}