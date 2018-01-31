package com.fasten.executor_driver.view.auth;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.phone.PhoneViewActions;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.view.BaseFragment;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Отображает поле для ввода логина.
 */

public class LoginFragment extends BaseFragment implements PhoneViewActions {

	private PhoneViewModel phoneViewModel;
	private TextInputLayout phoneInputLayout;
	private TextInputEditText phoneInput;
	private Button goNext;
	private ProgressBar pendingIndicator;
	private Context context;

	private ViewModelProvider.Factory viewModelFactory;

	@Inject
	public void setViewModelFactory(@Named("phone") ViewModelProvider.Factory viewModelFactory) {
		this.viewModelFactory = viewModelFactory;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	protected void onDependencyInject(AppComponent appComponent) {
		// Required by Dagger2 for field injection
		appComponent.inject(this);
		phoneViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhoneViewModelImpl.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auth_login, container, false);
		phoneInputLayout = view.findViewById(R.id.phoneInputLayout);
		phoneInput = view.findViewById(R.id.phoneInput);
		goNext = view.findViewById(R.id.goNext);
		pendingIndicator = view.findViewById(R.id.pending);

		goNext.setOnClickListener(v -> phoneViewModel.nextClicked());
		phoneViewModel.getViewStateLiveData().observe(this, viewState -> {
			if (viewState != null) {
				viewState.apply(this);
			}
		});
		// Если не было сохраненного состояния (первый запуск)
		if (savedInstanceState == null) {
			phoneInput.setText("+7 (");
			phoneInput.setSelection(4);
		}
		setTextListener();
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		context = null;
	}

	@Override
	public void enableButton(boolean enable) {
		goNext.setEnabled(enable);
	}

	@Override
	public void showPending(boolean pending) {
		pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setInputEditable(boolean editable) {
		phoneInput.setEnabled(editable);
		if (!editable) {
			phoneInput.requestFocus();
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) imm.showSoftInput(phoneInput, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	@Override
	public void showError(@Nullable Throwable error) {
		if (error == null) {
			phoneInputLayout.setError(null);
		} else {
			if (error instanceof NoNetworkException) {
				phoneInputLayout.setError(getString(R.string.no_network_connection));
			} else {
				phoneInputLayout.setError(getString(R.string.phone_not_found));
			}
		}
	}

	@Override
	public void proceedNext(@NonNull String login) {
		System.out.println(login);
	}

	// Замудренная логика форматировния ввода номера телефона в режиме реального времени
	private void setTextListener() {
		phoneInput.addTextChangedListener(new TextWatcher() {
			// Флаг, предотвращающий переолнение стека. Разделяет ручной ввод и форматирование.
			private boolean mFormatting;
			private int mAfter;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				System.out.println("CharSequence: \"" + s + "\"\tstart: " + start + "\tbefore: " + before + "\tcount: " + count);
			}

			//called before the text is changed...
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				mAfter = after; // флаг определения backspace.
				System.out.println("CharSequence: \"" + s + "\"\tstart: " + start + "\tcount: " + count + "\tafter: " + after);
			}

			@Override
			public void afterTextChanged(Editable s) {
				System.out.println("Editable: \"" + s);
				// Игнорируем изменения, произведенные ниже (фильтруем действия нашего алгоритма).
				if (!mFormatting) {
					mFormatting = true;
					// Берем текущую позицию курсора после изменения текста.
					int selection = phoneInput.getSelectionStart();
					// Берем текущую строку после изменения текста.
					String numbers = s.toString();
					// Если был удален не-цифровой символ, то удаляем цифровой символ слева,
					// и сдвигаем курсор влево.
					if (mAfter == 0) {
						if (selection == 15 || selection == 12) {
							numbers = new StringBuilder(numbers).deleteCharAt(--selection).toString();
						}
						if (selection == 8) {
							numbers = new StringBuilder(numbers).deleteCharAt(--selection).toString();
						}
						if (selection == 7) {
							numbers = new StringBuilder(numbers).deleteCharAt(--selection).toString();
						}
					}
					// Удаляем все нецифровые символы.
					numbers = numbers.replaceAll("[^\\d]", "");
					if (numbers.isEmpty()) {
						// Если строка оказалась пуста, то добавляем код страны 7.
						numbers = "7";
					} else if (!numbers.substring(0, 1).equals("7")) {
						// Если был ввод цифы перед кодом страны, то меняем их местами.
						numbers = new StringBuilder(numbers).deleteCharAt(1).insert(0, "7").toString();
					}
					// Форматируем ввод в виде +7 (XXX) XXX-XX-XX.
					numbers = numbers.replaceFirst("(\\d)", "+$1 (")
							.replaceFirst("(\\(\\d{3})", "$1) ")
							.replaceFirst("( \\d{3})", "$1-")
							.replaceFirst("(-\\d{2})", "$1-");
					// Если курсор оказался перед открывающей скобкой, то помещаем его после нее.
					if (selection < 5) {
						selection = 5;
					} else {
						if (mAfter != 0) {
							if (selection == 7 || selection == 8) selection++;
							if (selection == 8 || selection == 9) selection++;
							if (selection == 12 || selection == 13) selection++;
							if (selection == 15 || selection == 16) selection++;
						} else {
							if (selection == 16 || selection == 13) selection--;
							numbers = numbers.replaceAll("-$", "");
						}
					}
					// Защищаемся от {@link IndexOutOfBoundsException}
					selection = Math.min(selection, numbers.length());
					// Закидываем отформатированную строку в поле ввода
					phoneInput.setText(numbers);
					// Сдвигаем курсор на нужную позицию
					phoneInput.setSelection(selection);

					mFormatting = false;
					phoneViewModel.phoneNumberChanged(numbers);
				}
			}
		});
	}
}
