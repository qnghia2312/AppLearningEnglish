package com.example.learningenglish.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.Vocabulary;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyAdminAdapter extends RecyclerView.Adapter<VocabularyAdminAdapter.VocabularyAdminViewHolder> {

    private Context context;
    private List<Vocabulary> vocabularyList;

    public VocabularyAdminAdapter(Context context, List<Vocabulary> vocabularyList) {
        this.context = context;
        this.vocabularyList = vocabularyList;
    }

    @NonNull
    @Override
    public VocabularyAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vocabulary_admin, parent, false);
        return new VocabularyAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyAdminViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.etWord.setText(vocabulary.getWord());
        holder.etMean.setText(vocabulary.getMean());
        holder.etPronunciation.setText(vocabulary.getPronunciation());
        holder.etExample.setText(vocabulary.getExample());
        holder.etTopic.setText(vocabulary.getTopic());
        holder.etType.setText(vocabulary.getType());

        holder.btnEdit.setOnClickListener(v -> {
            holder.etWord.setEnabled(true);
            holder.etMean.setEnabled(true);
            holder.etPronunciation.setEnabled(true);
            holder.etExample.setEnabled(true);
            holder.etTopic.setEnabled(true);
            holder.etType.setEnabled(true);
            holder.btnSave.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        });

        holder.btnCancel.setOnClickListener(v -> {
            holder.etWord.setEnabled(false);
            holder.etMean.setEnabled(false);
            holder.etPronunciation.setEnabled(false);
            holder.etExample.setEnabled(false);
            holder.etTopic.setEnabled(false);
            holder.etType.setEnabled(false);
            holder.btnSave.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        });

        holder.btnSave.setOnClickListener(v -> {
            updateVocabulary(vocabulary, holder);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa từ vựng")
                    .setMessage("Bạn có chắc chắn muốn xóa từ vựng này?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteVocabulary(vocabulary, holder))
                    .setNegativeButton("Hủy", null)
                    .show();
        });

    }

    private void deleteVocabulary(Vocabulary vocabulary, VocabularyAdminViewHolder holder) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String word = vocabulary.getWord();

        Call<ApiResponse<Void>> call = apiService.deleteVocabulary(word);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            // Xóa thành công
                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            // cập nhật
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("API Error", "Failure: apiResponse is null");
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi xóa từ vựng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }

    private void updateVocabulary(Vocabulary vocabulary, VocabularyAdminViewHolder holder) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Lấy dữ liệu từ các EditText
        String word = holder.etWord.getText().toString().trim();
        String mean = holder.etMean.getText().toString().trim();
        String pronunciation = holder.etPronunciation.getText().toString().trim();
        String example = holder.etExample.getText().toString().trim();
        String topic = holder.etTopic.getText().toString().trim();
        String type = holder.etType.getText().toString().trim();

        // Tạo đối tượng Vocabulary mới với dữ liệu đã chỉnh sửa
        Vocabulary updatedVocabulary = new Vocabulary(word, mean, pronunciation, example, topic, type);

        // Gửi yêu cầu cập nhật từ vựng
        Call<ApiResponse<Void>> call = apiService.updateVocabulary(vocabulary.getWord(), updatedVocabulary);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            // Cập nhật thành công
                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            // Cập nhật danh sách từ vựng và UI
                            holder.etWord.setEnabled(false);
                            holder.etMean.setEnabled(false);
                            holder.etPronunciation.setEnabled(false);
                            holder.etExample.setEnabled(false);
                            holder.etTopic.setEnabled(false);
                            holder.etType.setEnabled(false);
                            holder.btnSave.setVisibility(View.GONE);
                            holder.btnCancel.setVisibility(View.GONE);
                            holder.btnEdit.setVisibility(View.VISIBLE);
                            holder.btnDelete.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(context, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("API Error", "Failure: apiResponse is null");
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi cập nhật từ vựng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("API Error", "Failure: " + t.getMessage());
            }
        });
    }



    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    public static class VocabularyAdminViewHolder extends RecyclerView.ViewHolder {
        EditText etWord, etMean, etPronunciation, etExample, etTopic, etType;
        ImageButton btnEdit, btnDelete, btnSave, btnCancel;

        public VocabularyAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            etWord = itemView.findViewById(R.id.etWord);
            etMean = itemView.findViewById(R.id.etMean);
            etPronunciation = itemView.findViewById(R.id.etPronunciation);
            etExample = itemView.findViewById(R.id.etExample);
            etTopic = itemView.findViewById(R.id.etTopic);
            etType = itemView.findViewById(R.id.etType);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}