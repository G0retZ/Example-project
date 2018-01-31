package com.fasten.executor_driver.di;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.settings.AppPreferences;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.ConnectivityInterceptor;
import com.fasten.executor_driver.backend.web.ReceiveTokenInterceptor;
import com.fasten.executor_driver.backend.web.SendTokenInterceptor;
import com.fasten.executor_driver.backend.web.TokenKeeper;
import com.fasten.executor_driver.presentation.ViewModelFactory;
import com.fasten.executor_driver.entity.LoginValidator;
import com.fasten.executor_driver.entity.PasswordValidator;
import com.fasten.executor_driver.entity.PhoneNumberValidator;
import com.fasten.executor_driver.entity.Validator;
import com.fasten.executor_driver.gateway.LoginGatewayImpl;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;
import com.fasten.executor_driver.gateway.PhoneCallGatewayImpl;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import com.fasten.executor_driver.interactor.auth.LoginGateway;
import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PasswordGateway;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PhoneCallGateway;
import com.fasten.executor_driver.interactor.auth.PhoneCallUseCase;
import com.fasten.executor_driver.interactor.auth.PhoneCallUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.SmsGateway;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.interactor.auth.SmsUseCaseImpl;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.presentation.timeoutbutton.TimeoutButtonViewModel;
import com.fasten.executor_driver.presentation.timeoutbutton.TimeoutButtonViewModelImpl;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

	@NonNull
	private final Context appContext;

	public AppModule(@NonNull Context context) {
		this.appContext = context.getApplicationContext();
	}

	@Provides
	@NonNull
	@Singleton
	Context provideAppContext() {
		return appContext;
	}

	@Provides
	@NonNull
	@Singleton
	AppSettingsService provideAppSettingsService(@NonNull AppPreferences appPreferences) {
		return appPreferences;
	}

	@Provides
	@NonNull
	TokenKeeper provideTokenKeeper(@NonNull TokenKeeperImpl tokenKeeper) {
		return tokenKeeper;
	}

	@Provides
	@NonNull
	@Singleton
	ApiService provideApiService(@NonNull ConnectivityInterceptor connectivityInterceptor,
	                             @NonNull SendTokenInterceptor sendTokenInterceptor,
	                             @NonNull ReceiveTokenInterceptor receiveTokenInterceptor) {
		// build OkHttpClient builder
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.readTimeout(10, TimeUnit.SECONDS)
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(connectivityInterceptor)
				.addInterceptor(receiveTokenInterceptor)
				.addInterceptor(sendTokenInterceptor);
		// Add logging interceptor for debug build only
		if (BuildConfig.DEBUG) {
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.setLevel(HttpLoggingInterceptor.Level.BODY);
			builder.addInterceptor(logging);
		}
		return new Retrofit.Builder()
				.baseUrl(BuildConfig.BASE_URL)
				.client(builder.build())
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
				.create(ApiService.class);
	}

	@Provides
	LoginGateway provideLoginGateway(LoginGatewayImpl loginGateway) {
		return loginGateway;
	}

	@Provides
	PasswordGateway providePasswordGateway(PasswordGatewayImpl passwordGateway) {
		return passwordGateway;
	}

	@Provides
	PhoneCallGateway providePhoneCallGateway(PhoneCallGatewayImpl phoneCallGateway) {
		return phoneCallGateway;
	}

	@Provides
	SmsGateway provideSmsGateway(SmsGatewayImpl smsGateway) {
		return smsGateway;
	}

	@Provides
	@Named("loginValidator")
	Validator<String> provideLoginValidator(LoginValidator loginValidator) {
		return loginValidator;
	}

	@Provides
	@Named("passwordValidator")
	Validator<String> providePasswordValidator(PasswordValidator passwordValidator) {
		return passwordValidator;
	}

	@Provides
	@Named("phoneNumberValidator")
	Validator<String> providePhoneNumberValidator(PhoneNumberValidator phoneNumberValidator) {
		return phoneNumberValidator;
	}

	@Provides
	LoginUseCase provideLoginUseCase(LoginUseCaseImpl loginUseCase) {
		return loginUseCase;
	}

	@Provides
	PasswordUseCase providePasswordUseCase(PasswordUseCaseImpl passwordUseCase) {
		return passwordUseCase;
	}

	@Provides
	PhoneCallUseCase providePhoneCallUseCase(PhoneCallUseCaseImpl phoneCallUseCase) {
		return phoneCallUseCase;
	}

	@Provides
	SmsUseCase provideSmsUseCase(SmsUseCaseImpl smsUseCase) {
		return smsUseCase;
	}

	@Provides
	CodeViewModel provideCodeViewModel(CodeViewModelImpl codeViewModel) {
		return codeViewModel;
	}

	@Provides
	PhoneViewModel providePhoneViewModel(PhoneViewModelImpl codeViewModel) {
		return codeViewModel;
	}

	@Provides
	TimeoutButtonViewModel provideTimeoutButtonViewModel(TimeoutButtonViewModelImpl timeoutButtonViewModel) {
		return timeoutButtonViewModel;
	}

	@Provides
	@Named("phone")
	ViewModelProvider.Factory providePhoneViewModelFactory(ViewModelFactory<PhoneViewModel> factory) {
		return factory;
	}
}
