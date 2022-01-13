package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MessageFcmFilterTest {

  private MessageFcmFilter filter;
  @Mock
  private Map<String, String> map;

  @Before
  public void setUp() {
    filter = new MessageFcmFilter();
  }

  /**
   * Должен отказать, если заголовок не соответствует фильтруемому.
   */
  @Test
  public void FilterIfExecutorStateIncorrect() {
    // Action и Effect:
    assertFalse(filter.test(map));
  }

  /**
   * Должен пропустить, если сообщение с заголовком MissedOrder.
   */
  @Test
  public void allowForHeaderWithCorrectValue() {
    // Given:
    when(map.get("action")).thenReturn("ANNOUNCEMENT");

    // Action и Effect:
    assertTrue(filter.test(map));
  }
}