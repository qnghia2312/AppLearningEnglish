package com.example.learningenglish.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.learningenglish.R;
import com.example.learningenglish.adapter.VocabularyAdminAdapter;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.databinding.FragmentHomeAdminBinding;
import com.example.learningenglish.databinding.FragmentHomeBinding;
import com.example.learningenglish.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAdmin extends Fragment {

    private FragmentHomeAdminBinding binding;
    private VocabularyAdminAdapter vocabularyAdapter;
    private List<Vocabulary> vocabularyList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vocabularyList = new ArrayList<>();
        vocabularyAdapter = new VocabularyAdminAdapter(getContext(), vocabularyList);

        binding.recyclerVocabulary.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerVocabulary.setAdapter(vocabularyAdapter);

        fetchVocabularyData(null);
        setupSearchView();
        setupAddButton();

        return root;
    }

    private void setupSearchView() {
        binding.searchVocabulary.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchVocabularyData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.searchVocabulary.setOnCloseListener(() -> {
            fetchVocabularyData(null);
            return false;
        });
    }

    private void setupAddButton() {
        binding.btnAddVocabulary.setOnClickListener(v -> showAddVocabularyDialog());
    }

    private void showAddVocabularyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_vocabulary, null);
        builder.setView(dialogView);

        EditText etWord = dialogView.findViewById(R.id.etWord);
        EditText etMean = dialogView.findViewById(R.id.etMean);
        EditText etPronunciation = dialogView.findViewById(R.id.etPronunciation);
        EditText etExample = dialogView.findViewById(R.id.etExample);
        EditText etTopic = dialogView.findViewById(R.id.etTopic);
        EditText etType = dialogView.findViewById(R.id.etType);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String word = etWord.getText().toString().trim();
            String mean = etMean.getText().toString().trim();
            String pronunciation = etPronunciation.getText().toString().trim();
            String example = etExample.getText().toString().trim();
            String topic = etTopic.getText().toString().trim();
            String type = etType.getText().toString().trim();

            if (!word.isEmpty() && !mean.isEmpty()) {
                Vocabulary newVocabulary = new Vocabulary(word, mean, pronunciation, example, topic, type);
                addVocabulary(newVocabulary);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Word and Mean are required fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addVocabulary(Vocabulary vocabulary) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Vocabulary>> call = apiService.addVocabulary(vocabulary);

        call.enqueue(new Callback<ApiResponse<Vocabulary>>() {
            @Override
            public void onResponse(Call<ApiResponse<Vocabulary>> call, Response<ApiResponse<Vocabulary>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Vocabulary> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        vocabularyList.add(apiResponse.getData());
                        vocabularyAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Vocabulary added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Response Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Vocabulary>> call, Throwable t) {
                Toast.makeText(getContext(), "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVocabularyData(String query) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Vocabulary>>> call;

        if (query == null || query.trim().isEmpty()) {
            call = apiService.getVocabulary();
        } else {
            call = apiService.searchVocabulary(query.trim());
        }

        call.enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vocabulary>>> call, Response<ApiResponse<List<Vocabulary>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Vocabulary>> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            List<Vocabulary> fetchedVocabularyList = apiResponse.getData();
                            vocabularyList.clear();
                            vocabularyList.addAll(fetchedVocabularyList);
                            vocabularyAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e("API Error", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Vocabulary>>> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
