package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.entity.PackageCostDetails;
import com.cargopull.executor_driver.entity.PackageOptionCost;
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
                new PackageOptionCost(
                    "name1", 10
                ),
                new PackageOptionCost(
                    "name2", 20
                ),
                new PackageOptionCost(
                    "name3", 30
                )
            )
        ),
        new PackageCostDetails(
            117, 172, 228, 283,
            Arrays.asList(
                new PackageOptionCost(
                    "name1", 5
                ),
                new PackageOptionCost(
                    "name2", 10
                ),
                new PackageOptionCost(
                    "name3", 15
                )
            )
        ),
        new PackageCostDetails(
            23, 35, 46, 57,
            Arrays.asList(
                new PackageOptionCost(
                    "name1", 1
                ),
                new PackageOptionCost(
                    "name2", 2
                ),
                new PackageOptionCost(
                    "name3", 3
                )
            )
        )
    );
  }
}
