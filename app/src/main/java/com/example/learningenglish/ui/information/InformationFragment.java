package com.example.learningenglish.ui.information;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.learningenglish.Activity.ChangePassword;
import com.example.learningenglish.Activity.HistoryActivity;
import com.example.learningenglish.Activity.LoginActivity;
import com.example.learningenglish.DataLogin;
import com.example.learningenglish.R;
import com.example.learningenglish.databinding.FragmentInformationBinding;

import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.User;
import com.example.learningenglish.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationFragment extends Fragment {
    private static final String USERNAME = DataLogin.username;
    private FragmentInformationBinding binding;
    private List<Vocabulary> favoriteVocabularyList = new ArrayList<>();
    private User thisUser;

    private EditText edtName, edtEmail;
    private TextView  txtNumFav, btnChangePassword, btnHistory;
    private ImageButton btnEdit, btnCancel;
    private ImageView avatarImageView;
    private Button btnLogout;
    private boolean isEditing = false;

    private String avatarPath;
    private ActivityResultLauncher<String> imagePickerLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        control();
        event();



        return root;
    }

    private void event() {
        fetchUserInformation(USERNAME);
        fetchFavorite(USERNAME);
        btnCancel.setVisibility(View.GONE);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        avatarPath = uri.toString();
                        Glide.with(getContext())
                                .load(avatarPath)
                                .apply(new RequestOptions().placeholder(R.drawable.baseline_account_circle_24).error(R.drawable.baseline_account_circle_24))
                                .into(avatarImageView);
                    }
                });

        btnEdit.setOnClickListener(view -> {
            if (isEditing) {
                saveUserInfo();
            } else {
                enableEditing();
            }
        });

        btnCancel.setOnClickListener(view -> cancelEditing());

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChangePassword.class);
            intent.putExtra("username", thisUser.getUsername());
            intent.putExtra("name", thisUser.getName());
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HistoryActivity.class);
            startActivity(intent);
        });

        avatarImageView.setOnClickListener(v -> {
            if (isEditing) {
                openImagePicker();
            }
        });

        btnLogout.setOnClickListener(v -> {
            DataLogin.username = "";

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // Xoá tất cả các activity trong stack để không quay lại được
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    private void fetchFavorite(String username) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Vocabulary>>> call = apiService.getFavorites(username);

        call.enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vocabulary>>> call, Response<ApiResponse<List<Vocabulary>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Vocabulary>> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            List<Vocabulary> favorites = apiResponse.getData();
                            favoriteVocabularyList.clear();
                            if (favorites != null) {
                                favoriteVocabularyList.addAll(favorites);
                            }
                            int favoriteCount = favoriteVocabularyList.size();
                            txtNumFav.setText("Bạn đang có " + String.valueOf(favoriteCount) + " từ yêu thích");
                        } else {
                            Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "Lỗi: Dữ liệu trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy thông tin yêu thích: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Vocabulary>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserInformation(String username) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<User>> call = apiService.getUserByUsername(username);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.getStatus() == 200) {
                            thisUser = apiResponse.getData();
                            if (thisUser != null) {
                                edtName.setText(thisUser.getName());
                                edtEmail.setText(thisUser.getEmail());
                                // Hiển thị avatar nếu có
                                String avatarUrl = thisUser.getAvatar();
                                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    Glide.with(getContext())
                                            .load(avatarUrl)
                                            .apply(new RequestOptions().placeholder(R.drawable.baseline_account_circle_24).error(R.drawable.baseline_account_circle_24))
                                            .into(avatarImageView);
                                }
                            }
                        }  else {
                            Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "Lỗi: Dữ liệu trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy thông tin: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing() {
        isEditing = true;
        edtName.setFocusableInTouchMode(true);
        edtEmail.setFocusableInTouchMode(true);
        btnEdit.setImageResource(R.drawable.save_icon);
        btnCancel.setVisibility(View.VISIBLE);
    }

    private void saveUserInfo() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        User updatedUser = new User(USERNAME, name, email);
        updatedUser.setAvatar(avatarPath);
        Call<ApiResponse<Void>> call = apiService.updateUserInfo(USERNAME, updatedUser);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        thisUser.setName(name);
                        thisUser.setEmail(email);
                        thisUser.setAvatar(avatarPath);
                        Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        disableEditing();
                    } else {
                        Toast.makeText(getContext(), apiResponse.getStatus() + " " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lưu thông tin người dùng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelEditing() {
        edtName.setText(thisUser.getName());
        edtEmail.setText(thisUser.getEmail());

        String avatarUrl = thisUser.getAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(getContext())
                    .load(avatarUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.baseline_account_circle_24).error(R.drawable.baseline_account_circle_24))
                    .into(avatarImageView);
        } else {
            avatarImageView.setImageResource(R.drawable.baseline_account_circle_24);
        }
        disableEditing();
    }

    private void disableEditing() {
        isEditing = false;
        edtName.setFocusable(false);
        edtEmail.setFocusable(false);
        btnEdit.setImageResource(R.drawable.edit_icon);
        btnCancel.setVisibility(View.GONE);

        // Clear focus from EditTexts
        binding.getRoot().requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }

    private void openImagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn ảnh đại diện");

        String[] options = {"Chọn từ thư viện", "Không có avatar"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Chọn từ thư viện
                imagePickerLauncher.launch("image/*");
            } else if (which == 1) {
                // Không có avatar
                avatarPath = ""; // Đặt avatarPath là chuỗi trống
                avatarImageView.setImageResource(R.drawable.baseline_account_circle_24);
            }
        });

        builder.show();
    }





    private void control() {
        edtName = binding.edtName;
        edtEmail = binding.edtEmail;
        txtNumFav = binding.txtNumFav;
        btnEdit = binding.btnEdit;
        btnCancel = binding.btnCancel;
        btnChangePassword = binding.btnChangePassword;
        avatarImageView = binding.avatarImageView;
        btnLogout = binding.btnLogout;
        btnHistory = binding.btnHistory;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}