package com.cargopull.executor_driver.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SelectedOrderUseCaseImplTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private SelectedOrderUseCaseImpl useCase;

  @Mock
  private ErrorReporter errorReporter;
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
    useCase = new SelectedOrderUseCaseImpl(errorReporter, ordersUseCase);
  }

  /* Проверяем работу с юзкейсом списка заказов */

  /**
   * Должен запросить у юзкейса списка получение заказов только раз при первой подписке.
   */
  @Test
  public void askUseCaseForOrdersListForGet() {
    // Действие:
    useCase.getOrders().test();
    useCase.getOrders().test();
    useCase.getOrders().test();
    useCase.getOrders().test();

    // Результат:
    verify(ordersUseCase, only()).getOrdersSet();
    verifyNoMoreInteractions(ordersUseCase);
  }

  /**
   * Должен запросить у юзкейса списка получение заказов для осуществления выбора.
   */
  @Test
  public void askUseCaseForOrdersListForSet() {
    // Действие:
    useCase.setSelectedOrder(order1).test();
    useCase.setSelectedOrder(order2).test();
    useCase.setSelectedOrder(order3).test();

    // Результат:
    verify(ordersUseCase, times(3)).getOrdersSet();
    verifyNoMoreInteractions(ordersUseCase);
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Не должен отправлять ошибку при подписке (должны отправлена вышестоящим юзкейсом).
   */
  @Test
  public void reportErrorOnGet() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getOrders().test();

    // Результат:
    verifyZeroInteractions(errorReporter);
  }

  /**
   * Должен отправить ошибку при установке выбора.
   */
  @Test
  public void reportErrorOnSet() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.setSelectedOrder(order).test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /**
   * Должен отправить ошибку при неверном выборе.
   */
  @Test
  public void reportErrorOnSetWrong() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1)))
            .concatWith(Flowable.never())
    );

    // Действие:
    useCase.setSelectedOrder(order2).test();

    // Результат:
    verify(errorReporter, only()).reportError(any(NoSuchElementException.class));
  }

  /* Проверяем ответы */

  /**
   * Не долже ничем отвечать, если выбора не было.
   */
  @Test
  public void doNotAnswerForNoChoice() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить выбранными заказами.
   */
  @Test
  public void answerWithSelectedOrders() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setSelectedOrder(order1).test();
    useCase.setSelectedOrder(order2).test();
    useCase.setSelectedOrder(order1).test();
    useCase.setSelectedOrder(order).test();

    // Результат:
    testSubscriber.assertValues(order1, order2, order1, order);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить последним выбранным заказом.
   */
  @Test
  public void answerWithLastSelectedOrder() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    useCase.setSelectedOrder(order1).test();
    useCase.setSelectedOrder(order2).test();
    useCase.setSelectedOrder(order1).test();
    useCase.setSelectedOrder(order).test();
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertValues(order);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен ответить успешным выбром заказов.
   */
  @Test
  public void answerWithSelectOrdersSuccess() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    useCase.getOrders().test();

    // Результат:
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
    // Дано:
    PublishSubject<Set<Order>> publishSubject = PublishSubject.create();
    when(ordersUseCase.getOrdersSet()).thenReturn(
        publishSubject.toFlowable(BackpressureStrategy.BUFFER)
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order3, order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order1, order3)));

    // Результат:
    testSubscriber.assertValues(order1);
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerError() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть ошибку выбора.
   */
  @Test
  public void answerErrorOnSelection() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestObserver testObserver = useCase.setSelectedOrder(order1).test();

    // Результат:
    testObserver.assertError(DataMappingException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  /**
   * Не должен возвращать подписчикам ошибку, при неверном выборе.
   */
  @Test
  public void doNotBotherSubscribersWithWrongChoiceError() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    useCase.setSelectedOrder(order2).test();

    // Результат:
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку, если выбранного элемента нет в списке.
   */
  @Test
  public void answerWrongSelectionError() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    useCase.getOrders().test();
    TestObserver<Void> testObserver = useCase.setSelectedOrder(order2).test();

    // Результат:
    testObserver.assertError(NoSuchElementException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
  }

  /**
   * Должен вернуть подписчикам ошибку, если выбранного заказа больше нет в списке.
   */
  @Test
  public void answerWrongSelectionErrorForNewList() {
    // Дано:
    PublishSubject<Set<Order>> publishSubject = PublishSubject.create();
    when(ordersUseCase.getOrdersSet()).thenReturn(
        publishSubject.toFlowable(BackpressureStrategy.BUFFER)
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test();
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    publishSubject.onNext(new HashSet<>(Arrays.asList(order, order2, order3)));

    // Результат:
    testSubscriber.assertValues(order1);
    testSubscriber.assertError(NoSuchElementException.class);
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен завершить получение выбора заказов.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();

    // Результат:
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
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never()));

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    TestSubscriber<Order> testSubscriber0 = useCase.getOrders().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test();
    emitter.onError(new Exception());
    TestSubscriber<Order> testSubscriber1 = useCase.getOrders().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));

    // Результат:
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertValues(order1);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertError(Exception.class);
    testSubscriber0.assertValues(order1);
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
    // Дано:
    when(ordersUseCase.getOrdersSet()).thenReturn(
        Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER),
        Flowable.<Set<Order>>just(new HashSet<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<Order> testSubscriber = useCase.getOrders().test();
    TestSubscriber<Order> testSubscriber0 = useCase.getOrders().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));
    useCase.setSelectedOrder(order1).test();
    emitter.onComplete();
    TestSubscriber<Order> testSubscriber1 = useCase.getOrders().test();
    emitter.onNext(new HashSet<>(Arrays.asList(order, order1, order2)));

    // Результат:
    testSubscriber.assertNoErrors();
    testSubscriber.assertValues(order1);
    testSubscriber.assertNotComplete();
    testSubscriber0.assertNoErrors();
    testSubscriber0.assertValues(order1);
    testSubscriber0.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}