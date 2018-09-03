package com.cargopull.executor_driver.interactor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.UseCaseThreadTestRule;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.utils.ErrorReporter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersUseCaseTest {

  @ClassRule
  public static final UseCaseThreadTestRule classRule = new UseCaseThreadTestRule();

  private PreOrdersUseCase useCase;

  @Mock
  private ErrorReporter errorReporter;
  @Mock
  private CommonGateway<List<Order>> gateway;
  @Mock
  private Order order;
  @Mock
  private Order order1;
  @Mock
  private Order order2;
  @Mock
  private Order order3;

  @Before
  public void setUp() {
    when(gateway.getData()).thenReturn(Flowable.never());
    useCase = new PreOrdersUseCaseImpl(errorReporter, gateway);
  }

  /* Проверяем работу с гейтвеем */

  /**
   * Должен запросить у гейтвея получение запланированных предзаказов только раз.
   */
  @Test
  public void askGatewayForOrdersOnlyOnce() {
    // Действие:
    useCase.getPreOrders().test();
    useCase.getPreOrders().test();
    useCase.unSchedulePreOrder(order1);
    useCase.getPreOrders().test();
    useCase.getPreOrders().test();
    useCase.schedulePreOrder(order3);

    // Результат:
    verify(gateway, only()).getData();
  }

  /* Проверяем отправку ошибок в репортер */

  /**
   * Должен отправить ошибку.
   */
  @Test
  public void reportError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    useCase.getPreOrders().test();

    // Результат:
    verify(errorReporter, only()).reportError(any(DataMappingException.class));
  }

  /* Проверяем ответы */

  /**
   * Должен ответить заказами.
   */
  @Test
  public void answerWithOrders() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<List<Order>>just(new ArrayList<>(Arrays.asList(order, order1, order2)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();

    // Результат:
    assertEquals(1, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order2)),
        testSubscriber.values().get(0)
    );
    testSubscriber.assertNoErrors();
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть ошибку.
   */
  @Test
  public void answerError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.error(DataMappingException::new));

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();

    // Результат:
    testSubscriber.assertError(DataMappingException.class);
    testSubscriber.assertNotComplete();
    testSubscriber.assertNoValues();
  }

  /**
   * Должен вернуть список, затем его обновление без удаленного элемента.
   */
  @Test
  public void answerWithUpdatedListOnUnSchedule() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<List<Order>>just(new ArrayList<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();
    useCase.unSchedulePreOrder(order2);

    // Результат:
    assertEquals(2, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order2, order3)),
        testSubscriber.values().get(0)
    );
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order3)),
        testSubscriber.values().get(1)
    );
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть список, затем его обновление с добавленным элементом.
   */
  @Test
  public void answerWithUpdatedListOnSchedule() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<List<Order>>just(new ArrayList<>(Arrays.asList(order, order1, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();
    useCase.schedulePreOrder(order2);

    // Результат:
    assertEquals(2, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order3)),
        testSubscriber.values().get(0)
    );
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order3, order2)),
        testSubscriber.values().get(1)
    );
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошено удаленние отсутствующего элемента.
   */
  @Test
  public void answerWithSameListOnUnSchedule() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<List<Order>>just(new ArrayList<>(Arrays.asList(order, order1, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();
    useCase.unSchedulePreOrder(order2);

    // Результат:
    assertEquals(2, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order3)),
        testSubscriber.values().get(0)
    );
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order3)),
        testSubscriber.values().get(1)
    );
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен вернуть тот же список, если запрошено добавление присутствующего элемента.
   */
  @Test
  public void answerWithSameListOnSchedule() {
    // Дано:
    when(gateway.getData()).thenReturn(
        Flowable.<List<Order>>just(new ArrayList<>(Arrays.asList(order, order1, order2, order3)))
            .concatWith(Flowable.never())
    );

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();
    useCase.schedulePreOrder(order2);

    // Результат:
    assertEquals(2, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order2, order3)),
        testSubscriber.values().get(0)
    );
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order2, order3)),
        testSubscriber.values().get(1)
    );
    testSubscriber.assertNotComplete();
  }

  /**
   * Должен завершить получение заказов.
   */
  @Test
  public void answerComplete() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.empty());

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();

    // Результат:
    testSubscriber.assertComplete();
    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }

  /**
   * Не должен возвращать полученые ранее заказы после ошибки.
   */
  @Test
  public void answerNothingAfterError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<List<Order>>() {
      private boolean run;

      @Override
      public void subscribe(FlowableEmitter<List<Order>> emitter) {
        if (!run) {
          emitter.onNext(new ArrayList<>(Arrays.asList(order, order1, order2)));
          emitter.onError(new Exception());
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();
    TestSubscriber<List<Order>> testSubscriber1 = useCase.getPreOrders().test();

    // Результат:
    assertEquals(1, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order2)),
        testSubscriber.values().get(0)
    );
    testSubscriber.assertError(Exception.class);
    testSubscriber.assertNotComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }

  /**
   * Не должен возвращать полученые ранее заказы после завершения.
   */
  @Test
  public void answerNothingAfterPreOrderExpiredError() {
    // Дано:
    when(gateway.getData()).thenReturn(Flowable.create(new FlowableOnSubscribe<List<Order>>() {
      private boolean run;

      @Override
      public void subscribe(FlowableEmitter<List<Order>> emitter) {
        if (!run) {
          emitter.onNext(new ArrayList<>(Arrays.asList(order, order1, order2)));
          emitter.onComplete();
          run = true;
        }
      }
    }, BackpressureStrategy.BUFFER));

    // Действие:
    TestSubscriber<List<Order>> testSubscriber = useCase.getPreOrders().test();
    TestSubscriber<List<Order>> testSubscriber1 = useCase.getPreOrders().test();

    // Результат:
    assertEquals(1, testSubscriber.values().size());
    assertEquals(
        new ArrayList<>(Arrays.asList(order, order1, order2)),
        testSubscriber.values().get(0)
    );
    testSubscriber.assertNoErrors();
    testSubscriber.assertComplete();
    testSubscriber1.assertNoValues();
    testSubscriber1.assertNoErrors();
    testSubscriber1.assertNotComplete();
  }
}