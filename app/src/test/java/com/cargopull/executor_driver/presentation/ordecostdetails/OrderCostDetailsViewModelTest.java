package com.cargopull.executor_driver.presentation.ordecostdetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderCostDetailsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
public class OrderCostDetailsViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderCostDetailsViewModel viewModel;
  @Mock
  private OrderCostDetailsUseCase orderCostDetailsUseCase;
  @Mock
  private OrderCostDetails orderCostDetails;
  @Mock
  private OrderCostDetails orderCostDetails1;
  @Mock
  private OrderCostDetails orderCostDetails2;

  @Mock
  private Observer<ViewState<OrderCostDetailsViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderCostDetailsUseCase.getOrderCostDetails()).thenReturn(Flowable.never());
    viewModel = new OrderCostDetailsViewModelImpl(orderCostDetailsUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы, при первой и только при первой подписке.
   */
  @Test
  public void askUseCaseForOrderCostDetailsInitially() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getViewStateLiveData();

    // Результат:
    verify(orderCostDetailsUseCase, only()).getOrderCostDetails();
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Дано:
    InOrder inOrderCostDetails = Mockito.inOrder(viewStateObserver);

    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrderCostDetails.verify(viewStateObserver)
        .onChanged(any(OrderCostDetailsViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Дано:
    PublishSubject<OrderCostDetails> publishSubject = PublishSubject.create();
    when(orderCostDetailsUseCase.getOrderCostDetails())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(new OrderCostDetailsViewStatePending(null));
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    PublishSubject<OrderCostDetails> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderCostDetailsUseCase.getOrderCostDetails())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(orderCostDetails);
    publishSubject.onNext(orderCostDetails1);
    publishSubject.onNext(orderCostDetails2);

    // Результат:
    inOrder.verify(viewStateObserver)
        .onChanged(new OrderCostDetailsViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderCostDetailsViewStateIdle(
        new OrderCostDetailsItem(orderCostDetails))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderCostDetailsViewStateIdle(
        new OrderCostDetailsItem(orderCostDetails1))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderCostDetailsViewStateIdle(
        new OrderCostDetailsItem(orderCostDetails2))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Дано:
    PublishSubject<OrderCostDetails> publishSubject = PublishSubject.create();
    when(orderCostDetailsUseCase.getOrderCostDetails())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }
}