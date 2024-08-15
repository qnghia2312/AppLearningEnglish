package com.example.learningenglish.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.learningenglish.DataLogin;
import com.example.learningenglish.adapter.VocabularyAdapter;
import com.example.learningenglish.databinding.FragmentHomeBinding;
import com.example.learningenglish.model.Vocabulary;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private VocabularyAdapter vocabularyAdapter;
    private List<Vocabulary> vocabularyList;
    private Set<String> favoriteWords;
    private static final String USERNAME = DataLogin.username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        vocabularyList = new ArrayList<>();
        favoriteWords = new HashSet<>();

        vocabularyAdapter = new VocabularyAdapter(getContext(), vocabularyList, favoriteWords, USERNAME);

        binding.recyclerVocabulary.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerVocabulary.setAdapter(vocabularyAdapter);

        fetchVocabularyData(null);
        fetchFavoriteData();

        setupSearchView();

        return root;
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchVocabularyData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // You can choose to update results as the user types, if desired.
                return false;
            }
        });

        binding.searchView.setOnCloseListener(() -> {
            fetchVocabularyData(null);
            return false;
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

//                            for (Vocabulary vocab : fetchedVocabularyList) {
//                                Log.d("Vocabulary", vocab.getWord() + ": " + vocab.getMean());
//                            }
//                            Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void fetchFavoriteData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Vocabulary>>> call = apiService.getFavorites(USERNAME);

        call.enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vocabulary>>> call, Response<ApiResponse<List<Vocabulary>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Vocabulary>> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            List<Vocabulary> fetchedFavorites = apiResponse.getData();
                            favoriteWords.clear();
                            for (Vocabulary vocab : fetchedFavorites) {
                                favoriteWords.add(vocab.getWord());
                            }
                            vocabularyAdapter.notifyDataSetChanged();
//                            Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onStop() {
        super.onStop();
        binding.searchView.setIconified(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}