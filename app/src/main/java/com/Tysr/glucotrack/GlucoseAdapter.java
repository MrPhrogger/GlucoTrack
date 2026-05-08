package com.Tysr.glucotrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class GlucoseAdapter extends RecyclerView.Adapter<GlucoseAdapter.GlucoseViewHolder> {

    private List<Glucosereading> readings = new ArrayList<>();
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Glucosereading reading);
    }

    public GlucoseAdapter(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setReadings(List<Glucosereading> readings) {
        this.readings = readings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GlucoseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_glucose_reading, parent, false);
        return new GlucoseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlucoseViewHolder holder, int position) {
        Glucosereading reading = readings.get(position);
        holder.textViewValue.setText(reading.glucoseValue + " mg/dL");
        
        // Color coding based on glucose level
        int color;
        if (reading.glucoseValue < 70) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_yellow);
        } else if (reading.glucoseValue > 140) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_red);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_green);
        }
        holder.textViewValue.setTextColor(color);

        holder.textViewTimestamp.setText(reading.timestamp);
        holder.textViewStatus.setText("Status: " + reading.status);
        holder.textViewNotes.setText("Notes: " + reading.notes);
        
        if (reading.aiTip != null && !reading.aiTip.isEmpty()) {
            holder.textViewAiTip.setText("AI Tip: " + reading.aiTip);
            holder.textViewAiTip.setVisibility(View.VISIBLE);
        } else {
            holder.textViewAiTip.setVisibility(View.GONE);
        }

        if (reading.activitySuggestion != null && !reading.activitySuggestion.isEmpty()) {
            holder.textViewActivitySuggestion.setText("Suggestion: " + reading.activitySuggestion);
            holder.textViewActivitySuggestion.setVisibility(View.VISIBLE);
        } else {
            holder.textViewActivitySuggestion.setVisibility(View.GONE);
        }

        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(reading);
            }
        });
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }

    static class GlucoseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewValue, textViewTimestamp, textViewStatus, textViewNotes, textViewAiTip, textViewActivitySuggestion;
        ImageButton buttonDelete;

        public GlucoseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewValue = itemView.findViewById(R.id.textViewGlucoseValue);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            textViewAiTip = itemView.findViewById(R.id.textViewAiTip);
            textViewActivitySuggestion = itemView.findViewById(R.id.textViewActivitySuggestion);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
