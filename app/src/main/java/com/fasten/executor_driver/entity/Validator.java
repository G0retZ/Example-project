package com.fasten.executor_driver.entity;

import io.reactivex.annotations.Nullable;

/**
 * Валидатор входных данных
 *
 * @param <T> тип для валидации.
 */
public interface Validator<T> {
	/**
	 * @param data входные данные
	 * @return результат валидации
	 */
	boolean validate(@Nullable T data);
}
