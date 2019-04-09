package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.entity.ExecutorState
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Юзкейс перехода в следующее состояние исполнителя.
 */
interface NextExecutorStateUseCase {

    /**
     * Запрашивает переход к следующему состоянию исполнителя, выдает следующий статус.
     *
     * @return [<] результат запроса.
     */
    val proceedToNextState: Completable
}

class NextExecutorStateUseCaseImpl<D>(
        private val gateway: CommonGatewaySingle<Pair<ExecutorState, D?>>,
        private val updateExecutorStateUseCase: DataUpdateUseCase<ExecutorState>,
        private val updateDataUseCase: DataUpdateUseCase<D>
) : NextExecutorStateUseCase {

    override val proceedToNextState: Completable
        get() {
            return gateway.data
                    .observeOn(Schedulers.single())
                    .map { pair ->
                        updateExecutorStateUseCase.updateWith(pair.first)
                        pair.second?.let { updateDataUseCase.updateWith(it) }
                    }.toCompletable()

        }
}
