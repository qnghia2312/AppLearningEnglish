package com.example.learningenglish.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.model.History;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;
    private Context context;

    public HistoryAdapter(List<History> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.txtResult.setText("Kết quả: " + history.getResult());
        //Chuyển Date thành chuỗi ngày/tháng/năm giờ/phút/giây
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(history.getDate());

        holder.txtDate.setText("Thời gian: " + formattedDate);


        holder.btnDetail.setOnClickListener(v -> showDetailDialog(history));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtResult, txtDate;
        ImageButton btnDetail;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtResult = itemView.findViewById(R.id.txtResult);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }

    private void showDetailDialog(History history) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chi tiết lịch sử");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_history_detail, null);
        TextView txtDetailResult = view.findViewById(R.id.txtDetailResult);
        TextView txtDetailDate = view.findViewById(R.id.txtDetailDate);
        TextView txtDetailContent = view.findViewById(R.id.txtDetailContent);

        txtDetailResult.setText("Kết quả: " + history.getResult());

        //Chuyển Date thành chuỗi dạng ngày/tháng/năm giờ/phút/giây
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(history.getDate());
        txtDetailDate.setText("Thời gian: " + formattedDate);

        txtDetailContent.setText("Nội dung: \n" + history.getContent());

        builder.setView(view);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}