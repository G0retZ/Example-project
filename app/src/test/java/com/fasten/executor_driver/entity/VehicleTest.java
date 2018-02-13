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
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
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
    assertEquals(vehicle.getVehicleOptions(), new ArrayList<VehicleOption>(
        Arrays.asList(
            new VehicleOptionNumeric(0, "name0", 10, 0, 20),
            new VehicleOptionNumeric(0, "name0", -5, -18, 0),
            new VehicleOptionBoolean(0, "name0", false),
            new VehicleOptionBoolean(1, "name0", true)
        )
    ));
  }

  @Test
  public void testEquals() throws Exception {
    Vehicle vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.setVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(11, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturers", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "models", "colors", "license", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "licenses", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", true);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 20),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
    vehicle1 = new Vehicle(12, "manufacturer", "model", "color", "license", false);
    vehicle1.addVehicleOptions(
        new VehicleOptionNumeric(0, "name0", 10, 0, 19),
        new VehicleOptionNumeric(0, "name0", -5, -18, 0),
        new VehicleOptionBoolean(0, "name0", false),
        new VehicleOptionBoolean(1, "name0", true)
    );
    assertNotEquals(vehicle, vehicle1);
  }
}