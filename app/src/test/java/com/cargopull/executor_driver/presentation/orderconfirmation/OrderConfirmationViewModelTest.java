package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
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

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderConfirmationViewModel viewModel;
  @Mock
  private OrderConfirmationUseCase useCase;
  @Mock
  private TimeUtils timeUtils;
  private PublishSubject<Long> publishSubject;

  @Mock
  private Observer<ViewState<OrderConfirmationViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    publishSubject = PublishSubject.create();
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.never());
    when(useCase.getOrderDecisionTimeout())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new OrderConfirmationViewModelImpl(useCase, timeUtils);
  }

  /* Тетсируем работу с юзкейсом принятия заказа. */

  /**
   * Должен просить юзкейс получать таймауты при создании.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Результат:
    verify(useCase, only()).getOrderDecisionTimeout();
  }

  /**
   * Не должен просить юзкейс получать таймауты на подписках.
   */
  @Test
  public void doNotAskUseCaseForOrdersOnSubscriptions() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(useCase, only()).getOrderDecisionTimeout();
  }

  /**
   * Должен попросить юзкейс передать принятие заказа.
   */
  @Test
  public void askUseCaseToSendOrderAccepted() {
    // Дано:
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.just(""));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(useCase).getOrderDecisionTimeout();
    verify(useCase).sendDecision(true);
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askUseCaseToSendOrderDeclined() {
    // Дано:
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.just(""));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(useCase).getOrderDecisionTimeout();
    verify(useCase).sendDecision(false);
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос передачи решения еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Действие:
    viewModel.acceptOrder();
    viewModel.declineOrder();
    viewModel.acceptOrder();

    // Результат:
    verify(useCase).getOrderDecisionTimeout();
    verify(useCase).sendDecision(true);
    verifyNoMoreInteractions(useCase);
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
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученными таймаутами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(15_000L);
    publishSubject.onNext(17_000L);
    publishSubject.onNext(12_000L);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(15_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(17_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида при получении ошибки истечения заказа с сообщением.
   */
  @Test
  public void setNoViewStateToLiveDataForExpiredError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.getOrderDecisionTimeout()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Long>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Long> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(15_000L);
                  emitter.onError(new OrderOfferExpiredException("message"));
                } else {
                  emitter.onNext(17_000L);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderConfirmationViewModelImpl(useCase, timeUtils);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onComplete();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(15_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(17_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида при получении ошибки истечения заказа без сообщения.
   */
  @Test
  public void setNoViewStateToLiveDataForExpiredErrorWithoutMessage() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.getOrderDecisionTimeout()).thenReturn(
        Observable.create(
            new ObservableOnSubscribe<Long>() {
              private boolean run;

              @Override
              public void subscribe(ObservableEmitter<Long> emitter) {
                if (!run) {
                  run = true;
                  emitter.onNext(15_000L);
                  emitter.onError(new OrderOfferDecisionException());
                } else {
                  emitter.onNext(17_000L);
                }
              }
            }
        ).startWith(publishSubject)
            .toFlowable(BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderConfirmationViewModelImpl(useCase, timeUtils);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onComplete();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(15_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(17_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была иная ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onError(new Exception());

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(OrderConfirmationViewStatePending.class));
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForCounterTimeOut() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.counterTimeOut();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForAccept() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForDecline() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида при ошибке сети.
   */
  @Test
  public void setIdleViewStateWithoutOrderToLiveDataForAcceptOnNoNetworkError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида при ошибке сети.
   */
  @Test
  public void setIdleViewStateWithoutOrderToLiveDataForDeclineOnNoNetworkError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида при ошибке в данных.
   */
  @Test
  public void setIdleViewStateWithoutOrderToLiveDataForAcceptOnDataMappingError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(DataMappingException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида при ошибке в данных.
   */
  @Test
  public void setIdleViewStateWithoutOrderToLiveDataForDeclineOnDataMappingError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(DataMappingException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида результата с сообщением для неуспешного принятия.
   */
  @Test
  public void setResultViewStateToLiveDataForAcceptExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferExpiredException("34")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateResult("34"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида результата с сообщением для неуспешного отказа.
   */
  @Test
  public void setResultViewStateToLiveDataForDeclineExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferExpiredException("43")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateResult("43"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида для неуспешного принятия.
   */
  @Test
  public void setIdleViewStateToLiveDataForAcceptExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferDecisionException()));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида для неуспешного отказа.
   */
  @Test
  public void setIdleViewStateToLiveDataForDeclineExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferDecisionException()));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида результата с сообщением для успешного принятия.
   */
  @Test
  public void setResultViewStateToLiveDataForAcceptWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just("21"));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateResult("21"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать новых состояний вида для успешного отказа.
   */
  @Test
  public void setResultViewStateToLiveDataForDeclineWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just("12"));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(12_000L);
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateIdle(
        new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить изначально.
   */
  @Test
  public void doNotSetNavigateInitially() {
    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен перейти к закрытию карточки.
   */
  @Test
  public void setNavigateToCloseForMessageConsumed() {
    // Действие:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.messageConsumed();

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderConfirmationNavigate.CLOSE);
  }

  /**
   * Не должен никуда переходить для вида "В процессе отказа".
   */
  @Test
  public void doNotSetNavigateForDeclinePending() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения" для отказа.
   */
  @Test
  public void setNavigateToNoConnectionForDecline() {
    // Дано:
    when(useCase.sendDecision(false))
        .thenReturn(Single.error(IllegalStateException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Должен сразу перейти к закрытию карточки после успешного отказа.
   */
  @Test
  public void doNotSetNavigateForDeclineSuccess() {
    // Дано:
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(OrderConfirmationNavigate.CLOSE);
  }

  /**
   * Не должен никуда переходить для вида "В процессе отказа".
   */
  @Test
  public void doNotSetNavigateForAcceptPending() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorFor() {
    // Дано:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера" для принятия.
   */
  @Test
  public void setNavigateToServerDataErrorForAccept() {
    // Дано:
    when(useCase.sendDecision(true))
        .thenReturn(Single.error(DataMappingException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера" для отказа.
   */
  @Test
  public void setNavigateToServerDataErrorForDecline() {
    // Дано:
    when(useCase.sendDecision(false))
        .thenReturn(Single.error(DataMappingException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения" для отказа.
   */
  @Test
  public void setNavigateToNoConnectionForAccept() {
    // Дано:
    when(useCase.sendDecision(true))
        .thenReturn(Single.error(IllegalStateException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить после отказа.
   */
  @Test
  public void doNotSetNavigateForAcceptSuccess() {
    // Дано:
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(navigateObserver);
  }
}