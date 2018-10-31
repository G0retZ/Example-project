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
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
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
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
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

@RunWith(Parameterized.class)
public class ExecutorStateViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  @Rule
  public MockitoRule mrule = MockitoJUnit.rule();
  private ExecutorStateViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private ExecutorStateUseCase useCase;
  @Mock
  private Observer<String> navigationObserver;
  @Mock
  private Observer<ViewState<ExecutorStateViewActions>> viewStateObserver;
  @Captor
  private ArgumentCaptor<ViewState<ExecutorStateViewActions>> viewStateCaptor;
  @Mock
  private ExecutorStateViewActions viewActions;

  private PublishSubject<ExecutorState> publishSubject;

  private final ExecutorState conditionExecutorState;
  private final String conditionMessage;
  private final String expectedNavigation;
  private final String expectedInfo;
  private final String expectedMessage;

  // Each parameter should be placed as an argument here
  // Every time runner triggers, it will pass the arguments
  // from parameters we defined in primeNumbers() method

  public ExecutorStateViewModelTest(Pair<Pair<ExecutorState, String>, List<String>> conditions) {
    conditionExecutorState = conditions.first.first;
    expectedNavigation = conditions.first.second;
    conditionMessage = conditions.second.get(0);
    expectedMessage = conditions.second.get(1);
    expectedInfo = conditions.second.get(2);
  }

  @Parameterized.Parameters
  public static Iterable primeNumbers() {
    // Соответствия значений статуса направлениям навигации и сообщениям
    return Arrays.<Pair<Pair<ExecutorState, String>, List<String>>>asList(
        new Pair<>(
            new Pair<>(BLOCKED, ExecutorStateNavigate.BLOCKED),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(BLOCKED, ExecutorStateNavigate.BLOCKED),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(BLOCKED, ExecutorStateNavigate.BLOCKED),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(BLOCKED, ExecutorStateNavigate.BLOCKED),
            Arrays.asList("Message", null, "Message")
        ),
        new Pair<>(
            new Pair<>(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_CLOSED, ExecutorStateNavigate.MAP_SHIFT_CLOSED),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(SHIFT_OPENED, ExecutorStateNavigate.MAP_SHIFT_OPENED),
            Arrays.asList("Message", "Message", null)
        ),
        new Pair<>(
            new Pair<>(ONLINE, ExecutorStateNavigate.MAP_ONLINE),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(ONLINE, ExecutorStateNavigate.MAP_ONLINE),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(ONLINE, ExecutorStateNavigate.MAP_ONLINE),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(ONLINE, ExecutorStateNavigate.MAP_ONLINE),
            Arrays.asList("Message", "Message", null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_ORDER_CONFIRMATION, ExecutorStateNavigate.DRIVER_ORDER_CONFIRMATION),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
                ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
                ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
                ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
                ExecutorStateNavigate.DRIVER_PRELIMINARY_ORDER_CONFIRMATION),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(CLIENT_ORDER_CONFIRMATION, ExecutorStateNavigate.CLIENT_ORDER_CONFIRMATION),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(MOVING_TO_CLIENT, ExecutorStateNavigate.MOVING_TO_CLIENT),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(WAITING_FOR_CLIENT, ExecutorStateNavigate.WAITING_FOR_CLIENT),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(ORDER_FULFILLMENT, ExecutorStateNavigate.ORDER_FULFILLMENT),
            Arrays.asList("Message", null, null)
        ),
        new Pair<>(
            new Pair<>(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION),
            Arrays.asList(null, null, null)
        ),
        new Pair<>(
            new Pair<>(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION),
            Arrays.asList("", null, null)
        ),
        new Pair<>(
            new Pair<>(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION),
            Arrays.asList("\n", null, null)
        ),
        new Pair<>(
            new Pair<>(PAYMENT_CONFIRMATION, ExecutorStateNavigate.PAYMENT_CONFIRMATION),
            Arrays.asList("Message", null, null)
        )
    );
  }

  @Before
  public void setUp() {
    conditionExecutorState.setData(conditionMessage);
    publishSubject = PublishSubject.create();
    when(useCase.getExecutorStates())
        .thenReturn(publishSubject.toFlowable(BackpressureStrategy.BUFFER));
    viewModel = new ExecutorStateViewModelImpl(errorReporter, useCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом. */

  /**
   * Должен попросить у юзкейса статусы исполнителя только при создании.
   */
  @Test
  public void askUseCaseToSubscribeToExecutorStateUpdatesInitially() {
    // Результат:
    verify(useCase, only()).getExecutorStates();
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
    verify(useCase, only()).getExecutorStates();
  }

  /**
   * Не должен трогать юзкейс на прочтении сообщений.
   */
  @Test
  public void doNotTouchUseCaseForMessageReadEvent() {
    // Действие:
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Результат:
    verify(useCase, only()).getExecutorStates();
  }

  /* Тетсируем сообщение. */

  /**
   * Должен показать сопутствующее сообщение, если есть, затем null после его прочтения.
   */
  @Test
  public void showOrNotTheMessage() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    publishSubject.onNext(conditionExecutorState);
    if (expectedMessage != null) {
      viewModel.messageConsumed();
    }

    // Результат:
    if (expectedMessage != null) {
      verify(viewStateObserver, times(2)).onChanged(viewStateCaptor.capture());
      assertEquals(2, viewStateCaptor.getAllValues().size());
      assertNull(viewStateCaptor.getAllValues().get(1));
      viewStateCaptor.getAllValues().get(0).apply(viewActions);
      verify(viewActions, only()).showExecutorStatusMessage(expectedMessage);
      verifyNoMoreInteractions(viewStateObserver);
    } else if (expectedInfo != null) {
      verify(viewStateObserver, only()).onChanged(viewStateCaptor.capture());
      viewStateCaptor.getValue().apply(viewActions);
      verify(viewActions, only()).showExecutorStatusInfo(expectedInfo);
      verifyNoMoreInteractions(viewStateObserver);
    } else {
      verifyZeroInteractions(viewStateObserver);
    }
  }

  /* Тетсируем навигацию. */

  /**
   * Не должен никуда переходить.
   */
  @Test
  public void navigateToNowhere() {
    // Действие:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);
    viewModel.messageConsumed();
    viewModel.messageConsumed();
    viewModel.messageConsumed();

    // Результат:
    verifyZeroInteractions(navigationObserver);
  }

  /**
   * Должен вернуть "перейти к ошибке в данных сервера".
   */
  @Test
  public void navigateToNoNetwork() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onError(new DataMappingException());

    // Результат:
    verify(navigationObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен вернуть перейти к соответствующему экрану.
   */
  @Test
  public void navigateToExpectedDestination() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigationObserver);

    // Действие:
    publishSubject.onNext(conditionExecutorState);

    // Результат:
    verify(navigationObserver, only()).onChanged(expectedNavigation);
  }
}