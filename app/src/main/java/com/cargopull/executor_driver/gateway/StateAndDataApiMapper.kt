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
            val pair = Pair(ExecutorState.valueOf(from.status!!), data)
            pair.first.data = from.message
            return pair
        } catch (e: Exception) {
            throw DataMappingException("Mapping error: wrong status format!", e)
        }
    }
}
