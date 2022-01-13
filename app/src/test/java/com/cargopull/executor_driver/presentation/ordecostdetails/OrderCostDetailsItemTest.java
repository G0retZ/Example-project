package com.cargopull.executor_driver.presentation.ordecostdetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.entity.PackageCostDetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsItemTest {

  private OrderCostDetailsItem orderCostDetailsItem;
  @Mock
  private OrderCostDetails orderCostDetails;
  @Mock
  private OrderCostDetails orderCostDetails1;
  @Mock
  private PackageCostDetails packageCostDetails;

  @Before
  public void setUp() {
    orderCostDetailsItem = new OrderCostDetailsItem(orderCostDetails);
  }

  @Test
  public void testGetOrderCost() {
    // Given:
    when(orderCostDetails.getOrderCost()).thenReturn(123L);

      // Effect:
    assertEquals(orderCostDetailsItem.getTotalCost(), 123L);
  }

  @Test
  public void testGetEstimatedPackage() {
      // Given:
    when(orderCostDetails.getEstimatedCost()).thenReturn(packageCostDetails);
    orderCostDetailsItem = new OrderCostDetailsItem(orderCostDetails);

      // Effect:
    assertEquals(orderCostDetailsItem.getEstimatedPackage(),
        new PackageCostDetailsItem(packageCostDetails));
  }

  @Test
  public void testGetOverPackage() {
      // Given:
    when(orderCostDetails.getOverPackageCost()).thenReturn(packageCostDetails);
    orderCostDetailsItem = new OrderCostDetailsItem(orderCostDetails);

      // Effect:
    assertEquals(orderCostDetailsItem.getOverPackage(),
        new PackageCostDetailsItem(packageCostDetails));
  }

  @Test
  public void testGetOverPackageTariff() {
      // Given:
    when(orderCostDetails.getOverPackageTariff()).thenReturn(packageCostDetails);
    orderCostDetailsItem = new OrderCostDetailsItem(orderCostDetails);

      // Effect:
    assertEquals(orderCostDetailsItem.getOverPackageTariff(),
        new PackageCostDetailsItem(packageCostDetails));
  }

  @Test
  public void testEquals() {
    assertNotEquals(orderCostDetailsItem, null);
    assertNotEquals(orderCostDetailsItem, "");
    assertNotEquals(orderCostDetailsItem, new OrderCostDetailsItem(orderCostDetails1));
    assertEquals(orderCostDetailsItem, new OrderCostDetailsItem(orderCostDetails));
    assertEquals(orderCostDetailsItem, orderCostDetailsItem);
  }
}