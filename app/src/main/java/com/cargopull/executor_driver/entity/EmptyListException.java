package com.cargopull.executor_driver.entity;

/**
 * Исключение опустом списке элементов.
 */
public class EmptyListException extends Exception {

  public EmptyListException() {
    super();
  }

  public EmptyListException(String message) {
    super(message);
  }
}
