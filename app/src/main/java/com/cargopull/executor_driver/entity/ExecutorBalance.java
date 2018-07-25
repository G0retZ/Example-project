package com.cargopull.executor_driver.entity;

/**
 * Бизнес сущность баланса исполнителя. Immutable.
 * Создается через конструктор с не нулевыми полями.
 */
public class ExecutorBalance {

  private final long mainAccount;
  private final long bonusAccount;
  private final long cashlessAccount;

  public ExecutorBalance(long mainAccount, long bonusAccount, long cashlessAccount) {
    this.mainAccount = mainAccount;
    this.bonusAccount = bonusAccount;
    this.cashlessAccount = cashlessAccount;
  }

  public long getMainAccount() {
    return mainAccount;
  }

  public long getBonusAccount() {
    return bonusAccount;
  }

  public long getCashlessAccount() {
    return cashlessAccount;
  }
}
