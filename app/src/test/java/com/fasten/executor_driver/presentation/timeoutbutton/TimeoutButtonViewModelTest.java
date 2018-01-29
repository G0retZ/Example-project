package com.fasten.executor_driver.presentation.timeoutbutton;

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
public class TimeoutButtonViewModelTest {

	private TimeoutButtonViewModel timeoutButtonViewModel;

	private TestScheduler testScheduler;


	@Rule
	public TestRule rule = new InstantTaskExecutorRule();

	@Mock
	private Observer<ViewState<TimeoutButtonViewActions>> viewStateObserver;

	@Before
	public void setUp() throws Exception {
		testScheduler = new TestScheduler();
		RxJavaPlugins.setIoSchedulerHandler(scheduler -> testScheduler);
		timeoutButtonViewModel = new TimeoutButtonViewModelImpl(10);
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
		timeoutButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

		// Результат:
		verify(viewStateObserver, only()).onChanged(any(TimeoutButtonViewStateReady.class));
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
		timeoutButtonViewModel.getViewStateLiveData().observeForever(viewStateObserver);

		// Действие:
		timeoutButtonViewModel.buttonClicked();
		testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

		// Результат:
		inOrder.verify(viewStateObserver).onChanged(any(TimeoutButtonViewStateReady.class));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(10));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(9));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(8));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(7));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(6));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(5));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(4));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(3));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(2));
		inOrder.verify(viewStateObserver).onChanged(new TimeoutButtonViewStateHold(1));
		inOrder.verify(viewStateObserver).onChanged(any(TimeoutButtonViewStateReady.class));
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
		assertTrue(timeoutButtonViewModel.buttonClicked());
	}

	/**
	 * Должен вернуть false после true.
	 *
	 * @throws Exception error
	 */
	@Test
	public void returnFalseAfterLegalClick() throws Exception {
		assertTrue(timeoutButtonViewModel.buttonClicked());
		assertFalse(timeoutButtonViewModel.buttonClicked());
	}

	/**
	 * Должен вернуть true после истечения таймера.
	 *
	 * @throws Exception error
	 */
	@Test
	public void returnTrueForLegalClickAfterTimeout() throws Exception {
		assertTrue(timeoutButtonViewModel.buttonClicked());
		assertFalse(timeoutButtonViewModel.buttonClicked());
		testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
		assertFalse(timeoutButtonViewModel.buttonClicked());
		testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
		assertFalse(timeoutButtonViewModel.buttonClicked());
		testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		assertTrue(timeoutButtonViewModel.buttonClicked());
	}
}
