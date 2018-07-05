package com.fasten.executor_driver.view;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.services.ServicesListItem;
import com.fasten.executor_driver.view.ServicesAdapter.ServiceViewHolder;
import java.util.ArrayList;
import java.util.List;

class ServicesAdapter extends RecyclerView.Adapter<ServiceViewHolder> {

  @NonNull
  private final SortedList<ServicesListItem> sortedList;

  ServicesAdapter(Runnable runnable) {
    sortedList = new SortedList<>(ServicesListItem.class,
        new SortedListAdapterCallback<ServicesListItem>(this) {

          @Override
          public int compare(ServicesListItem o1, ServicesListItem o2) {
            return o1.getPriceValue() - o2.getPriceValue();
          }

          @Override
          public boolean areContentsTheSame(ServicesListItem oldItem, ServicesListItem newItem) {
            return oldItem.getService() == newItem.getService();
          }

          @Override
          public boolean areItemsTheSame(ServicesListItem item1, ServicesListItem item2) {
            return item1.equals(item2);
          }

          @Override
          public void onInserted(int position, int count) {
            super.onInserted(position, count);
            if (position == 0) {
              runnable.run();
            }
          }
        });
  }

  @Override
  public int getItemCount() {
    return sortedList.size();
  }

  @NonNull
  List<ServicesListItem> getServicesListItems() {
    List<ServicesListItem> servicesListItems = new ArrayList<>();
    for (int i = 0; i < getItemCount(); i++) {
      servicesListItems.add(sortedList.get(i));
    }
    return servicesListItems;
  }

  @NonNull
  @Override
  public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_services_list_item, parent, false);
    return new ServiceViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
    ServicesListItem item = sortedList.get(position);
    holder.itemView.setOnClickListener(null);
    holder.nameText.setText(item.getName());
    holder.priceText.setText(item.getPrice(holder.itemView.getResources()));
    holder.switchCompat.setOnCheckedChangeListener(null);
    holder.switchCompat.setChecked(item.isChecked());
    holder.switchCompat.setOnCheckedChangeListener((v, b) -> item.setChecked(b));
    holder.itemView.setOnClickListener(v -> holder.switchCompat.performClick());
  }

  public void submitList(List<ServicesListItem> servicesListItems) {
    sortedList.replaceAll(servicesListItems);
  }

  final class ServiceViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView nameText;
    @NonNull
    private final TextView priceText;
    @NonNull
    private final SwitchCompat switchCompat;

    ServiceViewHolder(@NonNull View itemView) {
      super(itemView);
      nameText = itemView.findViewById(R.id.serviceName);
      priceText = itemView.findViewById(R.id.servicePrice);
      switchCompat = itemView.findViewById(R.id.enableSwitch);
    }
  }
}
