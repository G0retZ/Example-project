package com.cargopull.executor_driver.interactor;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.gateway.DataMappingException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class SelectedOrderUseCaseImplTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private SelectedOrderUseCaseImpl useCase;

  @Mock
  private OrdersUseCase ordersUseCase;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;
  private FlowableEmitter<Set<Order>> emitter;

  @Before
  public void setUp() {
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.never());
    useCase = new SelectedOrderUseCaseImpl(ordersUseCase);
  }

  /* Проверяем работу с юзкейсом списка заказов */

  /**
   * Должен запросить у юзкейса списка получение заказов только раз при первой подписке.
   */
  @Test
  public void askUseCaseForOrdersListForGet() {
    // Action:
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();
    useCase.getOrders().test().isDisposed();

    // Effect:
    verify(ordersUseCase, only()).getOrdersSet();
    verifyNoMoreInteractions(ordersUseCase);
  }

  /**
   * Должен запросить у юзкейса списка получение заказов для осуществления выбора.
   */
  @Test
  public void askUseCaseForOrdersListForSet() {
    // Action:
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order2).test().isDisposed();
    useCase.setSelectedOrder(order3).test().isDisposed();

    // Effect:
    verify(ordersUseCase, times(3)).getOrdersSet();
    verifyNoMoreInteractions(ordersUseCase);
  }

  /* Проверяем ответы */

  /**
   * Не долже ничем отвечать, если выбора не было.
   */
  @Test
  public void doNotAnswerForNoChoice() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить выбранными заказами.
   */
  @Test
  public void answerWithSelectedOrders() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order2).test().isDisposed();
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order).test().isDisposed();

    // Effect:
    testSubscriber.assertValues(order1, order2, order1, order);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить последним выбранным заказом.
   */
  @Test
  public void answerWithLastSelectedOrder() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Action:
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order2).test().isDisposed();
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order).test().isDisposed();
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertValues(order);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить успешным выбром заказов.
   */
  @Test
  public void answerWithSelectOrdersSuccess() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Action:
    useCase.getOrders().test().isDisposed();

    // Effect:
    useCase.setSelectedOrder(order1).test().assertComplete();
    useCase.setSelectedOrder(order2).test().assertComplete();
    useCase.setSelectedOrder(order1).test().assertComplete();
    useCase.setSelectedOrder(order).test().assertComplete();
  }

  /**
   * Должен ответить выбранным заказом, пока он есть в списке, но не спамить.
   */
  @Test
  public void answerWithSelectedOrder() {
    // Given:
    PublishSubject<Set<Order>> publishSubject = PublishSubject.create();
    when(ordersUseCase.getOrdersSet()).thenReturn(
        publishSubject.toFlowable(BackpressureStrategy.BUFFER)
    );

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test().isDisposed();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order3, order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order1, order3)));

    // Effect:
    testSubscriber.assertValues(order1);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerError() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку выбора.
   */
  @Test
  public void answerErrorOnSelection() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.error(DataMappingException::new));

    // Action:
    TestObserver testObserver = useCase.setSelectedOrder(order1).test();

    // Effect:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  /**
   * Не должен возвращать подписчикам ошибку, при неверном выборе.
   */
  @Test
  public void doNotBotherSubscribersWithWrongChoiceError() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order3)))
            .concatWith(Flowable.never())
    );

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setSelectedOrder(order2).test().isDisposed();

    // Effect:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку, если выбранного элемента нет в списке.
   */
  @Test
  public void answerWrongSelectionError() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order3)))
            .concatWith(Flowable.never())
    );

    // Action:
    useCase.getOrders().test().isDisposed();
    TestObserver<Void> testObserver = useCase.setSelectedOrder(order2).test();

    // Effect:
    testObserver.assertError(NoSuchElementException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  /**
   * Должен вернуть подписчикам ошибку отмененного выбранного заказа, если его больше нет в списке.
   */
  @Test
  public void answerWrongSelectionErrorForNewList() {
    // Given:
    PublishSubject<Set<Order>> publishSubject = PublishSubject.create();
    when(ordersUseCase.getOrdersSet()).thenReturn(
        publishSubject.toFlowable(BackpressureStrategy.BUFFER)
    );

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test().isDisposed();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order2, order3)));

    // Effect:
    testSubscriber.assertValues(order1);
    testSubscriber.assertError(OrderCancelledException.class);
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен завершить получение выбора заказов.
   */
  @Test
  public void answerComplete() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.empty());

    // Action:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Effect:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Не должен возвращать полученые ранее выбранные заказы после ошибки.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerNothingAfterError() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never()));

    // Action:
    Flowable<Order> orders = useCase.getOrders();
    TestSubscriber<Order> testSubscriber = orders.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order).test().isDisposed();
    useCase.setSelectedOrder(order2).test().isDisposed();
    TestSubscriber<Order> testSubscriber0 = orders.test();
    emitter.onError(new Exception());
    TestSubscriber<Order> testSubscriber1 = orders.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));

    // Effect:
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertValues(order1, order, order2);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertError(Exception.class);
    testSubscriber0.assertValues(order2);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }

  /**
   * Не должен возвращать полученые ранее выбранные заказы после завершения.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void answerNothingAfterComplete() {
    // Given:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Action:
    Flowable<Order> orders = useCase.getOrders().doOnNext(System.out::println);
    TestSubscriber<Order> testSubscriber = orders.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test().isDisposed();
    useCase.setSelectedOrder(order).test().isDisposed();
    useCase.setSelectedOrder(order2).test().isDisposed();
    TestSubscriber<Order> testSubscriber0 = orders.test();
    emitter.onComplete();
    TestSubscriber<Order> testSubscriber1 = orders.test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));

    // Effect:
    testSubscriber.assertNoErrors();
    testSubscriber.assertValues(order1, order, order2);
    testSubscriber.assertComplete();
    testSubscriber0.assertNoErrors();
    testSubscriber0.assertValues(order2);
    testSubscriber0.assertComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}