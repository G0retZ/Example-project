package com.cargopull.executor_driver.entity;

/**
 * Бизнес сущность баланса исполнителя. Immutable.
 * Создается через конструктор с не нулевыми полями.
 */
public class ExecutorBalance {

  private final int mainAccount;
  private final int bonusAccount;
  private final int cashlessAccount;

  @SuppressWarnings("SameParameterValue")
  public ExecutorBalance(int mainAccount, int bonusAccount, int cashlessAccount) {
    this.mainAccount = mainAccount;
    this.bonusAccount = bonusAccount;
    this.cashlessAccount = cashlessAccount;
  }

  public int getMainAccount() {
    return mainAccount;
  }

  public int getBonusAccount() {
    return bonusAccount;
  }

  public int getCashlessAccount() {
    return cashlessAccount;
  }
}
