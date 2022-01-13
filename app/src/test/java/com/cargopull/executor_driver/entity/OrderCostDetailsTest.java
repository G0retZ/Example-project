package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsTest {

  @Mock
  private PackageCostDetails estimatedCost;
  @Mock
  private PackageCostDetails overPackageCost;
  @Mock
  private PackageCostDetails overPackageTariff;

  @Test
  public void testConstructor() {
    // Given:
    OrderCostDetails orderCostDetails =
        new OrderCostDetails(123, estimatedCost, overPackageCost, overPackageTariff);

      // Effect:
    assertEquals(orderCostDetails.getOrderCost(), 123);
    assertEquals(orderCostDetails.getEstimatedCost(), estimatedCost);
    assertEquals(orderCostDetails.getOverPackageCost(), overPackageCost);
    assertEquals(orderCostDetails.getOverPackageTariff(), overPackageTariff);
  }
}