package com.example.learningenglish.Activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.DataLogin;
import com.example.learningenglish.R;
import com.example.learningenglish.adapter.HistoryAdapter;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.History;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    private static final String USERNAME = DataLogin.username;
    private TextView txtLuyenTap, txtOnTap;
    private ImageButton btnBack;
    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;
    private String currentType = "Luyện tập";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        txtLuyenTap = findViewById(R.id.txtLuyenTap);
        txtOnTap = findViewById(R.id.txtOnTap);
        btnBack = findViewById(R.id.btnBack);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList, this);
        recyclerViewHistory.setAdapter(historyAdapter);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        txtLuyenTap.setOnClickListener(v -> {
            currentType = "Luyện tập";
            fetchHistory();
        });

        txtOnTap.setOnClickListener(v -> {
            currentType = "Ôn tập";
            fetchHistory();
        });

        btnBack.setOnClickListener(v -> finish());

        fetchHistory();
    }

    private void fetchHistory() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<History>>> call = apiService.getHistory(USERNAME, currentType);

        call.enqueue(new Callback<ApiResponse<List<History>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<History>>> call, Response<ApiResponse<List<History>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<History>> apiResponse = response.body();
                    if (apiResponse.getStatus()==200) {
                        historyList.clear();
                        historyList.addAll(response.body().getData());
                        historyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HistoryActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "Không có dữ liệu lịch sử", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<History>>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi khi lấy dữ liệu lịch sử", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
