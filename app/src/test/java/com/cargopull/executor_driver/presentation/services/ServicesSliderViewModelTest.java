package com.cargopull.executor_driver.presentation.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesSliderViewModelTest {

  @Rule
  public TestRule rule = new InstantTaskExecutorRule();
  private ServicesSliderViewModel viewModel;
  @Mock
  private ServicesListItems servicesListItems;

  @Mock
  private Observer<ViewState<ServicesSliderViewActions>> viewStateObserver;

  @Captor
  private ArgumentCaptor<ViewState<ServicesSliderViewActions>> viewStateCaptor;

  @Mock
  private ServicesSliderViewActions viewActions;

  @Before
  public void setUp() {
    viewModel = new ServicesSliderViewModelImpl(servicesListItems);
    when(servicesListItems.getCurrentPosition()).thenReturn(30);
    when(servicesListItems.getMaxPrice()).thenReturn(1500);
    when(servicesListItems.getMinPrice()).thenReturn(500);
  }

  /* Тетсируем работу с фильтром услуг. */

  /**
   * Должен запросить у фильтра параметры ползунка.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  public void askServicesFilter() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.refresh();
    verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);

    // Результат:
    verify(servicesListItems).getCurrentPosition();
    verify(servicesListItems).getMaxPrice();
    verify(servicesListItems).getMinPrice();
    verifyNoMoreInteractions(servicesListItems);
  }

  /* Тетсируем применение состояния. */
  @Test
  public void stateApply() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.refresh();
    verify(viewStateObserver).onChanged(viewStateCaptor.capture());
    viewStateCaptor.getValue().apply(viewActions);

    // Результат:
    verify(viewActions).setMaxPrice(1500);
    verify(viewActions).setMinPrice(500);
    verify(viewActions).setSliderPosition(30);
    verifyNoMoreInteractions(viewActions);
  }

  /* Тетсируем переключение состояний. */

  /**
   * Должен вернуть свое состояние вида.
   */
  @Test
  public void setViewStateToLiveData() {
    // Дано:
    viewModel.getViewStateLiveData().observeForever(viewStateObserver);

    // Действие:
    viewModel.refresh();

    // Результат:
    verify(viewStateObserver).onChanged(any(ServicesSliderViewModelImpl.class));
    verifyNoMoreInteractions(viewStateObserver);
  }

}