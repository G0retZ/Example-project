package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.res.Resources;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.Service;
import java.text.DecimalFormat;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesListItemTest {

  private ServicesListItem servicesListItem;

  @Mock
  private Resources resources;

  @Before
  public void setUp() {
    when(resources.getString(R.string.currency_format)).thenReturn("##,###,### \u20BD");
    servicesListItem = new ServicesListItem(new Service(11, "name", 123_000, false));
    System.out.println(Locale.getDefault());
  }

  @Test
  public void testGetters() {
    assertEquals(servicesListItem.getName(), "name");
    assertEquals(servicesListItem.getPrice(resources),
        "от " + new DecimalFormat("##,###,### \u20BD").format(1230) + " за первый час");
    assertEquals(servicesListItem.getPriceValue(), 123_000);
    assertFalse(servicesListItem.isChecked());
    when(resources.getString(R.string.currency_format)).thenReturn("##,###,### коп");
    assertEquals(servicesListItem.getPrice(resources),
        "от " + new DecimalFormat("##,###,### коп").format(1230) + " за первый час");
  }

  @Test
  public void testSetters() {
    servicesListItem.setChecked(true);
    assertEquals(servicesListItem.getName(), "name");
    assertEquals(servicesListItem.getPrice(resources),
        "от " + new DecimalFormat("##,###,### \u20BD").format(1230) + " за первый час");
    assertEquals(servicesListItem.getPriceValue(), 123_000);
    assertTrue(servicesListItem.isChecked());
    servicesListItem.setChecked(false);
    assertEquals(servicesListItem.getName(), "name");
    assertEquals(servicesListItem.getPrice(resources),
        "от " + new DecimalFormat("##,###,### \u20BD").format(1230) + " за первый час");
    assertEquals(servicesListItem.getPriceValue(), 123_000);
    assertFalse(servicesListItem.isChecked());
  }

  @Test
  public void testEquals() {
    assertEquals(servicesListItem, new ServicesListItem(new Service(11, "name", 123_000, false)));
    assertNotEquals(servicesListItem,
        new ServicesListItem(new Service(10, "name", 123_000, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(11, "nam", 123_000, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(11, "name", 12_300, false)));
    assertNotEquals(servicesListItem, new ServicesListItem(new Service(11, "name", 123_000, true)));
  }
}