package com.cargopull.executor_driver.presentation.geolocation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.GeoLocation;
import com.cargopull.executor_driver.interactor.GeoLocationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private GeoLocationViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private Observer<ViewState<GeoLocationViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigationObserver;

  @Mock
  private GeoLocationUseCase geoLocationUseCase;

  @Before
  public void setUp() {
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.never());
    viewModel = new GeoLocationViewModelImpl(errorReporter, geoLocationUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку в получении геолокации.
   */
  @Test
  public void reportGeoLocationError() {
    // Given:
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.error(SecurityException::new));

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
    verify(errorReporter, only()).reportError(any(SecurityException.class));
  }

  /* Тетсируем работу с публикатором местоположения. */

  /**
   * Должен попросить у публикатора подписку на обновления местоположения.
   */
  @Test
  public void askDataReceiverToSubscribeToLocationUpdates() {
    // Action:
    viewModel.updateGeoLocations();

    // Effect:
    verify(geoLocationUseCase, only()).getGeoLocations();
  }

  /**
   * Не должен просить у юзкейс подписку (после поворотов), если подписка уже была.
   */
  @Test
  public void DoNotTouchDataReceiverAfterFirstSubscription() {
    // Action:
    viewModel.updateGeoLocations();
    viewModel.updateGeoLocations();
    viewModel.updateGeoLocations();

    // Effect:
    verify(geoLocationUseCase, only()).getGeoLocations();
  }

  /* Тетсируем переключение состояний */

  /**
   * Должен вернуть состояния с обновлениями местоположения.
   */
  @Test
  public void setViewStatesWithDataToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9),
        new GeoLocation(11, 22, 33)
    ));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
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

  /**
   * Не должен возвращать состояний при ошибки данных сервера.
   */
  @Test
  public void setServerDataErrorViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(geoLocationUseCase.getGeoLocations()).thenReturn(
        Flowable.just(
            new GeoLocation(1, 2, 3),
            new GeoLocation(4, 5, 6),
            new GeoLocation(7, 8, 9),
            new GeoLocation(11, 22, 33)
        ).concatWith(Flowable.error(Exception::new))
    );
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
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

  /**
   * Не должен возвращать новых состояний при ошибке сети.
   */
  @Test
  public void setNoNewViewStateToLiveDataForNetworkError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(geoLocationUseCase.getGeoLocations()).thenReturn(
        Flowable.just(
            new GeoLocation(1, 2, 3),
            new GeoLocation(4, 5, 6),
            new GeoLocation(7, 8, 9),
            new GeoLocation(11, 22, 33)
        ).concatWith(Flowable.error(IllegalStateException::new))
    );
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
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

  /**
   * Не должен возвращать новых состояний при другой ошибке.
   */
  @Test
  public void setNoNewViewStateToLiveDataForError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(geoLocationUseCase.getGeoLocations()).thenReturn(
        Flowable.just(
            new GeoLocation(1, 2, 3),
            new GeoLocation(4, 5, 6),
            new GeoLocation(7, 8, 9),
            new GeoLocation(11, 22, 33)
        ).concatWith(Flowable.error(Exception::new))
    );
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
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

  /**
   * Не должен возвращать новых состояний при ошибке в геолокации.
   */
  @Test
  public void setNoNewViewStateToLiveDataForGeoLocationError() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(geoLocationUseCase.getGeoLocations()).thenReturn(
        Flowable.just(
            new GeoLocation(1, 2, 3),
            new GeoLocation(4, 5, 6),
            new GeoLocation(7, 8, 9),
            new GeoLocation(11, 22, 33)
        ).concatWith(Flowable.error(SecurityException::new))
    );
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
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
   */
  @Test
  public void navigateToResolveGoeLocationProblem() {
    // Given:
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.error(SecurityException::new));
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
    verify(navigationObserver, only()).onChanged(GeoLocationNavigate.RESOLVE_GEO_PERMISSIONS);
  }

  /**
   * Не должен ничего возвращать для ошибки подключения.
   */
  @Test
  public void setNavigateForNoNetworkError() {
    // Given:
    when(geoLocationUseCase.getGeoLocations())
        .thenReturn(Flowable.error(IllegalStateException::new));
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
    verifyNoInteractions(navigationObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Given:
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.error(Exception::new));
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен ничего возвращать для данных.
   */
  @Test
  public void doNotSetNavigateForData() {
    // Given:
    when(geoLocationUseCase.getGeoLocations()).thenReturn(Flowable.just(
        new GeoLocation(1, 2, 3),
        new GeoLocation(4, 5, 6),
        new GeoLocation(7, 8, 9),
        new GeoLocation(11, 22, 33)
    ));
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    viewModel.updateGeoLocations();

    // Effect:
    verifyNoInteractions(navigationObserver);
  }
}