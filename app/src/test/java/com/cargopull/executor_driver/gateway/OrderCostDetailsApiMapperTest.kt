package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.incoming.ApiOrderCostDetails
import com.cargopull.executor_driver.entity.OrderCostDetails
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OrderCostDetailsApiMapperTest {

    private lateinit var mapper: Mapper<ApiOrderCostDetails, OrderCostDetails>

    @Before
    fun setUp() {
        mapper = OrderCostDetailsApiMapper()
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения в детализацию заказа.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без суммарной цены.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutTotalAmountToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 0)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без времени пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutPackageTimeToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без общей дистанции заказа.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutPackageDistanceToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без предрасчетной цены.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutEstimatedAmountToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNull(executorOrderCostDetails.estimatedCost)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без предрасчетной цены пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutServicePackageCostToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 0)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен дать ошибку, если JSON в пейлоаде сообщения без блока со списком опций.
     *
     * @throws Exception ошибка
     */
    @Test(expected = DataMappingException::class)
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOptionsBlockToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        mapper.map(apiOrderCostDetails)
    }

    /**
     * Должен дать ошибку, если JSON в пейлоаде сообщения без списка опций.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOptionsToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertTrue(executorOrderCostDetails.estimatedCost!!.optionCosts.isEmpty())
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен дать ошибку, если JSON в пейлоаде сообщения с пустым списком опций.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithEmptyOptionsToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertTrue(executorOrderCostDetails.estimatedCost!!.optionCosts.isEmpty())
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен дать ошибку, если JSON в пейлоаде сообщения без имени опции.
     *
     * @throws Exception ошибка
     */
    @Test(expected = DataMappingException::class)
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOptionNameToOrderCostDetailsFail() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        mapper.map(apiOrderCostDetails)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без цены опции.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOptionPriceToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                0)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен дать ошибку, если JSON в пейлоаде сообщения без блока сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test(expected = DataMappingException::class)
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOverPackageToOrderCostDetailsFail() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        mapper.map(apiOrderCostDetails)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без времени сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOverPackageTimeToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без цены сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOverPackageCostToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNull(executorOrderCostDetails.overPackageCost)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без имени опции грузчиков сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutPorterOverPackageNameToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertTrue(executorOrderCostDetails.overPackageCost!!.optionCosts.isEmpty())
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без цены грузчиков сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutPorterOverPackageCostToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertTrue(executorOrderCostDetails.overPackageCost!!.optionCosts.isEmpty())
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 500)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].first,
                "moverTariffName")
        assertEquals(executorOrderCostDetails.overPackageTariff!!.optionCosts[0].second,
                250)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без тарифа сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutOverPackageTariffToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNull(executorOrderCostDetails.overPackageTariff)
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без имени тарифа грузчиков сверх
     * пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutPorterOverPackageTariffNameToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertTrue(executorOrderCostDetails.overPackageTariff!!.optionCosts.isEmpty())
    }

    /**
     * Должен успешно преобразовать JSON в пейлоаде сообщения без тарифа грузчиков сверх пакета.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingJsonStringWithoutPorterOverPackageTariffToOrderCostDetailsSuccess() {
        // Given
        val apiOrderCostDetails = Gson().fromJson(
                "{"
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
                        + "}",
                ApiOrderCostDetails::class.java
        )

        // Action:
        val executorOrderCostDetails = mapper.map(apiOrderCostDetails)

        // Effect:
        assertEquals(executorOrderCostDetails.orderCost, 320000)
        assertNotNull(executorOrderCostDetails.estimatedCost)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageTime, 12345678900L)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageDistance.toLong(), 15000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.packageCost, 310000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.serviceCost, 250000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts.size.toLong(), 3)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].first,
                "option 1")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[0].second,
                1000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].first,
                "option 2")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[1].second,
                2000)
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].first,
                "option 3")
        assertEquals(executorOrderCostDetails.estimatedCost!!.optionCosts[2].second,
                3000)
        assertNotNull(executorOrderCostDetails.overPackageCost)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageTime, 1_200_000L)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageCost!!.packageCost, 10000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.serviceCost, 5000)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts.size.toLong(), 1)
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].first,
                "moverName")
        assertEquals(executorOrderCostDetails.overPackageCost!!.optionCosts[0].second,
                5000)
        assertNotNull(executorOrderCostDetails.overPackageTariff)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageTime, 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageDistance.toLong(), 0)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.packageCost, 250)
        assertEquals(executorOrderCostDetails.overPackageTariff!!.serviceCost, 250)
        assertTrue(executorOrderCostDetails.overPackageTariff!!.optionCosts.isEmpty())
    }
}