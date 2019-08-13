package com.cargopull.executor_driver.interactor

import io.reactivex.Flowable

/**
 * Общий гейтвей получения любых данных потоком.
 */
interface CommonGateway<D> {

    /**
     * Ожидает данные.
     *
     * @return [<] данные.
     */
    val data: Flowable<D>
}
