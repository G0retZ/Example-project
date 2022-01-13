package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.web.incoming.ApiServiceItem;
import com.cargopull.executor_driver.entity.Service;

import org.junit.Before;
import org.junit.Test;

public class ServiceApiMapperTest {

  private Mapper<ApiServiceItem, Service> mapper;

  @Before
  public void setUp() {
    mapper = new ServiceApiMapper();
  }


  /**
   * Должен успешно преобразовать не выбранную услугу из АПИ в бизнес-сущность.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingToUnselectedService() throws Exception {
    // Given и Action:
    Service service = mapper.map(new ApiServiceItem(0, "name", 1000));

      // Effect:
    assertEquals(service, new Service(0, "name", 1000, false));
  }

  /**
   * Должен успешно преобразовать выбранную услугу из АПИ в бизнес-сущность.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingToSelectedService() throws Exception {
      // Given и Action:
    Service service = mapper.map(new ApiServiceItem(1, "n", 300).setSelected(true));

      // Effect:
    assertEquals(service, new Service(1, "n", 300, true));
  }

  /**
   * Должен дать ошибку, если имя null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullNameFail() throws Exception {
      // Given и Action:
    mapper.map(new ApiServiceItem(1, null, 300));
  }

  /**
   * Должен дать ошибку, если цена null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullPriceFail() throws Exception {
      // Given и Action:
    mapper.map(new ApiServiceItem(1, "a", null));
  }
}