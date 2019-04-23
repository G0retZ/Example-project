package com.cargopull.executor_driver.interactor

import io.reactivex.Single

/**
 * Общий гейтвей получения любых данных по одному ответу на запрос.
 */
interface CommonGatewaySingle<D> {

    /**
     * Ожидает данные.
     *
     * @return [<] данные.
     */
    val data: Single<D>
}
