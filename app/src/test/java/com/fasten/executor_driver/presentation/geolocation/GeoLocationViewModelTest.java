package com.fasten.executor_driver.presentation.geolocation;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.interactor.GeoLocationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private GeoLocationViewModel mapViewModel;
  @Mock
  private Observer<ViewState<GeoLocationViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigationObserver;

  @Mock
  private GeoLocationUseCase geoLocationUseCase;

  @Before
  public void setUp() throws Exception {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.never());
    mapViewModel = new GeoLocationViewModelImpl(geoLocationUseCase);
  }

  /* Тетсируем работу с публикатором местоположения. */

  /**
   * Должен попросить у публикатора подписку на обновления местоположения.
   *
   * @throws Exception error
   */
  @Test
  public void askDataReceiverToSubscribeToLocationUpdates() throws Exception {
    // Действие:
    mapViewModel.updateGeoLocations();

    // Результат:
    verify(geoLocationUseCase, only()).getGeoLocations();
  }

  /**
   * Не должен просить у юзкейс подписку (после поворотов), если подписка уже была.
   *
   * @throws Exception error
   */
  @Test
  public void DoNotTouchDataReceiverAfterFirstSubscription() throws Exception {
    // Действие:
    mapViewModel.updateGeoLocations();
    mapViewModel.updateGeoLocations();
    mapViewModel.updateGeoLocations();

    // Результат:
    verify(geoLocationUseCase, only()).getGeoLocations();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояния с обновлениями местоположения.
   *
   * @throws Exception error
   */
  @Test
  public void setViewStatesWithDataToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9),
        new GeoLocation(11, 22, 33)
    ));

    // Действие:
    mapViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    mapViewModel.updateGeoLocations();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(
        new GeoLocationViewState(new GeoLocation(1, 2, 3))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new GeoLocationViewState(new GeoLocation(4, 5, 6))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new GeoLocationViewState(new GeoLocation(7, 8, 9))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new GeoLocationViewState(new GeoLocation(11, 22, 33))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Должен вернуть "перейти к решению проблемы с геолокацией".
   *
   * @throws Exception error
   */
  @Test
  public void navigateToResolveGoeLocationProblem() throws Exception {
    // Дано:
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.error(NoNetworkException::new));

    // Действие:
    mapViewModel.getNavigationLiveData().observeForever(navigationObserver);
    mapViewModel.updateGeoLocations();

    // Результат:
    verify(navigationObserver, only()).onChanged(GeoLocationNavigate.RESOLVE_GEO_PROBLEM);
  }
}