package com.fasten.executor_driver.backend.web.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ApiLoginTest {

  private ApiLogin apiLogin;

  @Before
  public void setUp() throws Exception {
    apiLogin = new ApiLogin("name", "password");
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(apiLogin.getName(), "name");
    assertEquals(apiLogin.getPassword(), "password");
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(apiLogin, new ApiLogin("name", "password"));
    assertNotEquals(apiLogin, new ApiLogin("nam", "password"));
    assertNotEquals(apiLogin, new ApiLogin("name", "passwor"));
  }
}