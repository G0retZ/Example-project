package com.cargopull.executor_driver.presentation.geolocationstate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cargopull.executor_driver.ViewModelThreadTestRule
import com.cargopull.executor_driver.backend.analytics.EventLogger
import com.cargopull.executor_driver.backend.geolocation.GeolocationState
import com.cargopull.executor_driver.interactor.CommonGateway
import com.cargopull.executor_driver.presentation.ImageTextViewActions
import com.cargopull.executor_driver.presentation.ViewState
import com.cargopull.executor_driver.utils.TimeUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import java.util.*

@RunWith(Enclosed::class)
class GeoLocationStateViewModelTest {

    class CommonTests {
        @Rule
        @JvmField
        var mockRule = MockitoJUnit.rule()
        @Rule
        @JvmField
        var rule: TestRule = InstantTaskExecutorRule()

        private lateinit var viewModel: GeoLocationStateViewModel
        @Mock
        private lateinit var gateway: CommonGateway<Boolean>
        @Mock
        private lateinit var eventLogger: EventLogger
        @Mock
        private lateinit var geolocationState: GeolocationState
        @Mock
        private lateinit var timeUtils: TimeUtils
        @Mock
        private lateinit var viewStateObserver: Observer<ViewState<ImageTextViewActions>>

        private lateinit var publishSubject: PublishSubject<Boolean>

        @Before
        fun setUp() {
            publishSubject = PublishSubject.create()
            `when`(gateway.data).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))
            viewModel = GeoLocationStateViewModelImpl(eventLogger, geolocationState, timeUtils, gateway)
        }

        /* Тетсируем работу с гейтвеем. */

        /**
         * Должен запросить у гейтвея состояния сервисов местоположения изначально.
         */
        @Test
        fun askGatewayForGeolocationStateInitially() {
            // Результат:
            verify<CommonGateway<Boolean>>(gateway, only()).data
        }

        /**
         * Не должен трогать гейтвей на подписках.
         */
        @Test
        fun doNotTouchGatewayOnSubscriptions() {
            // Действие:
            viewModel.viewStateLiveData
            viewModel.navigationLiveData
            viewModel.viewStateLiveData
            viewModel.navigationLiveData

            // Результат:
            verify<CommonGateway<Boolean>>(gateway, only()).data
        }

        /* Тетсируем работу с менеджером локаций. */

        /**
         * Не должен трогать менеджер до получения состояния сервисов местоположения.
         */
        @Test
        fun doNotTouchLocationManager() {
            // Действие:
            viewModel.viewStateLiveData
            viewModel.navigationLiveData
            viewModel.viewStateLiveData
            viewModel.navigationLiveData

            // Результат:
            verifyNoInteractions(geolocationState)
        }

        /**
         * Должен запросить у менеджера состояние провайдеров геолокации.
         */
        @Test
        fun askLocationManagerForServiceProvidersStatus() {
            // Действие:
            publishSubject.onNext(true)
            publishSubject.onNext(false)
            viewModel.checkSettings()

            // Результат:
            verify<GeolocationState>(geolocationState, times(3)).isGpsEnabled
            verify<GeolocationState>(geolocationState, times(3)).isNetworkEnabled
            verifyNoMoreInteractions(geolocationState)
        }

        /* Тетсируем работу с логгером. */

        /**
         * Не должен трогать логгер до получения состояния сервисов местоположения.
         */
        @Test
        fun doNotTouchEventLogger() {
            // Действие:
            viewModel.viewStateLiveData
            viewModel.navigationLiveData
            viewModel.viewStateLiveData
            viewModel.navigationLiveData

            // Результат:
            verifyNoInteractions(geolocationState)
        }

        /* Тетсируем смену состояний. */

        /**
         * Должен передать состояние вида готовности для доступности геоданных.
         */
        @Test
        fun showGeolocationStateReadyForAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(true)
            `when`(geolocationState.isNetworkEnabled).thenReturn(true)

            // Действие:
            publishSubject.onNext(true)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateReadyViewState::class.java))
        }

        /**
         * Должен передать состояние вида готовности для недоступности геоданных.
         */
        @Test
        fun showGeolocationStateReadyForUnAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(true)
            `when`(geolocationState.isNetworkEnabled).thenReturn(true)

            // Действие:
            publishSubject.onNext(false)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateReadyViewState::class.java))
        }

        /**
         * Должен передать состояние вида недоступности гелокации для доступности геоданных.
         */
        @Test
        fun showGeolocationStateNoLocationForAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(false)
            `when`(geolocationState.isNetworkEnabled).thenReturn(false)

            // Действие:
            publishSubject.onNext(true)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateNoLocationViewState::class.java))
        }

        /**
         * Должен передать состояние вида недоступности гелокации для недоступности геоданных.
         */
        @Test
        fun showGeolocationStateNoLocationForUnAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(false)
            `when`(geolocationState.isNetworkEnabled).thenReturn(false)

            // Действие:
            publishSubject.onNext(false)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateNoLocationViewState::class.java))
        }

        /**
         * Должен передать состояние вида недоступности GPS для доступности геоданных.
         */
        @Test
        fun showGeolocationStateNoGpsForAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(false)
            `when`(geolocationState.isNetworkEnabled).thenReturn(true)

            // Действие:
            publishSubject.onNext(true)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateNoGpsDetectionViewState::class.java))
        }

        /**
         * Должен передать состояние вида недоступности GPS для недоступности геоданных.
         */
        @Test
        fun showGeolocationStateNoGpsForUnAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(false)
            `when`(geolocationState.isNetworkEnabled).thenReturn(true)

            // Действие:
            publishSubject.onNext(false)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateNoGpsDetectionViewState::class.java))
        }

        /**
         * Должен передать состояние вида недоступности сети для доступности геоданных.
         */
        @Test
        fun showGeolocationStateNoNetworkForAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(true)
            `when`(geolocationState.isNetworkEnabled).thenReturn(false)

            // Действие:
            publishSubject.onNext(true)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateNoNetworkDetectionViewState::class.java))
        }

        /**
         * Должен передать состояние вида недоступности сети для недоступности геоданных.
         */
        @Test
        fun showGeolocationStateNoNetworkForUnAvailable() {
            // Дано:
            viewModel.viewStateLiveData.observeForever(viewStateObserver)
            `when`(geolocationState.isGpsEnabled).thenReturn(true)
            `when`(geolocationState.isNetworkEnabled).thenReturn(false)

            // Действие:
            publishSubject.onNext(false)

            // Результат:
            verify(viewStateObserver, only()).onChanged(any(GeoLocationStateNoNetworkDetectionViewState::class.java))
        }

        companion object {

            @ClassRule
            @JvmField
            val classRule = ViewModelThreadTestRule()
        }
    }

    @RunWith(Parameterized::class)
    class LogEventsTests(conditions: Pair<List<Boolean>, Pair<Int, Int>>) {
        private val fromAvailability: Boolean = conditions.first[0]
        private val fromGps: Boolean = conditions.first[1]
        private val fromNetwork: Boolean = conditions.first[2]
        private val toAvailability: Boolean = conditions.first[3]
        private val toGps: Boolean = conditions.first[4]
        private val toNetwork: Boolean = conditions.first[5]
        private val toAvailability1: Boolean = conditions.first[6]
        private val toGps1: Boolean = conditions.first[7]
        private val toNetwork1: Boolean = conditions.first[8]
        private val sendReport: Int = conditions.second.first
        private val tuInvocations: Int = conditions.second.second
        @Rule
        @JvmField
        var mockRule = MockitoJUnit.rule()
        @Rule
        @JvmField
        var rule: TestRule = InstantTaskExecutorRule()
        private lateinit var viewModel: GeoLocationStateViewModel
        @Mock
        private lateinit var gateway: CommonGateway<Boolean>
        @Mock
        private lateinit var eventLogger: EventLogger
        @Mock
        private lateinit var geolocationState: GeolocationState
        @Mock
        private lateinit var timeUtils: TimeUtils
        private lateinit var publishSubject: PublishSubject<Boolean>

        init {
            println("$fromAvailability, $fromGps, $fromNetwork")
            println("$toAvailability, $toGps, $toNetwork")
            println("$toAvailability1, $toGps1, $toNetwork1")
            println("---")
            println("$sendReport, $tuInvocations")
        }

        companion object {

            @ClassRule
            @JvmField
            val classRule = ViewModelThreadTestRule()

            @Parameterized.Parameters
            @JvmStatic
            fun primeConditions(): Iterable<Pair<List<Boolean>, Pair<Int, Int>>> {
                val states = listOf(
                        // Состояния: Доступность, GPS, Сеть
                        listOf(false, false, false), // 0
                        listOf(false, false, true),  // 1
                        listOf(false, true, false),  // 2
                        listOf(false, true, true),   // 3
                        listOf(true, false, false),  // 4
                        listOf(true, false, true),   // 5
                        listOf(true, true, false),   // 6
                        listOf(true, true, true)     // 7
                )

                val pairs = ArrayList<Pair<List<Boolean>, Pair<Int, Int>>>()
                for (from in states.indices) {
                    for (to in states.indices) {
                        for (to1 in states.indices) {
                            val booleans = ArrayList(states[from])
                            booleans.addAll(states[to])
                            booleans.addAll(states[to1])
                            // Если переход по состояниям был: 5|6|7 -> 1|2|3 -> *
                            val send = if (
                                    states[from][0] && (states[from][1] || states[from][2])
                                    && !states[to][0] && (states[to][1] || states[to][2])) {
                                when {
                                    // Если переход по состояниям был: * -> * -> 5|6|7
                                    states[to1][0] && (states[to1][1] || states[to1][2]) -> 1   // Отправляем отчет о восстановлении доступности геолокации
                                    // Если переход по состояниям был: * -> * -> 0|4
                                    !states[to1][1] && !states[to1][2] -> -1                    // Отправляем отчет о потери доступности геолокации
                                    else -> 0
                                }
                            } else {
                                0
                            }
                            val invocations = when {
                                // Если переход по состояниям был: 5|6|7 -> 1|2|3 -> *
                                states[from][0] && (states[from][1] || states[from][2])
                                        && !states[to][0] && (states[to][1] || states[to][2]) -> {
                                    // Если переход по состояниям был: * -> * -> 0|4|5|6|7
                                    if (states[to1][0] || !states[to1][1] && !states[to1][2]) {
                                        3  // Добавляем взаимодействие с утилитой времени в обеих сменах
                                    } else {
                                        1  // Добавляем взаимодействие с утилитой времени в первой смене
                                    }
                                }
                                // Если переход по состояниям был: * -> 5|6|7 -> 1|2|3
                                states[to][0] && (states[to][1] || states[to][2])
                                        && !states[to1][0] && (states[to1][1] || states[to1][2]) -> 2   // Добавляем взаимодействие с утилитой времени во второй смене
                                else -> 0
                            }
                            pairs.add(Pair(booleans, Pair(send, invocations)))
                        }
                    }
                }

                return pairs
            }
        }

        @Before
        fun setUp() {
            publishSubject = PublishSubject.create()
            if (tuInvocations > 0) {
                `when`(timeUtils.currentTimeMillis()).thenReturn(10L, 300L)
            }
            `when`(gateway.data).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER))
            `when`(geolocationState.isGpsEnabled).thenReturn(fromGps, toGps, toGps1)
            `when`(geolocationState.isNetworkEnabled).thenReturn(fromNetwork, toNetwork, toNetwork1)
            viewModel = GeoLocationStateViewModelImpl(eventLogger, geolocationState, timeUtils, gateway)
        }

        /**
         * Должен запросить таймстамп при определенных условиях.
         */
        @Test
        fun askTimeUtilsForTimeStamp() {
            // Действие:
            publishSubject.onNext(fromAvailability)
            publishSubject.onNext(toAvailability)

            // Результат:
            verify<TimeUtils>(timeUtils, times(tuInvocations % 2)).currentTimeMillis()
            verifyNoMoreInteractions(timeUtils)
        }

        /**
         * Должен запросить таймстамп повторно при определенных условиях.
         */
        @Test
        fun askTimeUtilsForTimeStampAgain() {
            // Действие:
            publishSubject.onNext(fromAvailability)
            publishSubject.onNext(toAvailability)
            publishSubject.onNext(toAvailability1)

            // Результат:
            verify<TimeUtils>(timeUtils, times(tuInvocations % 2 + tuInvocations / 2)).currentTimeMillis()
            verifyNoMoreInteractions(timeUtils)
        }

        /**
         * Должен запросить у логгера отправку отчета при определенных условиях.
         */
        @Test
        fun askEventLoggerToSendReport() {
            // Действие:
            publishSubject.onNext(fromAvailability)
            publishSubject.onNext(toAvailability)
            publishSubject.onNext(toAvailability1)

            // Результат:
            return when (sendReport) {
                1 -> verify<EventLogger>(eventLogger, only()).reportEvent(
                    "geolocation_restored",
                    mutableMapOf("loss_duration" to "290")
                )
                -1 -> verify<EventLogger>(eventLogger, only()).reportEvent(
                    "geolocation_lost",
                    mutableMapOf("loss_duration" to "290")
                )
                else -> verifyNoInteractions(eventLogger)
            }
        }

        /**
         * Не должен запрашивать у логгера отправку отчета при обновлении настроек.
         */
        @Test
        fun doNotAskEventLoggerToSendReportOnCheckSettings() {
            // Действие:
            publishSubject.onNext(fromAvailability)
            publishSubject.onNext(toAvailability)
            publishSubject.onNext(toAvailability1)
            viewModel.checkSettings()

            // Результат:
            return when (sendReport) {
                1 -> verify<EventLogger>(eventLogger, only()).reportEvent(
                    "geolocation_restored",
                    mutableMapOf("loss_duration" to "290")
                )
                -1 -> verify<EventLogger>(eventLogger, only()).reportEvent(
                    "geolocation_lost",
                    mutableMapOf("loss_duration" to "290")
                )
                else -> verifyNoInteractions(eventLogger)
            }
        }
    }
}
