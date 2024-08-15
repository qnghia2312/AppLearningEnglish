package com.example.learningenglish.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.User;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextCode;
    private Button buttonSendCode, buttonVerifyCode, buttonResendCode, buttonCancel;
    private TextView textViewTimer, tvUsername, tvEmail;
    private ImageButton btnBack;
    private String randomCode;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long timeRemaining = 60000; // 1 phút

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        control();
        event();

//        buttonVerifyCode.setVisibility(View.GONE);
//        buttonResendCode.setVisibility(View.GONE);
//        editTextCode.setVisibility(View.GONE);
//        textViewTimer.setVisibility(View.GONE);

    }

    private void event() {
        btnBack.setOnClickListener(v -> finish());

        //Gửi mã
        buttonSendCode.setOnClickListener(v -> sendCode());

        //Xác nhận mã
        buttonVerifyCode.setOnClickListener(v -> verifyCode());

        //Gửi lại mã
        buttonResendCode.setOnClickListener(v -> resendCode());

        buttonCancel.setOnClickListener(v -> CancelVerifyCode());
    }



    private void control() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCode = findViewById(R.id.editTextCode);
        btnBack = findViewById(R.id.btnBack);
        buttonSendCode = findViewById(R.id.buttonSendCode);
        buttonVerifyCode = findViewById(R.id.buttonVerifyCode);
        buttonResendCode = findViewById(R.id.buttonResendCode);
        buttonCancel = findViewById(R.id.buttonCancel);
        textViewTimer = findViewById(R.id.textViewTimer);
        tvUsername = findViewById(R.id.textView16);
        tvEmail = findViewById(R.id.textView17);
    }

    private void sendCode() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.contains(" ") || email.contains(" ")) {
            Toast.makeText(this, "username và email không được chứa dấu cách.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        User us = new User(username, email);
        Call<ApiResponse<String>> call = apiService.forgotPassword(us);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        Toast.makeText(ForgotPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        randomCode = apiResponse.getData();
                        showCodeEntry();
                        startTimer();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi khi gửi mã.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyCode() {
        String code = editTextCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Vui lòng nhập mã.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.equals(randomCode)) {
            Toast.makeText(this, "Xác thực thành công.", Toast.LENGTH_SHORT).show();
            showResetPasswordDialog();
        } else {
            Toast.makeText(this, "Mã không chính xác.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mật khẩu mới");

        // Tạo layout cho AlertDialog
        final EditText input = new EditText(this);
        input.setHint("Mật khẩu mới");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString().trim();

                // Kiểm tra mật khẩu
                if (TextUtils.isEmpty(newPassword) || newPassword.contains(" ")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Mật khẩu không hợp lệ.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi hàm ressetPassword với mật khẩu mới
                resetPassword(newPassword);
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void resetPassword(String newPassword) {
        String username = editTextUsername.getText().toString().trim();

        // Tạo body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("newPassword", newPassword);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiResponse<Void>> call = apiService.resetPassword(requestBody);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.getStatus() == 200) {
                        Toast.makeText(ForgotPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Chuyển đến trang Login
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu không thành công.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối mạng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendCode() {
        sendCode();
    }

    private void showCodeEntry() {
        tvEmail.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        editTextUsername.setVisibility(View.GONE);
        editTextEmail.setVisibility(View.GONE);
        buttonSendCode.setVisibility(View.GONE);
        buttonResendCode.setVisibility(View.GONE);

        buttonCancel.setVisibility(View.VISIBLE);
        buttonVerifyCode.setVisibility(View.VISIBLE);
        editTextCode.setVisibility(View.VISIBLE);
        textViewTimer.setVisibility(View.VISIBLE);
    }

    private void CancelVerifyCode() {
        tvEmail.setVisibility(View.VISIBLE);
        tvUsername.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        editTextEmail.setVisibility(View.VISIBLE);
        buttonSendCode.setVisibility(View.VISIBLE);

        buttonResendCode.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        buttonVerifyCode.setVisibility(View.GONE);
        editTextCode.setVisibility(View.GONE);
        textViewTimer.setVisibility(View.GONE);

        randomCode = "";
    }

    private void startTimer() {
        timeRemaining = 60000; // Reset timer to 1 minute
        updateTimer();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeRemaining -= 1000;
                if (timeRemaining <= 0) {
                    randomCode = "";
                    textViewTimer.setText("Mã hết hạn");
                    buttonResendCode.setVisibility(View.VISIBLE);
                } else {
                    updateTimer();
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void updateTimer() {
        int seconds = (int) (timeRemaining / 1000) % 60;
        int minutes = (int) (timeRemaining / (1000 * 60));
        textViewTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

}
