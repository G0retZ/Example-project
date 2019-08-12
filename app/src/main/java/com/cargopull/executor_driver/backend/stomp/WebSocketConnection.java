package com.cargopull.executor_driver.backend.stomp;

import android.util.Log;
import com.cargopull.executor_driver.BuildConfig;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketConnection {

  private final OkHttpClient okHttpClient;
  private final String LOG_TAG = "WebSocketConnection(" + this.hashCode() + "): ";
  private volatile MyWebSocketListener myWebSocketListener;

  public WebSocketConnection(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  Flowable<String> connect(String url) {
    if (myWebSocketListener == null) {
      return Flowable.create(emitter -> {
        MyWebSocketListener myWebSocketListener = new MyWebSocketListener(emitter);
        emitter.setCancellable(() -> {
          this.myWebSocketListener = null;
          myWebSocketListener.destroy();
        });
        this.myWebSocketListener = myWebSocketListener;
        log("Opening Socket...");
        okHttpClient.newWebSocket(new Builder().url(url).build(), myWebSocketListener);
      }, BackpressureStrategy.BUFFER);
    } else {
      return Flowable.error(new IllegalStateException("Already connected"));
    }
  }

  Completable sendMessage(final String message) {
    if (myWebSocketListener == null) {
      return Completable.error(new IllegalStateException("Not connected"));
    }
    return myWebSocketListener.sendMessage(message);
  }

  private void log(String message) {
    if (BuildConfig.DEBUG) {
      Log.d(LOG_TAG, message);
    }
  }

  private class MyWebSocketListener extends WebSocketListener {

    private final FlowableEmitter<String> emitter;
    private WebSocket webSocket;

    private MyWebSocketListener(FlowableEmitter<String> emitter) {
      this.emitter = emitter;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
      this.webSocket = webSocket;
      if (emitter.isCancelled()) {
        // Это костыль, чтобы закрытый сокет случайно не открылся вновь
        webSocket.cancel();
      } else {
        emitter.onNext("CONN");
        log("OPEN = " + (response != null ? response.message() : ""));
      }
    }

    @Override
    public void onMessage(WebSocket webSocket, final String text) {
      if (!emitter.isCancelled()) {
        log("MESSAGE = " + text);
        emitter.onNext(text);
      }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
      log("MESSAGE = " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
      if (!emitter.isCancelled()) {
        emitter.onComplete();
        log("CLOSING = " + code + " " + reason);
      }
    }


    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
      if (!emitter.isCancelled()) {
        emitter.onComplete();
        log("CLOSE = " + code + " " + reason);
      }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
      if (!emitter.isCancelled()) {
        log("FAILURE = " + (response != null ? response.message() : ""));
        emitter.tryOnError(t);
      }
    }

    Completable sendMessage(final String message) {
      log("new message to send = " + message);
      if (webSocket != null) {
        return Completable.fromAction(() -> webSocket.send(message));
      }
      return Completable.error(new IllegalStateException("Not connected"));
    }

    void destroy() {
      log("deactivating...");
      if (webSocket != null) {
        boolean good = webSocket.close(1000, "NORMAL CLOSE");
        log("Closed Normal = " + good);
      }
    }
  }
}
