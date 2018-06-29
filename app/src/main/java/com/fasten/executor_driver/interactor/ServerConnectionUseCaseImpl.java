package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.AuthorizationException;
import io.reactivex.Flowable;
import java.util.concurrent.TimeUnit;

public class ServerConnectionUseCaseImpl implements ServerConnectionUseCase {

  @NonNull
  private final ServerConnectionGateway serverConnectionGateway;
  @Nullable
  private Flowable<Boolean> connectFlowable;

  public ServerConnectionUseCaseImpl(@NonNull ServerConnectionGateway serverConnectionGateway) {
    this.serverConnectionGateway = serverConnectionGateway;
  }

  @Override
  public Flowable<Boolean> connect() {
    if (connectFlowable == null) {
      connectFlowable = serverConnectionGateway.openSocket()
          .retryWhen(
              failed -> failed.concatMap(
                  throwable -> {
                    if (throwable instanceof AuthorizationException) {
                      return Flowable.<Long>error(throwable);
                    } else {
                      throwable.printStackTrace();
                      return Flowable.timer(15, TimeUnit.SECONDS);
                    }
                  }
              )
          ).share()
          .doOnTerminate(() -> connectFlowable = null);
    }
    return connectFlowable;
  }
}
