package com.cargopull.executor_driver.presentation.ordecostdetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.utils.Pair;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsViewStateIdleTest {

  private OrderCostDetailsViewStateIdle viewState;

  @Mock
  private OrderCostDetailsViewActions viewActions;

  @Mock
  private OrderCostDetailsItem orderCostDetailsItem;
  @Mock
  private OrderCostDetailsItem orderCostDetailsItem1;
  @Mock
  private PackageCostDetailsItem estimatedPackageCostDetailsItem;
  @Mock
  private PackageCostDetailsItem overPackageCostDetailsItem;
  @Mock
  private PackageCostDetailsItem overPackageTariffCostDetailsItem;
  @Mock
  private List<Pair<String, Integer>> estimatedOptionsCosts;
  @Mock
  private List<Pair<String, Integer>> overPackageOptionsCosts;
  @Mock
  private List<Pair<String, Integer>> overPackageOptionsTariffs;

  @Before
  public void setUp() {
    viewState = new OrderCostDetailsViewStateIdle(orderCostDetailsItem);
  }

  /**
   * Проверяем взаимодествие с вью, если есть только пакет предрасчета.
   */
  @Test
  public void testActionsForEstimatedOnly() {
    // Дано:
    when(orderCostDetailsItem.getTotalCost()).thenReturn(321L);
    when(orderCostDetailsItem.getEstimatedPackage()).thenReturn(estimatedPackageCostDetailsItem);
    when(estimatedPackageCostDetailsItem.getCost()).thenReturn(432L);
    when(estimatedPackageCostDetailsItem.getTime()).thenReturn(543L);
    when(estimatedPackageCostDetailsItem.getDistance()).thenReturn("12.3");
    when(estimatedPackageCostDetailsItem.getServiceCost()).thenReturn(654L);
    when(estimatedPackageCostDetailsItem.getOptionsCosts()).thenReturn(estimatedOptionsCosts);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(false);
    verify(viewActions).showOrderTotalCost(321L);
    verify(viewActions).showEstimatedOrderPackage(true);
    verify(viewActions).showEstimatedOrderCost(432L);
    verify(viewActions).showEstimatedOrderTime(543L);
    verify(viewActions).showEstimatedOrderDistance("12.3");
    verify(viewActions).showEstimatedOrderServiceCost(654L);
    verify(viewActions).showEstimatedOrderOptionsCosts(estimatedOptionsCosts);
    verify(viewActions).showOverPackage(false);
    verify(viewActions).showOverPackageTariff(false);
    verifyNoMoreInteractions(viewActions);
  }

  /**
   * Проверяем взаимодествие с вью, если есть только пакет превышения.
   */
  @Test
  public void testActionsForOverPackageOnly() {
    // Дано:
    when(orderCostDetailsItem.getTotalCost()).thenReturn(321L);
    when(orderCostDetailsItem.getOverPackage()).thenReturn(overPackageCostDetailsItem);
    when(overPackageCostDetailsItem.getCost()).thenReturn(43L);
    when(overPackageCostDetailsItem.getTime()).thenReturn(54L);
    when(overPackageCostDetailsItem.getServiceCost()).thenReturn(65L);
    when(overPackageCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsCosts);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(false);
    verify(viewActions).showOrderTotalCost(321L);
    verify(viewActions).showEstimatedOrderPackage(false);
    verify(viewActions).showOverPackage(true);
    verify(viewActions).showOverPackageCost(43L);
    verify(viewActions).showOverPackageTime(54L);
    verify(viewActions).showOverPackageServiceCost(65L);
    verify(viewActions).showOverPackageOptionsCosts(overPackageOptionsCosts);
    verify(viewActions).showOverPackageTariff(false);
    verifyNoMoreInteractions(viewActions);
  }


  /**
   * Проверяем взаимодествие с вью, если есть только пакет тарифа.
   */
  @Test
  public void testActionsForOverPackageTariffOnly() {
    // Дано:
    when(orderCostDetailsItem.getTotalCost()).thenReturn(321L);
    when(orderCostDetailsItem.getOverPackageTariff()).thenReturn(overPackageTariffCostDetailsItem);
    when(overPackageTariffCostDetailsItem.getCost()).thenReturn(4L);
    when(overPackageTariffCostDetailsItem.getServiceCost()).thenReturn(6L);
    when(overPackageTariffCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsTariffs);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(false);
    verify(viewActions).showOrderTotalCost(321L);
    verify(viewActions).showEstimatedOrderPackage(false);
    verify(viewActions).showOverPackage(false);
    verify(viewActions).showOverPackageTariff(true);
    verify(viewActions).showOverPackageTariffCost(4L);
    verify(viewActions).showOverPackageServiceTariff(6L);
    verify(viewActions).showOverPackageOptionsTariffs(overPackageOptionsTariffs);
    verifyNoMoreInteractions(viewActions);
  }

  /**
   * Проверяем взаимодествие с вью, если есть только пакет предрасчета и пакет превышения.
   */
  @Test
  public void testActionsForEstimatedAndOverPackageOnly() {
    // Дано:
    when(orderCostDetailsItem.getTotalCost()).thenReturn(321L);
    when(orderCostDetailsItem.getEstimatedPackage()).thenReturn(estimatedPackageCostDetailsItem);
    when(estimatedPackageCostDetailsItem.getCost()).thenReturn(432L);
    when(estimatedPackageCostDetailsItem.getTime()).thenReturn(543L);
    when(estimatedPackageCostDetailsItem.getDistance()).thenReturn("12.3");
    when(estimatedPackageCostDetailsItem.getServiceCost()).thenReturn(654L);
    when(estimatedPackageCostDetailsItem.getOptionsCosts()).thenReturn(estimatedOptionsCosts);
    when(orderCostDetailsItem.getOverPackage()).thenReturn(overPackageCostDetailsItem);
    when(overPackageCostDetailsItem.getCost()).thenReturn(43L);
    when(overPackageCostDetailsItem.getTime()).thenReturn(54L);
    when(overPackageCostDetailsItem.getServiceCost()).thenReturn(65L);
    when(overPackageCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsCosts);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(false);
    verify(viewActions).showOrderTotalCost(321L);
    verify(viewActions).showEstimatedOrderPackage(true);
    verify(viewActions).showEstimatedOrderCost(432L);
    verify(viewActions).showEstimatedOrderTime(543L);
    verify(viewActions).showEstimatedOrderDistance("12.3");
    verify(viewActions).showEstimatedOrderServiceCost(654L);
    verify(viewActions).showEstimatedOrderOptionsCosts(estimatedOptionsCosts);
    verify(viewActions).showOverPackage(true);
    verify(viewActions).showOverPackageCost(43L);
    verify(viewActions).showOverPackageTime(54L);
    verify(viewActions).showOverPackageServiceCost(65L);
    verify(viewActions).showOverPackageOptionsCosts(overPackageOptionsCosts);
    verify(viewActions).showOverPackageTariff(false);
    verifyNoMoreInteractions(viewActions);
  }

  /**
   * Проверяем взаимодествие с вью, если есть только пакет превышения и пакет тарифа.
   */
  @Test
  public void testActionsForOverPackageAndOverPackageTariffOnly() {
    // Дано:
    when(orderCostDetailsItem.getTotalCost()).thenReturn(321L);
    when(orderCostDetailsItem.getOverPackage()).thenReturn(overPackageCostDetailsItem);
    when(overPackageCostDetailsItem.getCost()).thenReturn(43L);
    when(overPackageCostDetailsItem.getTime()).thenReturn(54L);
    when(overPackageCostDetailsItem.getServiceCost()).thenReturn(65L);
    when(overPackageCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsCosts);
    when(orderCostDetailsItem.getOverPackageTariff()).thenReturn(overPackageTariffCostDetailsItem);
    when(overPackageTariffCostDetailsItem.getCost()).thenReturn(4L);
    when(overPackageTariffCostDetailsItem.getServiceCost()).thenReturn(6L);
    when(overPackageTariffCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsTariffs);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(false);
    verify(viewActions).showOrderTotalCost(321L);
    verify(viewActions).showEstimatedOrderPackage(false);
    verify(viewActions).showOverPackage(true);
    verify(viewActions).showOverPackageCost(43L);
    verify(viewActions).showOverPackageTime(54L);
    verify(viewActions).showOverPackageServiceCost(65L);
    verify(viewActions).showOverPackageOptionsCosts(overPackageOptionsCosts);
    verify(viewActions).showOverPackageTariff(true);
    verify(viewActions).showOverPackageTariffCost(4L);
    verify(viewActions).showOverPackageServiceTariff(6L);
    verify(viewActions).showOverPackageOptionsTariffs(overPackageOptionsTariffs);
    verifyNoMoreInteractions(viewActions);
  }

  /**
   * Проверяем взаимодествие с вью, если есть все пакеты.
   */
  @Test
  public void testActionsForAllPackages() {
    // Дано:
    when(orderCostDetailsItem.getTotalCost()).thenReturn(321L);
    when(orderCostDetailsItem.getEstimatedPackage()).thenReturn(estimatedPackageCostDetailsItem);
    when(estimatedPackageCostDetailsItem.getCost()).thenReturn(432L);
    when(estimatedPackageCostDetailsItem.getTime()).thenReturn(543L);
    when(estimatedPackageCostDetailsItem.getDistance()).thenReturn("12.3");
    when(estimatedPackageCostDetailsItem.getServiceCost()).thenReturn(654L);
    when(estimatedPackageCostDetailsItem.getOptionsCosts()).thenReturn(estimatedOptionsCosts);
    when(orderCostDetailsItem.getOverPackage()).thenReturn(overPackageCostDetailsItem);
    when(overPackageCostDetailsItem.getCost()).thenReturn(43L);
    when(overPackageCostDetailsItem.getTime()).thenReturn(54L);
    when(overPackageCostDetailsItem.getServiceCost()).thenReturn(65L);
    when(overPackageCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsCosts);
    when(orderCostDetailsItem.getOverPackageTariff()).thenReturn(overPackageTariffCostDetailsItem);
    when(overPackageTariffCostDetailsItem.getCost()).thenReturn(4L);
    when(overPackageTariffCostDetailsItem.getServiceCost()).thenReturn(6L);
    when(overPackageTariffCostDetailsItem.getOptionsCosts()).thenReturn(overPackageOptionsTariffs);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCostDetailsPending(false);
    verify(viewActions).showOrderTotalCost(321L);
    verify(viewActions).showEstimatedOrderPackage(true);
    verify(viewActions).showEstimatedOrderCost(432L);
    verify(viewActions).showEstimatedOrderTime(543L);
    verify(viewActions).showEstimatedOrderDistance("12.3");
    verify(viewActions).showEstimatedOrderServiceCost(654L);
    verify(viewActions).showEstimatedOrderOptionsCosts(estimatedOptionsCosts);
    verify(viewActions).showOverPackage(true);
    verify(viewActions).showOverPackageCost(43L);
    verify(viewActions).showOverPackageTime(54L);
    verify(viewActions).showOverPackageServiceCost(65L);
    verify(viewActions).showOverPackageOptionsCosts(overPackageOptionsCosts);
    verify(viewActions).showOverPackageTariff(true);
    verify(viewActions).showOverPackageTariffCost(4L);
    verify(viewActions).showOverPackageServiceTariff(6L);
    verify(viewActions).showOverPackageOptionsTariffs(overPackageOptionsTariffs);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, viewState);
    assertEquals(viewState, new OrderCostDetailsViewStateIdle(orderCostDetailsItem));
    assertNotEquals(viewState, new OrderCostDetailsViewStateIdle(orderCostDetailsItem1));
    assertNotEquals(viewState, null);
    assertNotEquals(viewState, "");
  }

  @Test
  public void testHashCode() {
    assertNotEquals(viewState.hashCode(), orderCostDetailsItem1.hashCode());
    assertEquals(viewState.hashCode(), orderCostDetailsItem.hashCode());
  }
}