package com.example.attendanceqr.AttendanceRecord;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceqr.AttendanceRecord.AttendanceRecord;
import com.example.attendanceqr.R;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> implements Filterable {

    private List<AttendanceRecord> fullList;
    private List<AttendanceRecord> filteredList;

    public AttendanceAdapter(List<AttendanceRecord> list) {
        this.fullList = list != null ? list : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.fullList);
    }


    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceRecord record = filteredList.get(position);

        holder.tvName.setText("Name: " + record.name);
        holder.tvUid.setText("UID: " + record.uid);
        holder.tvTime.setText("Time: " + record.time);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUid, tvTime;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUid = itemView.findViewById(R.id.tvUid);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    // Filter logic
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<AttendanceRecord> tempList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    tempList.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (AttendanceRecord item : fullList) {
                        if (item.name.toLowerCase().contains(filterPattern) ||
                                item.uid.toLowerCase().contains(filterPattern)) {
                            tempList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = tempList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<AttendanceRecord>) results.values);
                }
                notifyDataSetChanged();
            }

        };
    }
}
