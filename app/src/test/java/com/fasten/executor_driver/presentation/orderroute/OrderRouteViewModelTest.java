package com.fasten.executor_driver.presentation.orderroute;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
public class OrderRouteViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderRouteViewModel viewModel;
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
  private Observer<ViewState<OrderRouteViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderRouteUseCase.getOrderRoutePoints())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderRouteUseCase.nextRoutePoint(any())).thenReturn(Completable.never());
    viewModel = new OrderRouteViewModelImpl(orderRouteUseCase);
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
   * Должен попросить юзкейс выбрать следующую точку маршрута.
   */
  @Test
  public void askUseCaseToSelectNextRoutePoint() {
    // Действие:
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).nextRoutePoint(routePoint1);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос выбора следующей точки маршрута еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringSelectNextRoutePoint() {
    // Дано:
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint2));

    // Результат:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).nextRoutePoint(routePoint);
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
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStateServerDataError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStateServerDataError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrderAvailableErrorViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOrdersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStateServerDataError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "маршрута".
   */
  @Test
  public void setOrderRouteViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Collections.singletonList(routePoint));
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    publishSubject.onNext(Arrays.asList(routePoint1, routePoint, routePoint2));
    publishSubject.onNext(Arrays.asList(routePoint2, routePoint1, routePoint));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Collections.singletonList(new RoutePointItem(routePoint))
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint),
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint),
            new RoutePointItem(routePoint2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint2),
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint)
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем переключение состояний при выборе следующей точки. */

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateStateToLiveDataForSelectNextRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint),
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(
        new OrderRouteViewState(
            Arrays.asList(
                new RoutePointItem(routePoint),
                new RoutePointItem(routePoint1),
                new RoutePointItem(routePoint2)
            )
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида.
   */
  @Test
  public void setOrderRouteViewStateToLiveDataAfterPendingForSelectNextRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.nextRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint),
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(
        new OrderRouteViewState(
            Arrays.asList(
                new RoutePointItem(routePoint),
                new RoutePointItem(routePoint1),
                new RoutePointItem(routePoint2)
            )
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint),
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint2)
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать состояний после "В процессе".
   */
  @Test
  public void setNoViewStateToLiveDataAfterPendingForSelectNextRoutePoint() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.nextRoutePoint(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewState(
        Arrays.asList(
            new RoutePointItem(routePoint),
            new RoutePointItem(routePoint1),
            new RoutePointItem(routePoint2)
        )
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(
        new OrderRouteViewState(
            Arrays.asList(
                new RoutePointItem(routePoint),
                new RoutePointItem(routePoint1),
                new RoutePointItem(routePoint2)
            )
        )
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Не должен никуда ходить при запросе смены следующей точки маршрута.
   */
  @Test
  public void doNotTouchNavigationObserverForNextRouteSelection() {
    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть перейти к ошибке сети при запросе смены следующей точки маршрута.
   */
  @Test
  public void navigateToNoConnectionForNextRouteSelectionNoNetworkError() {
    // Дано:
    when(orderRouteUseCase.nextRoutePoint(routePoint))
        .thenReturn(Completable.error(new IllegalStateException()));

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderRouteNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда ходить при успешной смене следующей точки маршрута.
   */
  @Test
  public void doNotTouchNavigationObserverForNextRouteSelectionSuccess() {
    // Дано:
    when(orderRouteUseCase.nextRoutePoint(routePoint)).thenReturn(Completable.complete());

    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}