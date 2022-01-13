package com.cargopull.executor_driver.presentation.preorder;

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
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderViewModelTest {

  @ClassRule
  public static final ViewModelThreadTestRule classRule = new ViewModelThreadTestRule();
  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private PreOrderViewModel viewModel;
  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private OrderUseCase orderUseCase;
  @Mock
  private ShakeItPlayer shakeItPlayer;
  @Mock
  private RingTonePlayer ringTonePlayer;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  private FlowableEmitter<Order> emitter;

  @Mock
  private Observer<ViewState<PreOrderViewActions>> viewStateObserver;
  @Mock
  private Observer<String> navigateObserver;

  @Before
  public void setUp() {
    when(orderUseCase.getOrders()).thenReturn(
        Flowable.create(e -> emitter = e, BackpressureStrategy.BUFFER)
    );
    viewModel = new PreOrderViewModelImpl(errorReporter, orderUseCase, shakeItPlayer,
        ringTonePlayer);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Action:
    emitter.onError(new DataMappingException());

    // Effect:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Тетсируем работу с юзкейсом заказа. */

  /**
   * Должен просить юзкейс получать заказы при создании.
   */
  @Test
  public void askUseCaseForOrdersInitially() {
    // Effect:
    verify(orderUseCase, only()).getOrders();
  }

  /**
   * Не должен просить юзкейс получать заказы на подписках.
   */
  @Test
  public void doNotAskUseCaseForOrdersOnSubscriptions() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verify(orderUseCase, only()).getOrders();
  }

  /* Тетсируем работу с вибро и звуком. */

  /**
   * Не должен трогать вибро и звук изначально и на подписках.
   */
  @Test
  public void doNotTouchVibrationAndSoundInitially() {
    // Action:
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();
    viewModel.getViewStateLiveData();
    viewModel.getNavigationLiveData();

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при ошибке.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnError() {
    // Action:
    emitter.onError(new Exception());

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Должен дать вибро и звук отказа при получении заказов.
   */
  @Test
  public void useVibrationAndSound() {
    // Action:
    emitter.onNext(order);
    emitter.onNext(order1);
    emitter.onNext(order2);

    // Effect:
    verify(shakeItPlayer, times(3)).shakeIt(R.raw.preliminary_order_notify_vibro);
    verify(ringTonePlayer, times(3)).playRingTone(R.raw.preliminary_order_notify);
    verifyNoMoreInteractions(shakeItPlayer);
    verifyNoMoreInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при получении ошибки.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnDataMappingError() {
    // Action:
    emitter.onError(new DataMappingException());

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук при получении ошибки принятия решения по заказу.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnOrderOfferDecision() {
    // Action:
    emitter.onError(new OrderOfferDecisionException());

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук отказа при получении ошибки актуальности заказа.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnOrderOfferExpired() {
    // Action:
    emitter.onError(new OrderOfferExpiredException(""));

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /**
   * Не должен трогать вибро и звук отказа при получении ошибки отмены заказа.
   */
  @Test
  public void doNotTouchVibrationAndSoundOnOrderOfferCancelled() {
    // Action:
    emitter.onError(new OrderCancelledException(""));

    // Effect:
    verifyNoInteractions(shakeItPlayer);
    verifyNoInteractions(ringTonePlayer);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть состояние вида недоступности изначально.
   */
  @Test
  public void setUnAvailableViewStateToLiveDataInitially() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);

    // Action:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Не должен давать иных состояний вида если была ошибка.
   */
  @Test
  public void doNotSetAnyViewStateToLiveDataForError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    emitter.onError(new Exception());

    // Effect:
    verify(viewStateObserver, only()).onChanged(any(PreOrderViewStateUnAvailable.class));
  }

  /**
   * Должен вернуть состояния вида доступности полученными заказами.
   */
  @Test
  public void setIdleViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    emitter.onNext(order);
    emitter.onNext(order1);
    emitter.onNext(order2);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver, times(3)).onChanged(any(PreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" после ошибки принятия решения по заказу.
   */
  @Test
  public void setExpiredViewStateToLiveDataForOrderOfferDecision() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new PreOrderViewModelImpl(errorReporter, orderUseCase, shakeItPlayer,
        ringTonePlayer);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    emitter.onNext(order);
    emitter.onError(new OrderOfferDecisionException());
    emitter.onNext(order2);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" после ошибки актуальности заказа.
   */
  @Test
  public void setExpiredViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new PreOrderViewModelImpl(errorReporter, orderUseCase, shakeItPlayer,
        ringTonePlayer);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    emitter.onNext(order);
    emitter.onError(new OrderOfferExpiredException(""));
    emitter.onNext(order2);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /**
   * Должен вернуть состояния вида "заказ истек" после ошибки отмены заказа.
   */
  @Test
  public void setCancelledViewStateToLiveData() {
    // Given:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    viewModel = new PreOrderViewModelImpl(errorReporter, orderUseCase, shakeItPlayer,
        ringTonePlayer);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Action:
    emitter.onNext(order);
    emitter.onError(new OrderCancelledException(""));
    emitter.onNext(order2);

    // Effect:
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateUnAvailable.class));
    inOrder.verify(viewStateObserver).onChanged(any(PreOrderViewStateAvailable.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

  /* Тестируем навигацию. */

  /**
   * Должен вернуть "перейти к ошибке данных сервера".
   */
  @Test
  public void setNavigateToServerDataError() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    emitter.onError(new DataMappingException());

    // Effect:
    verify(navigateObserver, only()).onChanged(CommonNavigate.SERVER_DATA_ERROR);
  }

  /**
   * Должен перейти к подтверждению заказа.
   */
  @Test
  public void setNavigateToCloseForMessageConsumed() {
    // Given:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);
    viewModel.getNavigationLiveData().observeForever(navigateObserver);

    // Action:
    viewModel.preOrderConsumed();

    // Effect:
    verify(navigateObserver, only()).onChanged(PreOrderNavigate.ORDER_APPROVAL);
  }
}