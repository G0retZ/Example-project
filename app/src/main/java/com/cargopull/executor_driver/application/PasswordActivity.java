package com.cargopull.executor_driver.application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.code.CodeNavigate;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class PasswordActivity extends BaseActivity {

    @Nullable
    private ApiService apiService;
    @Nullable
    private ErrorReporter errorReporter;

    @Inject
    public void setApiService(@NonNull ApiService apiService) {
        this.apiService = apiService;
    }

    @Inject
    public void setErrorReporter(@NonNull ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwrod);
        Toolbar toolbar = findViewById(R.id.appBar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    @Override
    protected void onDependencyInject(AppComponent appComponent) {
        super.onDependencyInject(appComponent);
        appComponent.inject(this);
    }

    @Override
    public void navigate(@NonNull String destination) {
        if (CodeNavigate.ENTER_APP.equals(destination)) {
            sendMyFcmInstanceID();
            ((MainApplication) getApplication()).initServerConnection();
        } else {
            super.navigate(destination);
        }
    }

    // Отправка токена FCM на сервак
    private void sendMyFcmInstanceID() {
        // Get token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (apiService == null || errorReporter == null) {
                        throw new RuntimeException("Shit! WTF?!");
                    }
                    if (!task.isSuccessful()) {
                        Exception taskException = task.getException();
                        if (taskException == null) {
                            taskException = new RuntimeException("Не удалось получить FCM токен");
                        }
                        errorReporter.reportError(taskException);
                        return;
                    }
                    // Забрать и отправить токен
                    String token = task.getResult();
                    if (token != null) {
                        apiService.sendFcmInstanceID(token)
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> {
                                }, Throwable::printStackTrace).isDisposed();
                    }
                });
    }
}
