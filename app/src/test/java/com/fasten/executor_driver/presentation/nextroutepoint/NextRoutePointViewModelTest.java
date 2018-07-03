package com.fasten.executor_driver.presentation.nextroutepoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.entity.RoutePointState;
import com.fasten.executor_driver.interactor.OrderRouteUseCase;
import com.fasten.executor_driver.presentation.CommonNavigate;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class NextRoutePointViewModelTest {

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
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderRouteUseCase.getOrderRoutePoints())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.never());
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.never());
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
    // Результат:
    verify(orderRouteUseCase, only()).getOrderRoutePoints();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(orderRouteUseCase, only()).getOrderRoutePoints();
  }

  /**
   * Должен попросить юзкейс закрыть точку маршрута.
   */
  @Test
  public void askUseCaseToCloseRoutePoint() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Действие:
    viewModel.closeRoutePoint();

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).closeRoutePoint(routePoint);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /**
   * Должен попросить юзкейс завершить заказ.
   */
  @Test
  public void askUseCaseToCompleteTheOrder() {
    // Дано:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Действие:
    viewModel.completeTheOrder();

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).completeTheOrder();
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос закрытия точки маршрута еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringRoutePointClosing() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Действие:
    viewModel.closeRoutePoint();
    viewModel.closeRoutePoint();
    viewModel.completeTheOrder();
    viewModel.closeRoutePoint();
    viewModel.completeTheOrder();

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).closeRoutePoint(routePoint);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос завершения заказа еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderCompleting() {
    // Дано:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Действие:
    viewModel.completeTheOrder();
    viewModel.completeTheOrder();
    viewModel.closeRoutePoint();
    viewModel.completeTheOrder();
    viewModel.closeRoutePoint();

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).completeTheOrder();
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /* Тетсируем переключение состояний от сервера. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new NextRoutePointViewStatePending(null));
  }

  /**
   * Должен вернуть состояние вида "Без маршрута" для одной закрытой точки.
   */
  @Test
  public void setNoRouteViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    publishSubject.onNext(Collections.singletonList(routePoint));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "На маршруте" с полученными маршрутами.
   */
  @Test
  public void setEnRouteViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(true)
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида "после маршрута" после "В процессе" для закрытия точки.
   */
  @Test
  public void setNoRouteFalseViewStateToLiveDataAfterPendingForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.closeRoutePoint();

    // Результат:
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
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(false));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(false)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем переключение состояний при завершении заказа. */

  /**
   * Должен вернуть состояние вида "В процессе" во время "по маршруту" для закрытия точки.
   */
  @Test
  public void setPendingViewStateWithEnRouteViewStateToLiveDataForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setPendingViewStateWithNoRouteTrueViewStateToLiveDataForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setPendingViewStateWithNoRouteFalseViewStateToLiveDataForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setEnRouteViewStateToLiveDataAfterPendingForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.error(Exception::new));
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setNoRouteTrueViewStateToLiveDataAfterPendingForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.completeTheOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(true)
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(true));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида "после маршрута" после "В процессе" для закрытия точки.
   */
  @Test
  public void setNoRouteFalseViewStateToLiveDataAfterPendingForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setNoViewStateToLiveDataAfterPendingWithEnRouteForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.complete());
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setNoViewStateToLiveDataAfterPendingWithNoRouteTrueForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    viewModel.completeTheOrder();

    // Результат:
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
  public void setNoViewStateToLiveDataAfterPendingWithNoRouteFalseForCompleteTheOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint1.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);
    when(routePoint2.getRoutePointState()).thenReturn(RoutePointState.PROCESSED);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
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
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Не должен никуда ходить при закрытии точки маршрута.
   */
  @Test
  public void doNotTouchNavigationObserverForCloseRoutePoint() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к ошибке сети при закрытии точки маршрута.
   */
  @Test
  public void navigateToNoConnectionForCloseRoutePointNoNetworkError() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(orderRouteUseCase.closeRoutePoint(routePoint))
        .thenReturn(Completable.error(new IllegalStateException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить при успешном закрытии точки маршрута.
   */
  @Test
  public void doNotTouchNavigationObserverForCloseRoutePointSuccess() {
    // Дано:
    when(routePoint.getRoutePointState()).thenReturn(RoutePointState.ACTIVE);
    when(orderRouteUseCase.closeRoutePoint(routePoint)).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.closeRoutePoint();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Не должен никуда ходить при завершении заказа.
   */
  @Test
  public void doNotTouchNavigationObserverForCompleteOrder() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.completeTheOrder();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к ошибке сети при завершении заказа.
   */
  @Test
  public void navigateToNoConnectionForCompleteOrderNoNetworkError() {
    // Дано:
    when(orderRouteUseCase.completeTheOrder())
        .thenReturn(Completable.error(new IllegalStateException()));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.completeTheOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить при успешном завершении заказа.
   */
  @Test
  public void doNotTouchNavigationObserverForCompleteOrderSuccess() {
    // Дано:
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.complete());
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.completeTheOrder();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}