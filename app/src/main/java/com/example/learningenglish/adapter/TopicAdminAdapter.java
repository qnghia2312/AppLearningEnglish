package com.example.learningenglish.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.Topic;
import com.example.learningenglish.model.UpdateTopicRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicAdminAdapter extends RecyclerView.Adapter<TopicAdminAdapter.TopicAdminViewHolder> {

    private List<Topic> topics;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public TopicAdminAdapter(List<Topic> topics, Context context, OnItemClickListener onItemClickListener) {
        this.topics = topics;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TopicAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic_admin, parent, false);
        return new TopicAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdminViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.etTopicName.setText(topic.getName());
        holder.etTopicDescription.setText(topic.getDescription());

        holder.btnEdit.setOnClickListener(v -> {
            holder.etTopicName.setEnabled(true);
            holder.etTopicDescription.setEnabled(true);
            holder.btnSaveEdit.setVisibility(View.VISIBLE);
            holder.btnCancelEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        });

        holder.btnCancelEdit.setOnClickListener(v -> {
            holder.etTopicName.setEnabled(false);
            holder.etTopicDescription.setEnabled(false);
            holder.btnSaveEdit.setVisibility(View.GONE);
            holder.btnCancelEdit.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        });

        holder.btnSaveEdit.setOnClickListener(v -> updateTopic(topic, holder));
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa từ vựng")
                    .setMessage("Bạn có chắc chắn muốn xóa từ vựng này?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteTopic(topic, holder))
                    .setNegativeButton("Hủy", null)
                    .show();

        });

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    private void updateTopic(Topic oldTopic, TopicAdminViewHolder holder) {
        String newName = holder.etTopicName.getText().toString().trim();
        String newDescription = holder.etTopicDescription.getText().toString().trim();
        UpdateTopicRequest updatedTopic = new UpdateTopicRequest(newName, newDescription);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.updateTopic(oldTopic.getName(), updatedTopic);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        oldTopic.setName(newName);
                        oldTopic.setDescription(newDescription);
                        notifyDataSetChanged();
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi cập nhật topic: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTopic(Topic topic, TopicAdminViewHolder holder) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.deleteTopic(topic.getName());

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        topics.remove(topic);
                        notifyDataSetChanged();
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi xóa topic: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class TopicAdminViewHolder extends RecyclerView.ViewHolder {
        EditText etTopicName;
        EditText etTopicDescription;
        ImageButton btnEdit, btnDelete, btnCancelEdit, btnSaveEdit;

        public TopicAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            etTopicName = itemView.findViewById(R.id.etTopicName);
            etTopicDescription = itemView.findViewById(R.id.etTopicDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnCancelEdit = itemView.findViewById(R.id.btnCancelEdit);
            btnSaveEdit = itemView.findViewById(R.id.btnSaveEdit);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Topic topic);
    }
}
