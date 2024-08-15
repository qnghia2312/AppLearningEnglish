package com.example.learningenglish.ui.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.Activity.PracticeActivity;
import com.example.learningenglish.adapter.TopicsAdapter;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.databinding.FragmentPracticeBinding;
import com.example.learningenglish.model.Topic;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeFragment extends Fragment {

    private FragmentPracticeBinding binding;

    private TopicsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPracticeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        fetchTopics();




        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewTopics;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchTopics() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Topic>>> call = apiService.getTopics();

        call.enqueue(new Callback<ApiResponse<List<Topic>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Topic>>> call, Response<ApiResponse<List<Topic>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Topic>> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            List<Topic> topics = apiResponse.getData();
                            adapter = new TopicsAdapter(topics, getContext(), topic -> {
                                Intent intent = new Intent(getActivity(), PracticeActivity.class);
                                intent.putExtra("TOPIC_NAME", topic.getName());
                                startActivity(intent);
                            });
                            binding.recyclerViewTopics.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(),apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi: Dữ liệu trống", Toast.LENGTH_LONG).show();
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}