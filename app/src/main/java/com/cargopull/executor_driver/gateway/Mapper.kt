package com.cargopull.executor_driver.gateway

/**
 * Однонаправленный преобразователь данных из формы [F] в форму [T].
 *
 * @param <F> тип исходных данных
 * @param <T> тип выходных данных
</T></F> */
interface Mapper<F, T> {

    /**
     * Преобразует отдельный элемент типа [F] в тип [T].
     *
     * @param from исходный элемент
     * @return преобразованный элемент
     * @throws Exception если возникает какое-либо несовместимое с преобразованием обстоятельство
     */
    @Throws(Exception::class)
    fun map(from: F): T
}
