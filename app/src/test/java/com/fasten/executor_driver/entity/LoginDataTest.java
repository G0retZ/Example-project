package com.fasten.executor_driver.entity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LoginDataTest {

  private LoginData loginData;

  @Before
  public void setUp() throws Exception {
    loginData = new LoginData("name", "password");
  }

  @Test
  public void testConstructor() throws Exception {
    assertEquals(loginData.getLogin(), "name");
    assertEquals(loginData.getPassword(), "password");
  }

  @Test
  public void testSetters() throws Exception {
    assertEquals(loginData.setLogin("nam").getLogin(), "nam");
    assertEquals(loginData.setLogin("nam").getLogin(), "nam");
    assertEquals(loginData.setPassword("pass").getPassword(), "pass");
    assertEquals(loginData.setPassword("pass").getPassword(), "pass");
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(loginData, new LoginData("name", "password"));
    assertNotEquals(loginData, new LoginData("nam", "password"));
    assertNotEquals(loginData, new LoginData("name", "passwor"));
    assertNotEquals(loginData, loginData.setLogin("namee"));
    assertNotEquals(loginData, loginData.setPassword("passwordd"));
  }
}
