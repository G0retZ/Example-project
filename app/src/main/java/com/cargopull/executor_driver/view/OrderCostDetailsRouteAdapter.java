package com.cargopull.executor_driver.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.orderroute.RoutePointItem;
import java.util.List;
import java.util.Locale;

class OrderCostDetailsRouteAdapter extends
    RecyclerView.Adapter<OrderCostDetailsRouteAdapter.RoutePointViewHolder> {

  @NonNull
  private final List<RoutePointItem> routePointItems;

  OrderCostDetailsRouteAdapter(@NonNull List<RoutePointItem> routePointItems) {
    this.routePointItems = routePointItems;
  }

  @Override
  public int getItemCount() {
    return routePointItems.size();
  }

  @Override
  public int getItemViewType(int position) {
    return routePointItems.get(position).isProcessed()
        ? R.layout.fragment_order_cost_details_route_point_item_processed
        : R.layout.fragment_order_cost_details_route_point_item;
  }

  @NonNull
  @Override
  public OrderCostDetailsRouteAdapter.RoutePointViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    return new RoutePointViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull OrderCostDetailsRouteAdapter.RoutePointViewHolder holder,
      int position) {
    RoutePointItem item = routePointItems.get(position);
    if (holder.positionText != null) {
      holder.positionText.setText(String.format(Locale.US, "%d", position + 1));
    }
    holder.addressText.setText(item.getAddress());
  }

  final class RoutePointViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    private final TextView positionText;
    @NonNull
    private final TextView addressText;

    RoutePointViewHolder(@NonNull View itemView) {
      super(itemView);
      positionText = itemView.findViewById(R.id.positionText);
      addressText = itemView.findViewById(R.id.addressText);
    }
  }
}
