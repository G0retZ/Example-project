package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class StateAndDataApiMapperTest {
    private lateinit var mapper: StateAndDataApiMapper<String, Int>
    @Mock
    private lateinit var dataMapper: Mapper<String, Int>
    private var type = object : TypeToken<ApiSimpleResult<String>>() {}.type


    @Before
    fun setUp() {
        mapper = StateAndDataApiMapper(dataMapper)
    }

    /**
     * Должен успешно преобразовать JSON пару статуса и данных.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingApiSimpleResultSuccess() {
        // Дано
        val apiOrderCostDetails = Gson().fromJson<ApiSimpleResult<String>>("{"
                + "  \"code\": 200,"
                + "  \"message\": \"lalala\","
                + "  \"status\": \"ONLINE\","
                + "  \"data\": \"lololo\""
                + "}",
                type
        )
        `when`(dataMapper.map("lololo")).thenReturn(12345)

        // Действие:
        val pair = mapper.map(apiOrderCostDetails)

        // Результат:
        verify(dataMapper, only()).map("lololo")
        assertEquals(pair.first, ExecutorState.ONLINE)
        assertNotNull(pair.second)
        assertEquals(pair.second, 12345)
    }

    /**
     * Должен успешно преобразовать JSON пару статуса без данных.
     *
     * @throws Exception ошибка
     */
    @Test
    @Throws(Exception::class)
    fun mappingApiSimpleResultWithoutDataSuccess() {
        // Дано
        val apiOrderCostDetails = Gson().fromJson<ApiSimpleResult<String>>("{"
                + "  \"code\": 200,"
                + "  \"message\": \"lalala\","
                + "  \"status\": \"ONLINE\""
                + "}",
                type
        )

        // Действие:
        val pair = mapper.map(apiOrderCostDetails)

        // Результат:
        verifyZeroInteractions(dataMapper)
        assertEquals(pair.first, ExecutorState.ONLINE)
        assertNull(pair.second)
    }

    /**
     * Должен дать ошибку, если JSON без статуса.
     *
     * @throws Exception ошибка
     */
    @Test(expected = DataMappingException::class)
    @Throws(Exception::class)
    fun mappingJsonStringWithoutStatusFailed() {
        // Дано
        val apiOrderCostDetails = Gson().fromJson<ApiSimpleResult<String>>("{"
                + "  \"code\": 200,"
                + "  \"message\": \"lalala\","
                + "  \"data\": \"lololo\""
                + "}",
                type
        )

        // Действие:
        mapper.map(apiOrderCostDetails)
    }

    /**
     * Должен дать ошибку, если JSON c неверным значением статуса.
     *
     * @throws Exception ошибка
     */
    @Test(expected = DataMappingException::class)
    @Throws(Exception::class)
    fun mappingJsonStringWithWrongStatusFailed() {
        // Дано
        val apiOrderCostDetails = Gson().fromJson<ApiSimpleResult<String>>("{"
                + "  \"code\": 200,"
                + "  \"message\": \"lalala\","
                + "  \"status\": \"ONLIME\","
                + "  \"data\": \"lololo\""
                + "}",
                type
        )

        // Действие:
        mapper.map(apiOrderCostDetails)
    }
}
