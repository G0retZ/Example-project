package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.entity.PackageCostDetails;
import com.cargopull.executor_driver.utils.Pair;
import java.util.Arrays;
import javax.inject.Inject;

/**
 * Преобразуем строку из ответа сервера в бизнес объект детального расчета заказа.
 */
public class OrderCostDetailsApiMapper implements Mapper<String, OrderCostDetails> {

  @Inject
  public OrderCostDetailsApiMapper() {

  }

  @NonNull
  @Override
  public OrderCostDetails map(@NonNull String from) {
    return new OrderCostDetails(
        123,
        new PackageCostDetails(
            234, 345, 456, 567,
            Arrays.asList(
                new Pair<>(
                    "name1", 10
                ),
                new Pair<>(
                    "name2", 20
                ),
                new Pair<>(
                    "name3", 30
                )
            )
        ),
        new PackageCostDetails(
            117, 172, 228, 283,
            Arrays.asList(
                new Pair<>(
                    "name1", 5
                ),
                new Pair<>(
                    "name2", 10
                ),
                new Pair<>(
                    "name3", 15
                )
            )
        ),
        new PackageCostDetails(
            23, 35, 46, 57,
            Arrays.asList(
                new Pair<>(
                    "name1", 1
                ),
                new Pair<>(
                    "name2", 2
                ),
                new Pair<>(
                    "name3", 3
                )
            )
        )
    );
  }
}
