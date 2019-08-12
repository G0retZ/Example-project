package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.backend.stomp.StompFrame;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListFilterTest {

  private PreOrdersListFilter filter;
  @Mock
  private StompFrame stompFrame;

  @Before
  public void setUp() {
    filter = new PreOrdersListFilter();
  }

  /**
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void FilterIfNoCorrectHeader() {
    // Действие и Результат:
    assertFalse(filter.test(stompFrame));
  }

  /**
   * Должен пропустить, если сообщение с заголовком PreliminaryOrderList.
   */
  @Test
  public void allowForHeaderWithCorrectValue() {
    // Дано:
    when(stompFrame.getHeaders()).thenReturn(Collections.singletonMap("PreliminaryOrderList", ""));

    // Действие и Результат:
    assertTrue(filter.test(stompFrame));
  }
}