package com.cargopull.executor_driver.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListItem;
import io.reactivex.functions.Consumer;

class PreOrdersAdapter extends ListAdapter<PreOrdersListItem, RecyclerView.ViewHolder> {

  @NonNull
  private final Consumer<Order> selectionConsumer;

  PreOrdersAdapter(@NonNull Consumer<Order> selectionConsumer) {
    super(new ItemCallback<PreOrdersListItem>() {
      @Override
      public boolean areContentsTheSame(@NonNull PreOrdersListItem oldItem,
          @NonNull PreOrdersListItem newItem) {
        return oldItem.equals(newItem);
      }

      @Override
      public boolean areItemsTheSame(@NonNull PreOrdersListItem item1,
          @NonNull PreOrdersListItem item2) {
        return item1.equals(item2);
      }
    });
    this.selectionConsumer = selectionConsumer;
  }

  @Override
  public int getItemViewType(int position) {
    return getItem(position).getViewType();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == PreOrdersListItem.TYPE_HEADER) {
      return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_pre_orders_list_header, parent, false));
    } else if (viewType == PreOrdersListItem.TYPE_ITEM) {
      return new ItemViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_pre_orders_list_item, parent, false));
    }
    return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_pre_orders_list_header, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    PreOrdersListItem item = getItem(position);
    switch (item.getViewType()) {
      case PreOrdersListItem.TYPE_HEADER:
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.dayOfMonthText.setText(item.getOccupationDayOfMonth());
        headerViewHolder.monthText.setText(item.getOccupationMonth(holder.itemView.getResources()));
        headerViewHolder.dayOfWeekText.setText(item.getOccupationDayOfWeek());
        break;
      case PreOrdersListItem.TYPE_ITEM:
        holder.itemView.setOnClickListener(null);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.occupationTimeText.setText(item.getOccupationTime());
        itemViewHolder.addressText.setText(item.getNextAddress());
        itemViewHolder.distanceText
            .setText(holder.itemView.getResources().getString(R.string.km, item.getRouteLength()));
        itemViewHolder.priceText.setText(item.getEstimatedPrice(holder.itemView.getResources()));
        holder.itemView.setOnClickListener(v -> {
          try {
            selectionConsumer.accept(item.getOrder());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
        break;
    }
  }

  final class HeaderViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView dayOfMonthText;
    @NonNull
    private final TextView monthText;
    @NonNull
    private final TextView dayOfWeekText;

    HeaderViewHolder(@NonNull View itemView) {
      super(itemView);
      dayOfMonthText = itemView.findViewById(R.id.dayOfMonth);
      monthText = itemView.findViewById(R.id.month);
      dayOfWeekText = itemView.findViewById(R.id.dayOfWeek);
    }
  }

  final class ItemViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView occupationTimeText;
    @NonNull
    private final TextView addressText;
    @NonNull
    private final TextView distanceText;
    @NonNull
    private final TextView priceText;

    ItemViewHolder(@NonNull View itemView) {
      super(itemView);
      occupationTimeText = itemView.findViewById(R.id.occupationTime);
      addressText = itemView.findViewById(R.id.address);
      distanceText = itemView.findViewById(R.id.distanceText);
      priceText = itemView.findViewById(R.id.priceText);
    }
  }
}
