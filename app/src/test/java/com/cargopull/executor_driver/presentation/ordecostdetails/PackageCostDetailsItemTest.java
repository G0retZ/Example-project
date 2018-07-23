package com.cargopull.executor_driver.presentation.ordecostdetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.PackageCostDetails;
import com.cargopull.executor_driver.utils.Pair;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PackageCostDetailsItemTest {

  private PackageCostDetailsItem packageCostDetailsItem;
  @Mock
  private PackageCostDetails packageCostDetails;
  @Mock
  private PackageCostDetails packageCostDetails1;

  @Before
  public void setUp() {
    packageCostDetailsItem = new PackageCostDetailsItem(packageCostDetails);
  }

  @Test
  public void testGetPackageCost() {
    // Дано:
    when(packageCostDetails.getPackageCost()).thenReturn(123L);

    // Результат:
    assertEquals(packageCostDetailsItem.getCost(), 123L);
  }

  @Test
  public void testGetPackageTime() {
    // Дано:
    when(packageCostDetails.getPackageTime()).thenReturn(123L);

    // Результат:
    assertEquals(packageCostDetailsItem.getTime(), 123L);
  }

  @Test
  public void testGetPackageDistance() {
    // Дано:
    when(packageCostDetails.getPackageDistance()).thenReturn(123456);

    // Результат:
    assertEquals(packageCostDetailsItem.getDistance(),
        String.format(Locale.getDefault(), "%.2f", 123456 / 1000d));
  }

  @Test
  public void testGetServiceCost() {
    // Дано:
    when(packageCostDetails.getServiceCost()).thenReturn(123456L);

    // Результат:
    assertEquals(packageCostDetailsItem.getServiceCost(), 123456L);
  }

  @Test
  public void testGetOptionsCosts() {
    // Дано:
    when(packageCostDetails.getOptionCosts())
        .thenReturn(Arrays.asList(
            new Pair<>("name1", 1L),
            new Pair<>("name2", 2L),
            new Pair<>("name3", 3L)
        ));

    // Результат:
    assertEquals(packageCostDetailsItem.getOptionsCosts().size(), 3);
    assertEquals(packageCostDetailsItem.getOptionsCosts().get(0).first, "name1");
    assertEquals(packageCostDetailsItem.getOptionsCosts().get(0).second, new Long(1));
    assertEquals(packageCostDetailsItem.getOptionsCosts().get(1).first, "name2");
    assertEquals(packageCostDetailsItem.getOptionsCosts().get(1).second, new Long(2));
    assertEquals(packageCostDetailsItem.getOptionsCosts().get(2).first, "name3");
    assertEquals(packageCostDetailsItem.getOptionsCosts().get(2).second, new Long(3));
  }

  @Test
  public void testEquals() {
    assertNotEquals(packageCostDetailsItem, null);
    assertNotEquals(packageCostDetailsItem, "");
    assertNotEquals(packageCostDetailsItem, new PackageCostDetailsItem(packageCostDetails1));
    assertEquals(packageCostDetailsItem, new PackageCostDetailsItem(packageCostDetails));
    assertEquals(packageCostDetailsItem, packageCostDetailsItem);
  }

  @Test
  public void testHashCode() {
    assertNotEquals(packageCostDetailsItem.hashCode(), packageCostDetails1.hashCode());
    assertEquals(packageCostDetailsItem.hashCode(), packageCostDetails.hashCode());
  }
}