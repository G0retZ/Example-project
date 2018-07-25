package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.utils.Pair;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PackageCostDetailsTest {

  @Mock
  private Pair<String, Long> optionCost0;
  @Mock
  private Pair<String, Long> optionCost1;
  @Mock
  private Pair<String, Long> optionCost2;
  @Mock
  private Pair<String, Long> optionCost3;
  @Mock
  private Pair<String, Long> optionCost4;

  @Test
  public void testConstructor() {
    // Дано:
    PackageCostDetails packageOptionCost = new PackageCostDetails(123, 234, 345, 456,
        Arrays.asList(optionCost0, optionCost1, optionCost2, optionCost3, optionCost4));

    // Результат:
    assertEquals(packageOptionCost.getPackageTime(), 123);
    assertEquals(packageOptionCost.getPackageDistance(), 234);
    assertEquals(packageOptionCost.getPackageCost(), 345);
    assertEquals(packageOptionCost.getServiceCost(), 456);
    assertEquals(packageOptionCost.getOptionCosts().size(), 5);
    assertEquals(packageOptionCost.getOptionCosts().get(0), optionCost0);
    assertEquals(packageOptionCost.getOptionCosts().get(1), optionCost1);
    assertEquals(packageOptionCost.getOptionCosts().get(2), optionCost2);
    assertEquals(packageOptionCost.getOptionCosts().get(3), optionCost3);
    assertEquals(packageOptionCost.getOptionCosts().get(4), optionCost4);
  }
}