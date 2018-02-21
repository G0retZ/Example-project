package com.fasten.executor_driver.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsListItem;
import java.util.List;

class VehicleOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  @NonNull
  private final List<VehicleOptionsListItem<?>> list;

  VehicleOptionsAdapter(@NonNull List<VehicleOptionsListItem<?>> list) {
    this.list = list;
  }

  @NonNull
  public List<VehicleOptionsListItem<?>> getList() {
    return list;
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  @Override
  public int getItemViewType(int position) {
    return list.get(position).getLayoutType();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    if (viewType == R.layout.fragment_vehicle_options_list_item_boolean) {
      return new VehicleBooleanOptionViewHolder(view);
    } else {
      return new VehicleNumericOptionViewHolder(view);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof VehicleBooleanOptionViewHolder) {
      VehicleOptionsListItem<Boolean> item = (VehicleOptionsListItem<Boolean>) list.get(position);
      ((VehicleBooleanOptionViewHolder) holder).switchCompat.setText(item.getName());
      ((VehicleBooleanOptionViewHolder) holder).switchCompat.setOnCheckedChangeListener(null);
      ((VehicleBooleanOptionViewHolder) holder).switchCompat.setChecked(item.getValue());
      ((VehicleBooleanOptionViewHolder) holder).switchCompat
          .setOnCheckedChangeListener((v, b) -> item.setValue(b));
    } else if (holder instanceof VehicleNumericOptionViewHolder) {
      VehicleOptionsListItem<Integer> item = (VehicleOptionsListItem<Integer>) list.get(position);
      ((VehicleNumericOptionViewHolder) holder).nameText.setText(item.getName());
      ((VehicleNumericOptionViewHolder) holder).seekBar.setOnSeekBarChangeListener(null);
      ((VehicleNumericOptionViewHolder) holder).seekBar
          .setMax(item.getMaxValue() - item.getMinValue());
      ((VehicleNumericOptionViewHolder) holder).seekBar
          .setProgress(item.getValue() + item.getMinValue());
      ((VehicleNumericOptionViewHolder) holder).seekBar
          .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (fromUser) {
                item.setValue(item.getMinValue() + progress);
              }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
          });
    }
  }

  final class VehicleNumericOptionViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView nameText;
    @NonNull
    private final SeekBar seekBar;

    VehicleNumericOptionViewHolder(@NonNull View itemView) {
      super(itemView);
      nameText = itemView.findViewById(R.id.optionName);
      seekBar = itemView.findViewById(R.id.optionSeekBar);
    }
  }

  final class VehicleBooleanOptionViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final SwitchCompat switchCompat;

    VehicleBooleanOptionViewHolder(@NonNull View itemView) {
      super(itemView);
      switchCompat = itemView.findViewById(R.id.optionSwitch);
    }
  }
}
