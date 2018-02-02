package com.fasten.executor_driver.presentation.smsbutton;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.fasten.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewModelTest {

  private SmsButtonViewModel smsButtonViewModel;

  private TestScheduler testScheduler;


  @Rule
  public TestRule rule = new InstantTaskExecutorRule();

  @Mock
  private Observer<ViewState<SmsButtonViewActions>> viewStateObserver;

  @Before
  public void setUp() throws Exception {
    testScheduler = new TestScheduler();
    RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
    smsButtonViewModel = new SmsButtonViewModelImpl(10);
  }

	/* Тетсируем переключение состояний */

  /**
   * Должен вернуть рабочее состояние вида.
   *
   * @throws Exception error
   */
  @Test
  public void setReadyViewStateToLiveData() throws Exception {
    // Действие:
    smsButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Результат:
    verify(viewStateObserver, only()).onChanged(any(SmsButtonViewStateReady.class));
  }

  /**
   * Должен вернуть состояния вида "Ожидайте" с отсчетом всего таймаута и возвратом обратно в
   * состояние готовности.
   *
   * @throws Exception error
   */
  @Test
  public void setHoldViewStateToLiveData() throws Exception {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewStateObserver);
    smsButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    smsButtonViewModel.buttonClicked();
    testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

    // Результат:
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(10));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(9));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(8));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(7));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(6));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(5));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(4));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(3));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(2));
    inOrder.verify(viewStateObserver).onChanged(new SmsButtonViewStateHold(1));
    inOrder.verify(viewStateObserver).onChanged(any(SmsButtonViewStateReady.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

	/* Тетсируем легальность нажатий */

  /**
   * Должен вернуть true.
   *
   * @throws Exception error
   */
  @Test
  public void returnTrueForLegalClick() throws Exception {
    assertTrue(smsButtonViewModel.buttonClicked());
  }

  /**
   * Должен вернуть false после true.
   *
   * @throws Exception error
   */
  @Test
  public void returnFalseAfterLegalClick() throws Exception {
    assertTrue(smsButtonViewModel.buttonClicked());
    assertFalse(smsButtonViewModel.buttonClicked());
  }

  /**
   * Должен вернуть true после истечения таймера.
   *
   * @throws Exception error
   */
  @Test
  public void returnTrueForLegalClickAfterTimeout() throws Exception {
    assertTrue(smsButtonViewModel.buttonClicked());
    assertFalse(smsButtonViewModel.buttonClicked());
    testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
    assertFalse(smsButtonViewModel.buttonClicked());
    testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
    assertFalse(smsButtonViewModel.buttonClicked());
    testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
    assertTrue(smsButtonViewModel.buttonClicked());
  }
}
