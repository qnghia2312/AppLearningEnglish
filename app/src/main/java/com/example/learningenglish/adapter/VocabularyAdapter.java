package com.example.learningenglish.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.model.Favorite;
import com.example.learningenglish.model.Vocabulary;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.api.ApiResponse;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private Context context;
    private List<Vocabulary> vocabularyList;
    private Set<String> favoriteWords;
    private String username;

    public VocabularyAdapter(Context context, List<Vocabulary> vocabularyList, Set<String> favoriteWords, String username) {
        this.context = context;
        this.vocabularyList = vocabularyList;
        this.favoriteWords = favoriteWords;
        this.username = username;
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vocabulary, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.tvWord.setText(vocabulary.getWord());
        holder.tvMean.setText("Dịch nghĩa: " + vocabulary.getMean());
        holder.tvPronunciation.setText("Phát âm: " + vocabulary.getPronunciation());
        holder.tvExample.setText("Ví dụ: " + vocabulary.getExample());

        boolean isFavorite = favoriteWords.contains(vocabulary.getWord());
        holder.btnFavorite.setImageResource(isFavorite ? R.drawable.favorite_icon_true : R.drawable.favorite_icon_false);

        holder.btnFavorite.setOnClickListener(view -> {
            if (isFavorite) {
                removeFromFavorites(vocabulary, holder);
            } else {
                addToFavorites(vocabulary, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    private void addToFavorites(Vocabulary vocabulary, VocabularyViewHolder holder) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Favorite favorite = new Favorite(username, vocabulary.getWord());
        Call<ApiResponse<Void>> call = apiService.addFavorite(favorite);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            favoriteWords.add(vocabulary.getWord());
                            holder.btnFavorite.setImageResource(R.drawable.favorite_icon_true);
                            notifyDataSetChanged();
                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("API Error", "Failure: apiResponse is null");
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi thêm vào yêu thích: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }

    private void removeFromFavorites(Vocabulary vocabulary, VocabularyViewHolder holder) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Favorite favorite = new Favorite(username, vocabulary.getWord());
        Call<ApiResponse<Void>> call = apiService.removeFavorite(favorite);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            favoriteWords.remove(vocabulary.getWord());
                            holder.btnFavorite.setImageResource(R.drawable.favorite_icon_false);
                            notifyDataSetChanged();
                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }  else {
                        Log.e("API Error", "Failure: apiResponse is null");
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi xóa từ khỏi mục yêu thích: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }


    public static class VocabularyViewHolder extends RecyclerView.ViewHolder {

        TextView tvWord, tvMean, tvPronunciation, tvExample;
        ImageButton btnFavorite;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvMean = itemView.findViewById(R.id.tvMean);
            tvPronunciation = itemView.findViewById(R.id.tvPronunciation);
            tvExample = itemView.findViewById(R.id.tvExample);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}