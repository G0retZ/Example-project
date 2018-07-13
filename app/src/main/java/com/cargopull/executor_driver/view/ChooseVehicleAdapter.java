package com.cargopull.executor_driver.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleListItem;
import com.cargopull.executor_driver.view.ChooseVehicleAdapter.ChooseVehicleViewHolder;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.util.List;

class ChooseVehicleAdapter extends RecyclerView.Adapter<ChooseVehicleViewHolder> {

  @NonNull
  private final List<ChooseVehicleListItem> list;
  @NonNull
  private final PublishSubject<ChooseVehicleListItem> publishSubject = PublishSubject.create();

  ChooseVehicleAdapter(@NonNull List<ChooseVehicleListItem> list) {
    this.list = list;
  }

  @NonNull
  Observable<ChooseVehicleListItem> getSelectionCallbacks() {
    return publishSubject;
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  @NonNull
  @Override
  public ChooseVehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_choose_vehicle_list_item, parent, false);
    return new ChooseVehicleViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ChooseVehicleViewHolder holder, int position) {
    ChooseVehicleListItem chooseVehicleListItem = list.get(position);
    holder.nameText.setText(chooseVehicleListItem.getName());
    holder.labelText.setText(chooseVehicleListItem.getLabel());
    holder.itemView.setOnClickListener(
        chooseVehicleListItem.isSelectable() ?
            v -> publishSubject.onNext(chooseVehicleListItem) : null
    );
    holder.nameText.setEnabled(chooseVehicleListItem.isSelectable());
    holder.labelText.setEnabled(chooseVehicleListItem.isSelectable());
  }

  final class ChooseVehicleViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView nameText;
    @NonNull
    private final TextView labelText;

    ChooseVehicleViewHolder(@NonNull View itemView) {
      super(itemView);
      nameText = itemView.findViewById(R.id.vehicleNameText);
      labelText = itemView.findViewById(R.id.vehicleStatusText);
    }
  }
}
