package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.OrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
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
public class OrderConfirmationViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderConfirmationViewModel orderConfirmationViewModel;
  @Mock
  private OrderConfirmationUseCase orderConfirmationUseCase;
  @Mock
  private Offer offer;
  @Mock
  private Offer offer1;
  @Mock
  private Offer offer2;

  @Mock
  private Observer<ViewState<OrderConfirmationViewActions>> viewStateObserver;

  @Before
  public void setUp() {
    RxJavaPlugins.setSingleSchedulerHandler(scheduler -> Schedulers.trampoline());
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    when(orderConfirmationUseCase.getOffers()).thenReturn(Flowable.never());
    when(orderConfirmationUseCase.cancelOrder()).thenReturn(Completable.never());
    orderConfirmationViewModel = new OrderConfirmationViewModelImpl(orderConfirmationUseCase);
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы, при первой и только при первой подписке.
   */
  @Test
  public void askOrderConfirmationUseCaseForOffersInitially() {
    // Действие:
    orderConfirmationViewModel.getViewStateLiveData();
    orderConfirmationViewModel.getViewStateLiveData();
    orderConfirmationViewModel.getViewStateLiveData();

    // Результат:
    verify(orderConfirmationUseCase, only()).getOffers();
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askOrderConfirmationUseCaseToSendOrderConfirmationCanceled() {
    // Дано:
    when(orderConfirmationUseCase.cancelOrder()).thenReturn(Completable.complete());

    // Действие:
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    verify(orderConfirmationUseCase, only()).cancelOrder();
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос передачи решения еще не завершился.
   */
  @Test
  public void DoNotTouchOrderConfirmationUseCaseDuringOrderConfirmationSetting() {
    // Действие:
    orderConfirmationViewModel.cancelOrder();
    orderConfirmationViewModel.cancelOrder();
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    verify(orderConfirmationUseCase, only()).cancelOrder();
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
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoNetworkException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForMappingError() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOfferAvailableErrorViewStateToLiveData() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new NoOffersAvailableException());

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OrderConfirmationViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученнымы заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    publishSubject.onNext(offer1);
    publishSubject.onNext(offer2);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer1))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer2))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderConfirmationToLiveDataForCancel() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new OrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateWithoutOrderConfirmationToLiveDataForCancel() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateNetworkError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOffersAvailableErrorViewStateWithoutOrderConfirmationToLiveDataForCancel() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoOffersAvailableException::new));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver)
        .onChanged(new OrderConfirmationViewStateUnavailableError(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForCancelWithoutOffer() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.cancelOrder()).thenReturn(Completable.complete());
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver, times(2))
        .onChanged(new OrderConfirmationViewStatePending(null));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(
        new OrderConfirmationItem(offer)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" сети.
   */
  @Test
  public void setNoNetworkErrorViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoNetworkException::new));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(
        new OrderConfirmationItem(offer)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateNetworkError(
        new OrderConfirmationItem(offer)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "Ошибка" нет доступных заказов.
   */
  @Test
  public void setNoOrderConfirmationsAvailableErrorViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderConfirmationUseCase.cancelOrder())
        .thenReturn(Completable.error(NoOffersAvailableException::new));
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(
        new OrderConfirmationItem(offer)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateUnavailableError(
        new OrderConfirmationItem(offer)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать никакого состояния вида.
   */
  @Test
  public void setNoViewStateToLiveDataForCancel() {
    // Дано:
    PublishSubject<Offer> publishSubject = PublishSubject.create();
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(orderConfirmationUseCase.getOffers())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    when(orderConfirmationUseCase.cancelOrder()).thenReturn(Completable.complete());
    orderConfirmationViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(offer);
    orderConfirmationViewModel.cancelOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(null));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationItem(offer)
    ));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStatePending(
        new OrderConfirmationItem(offer)
    ));
    verifyNoMoreInteractions(viewStateObserver);
  }
}