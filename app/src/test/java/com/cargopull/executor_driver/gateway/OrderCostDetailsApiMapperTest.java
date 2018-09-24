package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.OrderCostDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.naiksoftware.stomp.client.StompMessage;

@RunWith(MockitoJUnitRunner.class)
public class OrderCostDetailsApiMapperTest {

  private Mapper<StompMessage, OrderCostDetails> mapper;
  @Mock
  private StompMessage stompMessage;

  @Before
  public void setUp() {
    mapper = new OrderCostDetailsApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения в детализацию заказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без суммарной цены.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutTotalAmountToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 0);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без времени пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutPackageTimeToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без общей дистанции заказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutPackageDistanceToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без предрасчетной цены.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEstimatedAmountToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNull(executorOrderCostDetails.getEstimatedCost());
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без предрасчетной цены пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutServicePackageCostToOrderCostDetailsSuccess()
      throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 0);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде сообщения без блока со списком опций.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutOptionsBlockToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде сообщения без списка опций.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOptionsToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {},"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertTrue(executorOrderCostDetails.getEstimatedCost().getOptionCosts().isEmpty());
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде сообщения с пустым списком опций.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithEmptyOptionsToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": []"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertTrue(executorOrderCostDetails.getEstimatedCost().getOptionCosts().isEmpty());
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде сообщения без имени опции.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutOptionNameToOrderCostDetailsFail() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без цены опции.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOptionPriceToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\""
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(0));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен дать ошибку, если JSON в пейлоаде сообщения без блока сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutOverPackageToOrderCostDetailsFail() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  }"
            + "}");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без времени сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOverPackageTimeToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без цены сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOverPackageCostToOrderCostDetailsSuccess() throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNull(executorOrderCostDetails.getOverPackageCost());
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без имени опции грузчиков сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutPorterOverPackageNameToOrderCostDetailsSuccess()
      throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertTrue(executorOrderCostDetails.getOverPackageCost().getOptionCosts().isEmpty());
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без цены грузчиков сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutPorterOverPackageCostToOrderCostDetailsSuccess()
      throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertTrue(executorOrderCostDetails.getOverPackageCost().getOptionCosts().isEmpty());
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 5_00);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).first,
        "moverTariffName");
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().get(0).second,
        new Long(2_50));
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без тарифа сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutOverPackageTariffToOrderCostDetailsSuccess()
      throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\","
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNull(executorOrderCostDetails.getOverPackageTariff());
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без имени тарифа грузчиков сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutPorterOverPackageTariffNameToOrderCostDetailsSuccess()
      throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPrice\": 250"
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertTrue(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().isEmpty());
  }

  /**
   * Должен успешно преобразовать JSON в пейлоаде сообщения без тарифа грузчиков сверх пакета.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutPorterOverPackageTariffToOrderCostDetailsSuccess()
      throws Exception {
    // Дано
    when(stompMessage.getPayload())
        .thenReturn("{"
            + "  \"totalAmount\": 320000,"
            + "  \"estimatedAmount\": 310000,"
            + "  \"overPackageStartCalculationTime\": 12345678900,"
            + "  \"estimatedRouteDistance\": 15000,"
            + "  \"estimatedAmountDetalization\": {"
            + "    \"payOptionDetails\": ["
            + "      {"
            + "        \"optionName\": \"option 1\","
            + "        \"optionPrice\": 1000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 2\","
            + "        \"optionPrice\": 2000"
            + "      },"
            + "      {"
            + "        \"optionName\": \"option 3\","
            + "        \"optionPrice\": 3000"
            + "      }"
            + "    ]"
            + "  },"
            + "  \"mobileDetalization\": {"
            + "    \"estimatedCsrPackageCost\": 250000,"
            + "    \"overPackageTime\": 1200000,"
            + "    \"overPackageCsrCost\": 5000,"
            + "    \"moverOverPackageCostName\": \"moverName\","
            + "    \"moverOverPackageCost\": 5000,"
            + "    \"overPackageCsrPrice\": 250,"
            + "    \"overPackageMoverPriceName\": \"moverTariffName\""
            + "  }"
            + "}");

    // Действие:
    OrderCostDetails executorOrderCostDetails = mapper.map(stompMessage);

    // Результат:
    assertEquals(executorOrderCostDetails.getOrderCost(), 3_200_00);
    assertNotNull(executorOrderCostDetails.getEstimatedCost());
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageTime(), 12345678900L);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageDistance(), 15_000);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getPackageCost(), 3_100_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getServiceCost(), 2_500_00);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().size(), 3);
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).first,
        "option 1");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(0).second,
        new Long(1_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).first,
        "option 2");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(1).second,
        new Long(2_000));
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).first,
        "option 3");
    assertEquals(executorOrderCostDetails.getEstimatedCost().getOptionCosts().get(2).second,
        new Long(3_000));
    assertNotNull(executorOrderCostDetails.getOverPackageCost());
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageTime(), 1_200_000L);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getPackageCost(), 100_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getServiceCost(), 50_00);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().size(), 1);
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).first,
        "moverName");
    assertEquals(executorOrderCostDetails.getOverPackageCost().getOptionCosts().get(0).second,
        new Long(50_00));
    assertNotNull(executorOrderCostDetails.getOverPackageTariff());
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageTime(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageDistance(), 0);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getPackageCost(), 2_50);
    assertEquals(executorOrderCostDetails.getOverPackageTariff().getServiceCost(), 2_50);
    assertTrue(executorOrderCostDetails.getOverPackageTariff().getOptionCosts().isEmpty());
  }

  /**
   * Должен дать ошибку, если в пейлоаде null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullFail() throws Exception {
    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде пустая строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("\n");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("dasie");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("12");

    // Действие:
    mapper.map(stompMessage);
  }

  /**
   * Должен дать ошибку, если в пейлоаде массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано
    when(stompMessage.getPayload()).thenReturn("[]");

    // Действие:
    mapper.map(stompMessage);
  }
}