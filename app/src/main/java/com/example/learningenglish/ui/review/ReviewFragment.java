package com.example.learningenglish.ui.review;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.learningenglish.Activity.ReviewActivity;
import com.example.learningenglish.DataLogin;
import com.example.learningenglish.adapter.VocabularyAdapter;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.databinding.FragmentReviewBinding;
import com.example.learningenglish.model.Vocabulary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewFragment extends Fragment {
    private static final String USERNAME = DataLogin.username;
    private FragmentReviewBinding binding;
    private VocabularyAdapter vocabularyAdapter;
    private List<Vocabulary> favoriteVocabularies;
    private Set<String> favoriteWords;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentReviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favoriteVocabularies = new ArrayList<>();
        favoriteWords = new HashSet<>();

        setupRecyclerView();
        fetchFavoriteVocabularies();

        binding.btnStartReview.setOnClickListener(v -> {
            if (favoriteVocabularies != null && !favoriteVocabularies.isEmpty()) {
                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("favoriteVocabularies", new ArrayList<>(favoriteVocabularies));
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Bạn chưa có từ vựng yêu thích nào", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void setupRecyclerView() {
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        vocabularyAdapter = new VocabularyAdapter(getContext(), favoriteVocabularies, favoriteWords, "user1");
        binding.recyclerViewFavorites.setAdapter(vocabularyAdapter);
    }

    private void fetchFavoriteVocabularies() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Vocabulary>>> call = apiService.getFavorites(USERNAME);
        call.enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vocabulary>>> call, Response<ApiResponse<List<Vocabulary>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Vocabulary> fetchedFavorites = response.body().getData();
                    if (fetchedFavorites != null && !fetchedFavorites.isEmpty()) {
                        favoriteVocabularies.clear();
                        favoriteWords.clear();
                        favoriteVocabularies.addAll(fetchedFavorites);
                        for (Vocabulary vocab : fetchedFavorites) {
                            favoriteWords.add(vocab.getWord());
                        }
                        vocabularyAdapter.notifyDataSetChanged();
                        binding.recyclerViewFavorites.setVisibility(View.VISIBLE);
                        binding.txtNoFavorites.setVisibility(View.GONE);
                    } else {
                        binding.recyclerViewFavorites.setVisibility(View.GONE);
                        binding.txtNoFavorites.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Vocabulary>>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi khi tải danh sách từ vựng yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}