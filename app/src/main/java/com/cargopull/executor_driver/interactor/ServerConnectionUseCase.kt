package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.backend.web.AuthorizationException
import com.cargopull.executor_driver.backend.web.DeprecatedVersionException
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Юзкейс вебсокета.
 */
interface ServerConnectionUseCase {

    /**
     * Соединяет с сокетом сервера. Первое значение говорит об успешном соединении. Чтобы закрыть
     * сеодинение, нужно отписаться.
     */
    fun connect(): Flowable<Boolean>
}


class ServerConnectionUseCaseImpl(private val serverConnectionGateway: ServerConnectionGateway,
                                  private val networkConnectionGateway: CommonGateway<Boolean>) : ServerConnectionUseCase {

    private val connectionSource: Flowable<Boolean> by lazy {
        val socketConnection = serverConnectionGateway.openSocket()
                .observeOn(Schedulers.single())
                .retryWhen { failed ->
                    failed.concatMap<Long> {
                        if (it is AuthorizationException || it is DeprecatedVersionException) {
                            Flowable.error<Long>(it)
                        } else {
                            it.printStackTrace()
                            Flowable.timer(1, TimeUnit.SECONDS)
                        }
                    }
                }
        networkConnectionGateway.data
                .distinctUntilChanged()
                .switchMap {
                    if (it) socketConnection else Flowable.never<Boolean>().startWith(Flowable.just(it))
                }
    }

    override fun connect(): Flowable<Boolean> = connectionSource
}
