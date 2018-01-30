package com.fasten.executor_driver.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.fasten.executor_driver.di.AppComponent;

import java.util.LinkedList;

import dagger.Component;

/**
 * Базовая {@link Activity} с поддержкой:
 * <ul>
 *     <li>Перехвата нажатия назад</li>
 *     <li>Lifecycle components</li>
 * </ul>
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

	@NonNull
	private final LinkedList<OnBackPressedInterceptor> onBackPressedInterceptors = new LinkedList<>();

	/**
	 * Добавляет {@link OnBackPressedInterceptor} в реестр перехватчиков.
	 * @param onBackPressedInterceptor  перехватчик нажатия кнопки "назад".
	 */
	public void registerOnBackPressedInterceptor(@NonNull OnBackPressedInterceptor onBackPressedInterceptor) {
		onBackPressedInterceptors.add(onBackPressedInterceptor);
	}

	/**
	 * Удаляет {@link OnBackPressedInterceptor} из реестра перехватчиков.
	 * @param onBackPressedInterceptor  перехватчик нажатия кнопки "назад"
	 */
	public void unregisterOnBackPressedInterceptor(@NonNull OnBackPressedInterceptor onBackPressedInterceptor) {
		onBackPressedInterceptors.remove(onBackPressedInterceptor);
	}

	/**
	 * Пробегает по реестру перехватчиков пока кто либо не обработает нажатие "назад".
	 * Если никто не обработал то вызывает воплощение метода в родителе (super).
	 */
	@Override
	public void onBackPressed() {
		for (OnBackPressedInterceptor onBackPressedInterceptor : onBackPressedInterceptors) {
			if (onBackPressedInterceptor.onBackPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	/**
	 * Возвращает {@link Component} для внедрения зависимостей
	 * @return  DI компонент
	 */
	@NonNull
	public AppComponent getDiComponent() {
		return ((MainApplication) getApplication()).getAppComponent();
	}

}
