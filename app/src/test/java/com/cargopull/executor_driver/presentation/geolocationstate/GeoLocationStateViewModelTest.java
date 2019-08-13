package com.cargopull.executor_driver.presentation.geolocationstate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.location.LocationManager;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.interactor.CommonGateway;
import com.cargopull.executor_driver.presentation.ImageTextViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.Pair;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@RunWith(Enclosed.class)
public class GeoLocationStateViewModelTest {

  public static class CommonTests {

    @ClassRule
    public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
    @Rule
    public MockitoRule mockRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private GeoLocationStateViewModel viewModel;
    @Mock
    private CommonGateway<Boolean> gateway;
    @Mock
    private EventLogger eventLogger;
    @Mock
    private LocationManager locationManager;
    @Mock
    private TimeUtils timeUtils;
    @Mock
    private Observer<ViewState<ImageTextViewActions>> viewStateObserver;

    private PublishSubject<Boolean> publishSubject;

    @Before
    public void setUp() {
      publishSubject = PublishSubject.create();
      when(gateway.getData()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
      viewModel = new GeoLocationStateViewModelImpl(eventLogger, locationManager, timeUtils,
          gateway);
    }

    /* Тетсируем работу с гейтвеем. */

    /**
     * Должен запросить у гейтвея состояния сервисов местоположения изначально.
     */
    @Test
    public void askGatewayForGeolocationStateInitially() {
      // Результат:
      verify(gateway, only()).getData();
    }

    /**
     * Не должен трогать гейтвей на подписках.
     */
    @Test
    public void doNotTouchGatewayOnSubscriptions() {
      // Действие:
      viewModel.getViewStateLiveData();
      viewModel.getNavigationLiveData();
      viewModel.getViewStateLiveData();
      viewModel.getNavigationLiveData();

      // Результат:
      verify(gateway, only()).getData();
    }

    /* Тетсируем работу с менеджером локаций. */

    /**
     * Не должен трогать менеджер до получения состояния сервисов местоположения.
     */
    @Test
    public void doNotTouchLocationManager() {
      // Действие:
      viewModel.getViewStateLiveData();
      viewModel.getNavigationLiveData();
      viewModel.getViewStateLiveData();
      viewModel.getNavigationLiveData();

      // Результат:
      verifyZeroInteractions(locationManager);
    }

    /**
     * Должен запросить у менеджера состояние провайдеров геолокации.
     */
    @Test
    public void askLocationManagerForServiceProvidersStatus() {
      // Действие:
      publishSubject.onNext(true);
      publishSubject.onNext(false);
      viewModel.checkSettings();

      // Результат:
      verify(locationManager, times(3)).isProviderEnabled(LocationManager.GPS_PROVIDER);
      verify(locationManager, times(3)).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
      verifyNoMoreInteractions(locationManager);
    }

    /* Тетсируем работу с логгером. */

    /**
     * Не должен трогать логгер до получения состояния сервисов местоположения.
     */
    @Test
    public void doNotTouchEventLogger() {
      // Действие:
      viewModel.getViewStateLiveData();
      viewModel.getNavigationLiveData();
      viewModel.getViewStateLiveData();
      viewModel.getNavigationLiveData();

      // Результат:
      verifyZeroInteractions(locationManager);
    }

    /* Тетсируем смену состояний. */

    /**
     * Должен передать состояние вида готовности для доступности геоданных.
     */
    @Test
    public void showGeolocationStateReadyForAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);

      // Действие:
      publishSubject.onNext(true);

      // Результат:
      inOrder.verify(viewStateObserver).onChanged(any(GeoLocationStateReadyViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида готовности для недоступности геоданных.
     */
    @Test
    public void showGeolocationStateReadyForUnAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);

      // Действие:
      publishSubject.onNext(false);

      // Результат:
      inOrder.verify(viewStateObserver).onChanged(any(GeoLocationStateReadyViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида недоступности гелокации для доступности геоданных.
     */
    @Test
    public void showGeolocationStateNoLocationForAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);

      // Действие:
      publishSubject.onNext(true);

      // Результат:
      inOrder.verify(viewStateObserver).onChanged(any(GeoLocationStateNoLocationViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида недоступности гелокации для недоступности геоданных.
     */
    @Test
    public void showGeolocationStateNoLocationForUnAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);

      // Действие:
      publishSubject.onNext(false);

      // Результат:
      inOrder.verify(viewStateObserver).onChanged(any(GeoLocationStateNoLocationViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида недоступности GPS для доступности геоданных.
     */
    @Test
    public void showGeolocationStateNoGpsForAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);

      // Действие:
      publishSubject.onNext(true);

      // Результат:
      inOrder.verify(viewStateObserver)
          .onChanged(any(GeoLocationStateNoGpsDetectionViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида недоступности GPS для недоступности геоданных.
     */
    @Test
    public void showGeolocationStateNoGpsForUnAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(true);

      // Действие:
      publishSubject.onNext(false);

      // Результат:
      inOrder.verify(viewStateObserver)
          .onChanged(any(GeoLocationStateNoGpsDetectionViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида недоступности сети для доступности геоданных.
     */
    @Test
    public void showGeolocationStateNoNetworkForAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);

      // Действие:
      publishSubject.onNext(true);

      // Результат:
      inOrder.verify(viewStateObserver)
          .onChanged(any(GeoLocationStateNoNetworkDetectionViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }

    /**
     * Должен передать состояние вида недоступности сети для недоступности геоданных.
     */
    @Test
    public void showGeolocationStateNoNetworkForUnAvailable() {
      // Дано:
      InOrder inOrder = Mockito.inOrder(viewStateObserver);
      viewModel.getViewStateLiveData().observeForever(viewStateObserver);
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)).thenReturn(false);

      // Действие:
      publishSubject.onNext(false);

      // Результат:
      inOrder.verify(viewStateObserver)
          .onChanged(any(GeoLocationStateNoNetworkDetectionViewState.class));
      verifyNoMoreInteractions(viewStateObserver);
    }
  }

  @RunWith(Parameterized.class)
  public static class LogEventsTests {

    @ClassRule
    public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
    private final boolean fromAvailability;
    private final boolean toAvailability;
    private final boolean toAvailability1;
    private final boolean fromGps;
    private final boolean toGps;
    private final boolean toGps1;
    private final boolean fromNetwork;
    private final boolean toNetwork;
    private final boolean toNetwork1;
    private final int sendReport;
    private final int tuInvocations;
    @Rule
    public MockitoRule mockRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private GeoLocationStateViewModel viewModel;
    @Mock
    private CommonGateway<Boolean> gateway;
    @Mock
    private EventLogger eventLogger;
    @Mock
    private LocationManager locationManager;
    @Mock
    private TimeUtils timeUtils;
    private PublishSubject<Boolean> publishSubject;

    public LogEventsTests(Pair<List<Boolean>, Pair<Integer, Integer>> conditions) {
      fromAvailability = conditions.first.get(0);
      fromGps = conditions.first.get(1);
      fromNetwork = conditions.first.get(2);
      toAvailability = conditions.first.get(3);
      toGps = conditions.first.get(4);
      toNetwork = conditions.first.get(5);
      toAvailability1 = conditions.first.get(6);
      toGps1 = conditions.first.get(7);
      toNetwork1 = conditions.first.get(8);
      sendReport = conditions.second.first;
      tuInvocations = conditions.second.second;
      System.out.println("" + fromAvailability + ", " + fromGps + ", " + fromNetwork);
      System.out.println("" + toAvailability + ", " + toGps + ", " + toNetwork);
      System.out.println("" + toAvailability1 + ", " + toGps1 + ", " + toNetwork1);
      System.out.println("---");
      System.out.println("" + sendReport + ", " + tuInvocations);
    }

    @Parameterized.Parameters
    public static Iterable<Pair<List<Boolean>, Pair<Integer, Integer>>> primeConditions() {
      List<List<Boolean>> states = Arrays.asList(
          // Состояния: Доступность, GPS, Сеть
          Arrays.asList(false, false, false), // 0
          Arrays.asList(false, false, true),  // 1
          Arrays.asList(false, true, false),  // 2
          Arrays.asList(false, true, true),   // 3
          Arrays.asList(true, false, false),  // 4
          Arrays.asList(true, false, true),   // 5
          Arrays.asList(true, true, false),   // 6
          Arrays.asList(true, true, true)     // 7
      );

      List<Pair<List<Boolean>, Pair<Integer, Integer>>> pairs = new ArrayList<>();
      for (int from = 0; from < states.size(); from++) {
        for (int to = 0; to < states.size(); to++) {
          for (int to1 = 0; to1 < states.size(); to1++) {
            ArrayList<Boolean> booleans = new ArrayList<>(states.get(from));
            booleans.addAll(states.get(to));
            booleans.addAll(states.get(to1));
            int send = 0;
            if (
              // Если переход по состояниям был: 5|6|7 -> 1|2|3 -> 5|6|7
                states.get(from).get(0) && (states.get(from).get(1) || states.get(from).get(2))
                    && !states.get(to).get(0) && (states.get(to).get(1) || states.get(to).get(2))
                    && states.get(to1).get(0) && (states.get(to1).get(1) || states.get(to1).get(2))
            ) {
              // Отправляем отчет о восстановлении доступности геолокации
              send = 1;
            } else if (
              // Если переход по состояниям был: 5|6|7 -> 1|2|3 -> 0|4
                states.get(from).get(0) && (states.get(from).get(1) || states.get(from).get(2))
                    && !states.get(to).get(0) && (states.get(to).get(1) || states.get(to).get(2))
                    && !states.get(to1).get(1) && !states.get(to1).get(2)
            ) {
              // Отправляем отчет о потери доступности геолокации
              send = -1;
            }
            int invocations = 0;
            if (
              // Если переход по состояниям был: 5|6|7 -> 1|2|3
                states.get(from).get(0) && (states.get(from).get(1) || states.get(from).get(2))
                    && !states.get(to).get(0) && (states.get(to).get(1) || states.get(to).get(2))
            ) {
              // Добавляем взаимодействие с утилитой времени в первой смене
              invocations = 1;
            } else if (
              // Если переход по состояниям был: * -> 5|6|7 -> 1|2|3
                states.get(to).get(0) && (states.get(to).get(1) || states.get(to).get(2))
                    && !states.get(to1).get(0) && (states.get(to1).get(1) || states.get(to1).get(2))
            ) {
              // Добавляем взаимодействие с утилитой времени во второй смене
              invocations = 2;
            }
            if (
              // Если переход по состояниям был: 5|6|7 -> 1|2|3 -> 0|4|5|6|7
                states.get(from).get(0) && (states.get(from).get(1) || states.get(from).get(2))
                    && !states.get(to).get(0) && (states.get(to).get(1) || states.get(to).get(2))
                    && (states.get(to1).get(0) || !states.get(to1).get(1) && !states.get(to1)
                    .get(2))
            ) {
              // Добавляем взаимодействие с утилитой времени в обеих сменах
              invocations = 3;
            }
            pairs.add(new Pair<>(booleans, new Pair<>(send, invocations)));
          }
        }
      }

      return pairs;
    }

    @Before
    public void setUp() {
      publishSubject = PublishSubject.create();
      if (tuInvocations > 0) {
        when(timeUtils.currentTimeMillis()).thenReturn(10L, 300L);
      }
      when(gateway.getData()).thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
      when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
          .thenReturn(fromGps, toGps, toGps1);
      when(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
          .thenReturn(fromNetwork, toNetwork, toNetwork1);
      viewModel = new GeoLocationStateViewModelImpl(eventLogger, locationManager, timeUtils,
          gateway);
    }

    /**
     * Должен запросить таймстамп при определенных условиях.
     */
    @Test
    public void askTimeUtilsForTimeStamp() {
      // Действие:
      publishSubject.onNext(fromAvailability);
      publishSubject.onNext(toAvailability);

      // Результат:
      verify(timeUtils, times(tuInvocations % 2 == 1 ? 1 : 0)).currentTimeMillis();
      verifyNoMoreInteractions(timeUtils);
    }

    /**
     * Должен запросить таймстамп повторно при определенных условиях.
     */
    @Test
    public void askTimeUtilsForTimeStampAgain() {
      // Действие:
      publishSubject.onNext(fromAvailability);
      publishSubject.onNext(toAvailability);
      publishSubject.onNext(toAvailability1);

      // Результат:
      verify(timeUtils, times(tuInvocations % 2 + tuInvocations / 2)).currentTimeMillis();
      verifyNoMoreInteractions(timeUtils);
    }

    /**
     * Должен запросить у логгера отправку отчета при определенных условиях.
     */
    @Test
    public void askEventLoggerToSendReport() {
      // Действие:
      publishSubject.onNext(fromAvailability);
      publishSubject.onNext(toAvailability);
      publishSubject.onNext(toAvailability1);

      // Результат:
      if (sendReport == 1) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loss_duration", "290");
        verify(eventLogger, only()).reportEvent("geolocation_restored", params);
      } else if (sendReport == -1) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loss_duration", "290");
        verify(eventLogger, only()).reportEvent("geolocation_lost", params);
      } else {
        verifyZeroInteractions(eventLogger);
      }
    }

    /**
     * Не должен запрашивать у логгера отправку отчета при обновлении настроек.
     */
    @Test
    public void doNotAskEventLoggerToSendReportOnCheckSettings() {
      // Действие:
      publishSubject.onNext(fromAvailability);
      publishSubject.onNext(toAvailability);
      publishSubject.onNext(toAvailability1);
      viewModel.checkSettings();

      // Результат:
      if (sendReport == 1) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loss_duration", "290");
        verify(eventLogger, only()).reportEvent("geolocation_restored", params);
      } else if (sendReport == -1) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loss_duration", "290");
        verify(eventLogger, only()).reportEvent("geolocation_lost", params);
      } else {
        verifyZeroInteractions(eventLogger);
      }
    }
  }
}