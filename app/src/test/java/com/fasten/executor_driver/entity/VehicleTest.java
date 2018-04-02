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
  public void setUp() {
    vehicle = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
  }

  @Test
  public void testConstructor() {
    assertEquals(vehicle.getId(), 12);
    assertEquals(vehicle.getManufacturer(), "manufacturer");
    assertEquals(vehicle.getModel(), "model");
    assertEquals(vehicle.getColor(), "color");
    assertEquals(vehicle.getLicensePlate(), "license");
    assertFalse(vehicle.isBusy());
    assertEquals(vehicle.getOptions(), new ArrayList<Option>(
        Arrays.asList(
            new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
            new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
            new OptionBoolean(2, "name2", "description2", true, false),
            new OptionBoolean(3, "name3", "description3", false, true)
        )
    ));
  }

  @Test
  public void testEquals() {
    Vehicle vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.setOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(11, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturers", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "models", "colors", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "licenses", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", true);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 20),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(1, "name1", "description0", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description1", true, false),
        new OptionBoolean(3, "name3", "description2", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new OptionNumeric(0, "name0", "description0", false, 10, 0, 19),
        new OptionNumeric(1, "name1", "description1", true, -5, -18, 0),
        new OptionBoolean(2, "name2", "description2", true, false),
        new OptionBoolean(3, "name3", "description3", false, true)
    );
    assertNotEquals(vehicle, vehicle1);
  }
}