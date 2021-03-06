package com.cargopull.executor_driver.presentation.executorstate;

import static com.cargopull.executor_driver.entity.ExecutorState.BLOCKED;
import static com.cargopull.executor_driver.entity.ExecutorState.CLIENT_ORDER_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.DRIVER_ORDER_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.MOVING_TO_CLIENT;
import static com.cargopull.executor_driver.entity.ExecutorState.ONLINE;
import static com.cargopull.executor_driver.entity.ExecutorState.ORDER_FULFILLMENT;
import static com.cargopull.executor_driver.entity.ExecutorState.PAYMENT_CONFIRMATION;
import static com.cargopull.executor_driver.entity.ExecutorState.SHIFT_CLOSED;
import static com.cargopull.executor_driver.entity.ExecutorState.SHIFT_OPENED;
import static com.cargopull.executor_driver.entity.ExecutorState.WAITING_FOR_CLIENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
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
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;

@RunWith(Parameterized.class)
public class ExecutorStateViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  private final TestDataSet conditionDataSet;
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  @Rule
  public MockitoRule mRule = MockitoJUnit.rule();
  private ExecutorStateViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateUseCase useCase;
  @Mock
  private ShakeItPlayer shakeItPlayer;
  @Mock
  private RingTonePlayer ringTonePlayer;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ExecutorStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ExecutorStateViewActions>> viewStateCaptor;
  @Mock
  private ExecutorStateViewActions viewActions;
  private PublishSubject<ExecutorState> publishSubject;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public ExecutorStateViewModelTest(TestDataSet conditions) {
    conditionDataSet = conditions;
  }

  @Parameterized.Parameters
  public static Iterable<TestDataSet> primeNumbers() {
    // ???????????????????????? ???????????????? ?????????????? ???????????????????????? ?????????????????? ?? ????????????????????
    return Arrays.asList(
            new TestDataSet(BLOCKED, ExecutorStateNavigate.BLOCKED,
                    null, null, null, false, false),
            new TestDataSet(BLOCKED, ExecutorStateNavigate.BLOCKED,
                    "", null, null, false, false),
            new TestDataSet(BLOCKED, ExecutorStateNavigate.BLOCKED,
                    "\n", null, null, false, false),
            new TestDataSet(BLOCKED, ExecutorStateNavigate.BLOCKED,
                    "Message", null, "Message", false, false),
            new TestDataSet(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED,
                    null, null, null, false, false),
            new TestDataSet(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED,
                    "", null, null, false, false),
            new TestDataSet(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED,
                    "\n", null, null, false, false),
            new TestDataSet(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED,
                    "Message", null, null, false, false),
            new TestDataSet(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED,
                    null, null, null, false, false),
        new TestDataSet(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED,
            "", null, null, false, false),
        new TestDataSet(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED,
            "\n", null, null, false, false),
        new TestDataSet(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED,
            "Message", "Message", null, false, false),
        new TestDataSet(ONLINE, ExecutorStateNavigate.MAP_ONLINE,
            null, null, null, false, false),
        new TestDataSet(ONLINE, ExecutorStateNavigate.MAP_ONLINE,
            "", null, null, false, false),
        new TestDataSet(ONLINE, ExecutorStateNavigate.MAP_ONLINE,
            "\n", null, null, false, false),
        new TestDataSet(ONLINE, ExecutorStateNavigate.MAP_ONLINE,
            "Message", "Message", null, true, false),
        new TestDataSet(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION,
            null, null, null, false, true),
        new TestDataSet(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION,
            "", null, null, false, true),
        new TestDataSet(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION,
            "\n", null, null, false, true),
        new TestDataSet(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION,
            "Message", null, null, false, true),
        new TestDataSet(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            null, null, null, false, true),
        new TestDataSet(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            "", null, null, false, true),
        new TestDataSet(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            "\n", null, null, false, true),
        new TestDataSet(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
            "Message", null, null, false, true),
        new TestDataSet(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION,
            null, null, null, false, false),
        new TestDataSet(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION,
            "", null, null, false, false),
        new TestDataSet(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION,
            "\n", null, null, false, false),
        new TestDataSet(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION,
            "Message", null, null, false, false),
        new TestDataSet(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT,
            null, null, null, false, false),
        new TestDataSet(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT,
            "", null, null, false, false),
        new TestDataSet(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT,
            "\n", null, null, false, false),
        new TestDataSet(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT,
            "Message", null, null, false, false),
        new TestDataSet(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT,
            null, null, null, false, false),
        new TestDataSet(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT,
            "", null, null, false, false),
        new TestDataSet(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT,
            "\n", null, null, false, false),
        new TestDataSet(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT,
            "Message", null, null, false, false),
        new TestDataSet(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT,
            null, null, null, false, false),
        new TestDataSet(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT,
            "", null, null, false, false),
        new TestDataSet(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT,
            "\n", null, null, false, false),
        new TestDataSet(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT,
            "Message", null, null, false, false),
        new TestDataSet(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION,
            null, null, null, false, false),
        new TestDataSet(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION,
            "", null, null, false, false),
        new TestDataSet(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION,
            "\n", null, null, false, false),
        new TestDataSet(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION,
            "Message", null, null, false, false)
    );
  }

  @Before
  public void setUp() {
    conditionDataSet.conditionExecutorState.setData(conditionDataSet.conditionMessage);
    publishSubject = PublishSubject.create();
    when(useCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new ExecutorStateViewModelImpl(errorReporter, useCase, shakeItPlayer,
        ringTonePlayer);
  }

  /* ?????????????????? ???????????????? ???????????? ?? ???????????????? */

  /**
   * ???????????? ?????????????????? ????????????.
   */
  @Test
  public void reportError() {
    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* ?????????????????? ???????????? ?? ????????????????. */

  /**
   * ???????????? ?????????????????? ?? ?????????????? ?????????????? ?????????????????????? ???????????? ?????? ????????????????.
   */
  @Test
  public void askUseCaseToSubscribeToExecutorStateUpdatesInitially() {
    // Effect:
    verify(useCase, only()).getExecutorStates();
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
    verify(useCase, only()).getExecutorStates();
  }

  /**
   * ???? ???????????? ?????????????? ???????????? ???? ?????????????????? ??????????????????.
   */
  @Test
  public void doNotTouchUseCaseForMessageReadEvent() {
    // Action:
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Effect:
    verify(useCase, only()).getExecutorStates();
  }

  /* ?????????????????? ??????????????????. */

  /**
   * ???????????? ???????????????? ?????????????????????????? ??????????????????, ???????? ????????, ?????????? null ?????????? ?????? ??????????????????.
   */
  @Test
  public void showOrNotTheMessage() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    publishSubject.onNext(conditionDataSet.conditionExecutorState);
    if (conditionDataSet.expectedMessage != null) {
      viewModel.messageConsumed();
    }

    // Effect:
    assertFalse(conditionDataSet.expectedMessage != null && conditionDataSet.expectedInfo != null);
    if (conditionDataSet.expectedMessage != null) {
      verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
      assertEquals(2, viewStateCaptor.getAllValues().size());
      assertNull(viewStateCaptor.getAllValues().get(1));
      viewStateCaptor.getAllValues().get(0).apply(viewActions);
      verify(viewActions, only()).showExecutorStatusMessage(conditionDataSet.expectedMessage);
      verifyNoMoreInteractions(viewStateObserver);
    } else if (conditionDataSet.expectedInfo != null) {
      verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
      viewStateCaptor.getValue().apply(viewActions);
      verify(viewActions, only()).showExecutorStatusInfo(conditionDataSet.expectedInfo);
      verifyNoMoreInteractions(viewStateObserver);
    } else {
      verifyNoInteractions(viewStateObserver);
    }
  }

  /* ?????????????????? ???????????? ?? ?????????? ?? ????????????. */

  /**
   * ???????????? ?????????????????????????????????? ?? ?????????? ?? ????????????.
   */
  @Test
  public void interactWithSoundAndVibrations() {
    // Action:
    publishSubject.onNext(conditionDataSet.conditionExecutorState);

    // Effect:
    assertFalse(conditionDataSet.expectedToRingAndVibrateSkip && conditionDataSet.expectedToRingAndVibrateOrder);
    if (conditionDataSet.expectedToRingAndVibrateSkip) {
      verify(ringTonePlayer, only()).playRingTone(R.raw.skip_order);
      verify(shakeItPlayer, only()).shakeIt(R.raw.skip_order_vibro);
    } else if (conditionDataSet.expectedToRingAndVibrateOrder) {
      verify(shakeItPlayer, only()).shakeIt(R.raw.regular_order_notify_vibro);
      verify(ringTonePlayer, only()).playRingTone(R.raw.regular_order_notify);
    } else {
      verifyNoInteractions(ringTonePlayer);
      verifyNoInteractions(shakeItPlayer);
    }
  }

  /* ?????????????????? ??????????????????. */

  /**
   * ???? ???????????? ???????????? ????????????????????.
   */
  @Test
  public void navigateToNowhere() {
    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Effect:
    verifyNoInteractions(navigationObserver);
  }

  /**
   * ???????????? ?????????????? "?????????????? ?? ???????????? ?? ???????????? ??????????????".
   */
  @Test
  public void navigateToNoNetwork() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    publishSubject.onError(new DataMappingException());

    // Effect:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * ???????????? ?????????????? ?????????????? ?? ???????????????????????????????? ????????????.
   */
  @Test
  public void navigateToExpectedDestination() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Action:
    publishSubject.onNext(conditionDataSet.conditionExecutorState);

    // Effect:
    verify(navigationObserver, only()).onChanged(conditionDataSet.expectedNavigation);
  }

  private static class TestDataSet {

    private final ExecutorState conditionExecutorState;
    private final String expectedNavigation;
    private final String conditionMessage;
    private final String expectedMessage;
    private final String expectedInfo;
    private final boolean expectedToRingAndVibrateSkip;
    private final boolean expectedToRingAndVibrateOrder;

    private TestDataSet(ExecutorState conditionExecutorState,
        String expectedNavigation, String conditionMessage,
        String expectedMessage, String expectedInfo, boolean expectedToRingAndVibrateSkip,
        boolean expectedToRingAndVibrateOrder) {
      this.conditionExecutorState = conditionExecutorState;
      this.conditionMessage = conditionMessage;
      this.expectedNavigation = expectedNavigation;
      this.expectedMessage = expectedMessage;
      this.expectedInfo = expectedInfo;
      this.expectedToRingAndVibrateSkip = expectedToRingAndVibrateSkip;
      this.expectedToRingAndVibrateOrder = expectedToRingAndVibrateOrder;
    }
  }
}