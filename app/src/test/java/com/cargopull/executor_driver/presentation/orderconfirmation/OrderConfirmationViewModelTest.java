package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.TimeUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Single;
import java.util.Arrays;
import java.util.HashMap;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import retrofit2.HttpException;
import retrofit2.Response;

@RunWith(Parameterized.class)
public class OrderConfirmationViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private OrderConfirmationViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateUseCase sUseCase;
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
  private FlowableEmitter<ExecutorState> sEmitter;
  private FlowableEmitter<Pair<Long, Long>> emitter;

  final private ExecutorState executorState;
  final private boolean acceptEnabled;

  protected boolean getPrimeCondition(ExecutorState executorState) {
    return false;
  }

  public OrderConfirmationViewModel getViewModel(
      ErrorReporter errorReporter,
      ExecutorStateUseCase sUseCase,
      OrderConfirmationUseCase useCase, TimeUtils timeUtils,
      EventLogger eventLogger) {
    return new OrderConfirmationViewModelImpl(errorReporter, sUseCase, useCase, timeUtils,
        eventLogger);
  }

  @SuppressWarnings("WeakerAccess")
  public OrderConfirmationViewModelTest(ExecutorState condition) {
    executorState = condition;
    acceptEnabled = getPrimeCondition(condition);
  }

  @Parameterized.Parameters
  public static Iterable<ExecutorState> primeConditions() {
    return Arrays.asList(ExecutorState.values());
  }


  @Before
  public void setUp() {
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.never());
    when(sUseCase.getExecutorStates()).thenReturn(
        Flowable.create(e -> sEmitter = e, BackpressureStrategy.BUFFER)
    );
    when(useCase.getOrderDecisionTimeout()).thenReturn(
        Flowable.create(e -> emitter = e, BackpressureStrategy.BUFFER)
    );
    viewModel = getViewModel(errorReporter, sUseCase, useCase, timeUtils, eventLogger);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку полуения статуса исполнителя.
   */
  @Test
  public void reportErrorForExecutorStatus() {
    // Действие:
    sEmitter.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportExecutorStatusNetworkError() {
    // Действие:
    sEmitter.onError(new HttpException(
        Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
    ));

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportExecutorStatusNoNetworkError() {
    // Действие:
    sEmitter.onError(new NoNetworkException());

    // Результат:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку полуения таймаута подиверждения заказа.
   */
  @Test
  public void reportErrorForOrderTimeout() {
    // Действие:
    emitter.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportOrderTimeoutNetworkError() {
    // Действие:
    emitter.onError(new HttpException(
        Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
    ));

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportOrderTimeoutNoNetworkError() {
    // Действие:
    emitter.onError(new NoNetworkException());

    // Результат:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportAcceptNetworkError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new HttpException(
            Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
        )
    ));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportAcceptNoNetworkError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку маппинга данных.
   */
  @Test
  public void reportAcceptDataMapingError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new DataMappingException()));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить другую ошибку.
   */
  @Test
  public void reportAcceptOtherError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new Exception()));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(errorReporter, only()).reportError(any(Exception.class));
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportAcceptOrderConfirmationFailedError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderConfirmationFailedException("")
    ));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportAcceptOrderOfferExpiredError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferExpiredException("")
    ));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportAcceptOrderOfferDecisionError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferDecisionException()
    ));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportDeclineNetworkError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new HttpException(
            Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
        )
    ));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportDeclineNoNetworkError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new NoNetworkException()));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку маппинга данных.
   */
  @Test
  public void reportDeclineDataMapingError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new DataMappingException()));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить другую ошибку.
   */
  @Test
  public void reportDeclineOtherError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new Exception()));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(errorReporter, only()).reportError(any(Exception.class));
  }

  /**
   * Не должен отправлять ошибку отказа от заказа.
   */
  @Test
  public void doNotReportDeclineOrderConfirmationFailedError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderConfirmationFailedException("")
    ));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportDeclineOrderOfferExpiredError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferExpiredException("")
    ));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportDeclineOrderOfferDecisionError() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferDecisionException()
    ));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verifyZeroInteractions(errorReporter);
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

  /* Тетсируем работу с юзкейсом статуса исполнителя. */

  /**
   * Должен просить юзкейс получать статусы исполнителя.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Результат:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCase() {
    // Действие:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Результат:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на принятии заказа.
   */
  @Test
  public void doNotTouchUseCaseOnSendOrderAccepted() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

    // Действие:
    viewModel.acceptOrder();

    // Результат:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на отказе от заказа.
   */
  @Test
  public void doNotTouchUseCaseOnSendOrderDeclined() {
    // Дано:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

    // Действие:
    viewModel.declineOrder();

    // Результат:
    verify(sUseCase, only()).getExecutorStates();
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
    sEmitter.onNext(executorState);
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
  public void doNotAskForCurrentTimeStampOnDeclineErrors() {
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
    sEmitter.onNext(executorState);
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
    sEmitter.onNext(executorState);
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onNext(new Pair<>(2L, 17_000L));
    emitter.onNext(new Pair<>(1L, 12_000L));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(15_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(17_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    viewModel = getViewModel(errorReporter, sUseCase, useCase, timeUtils,
        eventLogger);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onError(new OrderOfferExpiredException("message"));
    emitter.onNext(new Pair<>(1L, 17_000L));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(15_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStateExpired.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(17_000L, timeUtils),
            acceptEnabled)
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
    viewModel = getViewModel(errorReporter, sUseCase, useCase, timeUtils,
        eventLogger);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onError(new OrderOfferDecisionException());
    emitter.onNext(new Pair<>(1L, 17_000L));

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(15_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(17_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.counterTimeOut();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
    );
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    inOrder.verify(viewStateObserver).onChanged(
        new OrderConfirmationViewStateIdle(new OrderConfirmationTimeoutItem(12_000L, timeUtils),
            acceptEnabled)
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