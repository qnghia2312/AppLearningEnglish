package com.example.learningenglish.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.learningenglish.DataLogin;
import com.example.learningenglish.MainActivity;
import com.example.learningenglish.MainAdminActivity;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.databinding.LoginBinding;
import com.example.learningenglish.model.LoginRequest;
import com.example.learningenglish.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private LoginBinding binding;

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnRegister;
    private TextView txtForgotPassword;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        control();
        event();

    }

    private void event() {
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Kiểm tra username và password không rỗng
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Tên đăng nhập và mật khẩu không được để trống.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra username và pasword chứa dấu cách
            if (username.contains(" ") || password.contains(" ")) {
                Toast.makeText(LoginActivity.this, "Tên đăng nhập và mật khẩu không được chứa dấu cách.", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        txtForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<ApiResponse<User>> call = apiService.userLogin(loginRequest);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        User user = apiResponse.getData();
                        DataLogin.username = user.getUsername(); // Lưu username vào DataLogin
                        if (user.getPermission().equals("user")) {
                            // Chuyển đến MainActivity nếu quyền là user
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (user.getPermission().equals("admin")) {
                            // Chuyển đến MainAdminActivity nếu quyền là admin
                            Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập không thành công. Vui lòng kiểm tra lại thông tin đăng nhập.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void control() {
        edtUsername = binding.edtUsername;
        edtPassword = binding.edtPassword;
        btnLogin = binding.btnLogin;
        btnRegister = binding.btnRegister;
        txtForgotPassword = binding.txtForgotPassword;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}