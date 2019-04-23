package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.incoming.ApiOrderCostDetails
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderOptionsCostDetails
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderOverPackage
import com.cargopull.executor_driver.entity.OrderCostDetails
import com.cargopull.executor_driver.entity.PackageCostDetails
import com.cargopull.executor_driver.utils.Pair

/**
 * Преобразуем ответ сервера в бизнес объект детального расчета заказа.
 */
class OrderCostDetailsApiMapper : Mapper<ApiOrderCostDetails, OrderCostDetails> {

    @Throws(Exception::class)
    override fun map(from: ApiOrderCostDetails): OrderCostDetails {
        val apiOrderOverPackage: ApiOrderOverPackage
        try {
            apiOrderOverPackage = from.apiOrderOverPackage!!
        } catch (e: KotlinNullPointerException) {
            throw DataMappingException("Ошибка маппинга: Нет данных детализации и сверх пакета!")
        }
        val apiOrderOptionsCostDetails: ApiOrderOptionsCostDetails
        try {
            apiOrderOptionsCostDetails = from.apiOrderOptionsCostDetails!!
        } catch (e: KotlinNullPointerException) {
            throw DataMappingException("Ошибка маппинга: Нет данных детализации по опциям!")
        }
        val estimatedOptions = ArrayList<Pair<String, Long>>()
        apiOrderOptionsCostDetails.optionsCosts?.let {
            for (apiOrderOptionCost in it) {
                val optionName: String
                try {
                    optionName = apiOrderOptionCost.optionName!!
                } catch (e: KotlinNullPointerException) {
                    throw DataMappingException("Ошибка маппинга: Имя опциии не должно быть нуль!")
                }
                estimatedOptions.add(Pair(optionName, apiOrderOptionCost.optionPrice))
            }
        }
        val overPackageOptions = ArrayList<Pair<String, Long>>()
        val overPackageMoverCostName = apiOrderOverPackage.overPackageMoverCostName
        val overPackageMoverCost = apiOrderOverPackage.overPackageMoverCost
        overPackageMoverCostName?.let {
            if (overPackageMoverCost > 0) overPackageOptions.add(Pair(it, overPackageMoverCost))
        }
        val overPackageOptionsTariffs = ArrayList<Pair<String, Long>>()
        val overPackageMoverTariffName = apiOrderOverPackage.overPackageMoverTariffName
        val overPackageMoverTariff = apiOrderOverPackage.overPackageMoverTariff
        overPackageMoverTariffName?.let {
            if (overPackageMoverTariff > 0) overPackageOptionsTariffs.add(Pair(it, overPackageMoverTariff))
        }
        return OrderCostDetails(
                from.totalAmount,
                if (from.estimatedAmount == 0L) null else PackageCostDetails(
                        from.estimatedTime,
                        from.estimatedRouteDistance,
                        from.estimatedAmount,
                        apiOrderOverPackage.estimatedPackageCost,
                        estimatedOptions
                ),
                if (apiOrderOverPackage.overPackageCost == 0L) null else PackageCostDetails(
                        apiOrderOverPackage.overPackageTime,
                        0,
                        apiOrderOverPackage.overPackageCost +
                                if (overPackageMoverCostName == null) 0 else overPackageMoverCost,
                        apiOrderOverPackage.overPackageCost,
                        overPackageOptions
                ),
                if (apiOrderOverPackage.overPackageTariff == 0L) null else PackageCostDetails(
                        0, 0,
                        apiOrderOverPackage.overPackageTariff +
                                if (overPackageMoverTariffName == null) 0 else overPackageMoverTariff,
                        apiOrderOverPackage.overPackageTariff,
                        overPackageOptionsTariffs
                )
        )
    }
}
