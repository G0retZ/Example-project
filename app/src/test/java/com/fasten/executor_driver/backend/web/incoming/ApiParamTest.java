package com.fasten.executor_driver.backend.web.incoming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class ApiParamTest {

  private ApiParam apiParam;

  @Before
  public void setUp() throws Exception {
    apiParam = new ApiParam("name");
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiParam.getName(), "name");
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() throws Exception {
    assertEquals(apiParam, new ApiParam("name"));
    assertNotEquals(apiParam, new ApiParam("names"));
  }
}