package com.example.learningenglish.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.adapter.AccountsAdapter;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.databinding.FragmentAccountAdminBinding;
import com.example.learningenglish.databinding.FragmentHomeBinding;
import com.example.learningenglish.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAdmin extends Fragment {
    private FragmentAccountAdminBinding binding;
    private AccountsAdapter adapter;
    private boolean isActive = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupTabs();
        setupRecyclerView();
        fetchAccounts();

        return root;
    }

    private void setupTabs() {
        binding.txtActive.setSelected(true);

        binding.txtActive.setOnClickListener(v -> {
            isActive = true;
            binding.txtActive.setSelected(true);
            binding.txtInactive.setSelected(false);
            fetchAccounts();
        });

        binding.txtInactive.setOnClickListener(v -> {
            isActive = false;
            binding.txtActive.setSelected(false);
            binding.txtInactive.setSelected(true);
            fetchAccounts();
        });
    }

    private void setupRecyclerView() {
        binding.recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountsAdapter(new ArrayList<>(), getContext());
        binding.recyclerViewAccounts.setAdapter(adapter);
    }

    private void fetchAccounts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<User>>> call = isActive ? apiService.getActiveUsers() : apiService.getInactiveUsers();

        call.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<User>> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        adapter.updateUsers(apiResponse.getData());
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
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