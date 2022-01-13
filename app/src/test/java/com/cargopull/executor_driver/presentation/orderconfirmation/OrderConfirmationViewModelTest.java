package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.ViewModelThreadTestRule;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
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

import java.util.Arrays;
import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Single;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

@RunWith(Parameterized.class)
public class OrderConfirmationViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  final private ExecutorState executorState;
  final private boolean acceptEnabled;
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
  private ShakeItPlayer shakeItPlayer;
  @Mock
  private RingTonePlayer ringTonePlayer;
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

  @SuppressWarnings("WeakerAccess")
  public OrderConfirmationViewModelTest(ExecutorState condition) {
    executorState = condition;
    acceptEnabled = getPrimeCondition(condition);
  }

  @Parameterized.Parameters
  public static Iterable<ExecutorState> primeConditions() {
    return Arrays.asList(ExecutorState.values());
  }

  protected boolean getPrimeCondition(ExecutorState executorState) {
    return false;
  }

  public OrderConfirmationViewModel getViewModel(
      ErrorReporter errorReporter,
      ExecutorStateUseCase sUseCase,
      OrderConfirmationUseCase useCase,
      ShakeItPlayer shakeItPlayer,
      RingTonePlayer ringTonePlayer,
      TimeUtils timeUtils,
      EventLogger eventLogger) {
    return new OrderConfirmationViewModelImpl(errorReporter, sUseCase, useCase, shakeItPlayer,
        ringTonePlayer, timeUtils, eventLogger);
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
    viewModel = getViewModel(errorReporter, sUseCase, useCase, shakeItPlayer, ringTonePlayer, timeUtils, eventLogger);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку полуения статуса исполнителя.
   */
  @Test
  public void reportErrorForExecutorStatus() {
    // Action:
    sEmitter.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportExecutorStatusNetworkError() {
    // Action:
    sEmitter.onError(new HttpException(
        Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
    ));

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportExecutorStatusNoNetworkError() {
    // Action:
    sEmitter.onError(new NoNetworkException());

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку полуения таймаута подиверждения заказа.
   */
  @Test
  public void reportErrorForOrderTimeout() {
    // Action:
    emitter.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportOrderTimeoutNetworkError() {
    // Action:
    emitter.onError(new HttpException(
        Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
    ));

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportOrderTimeoutNoNetworkError() {
    // Action:
    emitter.onError(new NoNetworkException());

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportAcceptNetworkError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new HttpException(
            Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
        )
    ));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportAcceptNoNetworkError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new NoNetworkException()));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку маппинга данных.
   */
  @Test
  public void reportAcceptDataMapingError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new DataMappingException()));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить другую ошибку.
   */
  @Test
  public void reportAcceptOtherError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new Exception()));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(errorReporter, only()).reportError(any(Exception.class));
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportAcceptOrderConfirmationFailedError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderConfirmationFailedException("")
    ));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportAcceptOrderOfferExpiredError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferExpiredException("")
    ));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportAcceptOrderOfferDecisionError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferDecisionException()
    ));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должен отправлять сетевую ошибку.
   */
  @Test
  public void doNotReportDeclineNetworkError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new HttpException(
            Response.error(404, ResponseBody.create(MediaType.get("applocation/json"), ""))
        )
    ));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку сети.
   */
  @Test
  public void reportDeclineNoNetworkError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new NoNetworkException()));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(errorReporter, only()).reportError(any(NoNetworkException.class));
  }

  /**
   * Должен отправить ошибку маппинга данных.
   */
  @Test
  public void reportDeclineDataMapingError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new DataMappingException()));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить другую ошибку.
   */
  @Test
  public void reportDeclineOtherError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(new Exception()));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(errorReporter, only()).reportError(any(Exception.class));
  }

  /**
   * Не должен отправлять ошибку отказа от заказа.
   */
  @Test
  public void doNotReportDeclineOrderConfirmationFailedError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderConfirmationFailedException("")
    ));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportDeclineOrderOfferExpiredError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferExpiredException("")
    ));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /**
   * Не должен отправлять ошибку принятия заказа.
   */
  @Test
  public void doNotReportDeclineOrderOfferDecisionError() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(
        new OrderOfferDecisionException()
    ));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(errorReporter);
  }

  /* Тетсируем работу с юзкейсом принятия заказа. */

  /**
   * Должен просить юзкейс получать таймауты при создании.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Effect:
    verify(useCase, only()).getOrderDecisionTimeout();
  }

  /* Тетсируем работу с юзкейсом статуса исполнителя. */

  /**
   * Должен просить юзкейс получать статусы исполнителя.
   */
  @Test
  public void doNotTouchUseCaseOnSubscriptions() {
    // Effect:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на подписках.
   */
  @Test
  public void doNotTouchUseCase() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на принятии заказа.
   */
  @Test
  public void doNotTouchUseCaseOnSendOrderAccepted() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на отказе от заказа.
   */
  @Test
  public void doNotTouchUseCaseOnSendOrderDeclined() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(sUseCase, only()).getExecutorStates();
  }

  /**
   * Не должен просить юзкейс получать таймауты на подписках.
   */
  @Test
  public void doNotAskUseCaseForOrdersOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(useCase, only()).getOrderDecisionTimeout();
  }

  /**
   * Должен попросить юзкейс передать принятие заказа.
   */
  @Test
  public void askUseCaseToSendOrderAccepted() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(useCase).getOrderDecisionTimeout();
    verify(useCase).sendDecision(true);
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Должен попросить юзкейс передать отказ от заказа.
   */
  @Test
  public void askUseCaseToSendOrderDeclined() {
    // Given:
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just(""));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(useCase).getOrderDecisionTimeout();
    verify(useCase).sendDecision(false);
    verifyNoMoreInteractions(useCase);
  }

  /**
   * Не должен трогать юзкейс, если предыдущий запрос передачи решения еще не завершился.
   */
  @Test
  public void DoNotTouchUseCaseDuringOrderSetting() {
    // Action:
    viewModel.acceptOrder();
    viewModel.declineOrder();
    viewModel.acceptOrder();

    // Effect:
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
    // Effect:
    verifyNoInteractions(timeUtils);
  }

  /**
   * Должен запросить текущий таймстамп на новые данные.
   */
  @Test
  public void askForCurrentTimeStampOnNewData() {
    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    emitter.onNext(new Pair<>(3L, 10_000L));
    emitter.onNext(new Pair<>(101L, 2_000L));

    // Effect:
    verify(timeUtils, times(3)).currentTimeMillis();
  }

  /**
   * Должен запросить текущий таймстамп повторно при успешном принятии заказа.
   */
  @Test
  public void askForCurrentTimeStampAgainIfAccepted() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));

    // Action:
    viewModel.acceptOrder();
    viewModel.acceptOrder();
    viewModel.acceptOrder();

    // Effect:
    verify(timeUtils, times(3)).currentTimeMillis();
    verifyNoMoreInteractions(timeUtils);
  }

  /**
   * Не должен запрашивать текущий таймстамп повторно при ошибках принятия заказа.
   */
  @Test
  public void doNotAskForCurrentTimeStampOnAcceptErrors() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));

    // Action:
    viewModel.acceptOrder();
    viewModel.acceptOrder();
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(timeUtils);
  }

  /**
   * Должен запросить текущий таймстамп повторно при успешном отказе от заказа.
   */
  @Test
  public void askForCurrentTimeStampAgainIfDeclined() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));

    // Action:
    viewModel.declineOrder();
    viewModel.declineOrder();
    viewModel.declineOrder();

    // Effect:
    verify(timeUtils, times(3)).currentTimeMillis();
    verifyNoMoreInteractions(timeUtils);
  }

  /**
   * Не должен запрашивать текущий таймстамп повторно при ошибках отказа от заказа.
   */
  @Test
  public void doNotAskForCurrentTimeStampOnDeclineErrors() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));

    // Action:
    viewModel.declineOrder();
    viewModel.declineOrder();
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(timeUtils);
  }

  /* Тетсируем работу с логгером событий. */

  /**
   * Не должен трогать логгер изначально.
   */
  @Test
  public void doNotTouchEventLoggerInitially() {
    // Effect:
    verifyNoInteractions(eventLogger);
  }

  /**
   * Должен передать данные для лога при успешном принятии заказа.
   */
  @Test
  public void askLoggerToLogEventIfAccepted() {
    // Given:
    InOrder inOrder = Mockito.inOrder(eventLogger);
    when(timeUtils.currentTimeMillis()).thenReturn(0L, 12345L, 0L, 67890L, 0L, 1234567890L);
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();
    emitter.onNext(new Pair<>(3L, 10_000L));
    viewModel.acceptOrder();
    emitter.onNext(new Pair<>(101L, 2_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));

    // Action:
    viewModel.acceptOrder();
    viewModel.acceptOrder();
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(eventLogger);
  }

  /**
   * Должен передать данные для лога при успешном принятии заказа.
   */
  @Test
  public void askLoggerToLogEventIfDeclined() {
    // Given:
    InOrder inOrder = Mockito.inOrder(eventLogger);
    when(timeUtils.currentTimeMillis()).thenReturn(0L, 12345L, 0L, 67890L, 0L, 1234567890L);
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();
    emitter.onNext(new Pair<>(3L, 10_000L));
    viewModel.declineOrder();
    emitter.onNext(new Pair<>(101L, 2_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));

    // Action:
    viewModel.declineOrder();
    viewModel.declineOrder();
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(eventLogger);
  }

  /* Тетсируем работу с вибро и звуком. */

  /**
   * Не должен трогать вибро и звук изначально.
   */
  @Test
  public void doNotTouchVibrationAndSoundInitially() {
    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при получении таймаута заказа.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnTimeoutLoadSuccess() {
    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    emitter.onNext(new Pair<>(3L, 10_000L));
    emitter.onNext(new Pair<>(101L, 2_000L));

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при ошибке получения таймаута заказа.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnTimeoutLoadError() {
    // Action:
    sEmitter.onNext(executorState);
    emitter.onError(new Exception("message"));

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук отказа при ошибке актуальности заказа.
   */
  @Test
  public void useVibrationAndSoundOnOrderCanceled() {
    // Action:
    sEmitter.onNext(executorState);
    emitter.onError(new OrderOfferExpiredException("message"));

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.skip_order_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
  }

  /**
   * Не должен трогать звук при ошибке принятния заказа.
   */
  @Test
  public void doNotTouchSoundOnAcceptanceError() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.single_shot_vibro);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук принятия при принятнии заказа.
   */
  @Test
  public void useVibrationAndSoundOnAcceptanceSuccess() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(shakeItPlayer).shakeIt(R.raw.single_shot_vibro);
    verify(shakeItPlayer).shakeIt(R.raw.confirmation_notify_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.confirmation_notify);
    verifyNoMoreInteractions(shakeItPlayer);
  }

  /**
   * Не должен трогать звук при ошибке отказа от заказа.
   */
  @Test
  public void doNotTouchSoundOnDeclineError() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.single_shot_vibro);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук отказа при отказе от заказа.
   */
  @Test
  public void useVibrationAndSoundOnDeclineSuccess() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(shakeItPlayer).shakeIt(R.raw.single_shot_vibro);
    verify(shakeItPlayer).shakeIt(R.raw.skip_order_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
    verifyNoMoreInteractions(shakeItPlayer);
  }

  /**
   * Не должен трогать вибро и звук при потреблении сообщения.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnMessageConsumption() {
    // Action:
    viewModel.messageConsumed();

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук отказа при таймауте.
   */
  @Test
  public void useVibrationAndSoundOnTimeout() {
    // Action:
    viewModel.counterTimeOut();

    // Effect:
    verify(shakeItPlayer, only()).shakeIt(R.raw.skip_order_vibro);
    verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида ожидания изначально.
   */
  @Test
  public void setPendingViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(OrderConfirmationViewStatePending.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "Бездействие" с полученными таймаутами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onNext(new Pair<>(2L, 17_000L));
    emitter.onNext(new Pair<>(1L, 12_000L));

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = getViewModel(errorReporter, sUseCase, useCase, shakeItPlayer, ringTonePlayer, timeUtils, eventLogger);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onError(new OrderOfferExpiredException("message"));
    emitter.onNext(new Pair<>(1L, 17_000L));

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = getViewModel(errorReporter, sUseCase, useCase, shakeItPlayer, ringTonePlayer, timeUtils, eventLogger);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 15_000L));
    emitter.onError(new OrderOfferDecisionException());
    emitter.onNext(new Pair<>(1L, 17_000L));

    // Effect:
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
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    emitter.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(OrderConfirmationViewStatePending.class));
  }

  /**
   * Должен вернуть состояние вида "В процессе".
   */
  @Test
  public void setPendingViewStateWithoutOrderToLiveDataForCounterTimeOut() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.counterTimeOut();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(NoNetworkException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(DataMappingException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.error(DataMappingException::new));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferExpiredException("34")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferExpiredException("43")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderConfirmationFailedException("34")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderConfirmationFailedException("43")));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferDecisionException()));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean()))
        .thenReturn(Single.error(new OrderOfferDecisionException()));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just("21"));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.acceptOrder();

    // Effect:
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
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    when(useCase.sendDecision(anyBoolean())).thenReturn(Single.just("12"));
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    sEmitter.onNext(executorState);
    emitter.onNext(new Pair<>(1L, 12_000L));
    viewModel.declineOrder();

    // Effect:
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
    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен перейти к закрытию карточки.
   */
  @Test
  public void setNavigateToCloseForMessageConsumed() {
    // Action:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.messageConsumed();

    // Effect:
    verify(navigateObserver, only()).onChanged(OrderConfirmationNavigate.CLOSE);
  }

  /**
   * Не должен никуда переходить для вида "В процессе отказа".
   */
  @Test
  public void doNotSetNavigateForDeclinePending() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения" для отказа.
   */
  @Test
  public void setNavigateToNoConnectionForDecline() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.error(IllegalStateException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить после успешного отказа.
   */
  @Test
  public void doNotSetNavigateForDeclineSuccess() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.just(""));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.declineOrder();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Не должен никуда переходить для вида "В процессе отказа".
   */
  @Test
  public void doNotSetNavigateForAcceptPending() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера".
   */
  @Test
  public void setNavigateToServerDataErrorFor() {
    // Given:
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    emitter.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера" для принятия.
   */
  @Test
  public void setNavigateToServerDataErrorForAccept() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.error(DataMappingException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера" для отказа.
   */
  @Test
  public void setNavigateToServerDataErrorForDecline() {
    // Given:
    when(useCase.sendDecision(false)).thenReturn(Single.error(DataMappingException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.declineOrder();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть "перейти к ошибке соединения" для отказа.
   */
  @Test
  public void setNavigateToNoConnectionForAccept() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.error(IllegalStateException::new));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.NO_CONNECTION);
  }

  /**
   * Не должен никуда переходить после отказа.
   */
  @Test
  public void doNotSetNavigateForAcceptSuccess() {
    // Given:
    when(useCase.sendDecision(true)).thenReturn(Single.just(""));
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.acceptOrder();

    // Effect:
    verifyNoInteractions(navigateObserver);
  }
}