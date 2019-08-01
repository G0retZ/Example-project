package com.cargopull.executor_driver.di

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import com.cargopull.executor_driver.BASE_URL
import com.cargopull.executor_driver.BuildConfig
import com.cargopull.executor_driver.SOCKET_URL
import com.cargopull.executor_driver.backend.analytics.ErrorReporter
import com.cargopull.executor_driver.backend.analytics.ErrorReporterImpl
import com.cargopull.executor_driver.backend.analytics.EventLogger
import com.cargopull.executor_driver.backend.analytics.EventLoggerImpl
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenterImpl
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer
import com.cargopull.executor_driver.backend.ringtone.SingleRingTonePlayer
import com.cargopull.executor_driver.backend.settings.AppPreferences
import com.cargopull.executor_driver.backend.settings.AppSettingsService
import com.cargopull.executor_driver.backend.vibro.*
import com.cargopull.executor_driver.backend.web.*
import com.cargopull.executor_driver.gateway.TokenKeeperImpl
import com.cargopull.executor_driver.interactor.DataReceiver
import com.cargopull.executor_driver.utils.Releasable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.client.StompClient
import java.util.concurrent.TimeUnit

class BackendComponent(private val appContext: Context) : Releasable {

    val ringTonePlayer: RingTonePlayer by lazy {
        SingleRingTonePlayer(appContext)
    }
    val shakeItPlayer: ShakeItPlayer by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SingleShakePlayer(appContext, NewPatternMapper())
        } else {
            OldSingleShakePlayer(appContext, OldPatternMapper())
        }
    }
    val locationManager: LocationManager by lazy {
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    val eventLogger: EventLogger by lazy {
        EventLoggerImpl(appSettingsService, appContext)
    }
    val errorReporter: ErrorReporter by lazy {
        ErrorReporterImpl()
    }
    val appSettingsService: AppSettingsService by lazy { AppPreferences(appContext) }
    val geolocationCenter: GeolocationCenter by lazy { GeolocationCenterImpl(appContext) }
    val apiService: ApiService by lazy {
        Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiService::class.java)
    }
    val stompClient: StompClient by lazy {
        Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                socketUrl, null,
                okHttpClient
        )
    }

    fun personalTopicListener(loginReceiver: DataReceiver<String>) =
            PersonalQueueListener(
                    stompClient,
                    loginReceiver
            )

    val fcmSender: Observable<Map<String, String>> by lazy {
        fcmSubject
    }
    val fcmReceiver: Observer<Map<String, String>> by lazy {
        fcmSubject
    }

    private val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val fcmSubject: PublishSubject<Map<String, String>> by lazy {
        PublishSubject.create<Map<String, String>>()
    }
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
        for (interceptor in interceptors) {
            builder.addInterceptor(interceptor)
        }
        // Add logging interceptor for debug build only
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        builder.build()
    }
    private val interceptors: Array<Interceptor> by lazy {
        val tokenKeeper = TokenKeeperImpl(appSettingsService)
        arrayOf(
                ConnectivityInterceptor(connectivityManager),
                SendVersionInterceptor(),
                DeprecatedVersionInterceptor(),
                AuthorizationInterceptor(),
                ServerResponseInterceptor(),
                SendTokenInterceptor(tokenKeeper),
                ReceiveTokenInterceptor(tokenKeeper)
        )
    }
    private val apiUrl: String by lazy {
        val address = if (BuildConfig.DEBUG) appSettingsService.getData("address") else null
        val port = if (BuildConfig.DEBUG) appSettingsService.getData("port") else null
        address?.let { port?.let { "http://$address.xip.io:$port/executor/" } }
                ?: BASE_URL
    }
    private val socketUrl: String by lazy {
        val address = if (BuildConfig.DEBUG) appSettingsService.getData("address") else null
        val port = if (BuildConfig.DEBUG) appSettingsService.getData("port") else null
        address?.let { port?.let { "http://$address.xip.io:$port/executor/ws" } }
                ?: SOCKET_URL
    }

    override fun release() {
    }
}
