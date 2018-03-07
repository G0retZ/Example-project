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
import com.fasten.executor_driver.presentation.options.OptionsListItem;
import com.fasten.executor_driver.presentation.options.OptionsListItems;
import java.util.Locale;

class OptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  @NonNull
  private final OptionsListItems optionsListItems;

  OptionsAdapter(@NonNull OptionsListItems optionsListItems) {
    this.optionsListItems = optionsListItems;
  }

  @NonNull
  OptionsListItems getOptionsListItems() {
    return optionsListItems;
  }

  @Override
  public int getItemCount() {
    return optionsListItems.size();
  }

  @Override
  public int getItemViewType(int position) {
    return optionsListItems.get(position).getLayoutType();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
    if (viewType == R.layout.fragment_vehicle_options_list_item_boolean) {
      return new VehicleBooleanOptionViewHolder(view);
    } else {
      return new VehicleNumericOptionViewHolder(view);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof VehicleBooleanOptionViewHolder) {
      OptionsListItem<Boolean> item = (OptionsListItem<Boolean>) optionsListItems.get(position);
      holder.itemView.setOnClickListener(null);
      ((VehicleBooleanOptionViewHolder) holder).nameText.setText(item.getName());
      ((VehicleBooleanOptionViewHolder) holder).descriptionText
          .setVisibility(item.getDescription() == null ? View.GONE : View.VISIBLE);
      ((VehicleBooleanOptionViewHolder) holder).descriptionText.setText(item.getDescription());
      ((VehicleBooleanOptionViewHolder) holder).switchCompat.setOnCheckedChangeListener(null);
      ((VehicleBooleanOptionViewHolder) holder).switchCompat.setChecked(item.getValue());
      ((VehicleBooleanOptionViewHolder) holder).switchCompat
          .setOnCheckedChangeListener((v, b) -> item.setValue(b));
      holder.itemView.setOnClickListener(v ->
          ((VehicleBooleanOptionViewHolder) holder).switchCompat.performClick()
      );
    } else if (holder instanceof VehicleNumericOptionViewHolder) {
      OptionsListItem<Integer> item = (OptionsListItem<Integer>) optionsListItems.get(position);
      ((VehicleNumericOptionViewHolder) holder).nameText.setText(item.getName());
      ((VehicleNumericOptionViewHolder) holder).descriptionText.setText(item.getDescription());
      ((VehicleNumericOptionViewHolder) holder).seekBar.setOnSeekBarChangeListener(null);
      ((VehicleNumericOptionViewHolder) holder).seekBar
          .setMax(item.getMaxValue() - item.getMinValue());
      ((VehicleNumericOptionViewHolder) holder).seekBar
          .setProgress(item.getValue() + item.getMinValue());
      ((VehicleNumericOptionViewHolder) holder).amountText.setText(
          String.format(Locale.getDefault(), "%d", (item.getValue() + item.getMinValue())));
      ((VehicleNumericOptionViewHolder) holder).seekBar
          .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (fromUser) {
                item.setValue(item.getMinValue() + progress);
                ((VehicleNumericOptionViewHolder) holder).amountText.setText(String
                    .format(Locale.getDefault(), "%d", (item.getValue() + item.getMinValue())));
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
    private final TextView descriptionText;
    @NonNull
    private final TextView amountText;
    @NonNull
    private final SeekBar seekBar;

    VehicleNumericOptionViewHolder(@NonNull View itemView) {
      super(itemView);
      nameText = itemView.findViewById(R.id.optionName);
      descriptionText = itemView.findViewById(R.id.optionDescription);
      seekBar = itemView.findViewById(R.id.optionSeekBar);
      amountText = itemView.findViewById(R.id.optionAmount);
    }
  }

  final class VehicleBooleanOptionViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView nameText;
    @NonNull
    private final TextView descriptionText;
    @NonNull
    private final SwitchCompat switchCompat;

    VehicleBooleanOptionViewHolder(@NonNull View itemView) {
      super(itemView);
      nameText = itemView.findViewById(R.id.optionName);
      descriptionText = itemView.findViewById(R.id.optionDescription);
      switchCompat = itemView.findViewById(R.id.optionSwitch);
    }
  }
}
