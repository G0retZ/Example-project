package com.cargopull.executor_driver.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class LoginDataTest {

  private LoginData loginData;

  @Before
  public void setUp() {
    loginData = new LoginData("name", "password");
  }

  @Test
  public void testConstructor() {
    assertEquals(loginData.getLogin(), "name");
    assertEquals(loginData.getPassword(), "password");
  }

  @Test
  public void testSetters() {
    assertEquals(loginData.setLogin("nam").getLogin(), "nam");
    assertEquals(loginData.setPassword("pass").getPassword(), "pass");
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testEquals() {
    assertEquals(loginData, new LoginData("name", "password"));
    assertNotEquals(loginData, new LoginData("nam", "password"));
    assertNotEquals(loginData, new LoginData("name", "passwor"));
    assertNotEquals(loginData, loginData.setLogin("namee"));
    assertNotEquals(loginData, loginData.setPassword("passwordd"));
  }
}
