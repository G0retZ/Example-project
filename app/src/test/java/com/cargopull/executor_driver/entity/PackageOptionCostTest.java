package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PackageOptionCostTest {

  @Test
  public void testConstructor() {
    // Дано:
    PackageOptionCost packageOptionCost = new PackageOptionCost("name", 23);

    // Результат:
    assertEquals(packageOptionCost.getName(), "name");
    assertEquals(packageOptionCost.getCost(), 23);
  }
}