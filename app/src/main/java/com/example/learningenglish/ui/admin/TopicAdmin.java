package com.example.learningenglish.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.adapter.TopicAdminAdapter;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.databinding.FragmentTopicAdminBinding;
import com.example.learningenglish.model.Topic;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicAdmin extends Fragment {

    private FragmentTopicAdminBinding binding;
    private TopicAdminAdapter adapter;
    private List<Topic> topicList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopicAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        fetchTopics();

        binding.btnAddTopic.setOnClickListener(v -> {
            showAddTopicDialog();
        });

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewTopicsAdmin;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopicAdminAdapter(topicList, getContext(), null); // Không cần OnItemClickListener ở đây
        recyclerView.setAdapter(adapter);
    }

    private void fetchTopics() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Topic>>> call = apiService.getTopics();

        call.enqueue(new Callback<ApiResponse<List<Topic>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Topic>>> call, Response<ApiResponse<List<Topic>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Topic>> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        topicList.clear();
                        topicList.addAll(apiResponse.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy thông tin: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Topic>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddTopicDialog() {
        // Inflate layout cho dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_topic, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Topic");
        builder.setView(dialogView);

        // Khởi tạo các thành phần giao diện
        EditText etTopicName = dialogView.findViewById(R.id.etTopicName);
        EditText etTopicDescription = dialogView.findViewById(R.id.etTopicDescription);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        // Xử lý nút Save
        btnSave.setOnClickListener(v -> {
            String topicName = etTopicName.getText().toString().trim();
            String topicDescription = etTopicDescription.getText().toString().trim();

            if (!topicName.isEmpty() && !topicDescription.isEmpty()) {
                addNewTopic(topicName, topicDescription);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter both name and description", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Cancel
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addNewTopic(String name, String description) {
        Topic newTopic = new Topic();
        newTopic.setName(name);
        newTopic.setDescription(description);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.addTopic(newTopic);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        fetchTopics(); // Cập nhật danh sách sau khi thêm thành công
                        Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi thêm topic: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
