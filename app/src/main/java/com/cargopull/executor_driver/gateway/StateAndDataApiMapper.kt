package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState

/**
 * Преобразуем строку ответа сервера в бизнес объект пары статуса и данных.
 */
class StateAndDataApiMapper<T, D>(private val mapper: Mapper<T, D>) : Mapper<ApiSimpleResult<T>, Pair<ExecutorState, D?>> {

    @Throws(Exception::class)
    override fun map(from: ApiSimpleResult<T>): Pair<ExecutorState, D?> {
        val data = from.data?.let { mapper.map(it) }
        try {
            return Pair(ExecutorState.valueOf(from.status!!), data)
        } catch (e: Exception) {
            throw DataMappingException("Ошибка маппинга: неверный формат статуса!", e)
        }
    }
}
