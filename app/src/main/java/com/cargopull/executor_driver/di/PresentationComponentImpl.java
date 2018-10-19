package com.cargopull.executor_driver.di;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.interactor.MemoryDataSharer;
import com.cargopull.executor_driver.presentation.ViewModelFactory;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModelImpl;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModelImpl;
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModel;
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModelImpl;
import com.cargopull.executor_driver.presentation.calltooperator.CallToOperatorViewModel;
import com.cargopull.executor_driver.presentation.calltooperator.CallToOperatorViewModelImpl;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModel;
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderViewModel;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModel;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModelImpl;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl;
import com.cargopull.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModel;
import com.cargopull.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModelImpl;
import com.cargopull.executor_driver.presentation.code.CodeViewModel;
import com.cargopull.executor_driver.presentation.code.CodeViewModelImpl;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModel;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModelImpl;
import com.cargopull.executor_driver.presentation.confirmorderpayment.ConfirmOrderPaymentViewModel;
import com.cargopull.executor_driver.presentation.confirmorderpayment.ConfirmOrderPaymentViewModelImpl;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModel;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModelImpl;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModelImpl;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModelImpl;
import com.cargopull.executor_driver.presentation.map.MapViewModel;
import com.cargopull.executor_driver.presentation.map.MapViewModelImpl;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModel;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewModel;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewModelImpl;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModelImpl;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModelImpl;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModelImpl;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.order.OrderViewModelImpl;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModelImpl;
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModel;
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModelImpl;
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModel;
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModelImpl;
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModel;
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModelImpl;
import com.cargopull.executor_driver.presentation.phone.PhoneViewModel;
import com.cargopull.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModel;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListItemsMapper;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModelImpl;
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel;
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModelImpl;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModelImpl;
import com.cargopull.executor_driver.presentation.services.ServicesListItems;
import com.cargopull.executor_driver.presentation.services.ServicesSliderViewModel;
import com.cargopull.executor_driver.presentation.services.ServicesSliderViewModelImpl;
import com.cargopull.executor_driver.presentation.services.ServicesViewModel;
import com.cargopull.executor_driver.presentation.services.ServicesViewModelImpl;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewModel;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewModel;
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewModelImpl;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModelImpl;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModel;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientViewModel;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientViewModelImpl;
import com.cargopull.executor_driver.utils.TimeUtils;

class PresentationComponentImpl implements PresentationComponent {

  @NonNull
  private final EventLogger eventLogger;
  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final MemoryDataSharer<String> loginSharer;
  @NonNull
  private final InteractorComponent interactorComponent;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private final ServicesListItems servicesListItems;
  @Nullable
  private AnnouncementViewModel announcementViewModel;
  @Nullable
  private BalanceViewModel balanceViewModel;
  @Nullable
  private CancelOrderReasonsViewModel cancelOrderReasonsViewModel;
  @Nullable
  private CurrentCostPollingViewModel currentCostPollingViewModel;
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;
  @Nullable
  private MissedOrderViewModel missedOrderViewModel;
  @Nullable
  private OrderCostDetailsViewModel orderCostDetailsViewModel;
  @Nullable
  private CancelledOrderViewModel cancelledOrderViewModel;
  @Nullable
  private OrderViewModel orderViewModel;
  @Nullable
  private PreOrderViewModel preOrderViewModel;
  @Nullable
  private PreOrdersListViewModel preOrdersListViewModel;
  @Nullable
  private ServerConnectionViewModel serverConnectionViewModel;
  @Nullable
  private ServerTimeViewModel serverTimeViewModel;
  @Nullable
  private ServicesSliderViewModel servicesSliderViewModel;
  @Nullable
  private UpcomingPreOrderMessageViewModel upcomingPreOrderMessagesViewModel;
  @Nullable
  private UpdateMessageViewModel updateMessageViewModel;
  @Nullable
  private UpcomingPreOrderViewModel upcomingPreOrderAvailabilityViewModel;

  PresentationComponentImpl(@NonNull EventLogger eventLogger,
      @NonNull ErrorReporter errorReporter,
      @NonNull MemoryDataSharer<String> loginSharer,
      @NonNull BackendComponent backendComponent,
      @NonNull TimeUtils timeUtils) {
    this.eventLogger = eventLogger;
    this.errorReporter = errorReporter;
    this.loginSharer = loginSharer;
    RepositoryComponent repositoryComponent = new RepositoryComponentImpl(backendComponent);
    this.interactorComponent = new InteractorComponentImpl(
        loginSharer, timeUtils, errorReporter, repositoryComponent
    );
    this.timeUtils = timeUtils;
    servicesListItems = new ServicesListItems();
  }

  @NonNull
  @Override
  public AnnouncementViewModel getAnnouncementViewModel() {
    if (announcementViewModel == null) {
      announcementViewModel = new AnnouncementViewModelImpl();
    }
    return announcementViewModel;
  }

  @NonNull
  @Override
  public BalanceViewModel getBalanceViewModel() {
    if (balanceViewModel == null) {
      balanceViewModel = new BalanceViewModelImpl(errorReporter,
          interactorComponent.getExecutorBalanceUseCase());
    }
    return balanceViewModel;
  }

  @NonNull
  @Override
  public CallToClientViewModel getCallToClientViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        CallToClientViewModelImpl.class,
        new CallToClientViewModelImpl(interactorComponent.getCallToClientUseCase())
    );
  }

  @NonNull
  @Override
  public CallToOperatorViewModel getCallToOperatorViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        CallToOperatorViewModelImpl.class,
        new CallToOperatorViewModelImpl()
    );
  }

  @NonNull
  @Override
  public CancelledOrderViewModel getCancelledOrderViewModel() {
    if (cancelledOrderViewModel == null) {
      cancelledOrderViewModel = new CancelledOrderViewModelImpl(
          errorReporter,
          interactorComponent.getCancelledOrderMessageUseCase()
      );
    }
    return cancelledOrderViewModel;
  }

  @NonNull
  @Override
  public CancelOrderViewModel getCancelOrderViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        CancelOrderViewModelImpl.class,
        new CancelOrderViewModelImpl(errorReporter, interactorComponent.getCancelOrderUseCase())
    );
  }

  @NonNull
  @Override
  public CancelOrderReasonsViewModel getCancelOrderReasonsViewModel() {
    if (cancelOrderReasonsViewModel == null) {
      cancelOrderReasonsViewModel = new CancelOrderReasonsViewModelImpl(
          errorReporter,
          interactorComponent.getCancelOrderReasonsUseCase()
      );
    }
    return cancelOrderReasonsViewModel;
  }

  @NonNull
  @Override
  public ChooseVehicleViewModel getChooseVehicleViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        ChooseVehicleViewModelImpl.class,
        new ChooseVehicleViewModelImpl(errorReporter, interactorComponent.getVehicleChoiceUseCase())
    );
  }

  @NonNull
  @Override
  public ChooseVehicleViewModel getCurrentChooseVehicleViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        ChooseVehicleViewModelImpl.class,
        new ChooseVehicleViewModelImpl(errorReporter,
            interactorComponent.getCurrentVehicleChoiceUseCase())
    );
  }

  @NonNull
  @Override
  public ClientOrderConfirmationTimeViewModel getClientOrderConfirmationTimeViewModel(
      @Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        ClientOrderConfirmationTimeViewModelImpl.class,
        new ClientOrderConfirmationTimeViewModelImpl(
            errorReporter,
            interactorComponent.getExecutorStateUseCase()
        )
    );
  }

  @NonNull
  @Override
  public CodeViewModel getCodeViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        CodeViewModelImpl.class,
        new CodeViewModelImpl(interactorComponent.getPasswordUseCase(), timeUtils, eventLogger)
    );
  }

  @NonNull
  @Override
  public CodeHeaderViewModel getCodeHeaderViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        CodeHeaderViewModelImpl.class,
        new CodeHeaderViewModelImpl(loginSharer)
    );
  }

  @NonNull
  @Override
  public ConfirmOrderPaymentViewModel getConfirmOrderPaymentViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        ConfirmOrderPaymentViewModelImpl.class,
        new ConfirmOrderPaymentViewModelImpl(interactorComponent.getConfirmOrderPaymentUseCase())
    );
  }

  @NonNull
  @Override
  public CurrentCostPollingViewModel getCurrentCostPollingViewModel() {
    if (currentCostPollingViewModel == null) {
      currentCostPollingViewModel = new CurrentCostPollingViewModelImpl(
          errorReporter,
          interactorComponent.getCurrentCostPollingUseCase()
      );
    }
    return currentCostPollingViewModel;
  }

  @NonNull
  @Override
  public ExecutorStateViewModel getExecutorStateViewModel() {
    if (executorStateViewModel == null) {
      executorStateViewModel = new ExecutorStateViewModelImpl(
          errorReporter,
          interactorComponent.getExecutorStateUseCase()
      );
    }
    return executorStateViewModel;
  }

  @NonNull
  @Override
  public GeoLocationViewModel getGeoLocationViewModel() {
    if (geoLocationViewModel == null) {
      geoLocationViewModel = new GeoLocationViewModelImpl(
          errorReporter,
          interactorComponent.getGeoLocationUseCase()
      );
    }
    return geoLocationViewModel;
  }

  @NonNull
  @Override
  public MapViewModel getMapViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        MapViewModelImpl.class,
        new MapViewModelImpl(interactorComponent.getHeatMapUseCase())
    );
  }

  @NonNull
  @Override
  public MissedOrderViewModel getMissedOrderViewModel() {
    if (missedOrderViewModel == null) {
      missedOrderViewModel = new MissedOrderViewModelImpl(
          errorReporter,
          interactorComponent.getMissedOrderUseCase()
      );
    }
    return missedOrderViewModel;
  }

  @NonNull
  @Override
  public MovingToClientViewModel getMovingToClientViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        MovingToClientViewModelImpl.class,
        new MovingToClientViewModelImpl(interactorComponent.getMovingToClientUseCase())
    );
  }

  @NonNull
  @Override
  public NextRoutePointViewModel getNextRoutePointViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        NextRoutePointViewModelImpl.class,
        new NextRoutePointViewModelImpl(interactorComponent.getOrderRouteUseCase())
    );
  }

  @NonNull
  @Override
  public OnlineButtonViewModel getOnlineButtonViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OnlineButtonViewModelImpl.class,
        new OnlineButtonViewModelImpl(interactorComponent.getVehiclesAndOptionsUseCase())
    );
  }

  @NonNull
  @Override
  public OnlineButtonViewModel getSelectedOnlineButtonViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OnlineButtonViewModelImpl.class,
        new OnlineButtonViewModelImpl(interactorComponent.getSelectedVehiclesAndOptionsUseCase())
    );
  }

  @NonNull
  @Override
  public OnlineSwitchViewModel getOnlineSwitchViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OnlineSwitchViewModelImpl.class,
        new OnlineSwitchViewModelImpl(
            errorReporter,
            interactorComponent.getExecutorStateNotOnlineUseCase(),
            interactorComponent.getExecutorStateUseCase()
        )
    );
  }

  @NonNull
  @Override
  public OnlineSwitchViewModel getExitOnlineSwitchViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OnlineSwitchViewModelImpl.class,
        new OnlineSwitchViewModelImpl(
            errorReporter,
            interactorComponent.getExecutorStateExitUseCase(),
            interactorComponent.getExecutorStateUseCase()
        )
    );
  }

  @NonNull
  @Override
  public OrderCostDetailsViewModel getOrderCostDetailsViewModel() {
    if (orderCostDetailsViewModel == null) {
      orderCostDetailsViewModel = new OrderCostDetailsViewModelImpl(
          errorReporter,
          interactorComponent.getOrderCostDetailsUseCase()
      );
    }
    return orderCostDetailsViewModel;
  }

  @NonNull
  @Override
  public OrderViewModel getOrderViewModel() {
    if (orderViewModel == null) {
      orderViewModel = new OrderViewModelImpl(
          errorReporter,
          interactorComponent.getOrderUseCase(),
          timeUtils
      );
    }
    return orderViewModel;
  }

  @NonNull
  @Override
  public OrderViewModel getPOrderViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderViewModelImpl.class,
        new OrderViewModelImpl(errorReporter, interactorComponent.getPreOrderUseCase(), timeUtils)
    );
  }

  @NonNull
  @Override
  public OrderConfirmationViewModel getOrderConfirmationViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderConfirmationViewModelImpl.class,
        new OrderConfirmationViewModelImpl(
            interactorComponent.getOrderConfirmationUseCase(),
            timeUtils,
            eventLogger
        )
    );
  }

  @NonNull
  @Override
  public OrderConfirmationViewModel getPreOrderBookingViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderConfirmationViewModelImpl.class,
        new OrderConfirmationViewModelImpl(
            interactorComponent.getPreOrderBookingUseCase(),
            timeUtils,
            null
        )
    );
  }

  @NonNull
  @Override
  public OrderCostViewModel getOrderCostViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderCostViewModelImpl.class,
        new OrderCostViewModelImpl(errorReporter, interactorComponent.getOrderCurrentCostUseCase())
    );
  }

  @NonNull
  @Override
  public OrderRouteViewModel getOrderRouteViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderRouteViewModelImpl.class,
        new OrderRouteViewModelImpl(interactorComponent.getOrderRouteUseCase())
    );
  }

  @NonNull
  @Override
  public OrderTimeViewModel getOrderTimeViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderTimeViewModelImpl.class,
        new OrderTimeViewModelImpl(interactorComponent.getOrderFulfillmentTimeUseCase())
    );
  }

  @NonNull
  @Override
  public PhoneViewModel getPhoneViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        PhoneViewModelImpl.class,
        new PhoneViewModelImpl(interactorComponent.getLoginUseCase())
    );
  }

  @NonNull
  @Override
  public PreOrderViewModel getPreOrderViewModel() {
    if (preOrderViewModel == null) {
      preOrderViewModel = new PreOrderViewModelImpl(
          errorReporter,
          interactorComponent.getPreOrderUseCase()
      );
    }
    return preOrderViewModel;
  }

  @NonNull
  @Override
  public PreOrdersListViewModel getPreOrdersListViewModel() {
    if (preOrdersListViewModel == null) {
      preOrdersListViewModel = new PreOrdersListViewModelImpl(
          errorReporter,
          interactorComponent.getPreOrdersSetUseCase(),
          interactorComponent.getSelectedOrderUseCase(),
          new PreOrdersListItemsMapper()
      );
    }
    return preOrdersListViewModel;
  }

  @NonNull
  @Override
  public SelectedVehicleViewModel getSelectedVehicleViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        SelectedVehicleViewModelImpl.class,
        new SelectedVehicleViewModelImpl(interactorComponent.getSelectedVehicleUseCase())
    );
  }

  @NonNull
  @Override
  public ServerConnectionViewModel getServerConnectionViewModel() {
    if (serverConnectionViewModel == null) {
      serverConnectionViewModel = new ServerConnectionViewModelImpl(
          interactorComponent.getServerConnectionUseCase()
      );
    }
    return serverConnectionViewModel;
  }

  @NonNull
  @Override
  public ServerTimeViewModel getServerTimeViewModel() {
    if (serverTimeViewModel == null) {
      serverTimeViewModel = new ServerTimeViewModelImpl(
          errorReporter,
          interactorComponent.getServerTimeUseCase()
      );
    }
    return serverTimeViewModel;
  }

  @NonNull
  @Override
  public ServicesSliderViewModel getServicesSliderViewModel() {
    if (servicesSliderViewModel == null) {
      servicesSliderViewModel = new ServicesSliderViewModelImpl(servicesListItems);
    }
    return servicesSliderViewModel;
  }

  @NonNull
  @Override
  public ServicesViewModel getServicesViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        ServicesViewModelImpl.class,
        new ServicesViewModelImpl(
            errorReporter,
            interactorComponent.getServicesUseCase(),
            getServicesSliderViewModel(),
            servicesListItems
        )
    );
  }

  @NonNull
  @Override
  public SmsButtonViewModel getSmsButtonViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        SmsButtonViewModelImpl.class,
        new SmsButtonViewModelImpl(interactorComponent.getSmsUseCase())
    );
  }

  @NonNull
  @Override
  public UpcomingPreOrderMessageViewModel getUpcomingPreOrderMessagesViewModel() {
    if (upcomingPreOrderMessagesViewModel == null) {
      upcomingPreOrderMessagesViewModel = new UpcomingPreOrderMessageViewModelImpl(
          errorReporter,
          interactorComponent.getUpcomingPreOrderMessagesUseCase()
      );
    }
    return upcomingPreOrderMessagesViewModel;
  }

  @NonNull
  @Override
  public UpdateMessageViewModel getUpdateMessageViewModel() {
    if (updateMessageViewModel == null) {
      updateMessageViewModel = new UpdateMessageViewModelImpl(
          errorReporter,
          interactorComponent.getUpdateMessageUseCase()
      );
    }
    return updateMessageViewModel;
  }

  @NonNull
  @Override
  public VehicleOptionsViewModel getVehicleOptionsViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        VehicleOptionsViewModelImpl.class,
        new VehicleOptionsViewModelImpl(interactorComponent.getVehicleOptionsUseCase())
    );
  }

  @NonNull
  @Override
  public VehicleOptionsViewModel getSelectedVehicleOptionsViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        VehicleOptionsViewModelImpl.class,
        new VehicleOptionsViewModelImpl(interactorComponent.getCurrentVehicleOptionsUseCase())
    );
  }

  @NonNull
  @Override
  public WaitingForClientViewModel getWaitingForClientViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        WaitingForClientViewModelImpl.class,
        new WaitingForClientViewModelImpl(interactorComponent.getWaitingForClientUseCase())
    );
  }

  @NonNull
  @Override
  public OrderViewModel getSelectedPreOrderViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderViewModelImpl.class,
        new OrderViewModelImpl(
            errorReporter,
            interactorComponent.getSelectedPreOrderUseCase(),
            timeUtils
        )
    );
  }

  @NonNull
  @Override
  public OrderConfirmationViewModel getSelectedPreOrderConfirmationViewModel(
      @Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderConfirmationViewModelImpl.class,
        new OrderConfirmationViewModelImpl(
            interactorComponent.getSelectedPreOrderConfirmationUseCase(),
            timeUtils,
            null
        )
    );
  }

  @NonNull
  @Override
  public OrderViewModel getUpcomingPreOrderViewModel(@Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderViewModelImpl.class,
        new OrderViewModelImpl(
            errorReporter,
            interactorComponent.getUpcomingPreOrderUseCase(),
            timeUtils
        )
    );
  }

  @NonNull
  @Override
  public OrderConfirmationViewModel getUpcomingPreOrderConfirmationViewModel(
      @Nullable Fragment fragment) {
    if (fragment == null) {
      throw new NullPointerException("Фрагмент не должен быть null");
    }
    return getViewModelInstance(
        fragment,
        OrderConfirmationViewModelImpl.class,
        new OrderConfirmationViewModelImpl(
            interactorComponent.getUpcomingPreOrderConfirmationUseCase(),
            timeUtils,
            null
        )
    );
  }

  @Override
  public UpcomingPreOrderViewModel getUpcomingPreOrderAvailabilityViewModel() {
    if (upcomingPreOrderAvailabilityViewModel == null) {
      upcomingPreOrderAvailabilityViewModel = new UpcomingPreOrderViewModelImpl(
          errorReporter,
          interactorComponent.getUpcomingPreOrderUseCase()
      );
    }
    return upcomingPreOrderAvailabilityViewModel;
  }

  private <V extends ViewModel> V getViewModelInstance(
      @NonNull Fragment fragment,
      @NonNull Class<V> vClass,
      @NonNull V viewModel) {
    return ViewModelProviders.of(
        fragment,
        new ViewModelFactory<>(viewModel)
    ).get(vClass);
  }
}
