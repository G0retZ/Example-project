package com.fasten.executor_driver.presentation.nextroutepoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.OrderRouteUseCase;
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
  private NextRoutePointViewModel movingToClientViewModel;
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

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderRouteUseCase.getOrderRoutePoints())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.never());
    when(orderRouteUseCase.completeTheOrder()).thenReturn(Completable.never());
    movingToClientViewModel = new NextRoutePointViewModelImpl(orderRouteUseCase);
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
    movingToClientViewModel.getViewStateLiveData();
    movingToClientViewModel.getNavigationLiveData();
    movingToClientViewModel.getViewStateLiveData();
    movingToClientViewModel.getNavigationLiveData();

    // Результат:
    verify(orderRouteUseCase, only()).getOrderRoutePoints();
  }

  /**
   * Должен попросить юзкейс закрыть точку маршрута.
   */
  @Test
  public void askUseCaseToCloseRoutePoint() {
    // Дано:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Действие:
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.completeTheOrder();

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
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));

    // Действие:
    movingToClientViewModel.closeRoutePoint();
    movingToClientViewModel.closeRoutePoint();
    movingToClientViewModel.completeTheOrder();
    movingToClientViewModel.closeRoutePoint();
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.completeTheOrder();
    movingToClientViewModel.completeTheOrder();
    movingToClientViewModel.closeRoutePoint();
    movingToClientViewModel.completeTheOrder();
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrderAvailableErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOrdersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Без маршрута" для одной закрытой точки.
   */
  @Test
  public void setNoRouteViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    when(routePoint.isChecked()).thenReturn(true);
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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint.isChecked()).thenReturn(true);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint1.isChecked()).thenReturn(true);
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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint.isChecked()).thenReturn(true);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint1.isChecked()).thenReturn(true);
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    when(routePoint2.isChecked()).thenReturn(true);
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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);
    when(routePoint1.isChecked()).thenReturn(true);
    when(routePoint2.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);
    when(routePoint1.isChecked()).thenReturn(true);
    when(routePoint2.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);
    when(routePoint1.isChecked()).thenReturn(true);
    when(routePoint2.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);
    when(routePoint1.isChecked()).thenReturn(true);
    when(routePoint2.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);
    when(routePoint1.isChecked()).thenReturn(true);
    when(routePoint2.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    movingToClientViewModel.completeTheOrder();

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
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);
    when(routePoint.isChecked()).thenReturn(true);
    when(routePoint1.isChecked()).thenReturn(true);
    when(routePoint2.isChecked()).thenReturn(true);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.completeTheOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateNoRoute(false));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new NextRoutePointViewStateNoRoute(false)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}