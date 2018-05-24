package com.fasten.executor_driver.presentation.nextroutepoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
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
   * Не должен трогать юзкейс, если предыдущий запрос закрытия точки маршрута еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringRoutePointClosing() {
    // Действие:
    movingToClientViewModel.closeRoutePoint();
    movingToClientViewModel.closeRoutePoint();
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).closeRoutePoint(any());
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /* Тетсируем переключение состояний. */

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
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
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
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStateError.class));
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
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStateError.class));
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
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы маршрутами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    publishSubject.onNext(Arrays.asList(routePoint1, routePoint2, routePoint));
    publishSubject.onNext(Arrays.asList(routePoint2, routePoint1, routePoint));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateIdle(
            new RoutePointItem(Arrays.asList(routePoint, routePoint2, routePoint1))
        )
    );
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateIdle(
            new RoutePointItem(Arrays.asList(routePoint1, routePoint, routePoint2))
        )
    );
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateIdle(
            new RoutePointItem(Arrays.asList(routePoint2, routePoint2, routePoint1))
        )
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(NextRoutePointViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderToLiveDataForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any()))
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrdersAvailableErrorViewStateWithoutOrderToLiveDataForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any()))
        .thenReturn(Completable.error(NoOrdersAvailableException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStateError.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setIdleViewStateToLiveDataForCloseRoutePointWithoutRoute() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(any(NextRoutePointViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateIdle(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any()))
        .thenReturn(Completable.error(NoNetworkException::new));
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateIdle(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateError(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setIdleViewStateToLiveDataForCloseRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.closeRoutePoint(any())).thenReturn(Completable.complete());
    movingToClientViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    movingToClientViewModel.closeRoutePoint();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(NextRoutePointViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStateIdle(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    inOrder.verify(viewStateObserver).onChanged(new NextRoutePointViewStatePending(
        new RoutePointItem(Arrays.asList(routePoint, routePoint1, routePoint2))
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}