package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.UseCaseThreadTestRule
import com.cargopull.executor_driver.backend.web.AuthorizationException
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class ServerConnectionUseCaseTest {

    companion object {

        @ClassRule
        @JvmField
        val classRule = UseCaseThreadTestRule()
    }

    private lateinit var useCase: ServerConnectionUseCase

    @Mock
    private lateinit var serverConnectionGateway: ServerConnectionGateway
    @Mock
    private lateinit var networkConnectionGateway: CommonGateway<Boolean>
    @Mock
    private lateinit var serverSubscriptionConsumer: Consumer<Subscription>
    @Mock
    private lateinit var networkSubscriptionConsumer: Consumer<Subscription>
    @Mock
    private lateinit var action: Action
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { scheduler -> testScheduler }
        `when`(networkConnectionGateway.data)
                .thenReturn(Flowable.never<Boolean>().doOnSubscribe(networkSubscriptionConsumer))
        `when`(serverConnectionGateway.socketState)
                .thenReturn(Flowable.never<Boolean>().doOnSubscribe(serverSubscriptionConsumer))
        useCase = ServerConnectionUseCaseImpl(serverConnectionGateway, networkConnectionGateway)
    }

    /* Проверяем работу с гейтвеем подключения к сети */

    /**
     * Должен запросить у гейтвея состояние сети. И подписаться несколько раз.
     */
    @Test
    @Throws(Exception::class)
    fun askGatewayToGetNetworkConnectionState() {
        // Action:
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed

        // Effect:
        verify(networkConnectionGateway, only()).data
        verify(networkSubscriptionConsumer, times(3)).accept(any())
        verifyNoMoreInteractions(networkSubscriptionConsumer)
    }

    /* Проверяем работу с гейтвеем подключения к сокету */

    /**
     * Должен запросить у гейтвея открытие сокета. И не подписаться ни разу до получения состояния
     * сети.
     */
    @Test
    @Throws(Exception::class)
    fun doNotAskGatewayToOpenSocket() {
        // Action:
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed

        // Effect:
        verify(serverConnectionGateway, only()).socketState
        verifyNoInteractions(serverSubscriptionConsumer)
    }

    /**
     * Не должен подписываться на открытие сокета, если сети нет.
     */
    @Test
    fun doNotTouchGatewayIfNotConnected() {
        // Given:
        `when`(networkConnectionGateway.data).thenReturn(Flowable.just(false))

        // Action:
        useCase.connect().test().isDisposed

        // Effect:
        verifyNoInteractions(serverSubscriptionConsumer)
    }

    /**
     * Должен запросить у гейтвея открытие сокета.
     */
    @Test
    @Throws(Exception::class)
    fun askGatewayToOpenSocket() {
        // Given:
        `when`(networkConnectionGateway.data).thenReturn(Flowable.just(true))

        // Action:
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed

        // Effect:
        verify(serverConnectionGateway, only()).socketState
        verify(serverSubscriptionConsumer, times(3)).accept(any())
        verifyNoMoreInteractions(serverSubscriptionConsumer)
    }

    /**
     * Должен запросить у гейтвея закрытие и открытие сокета.
     */
    @Test
    @Throws(Exception::class)
    fun doNotAskGatewayToCloseAndOpenSocketAgain() {
        // Given:
        val inOrder = inOrder(action, serverSubscriptionConsumer)
        `when`(networkConnectionGateway.data).thenReturn(Flowable.just(true, false, true))
        `when`(serverConnectionGateway.socketState).thenReturn(
                Flowable.never<Boolean>()
                        .doOnSubscribe(serverSubscriptionConsumer)
                        .doOnCancel(action)
        )

        // Action:
        useCase.connect().test().isDisposed

        // Effect:
        inOrder.verify(serverSubscriptionConsumer).accept(any())
        inOrder.verify(action).run()
        inOrder.verify(serverSubscriptionConsumer).accept(any())
        inOrder.verifyNoMoreInteractions()
        verifyNoMoreInteractions(serverSubscriptionConsumer, action)
    }

    /**
     * Должен просить у гейтвея повторные открытия сокета после ошибок авторизации.
     */
    @Test
    @Throws(Exception::class)
    fun askGatewayToOpenSocketAgainAfterAuthorizationErrors() {
        // Given:
        `when`(networkConnectionGateway.data).thenReturn(Flowable.just(true))
        `when`(serverConnectionGateway.socketState).thenReturn(
                Flowable.error<Boolean>(AuthorizationException())
                        .doOnSubscribe(serverSubscriptionConsumer)
        )

        // Action:
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed
        useCase.connect().test().isDisposed

        // Effect:
        verify(serverSubscriptionConsumer, times(3)).accept(any())
        verifyNoMoreInteractions(serverSubscriptionConsumer)
    }

    /**
     * Должен повторить попытку открытия сокета.
     *
     * @throws Exception error
     */
    @Test
    @Throws(Exception::class)
    fun retryOpenSocket() {
        // Given:
        lateinit var emitter: FlowableEmitter<Boolean>
        `when`(networkConnectionGateway.data).thenReturn(Flowable.just(true))
        `when`(serverConnectionGateway.socketState).thenReturn(
                Flowable.create<Boolean>({ e -> emitter = e }, BackpressureStrategy.BUFFER)
                        .doOnSubscribe(serverSubscriptionConsumer)
        )

        // Action:
        useCase.connect().test().isDisposed
        emitter.onError(Exception())
        emitter.onError(Exception())
        testScheduler.advanceTimeBy(4, TimeUnit.MINUTES)
        emitter.onError(Exception())
        emitter.onError(Exception())

        // Effect:
        verify(serverSubscriptionConsumer, times(5)).accept(any())
    }

    /* Проверяем ответы на запрос соединения */

    /**
     * Должен ответить ошибкой авторизации.
     */
    @Test
    fun answerAuthorizationError() {
        // Given:
        `when`(networkConnectionGateway.data)
                .thenReturn(Flowable.just(true).concatWith(Flowable.never()))
        `when`(serverConnectionGateway.socketState)
                .thenReturn(Flowable.error(AuthorizationException()))

        // Action:
        val testSubscriber = useCase.connect().test()

        // Effect:
        testSubscriber.assertError(AuthorizationException::class.java)
        testSubscriber.assertNoValues()
        testSubscriber.assertNotComplete()
    }

    /**
     * Должен игнорировать иные ошибки.
     */
    @Test
    fun ignoreOtherError() {
        // Given:
        lateinit var emitter: FlowableEmitter<Boolean>
        `when`(networkConnectionGateway.data)
                .thenReturn(Flowable.just(true).concatWith(Flowable.never()))
        `when`(serverConnectionGateway.socketState).thenReturn(
                Flowable.create({ e -> emitter = e }, BackpressureStrategy.BUFFER)
        )

        // Action:
        val testSubscriber = useCase.connect().test()
        emitter.onError(Exception())
        emitter.onError(Exception())
        testScheduler.advanceTimeBy(45, TimeUnit.MILLISECONDS)
        emitter.onError(Exception())
        emitter.onError(Exception())

        // Effect:
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
        testSubscriber.assertNotComplete()
    }

    /**
     * Должен ответить состояниями подключения сети.
     */
    @Test
    fun answerConnectionStates() {
        // Given:
        `when`(networkConnectionGateway.data)
                .thenReturn(Flowable.just(true).concatWith(Flowable.never()))
        `when`(serverConnectionGateway.socketState).thenReturn(
                Flowable.intervalRange(0, 6, 0, 30, TimeUnit.SECONDS, testScheduler)
                        .switchMap { i -> Flowable.just(i % 2 == 0L) }
                        .concatWith(Flowable.never())
        )

        // Action:
        val testSubscriber = useCase.connect().test()
        testScheduler.advanceTimeBy(989, TimeUnit.SECONDS)

        // Effect:
        testSubscriber.assertNotComplete()
        testSubscriber.assertNoErrors()
        testSubscriber.assertValues(true, false, true, false, true, false)
    }

    /**
     * Должен ответить закрытием сокета.
     */
    @Test
    fun answerConnectionClosed() {
        // Given:
        `when`(networkConnectionGateway.data).thenReturn(Flowable.just(true))
        `when`(serverConnectionGateway.socketState).thenReturn(Flowable.empty())

        // Action:
        val testSubscriber = useCase.connect().test()
        testScheduler.advanceTimeBy(45, TimeUnit.MINUTES)

        // Effect:
        testSubscriber.assertComplete()
        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
    }
}