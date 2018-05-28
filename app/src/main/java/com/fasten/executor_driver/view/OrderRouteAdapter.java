package com.fasten.executor_driver.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.orderroute.RoutePointItem;
import com.fasten.executor_driver.presentation.orderroute.RoutePointItems;
import io.reactivex.functions.Consumer;
import java.util.Locale;

class OrderRouteAdapter extends RecyclerView.Adapter<OrderRouteAdapter.RoutePointViewHolder> {

  @NonNull
  private final RoutePointItems routePointItems;
  @NonNull
  private final Consumer<RoutePointItem> selectListener;

  OrderRouteAdapter(@NonNull RoutePointItems routePointItems,
      @NonNull Consumer<RoutePointItem> selectListener) {
    this.routePointItems = routePointItems;
    this.selectListener = selectListener;
  }

  @Override
  public int getItemCount() {
    return routePointItems.size();
  }

  @Override
  public int getItemViewType(int position) {
    return routePointItems.get(position).isChecked()
        ? R.layout.fragment_order_route_point_item_checked
        : R.layout.fragment_order_route_point_item;
  }

  @NonNull
  @Override
  public OrderRouteAdapter.RoutePointViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    return new RoutePointViewHolder(view);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onBindViewHolder(@NonNull OrderRouteAdapter.RoutePointViewHolder holder,
      int position) {
    RoutePointItem item = routePointItems.get(position);
    if (holder.positionText != null) {
      holder.positionText.setText(String.format(Locale.US, "%d", position + 1));
    }
    holder.addressText.setText(item.getAddress());
    if (holder.selectNextAction != null) {
      if (routePointItems.isInProgress(item)) {
        holder.selectNextAction.setEnabled(false);
        holder.selectNextAction.setText(R.string.in_progress);
      } else {
        holder.selectNextAction.setEnabled(true);
        holder.selectNextAction.setText(R.string.select);
        holder.selectNextAction.setOnClickListener(v -> {
              try {
                selectListener.accept(item);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
        );
      }
    }
  }

  final class RoutePointViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    private final TextView positionText;
    @NonNull
    private final TextView addressText;
    @Nullable
    private final Button selectNextAction;

    RoutePointViewHolder(@NonNull View itemView) {
      super(itemView);
      positionText = itemView.findViewById(R.id.positionText);
      addressText = itemView.findViewById(R.id.addressText);
      selectNextAction = itemView.findViewById(R.id.selectNextAction);
    }
  }
}
