package com.cargopull.executor_driver.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.view.CancelOrderAdapter.CancelOrderReasonViewHolder;
import io.reactivex.functions.Consumer;
import java.util.List;

class CancelOrderAdapter extends RecyclerView.Adapter<CancelOrderReasonViewHolder> {

  @NonNull
  private final List<CancelOrderReason> cancelOrderReasons;
  @NonNull
  private final Consumer<CancelOrderReason> selectListener;

  CancelOrderAdapter(@NonNull List<CancelOrderReason> cancelOrderReasons,
      @NonNull Consumer<CancelOrderReason> selectListener) {
    this.cancelOrderReasons = cancelOrderReasons;
    this.selectListener = selectListener;
  }

  @Override
  public int getItemCount() {
    return cancelOrderReasons.size();
  }

  @NonNull
  @Override
  public CancelOrderReasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_cancel_order_reason_item, parent, false);
    return new CancelOrderReasonViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull CancelOrderReasonViewHolder holder, int position) {
    CancelOrderReason cancelOrderReason = cancelOrderReasons.get(position);
    if (holder.reasonNameText != null) {
      holder.reasonNameText.setText(cancelOrderReason.getName());
    }
    holder.itemView.setOnClickListener(v -> {
          try {
            selectListener.accept(cancelOrderReason);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
    );
  }

  final class CancelOrderReasonViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    private final TextView reasonNameText;

    CancelOrderReasonViewHolder(@NonNull View itemView) {
      super(itemView);
      reasonNameText = itemView.findViewById(R.id.reasonText);
    }
  }
}
