package com.cargopull.executor_driver.utils;

public interface Releasable {

  /**
   * When an object implementing interface <code>Releasable</code> is used to release sources, the
   * object's <code>release</code> method should be called.
   * <p>
   * The general contract of the method <code>release</code> is that it should take any action to
   * release held sources.
   */
  void release();
}
