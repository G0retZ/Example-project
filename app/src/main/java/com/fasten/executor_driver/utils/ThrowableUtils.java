package com.fasten.executor_driver.utils;

import android.support.annotation.Nullable;

/**
 * Утилиты для Throwable.
 */

public final class ThrowableUtils {

	/**
	 * Private constructor.
	 */
	private ThrowableUtils() {}

	/**
	 * Сравнивает 2 {@link Throwable}. Сравнивает по ссылкам, классам, сообщениям и причинам рекурсивно.
	 * @param t1
	 * 		{@link Throwable}.
	 * @param t2
	 * 		{@link Throwable}.
	 * @return
	 * 		boolean, верно если оба {@link Throwable} одинаковые.
	 */
	@SuppressWarnings("SimplifiableIfStatement")
	public static boolean throwableEquals(@Nullable Throwable t1, @Nullable Throwable t2) {
		if (t1 == null && t2 == null) return true;
		if (t1 == null || t2 == null) return false;
		if (t1.getClass() != t2.getClass()) return false;
		if (!throwableEquals(t1.getCause(), t2.getCause())) return false;
		if (t1.getMessage() == null && t2.getMessage() == null) return true;
		if (t1.getMessage() == null || t2.getMessage() == null) return false;
		return t1.getMessage().equals(t2.getMessage());
	}
}
