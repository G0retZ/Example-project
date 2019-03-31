package com.cargopull.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiOrderServiceTest {

  private ApiOrderService apiOrderService;

  @Before
  public void setUp() {
    apiOrderService = new ApiOrderService(4, "name", 2179);
  }

  @Test
  public void testConstructor() {
    assertEquals(apiOrderService.getId(), 4);
    assertEquals(apiOrderService.getName(), "name");
    assertEquals(apiOrderService.getPrice(), 2179);
  }
}