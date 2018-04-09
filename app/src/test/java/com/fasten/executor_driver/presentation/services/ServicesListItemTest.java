package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.entity.Service;
import org.junit.Before;
import org.junit.Test;

public class ServicesListItemTest {

  private ServicesListItem servicesListItem;

  @Before
  public void setUp() {
    servicesListItem = new ServicesListItem(new Service(11, "name", 1000, false));
  }

  @Test
  public void testGetters() {
    assertEquals(servicesListItem.getName(), "name");
    assertEquals(servicesListItem.getPrice(), "от 1000 рублей за первый час");
    assertEquals(servicesListItem.getPriceValue(), 1000);
    assertFalse(servicesListItem.isChecked());
  }

  @Test
  public void testSetters() {
    servicesListItem.setChecked(true);
    assertEquals(servicesListItem.getName(), "name");
    assertEquals(servicesListItem.getPrice(), "от 1000 рублей за первый час");
    assertEquals(servicesListItem.getPriceValue(), 1000);
    assertTrue(servicesListItem.isChecked());
    servicesListItem.setChecked(false);
    assertEquals(servicesListItem.getName(), "name");
    assertEquals(servicesListItem.getPrice(), "от 1000 рублей за первый час");
    assertEquals(servicesListItem.getPriceValue(), 1000);
    assertFalse(servicesListItem.isChecked());
  }

  @Test
  public void testEquals() {
    assertEquals(servicesListItem, new ServicesListItem(new Service(11, "name", 1000, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(10, "name", 1000, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(11, "nam", 1000, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(11, "name", 100, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(11, "name", 1000, true)));
  }
}