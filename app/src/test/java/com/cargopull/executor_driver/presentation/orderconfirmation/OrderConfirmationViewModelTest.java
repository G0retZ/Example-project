package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.EventLogger;
import com.cargopull.executor_driver.utils.Pair;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Single;
import java.util.HashMap;
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
  @Mock
  private EventLogger eventLogger;
  @Mock
  private Observer<ViewState<OrderConfirmationViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;
  private FlowableEmitter<Pair<Long, Long>> emitter;

  @Before
  public void setUp() {
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.never());
    when(useCase.getOrderDecisionTimeout()).thenReturn(
        Flowable.create(e -> emitter = e, BackpressureStrategy.BUFFER)
    );
    viewModel = new OrderConfirmationViewModelImpl(useCase, timeUtils, eventLogger);
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
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

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
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

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

  /* Тетсируем работу со временем. */

  /**
   * Не должен просить текущий таймстамп изначально.
   */
  @Test
  public void DoNotAskForCurrentTimeStampInitially() {
    // Результат:
    verifyZeroInteractions(timeUtils);
  }

  /**
   * Должен запросить текущий таймстамп на новые данные.
   */
  @Test
  public void askForCurrentTimeStampOnNewData() {
    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    emitter.onNext(new Pair<>(3L, 10_000L));
    emitter.onNext(new Pair<>(101L, 2_000L));

    // Результат:
    verify(timeUtils, times(3)).currentTimeMillis();
  }

  /**
   * Должен запросить текущий таймстамп повторно при успешном принятии заказа.
   */
  @Test
  public void askForCurrentTimeStampAgainIfAccepted() {
    // Дано:
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));

    // Действие:
    viewModel.acceptOrder();
    viewModel.acceptOrder();
    viewModel.acceptOrder();

    // Результат:
    verify(timeUtils, times(3)).currentTimeMillis();
    verifyNoMoreInteractions(timeUtils);
  }

  /**
   * Не должен запрашивать текущий таймстамп повторно при ошибках принятия заказа.
   */
  @Test
  public void doNotAskForCurrentTimeStampOnAcceptErrors() {
    // Дано:
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));

    // Действие:
    viewModel.acceptOrder();
    viewModel.acceptOrder();
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(timeUtils);
  }

  /**
   * Должен запросить текущий таймстамп повторно при успешном отказе от заказа.
   */
  @Test
  public void askForCurrentTimeStampAgainIfDeclined() {
    // Дано:
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));

    // Действие:
    viewModel.declineOrder();
    viewModel.declineOrder();
    viewModel.declineOrder();

    // Результат:
    verify(timeUtils, times(3)).currentTimeMillis();
    verifyNoMoreInteractions(timeUtils);
  }

  /**
   * Не должен запрашивать текущий таймстамп повторно при ошибках отказа от заказа.
   */
  @Test
  public void doNotAskForCurrentTimeStampOnDecineErrors() {
    // Дано:
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));

    // Действие:
    viewModel.declineOrder();
    viewModel.declineOrder();
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(timeUtils);
  }

  /* Тетсируем работу с логгером событий. */

  /**
   * Не должен трогать логгер изначально.
   */
  @Test
  public void doNotTouchEventLoggerInitially() {
    // Результат:
    verifyZeroInteractions(eventLogger);
  }

  /**
   * Должен передать данные для лога при успешном принятии заказа.
   */
  @Test
  public void askLoggerToLogEventIfAccepted() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(eventLogger);
    when(timeUtils.currentTimeMillis()).thenReturn(0L, 12345L, 0L, 67890L, 0L, 1234567890L);
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();
    emitter.onNext(new Pair<>(3L, 10_000L));
    viewModel.acceptOrder();
    emitter.onNext(new Pair<>(101L, 2_000L));
    viewModel.acceptOrder();

    // Результат:
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put("order_id", "1");
    hashMap.put("decision_duration", "12345");
    inOrder.verify(eventLogger).reportEvent("order_offer_accepted", hashMap);
    hashMap.clear();
    hashMap.put("order_id", "3");
    hashMap.put("decision_duration", "67890");
    inOrder.verify(eventLogger).reportEvent("order_offer_accepted", hashMap);
    hashMap.clear();
    hashMap.put("order_id", "101");
    hashMap.put("decision_duration", "1234567890");
    inOrder.verify(eventLogger).reportEvent("order_offer_accepted", hashMap);
    verifyNoMoreInteractions(eventLogger);
  }

  /**
   * Не должен передать данные для лога при ошибках принятии заказа.
   */
  @Test
  public void doNotTouchLoggerOnAcceptErrors() {
    // Дано:
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));

    // Действие:
    viewModel.acceptOrder();
    viewModel.acceptOrder();
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(eventLogger);
  }

  /**
   * Должен передать данные для лога при успешном принятии заказа.
   */
  @Test
  public void askLoggerToLogEventIfDeclined() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(eventLogger);
    when(timeUtils.currentTimeMillis()).thenReturn(0L, 12345L, 0L, 67890L, 0L, 1234567890L);
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();
    emitter.onNext(new Pair<>(3L, 10_000L));
    viewModel.declineOrder();
    emitter.onNext(new Pair<>(101L, 2_000L));
    viewModel.declineOrder();

    // Результат:
    HashMap<String, String> hashMap = new HashMap<>();
    hashMap.put("order_id", "1");
    hashMap.put("decision_duration", "12345");
    inOrder.verify(eventLogger).reportEvent("order_offer_declined", hashMap);
    hashMap.clear();
    hashMap.put("order_id", "3");
    hashMap.put("decision_duration", "67890");
    inOrder.verify(eventLogger).reportEvent("order_offer_declined", hashMap);
    hashMap.clear();
    hashMap.put("order_id", "101");
    hashMap.put("decision_duration", "1234567890");
    inOrder.verify(eventLogger).reportEvent("order_offer_declined", hashMap);
    verifyNoMoreInteractions(eventLogger);
  }

  /**
   * Не должен передать данные для лога при ошибках принятии заказа.
   */
  @Test
  public void doNotTouchLoggerOnDeclineErrors() {
    // Дано:
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));

    // Действие:
    viewModel.declineOrder();
    viewModel.declineOrder();
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(eventLogger);
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
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onNext(new Pair<>(2L, 17_000L));
    emitter.onNext(new Pair<>(1L, 12_000L));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(15_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(17_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "просрочки" с сообщением при получении ошибки истечения заказа.
   */
  @Test
  public void setNoViewStateToLiveDataForExpiredError() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new OrderConfirmationViewModelImpl(useCase, timeUtils, eventLogger);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onError(new OrderOfferExpiredException("message"));
    emitter.onNext(new Pair<>(1L, 17_000L));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(15_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateExpired.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(17_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен возвращать иных состояний вида при получении ошибки принятия решения по заказу.
   */
  @Test
  public void setNoViewStateToLiveDataForOfferDecisionErrorWithoutMessage() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new OrderConfirmationViewModelImpl(useCase, timeUtils, eventLogger);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onError(new OrderOfferDecisionException());
    emitter.onNext(new Pair<>(1L, 17_000L));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(15_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(17_000L, timeUtils))
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
    emitter.onError(new Exception());

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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.counterTimeOut();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(DataMappingException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида при получении ошибки истечения заказа.
   */
  @Test
  public void setExpiredViewStateToLiveDataForAcceptExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferExpiredException("34")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть предыдущее состояние вида при получении ошибки истечения заказа.
   */
  @Test
  public void setExpiredViewStateToLiveDataForDeclineExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferExpiredException("43")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "провала" с сообщением для неуспешного принятия.
   */
  @Test
  public void setFailedViewStateToLiveDataForAcceptExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderConfirmationFailedException("34")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateFailed("34"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "провала" с сообщением для неуспешного отказа.
   */
  @Test
  public void setFailedViewStateToLiveDataForDeclineExpiredWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderConfirmationFailedException("43")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateFailed("43"));
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
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
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "принял" с сообщением для успешного принятия.
   */
  @Test
  public void setAcceptedViewStateToLiveDataForAcceptWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just("21"));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateAccepted("21"));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояние вида "отказался" с сообщением для успешного принятия.
   */
  @Test
  public void setDeclinedViewStateToLiveDataForDeclineWithoutOrder() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just("12"));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils))
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(new OrderConfirmationViewStateDeclined("12"));
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
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить после успешного отказа.
   */
  @Test
  public void doNotSetNavigateForDeclineSuccess() {
    // Дано:
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(navigateObserver);
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
    emitter.onError(new DataMappingException());

    // Результат:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера" для принятия.
   */
  @Test
  public void setNavigateToServerDataErrorForAccept() {
    // Дано:
    when(useCase.sendDecision(true)).thenReturn(Single.error(DataMappingException::new));
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
    when(useCase.sendDecision(false)).thenReturn(Single.error(DataMappingException::new));
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
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));
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