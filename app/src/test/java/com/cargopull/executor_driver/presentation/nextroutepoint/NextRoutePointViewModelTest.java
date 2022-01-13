package com.cargopull.executor_driver.presentation.nextroutepoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderRouteUseCase;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;

@RunWith(MockitoJUnitRunner.class)
public class NextRoutePointViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private NextRoutePointViewModel viewModel;
  @Mock
  private OrderRouteUseCase orderRouteUseCase;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint1;
  @Mock
  private RoutePoint routePoint2;
  private PublishSubject<List<RoutePoint>> publishSubject;

  @Mock
  private Observer<ViewState<NextRoutePointViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(orderRouteUseCase.getOrderRoutePoints())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.never());
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.QUEUED);
    viewModel = new NextRoutePointViewModelImpl(orderRouteUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы только при создании.
   */
  @Test
  public void askUseCaseForRoutesInitially() {
    // Effect:
    verify(orderRouteUseCase, only()).getOrderRoutePoints();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(orderRouteUseCase, only()).getOrderRoutePoints();
  }

  /**
   * Должен попросить юзкейс закрыть точку маршрута.
   */
  @Test
  public void askUseCaseToCloseRoutePoint() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Action:
    viewModel.closeRoutePoint();

    // Effect:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).closeRoutePoint(routePoint);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос закрытия точки маршрута еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringRoutePointClosing() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Action:
    viewModel.closeRoutePoint();
    viewModel.closeRoutePoint();

    // Effect:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).closeRoutePoint(routePoint);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /* Тетсируем переключение состояний от сервера. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(new NextRoutePointViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "Без маршрута" для одной закрытой точки.
   */
  @Test
  public void setNoRouteViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    publishSubject.onNext(Collections.singletonList(routePoint));

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "На маршруте" с полученными маршрутами.
   */
  @Test
  public void setEnRouteViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateEnRoute(new RoutePointItem(routePoint))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateEnRoute(new RoutePointItem(routePoint1))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateEnRoute(new RoutePointItem(routePoint2))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Без маршрута" в итоге.
   */
  @Test
  public void setNoRouteViewStateLastToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateEnRoute(new RoutePointItem(routePoint))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateEnRoute(new RoutePointItem(routePoint1))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateEnRoute(new RoutePointItem(routePoint2))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new NextRoutePointViewStateNoRoute(false)
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем переключение состояний при закрытии точки. */

  /**
   * Должен вернуть состояние вида "В процессе" во время "по маршруту" для закрытия точки.
   */
  @Test
  public void setPendingViewStateWithEnRouteViewStateToLiveDataForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateEnRoute(
        new RoutePointItem(routePoint)
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateEnRoute(
            new RoutePointItem(routePoint)
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" во время "по городу" для закрытия точки.
   */
  @Test
  public void setPendingViewStateWithNoRouteTrueViewStateToLiveDataForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Action:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(true)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе" во время "после маршрута" для закрытия точки.
   */
  @Test
  public void setPendingViewStateWithNoRouteFalseViewStateToLiveDataForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(false));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(false)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида "по маршруту" после "В процессе" для закрытия точки.
   */
  @Test
  public void setEnRouteViewStateToLiveDataAfterPendingForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateEnRoute(
        new RoutePointItem(routePoint)
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateEnRoute(
            new RoutePointItem(routePoint)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateEnRoute(
        new RoutePointItem(routePoint)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида "по городу" после "В процессе" для закрытия точки.
   */
  @Test
  public void setNoRouteTrueViewStateToLiveDataAfterPendingForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(true)
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида "после маршрута" после "В процессе" для закрытия
   * точки.
   */
  @Test
  public void setNoRouteFalseViewStateToLiveDataAfterPendingForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(false));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(false)));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(false));
    verifyNoMoreInteractions(viewStateObserver);
  }


  /**
   * Не должен возвращать состояний после "В процессе" для закрытия точки.
   */
  @Test
  public void setNoViewStateToLiveDataAfterPendingWithEnRouteForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateEnRoute(
        new RoutePointItem(routePoint)
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateEnRoute(
            new RoutePointItem(routePoint)
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать состояний после "В процессе" для закрытия точки.
   */
  @Test
  public void setNoViewStateToLiveDataAfterPendingWithNoRouteTrueForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Action:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(true)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать состояний после "В процессе" для закрытия точки.
   */
  @Test
  public void setNoViewStateToLiveDataAfterPendingWithNoRouteFalseForCloseRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(false));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(false)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен никуда ходить при закрытии точки маршрута.
   */
  @Test
  public void doNotTouchNavigationObserverForCloseRoutePoint() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к ошибке сети при закрытии точки маршрута.
   */
  @Test
  public void navigateToNoConnectionForCloseRoutePointNoNetworkError() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(orderRouteUseCase.closeRoutePoint(routePoint))
        .thenReturn(Completable.error(new IllegalStateException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить при успешном закрытии точки маршрута.
   */
  @Test
  public void doNotTouchNavigationObserverForCloseRoutePointSuccess() {
    // Given:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(orderRouteUseCase.closeRoutePoint(routePoint)).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }
}