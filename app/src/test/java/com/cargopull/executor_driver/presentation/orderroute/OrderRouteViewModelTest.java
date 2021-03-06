package com.cargopull.executor_driver.presentation.orderroute;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.RoutePoint;
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
public class OrderRouteViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
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
    when(orderRouteUseCase.getOrderRoutePoints())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderRouteUseCase.nextRoutePoint(any())).thenReturn(Completable.never());
    viewModel = new OrderRouteViewModelImpl(orderRouteUseCase);
  }

  /* ?????????????????? ???????????? ?? ???????????????? ????????????. */

  /**
   * ???????????? ?????????????? ???????????? ???????????????? ???????????? ???????????? ?????? ????????????????.
   */
  @Test
  public void askUseCaseForRoutesInitially() {
    // Effect:
    verify(orderRouteUseCase, only()).getOrderRoutePoints();
  }

  /**
   * ???? ???????????? ?????????????? ???????????? ???? ??????????????????.
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
   * ???????????? ?????????????????? ???????????? ?????????????? ?????????????????? ?????????? ????????????????.
   */
  @Test
  public void askUseCaseToSelectNextRoutePoint() {
    // Action:
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Effect:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).nextRoutePoint(routePoint1);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /**
   * ???? ???????????? ?????????????? ????????????, ???????? ???????????????????? ???????????? ???????????? ?????????????????? ?????????? ???????????????? ?????? ????
   * ????????????????????.
   */
  @Test
  public void DoNotTouchUseCaseDuringSelectNextRoutePoint() {
    // Given:
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint2));

    // Effect:
    verify(orderRouteUseCase).getOrderRoutePoints();
    verify(orderRouteUseCase).nextRoutePoint(routePoint);
    verifyNoMoreInteractions(orderRouteUseCase);
  }

  /* ?????????????????? ???????????????????????? ?????????????????? ???? ??????????????. */

  /**
   * ???????????? ?????????????? ?????????????????? ???????? ???????????????? ????????????????????.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(new OrderRouteViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * ???? ???????????? ???????????? ???????? ?????????????????? ???????? ???????? ???????? ????????????.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onError(new NoNetworkException());

    // Effect:
    verify(viewStateObserver, only()).onChanged(new OrderRouteViewStatePending(null));
  }

  /**
   * ???????????? ?????????????? ?????????????????? ???????? "????????????????".
   */
  @Test
  public void setOrderRouteViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Collections.singletonList(routePoint));
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    publishSubject.onNext(Arrays.asList(routePoint1, routePoint, routePoint2));
    publishSubject.onNext(Arrays.asList(routePoint2, routePoint1, routePoint));

    // Effect:
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

  /* ?????????????????? ???????????????????????? ?????????????????? ?????? ???????????? ?????????????????? ??????????. */

  /**
   * ???????????? ?????????????? ?????????????????? ???????? "?? ????????????????".
   */
  @Test
  public void setPendingViewStateStateToLiveDataForSelectNextRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Effect:
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
   * ???????????? ?????????????? ???????????????????? ?????????????????? ????????.
   */
  @Test
  public void setOrderRouteViewStateToLiveDataAfterPendingForSelectNextRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.nextRoutePoint(any())).thenReturn(Completable.error(Exception::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Effect:
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
   * ???? ???????????? ???????????????????? ?????????????????? ?????????? "?? ????????????????".
   */
  @Test
  public void setNoViewStateToLiveDataAfterPendingForSelectNextRoutePoint() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderRouteUseCase.nextRoutePoint(any())).thenReturn(Completable.complete());
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(Arrays.asList(routePoint, routePoint1, routePoint2));
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint1));

    // Effect:
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

  /* ?????????????????? ??????????????????. */

  /**
   * ???????????? ?????????????? "?????????????? ?? ???????????? ???????????? ??????????????".
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
   * ???? ???????????? ???????????? ???????????? ?????? ?????????????? ?????????? ?????????????????? ?????????? ????????????????.
   */
  @Test
  public void doNotTouchNavigationObserverForNextRouteSelection() {
    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * ???????????? ?????????????? ?????????????? ?? ???????????? ???????? ?????? ?????????????? ?????????? ?????????????????? ?????????? ????????????????.
   */
  @Test
  public void navigateToNoConnectionForNextRouteSelectionNoNetworkError() {
    // Given:
    when(orderRouteUseCase.nextRoutePoint(routePoint))
        .thenReturn(Completable.error(new IllegalStateException()));

    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * ???? ???????????? ???????????? ???????????? ?????? ???????????????? ?????????? ?????????????????? ?????????? ????????????????.
   */
  @Test
  public void doNotTouchNavigationObserverForNextRouteSelectionSuccess() {
    // Given:
    when(orderRouteUseCase.nextRoutePoint(routePoint)).thenReturn(Completable.complete());

    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.selectNextRoutePoint(new RoutePointItem(routePoint));

    // Effect:
    verifyNoInteractions(navigateObserver);
  }
}