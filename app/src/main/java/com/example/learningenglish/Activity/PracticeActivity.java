package com.example.learningenglish.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglish.DataLogin;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;

import com.example.learningenglish.R;
import com.example.learningenglish.model.Vocabulary;
import com.example.learningenglish.model.History;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeActivity extends AppCompatActivity {
    private static final String USERNAME = DataLogin.username;
    private TextView txtQuestion, txtResult, txtNextQuestion, txtNoVocabulary, txtCorrectAnswer ;
    private EditText edtAnswer;
    private ImageButton btnBack, btnNext;
    private Button btnCheck, btnShowAnswer ;
    private List<Vocabulary> vocabularyList;
    private Vocabulary currentVocabulary;
    private List<Integer> usedIndexes = new ArrayList<>();
    private Map<String, Boolean> correctAnswerMap = new HashMap<>();
    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private StringBuilder historyContent = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);


        control();

        event();
    }

    private void event() {
        Intent intent = getIntent();
        String topic = intent.getStringExtra("TOPIC_NAME");

        //Lấy các từ vựng theo chủ đề đã chọn
        fetchVocabulary(topic);

        btnBack.setOnClickListener(v -> {
            // Hiển thị một dialog hỏi người dùng có muốn lưu lịch sử hay không
            new AlertDialog.Builder(PracticeActivity.this)
                    .setTitle("Lưu lịch sử")
                    .setMessage("Bạn có muốn lưu lại lịch sử của lần luyện tập này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        //Lưu lại lịch sử
                        saveHistory();

                        finish();
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        finish();
                    })
                    .show();
        });

        //Chuyển sang câu tiếp theo
        btnNext.setOnClickListener(v -> showNextQuestion());

        //Kiểm tra xem từ dã nhập có chính xác không
        btnCheck.setOnClickListener(v -> checkAnswer());

        //Xem kết quả chính xác
        btnShowAnswer.setOnClickListener(v -> {
            txtCorrectAnswer.setText("Đáp án chính xác là: " + currentVocabulary.getWord());
            txtCorrectAnswer.setVisibility(View.VISIBLE);
            btnShowAnswer.setVisibility(View.GONE);
        });
    }

    private void control() {
        txtQuestion = findViewById(R.id.txtQuestion);
        txtResult = findViewById(R.id.txtResult);
        edtAnswer = findViewById(R.id.edtAnswer);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnCheck = findViewById(R.id.btnCheck);
        txtNextQuestion = findViewById(R.id.txtNextQuestion);
        txtNoVocabulary = findViewById(R.id.txtNoVocabulary);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        txtCorrectAnswer = findViewById(R.id.txtCorrectAnswer);
    }

    //Lấy danh sách từ vựng theo topic
    private void fetchVocabulary(String topic) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Map<String, String> body = new HashMap<>();
        body.put("topic", topic);

        Call<ApiResponse<List<Vocabulary>>> call = apiService.getVocabularyByTopic(body);

        call.enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Vocabulary>>> call, Response<ApiResponse<List<Vocabulary>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Vocabulary>> apiResponse = response.body();
                    if (apiResponse != null) {
                        if(apiResponse.getStatus() == 200) {
                            vocabularyList = apiResponse.getData();
                            showNextQuestion();
                        } else {
                            showNoVocabularyMessage();
                            Toast.makeText(PracticeActivity.this,apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showNoVocabularyMessage();
                        Toast.makeText(PracticeActivity.this, "Không tìm thấy từ vựng cho chủ đề này.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showNoVocabularyMessage();
                    Toast.makeText(PracticeActivity.this, "Lỗi khi lấy từ vựng: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Vocabulary>>> call, Throwable t) {
                Toast.makeText(PracticeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Khi không tìm thấy từ vựng nào thuộc topic này
    private void showNoVocabularyMessage() {
        txtQuestion.setVisibility(View.GONE);
        edtAnswer.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnShowAnswer.setVisibility(View.GONE);
        txtResult.setVisibility(View.GONE);
        txtNextQuestion.setVisibility(View.GONE);
        txtNoVocabulary.setVisibility(View.VISIBLE);
        txtNoVocabulary.setText("Không có từ vựng nào thuộc chủ đề này. Vui lòng thử lại sau.");
    }

    //Hiển thị câu hỏi tiếp theo(Lấy ngẫu nhiên trong danh sách từ vựng tìm được)
    private void showNextQuestion() {
        if (usedIndexes.size() >= vocabularyList.size()) {
            Toast.makeText(PracticeActivity.this, "Đã hết từ vựng trong chủ đề này", Toast.LENGTH_LONG).show();
            btnNext.setVisibility(View.GONE);
            txtNextQuestion.setVisibility(View.GONE);
            return;
        }

        int randomIndex;
        do {
            Random random = new Random();
            randomIndex = random.nextInt(vocabularyList.size());
        } while (usedIndexes.contains(randomIndex));

        // Lưu kết quả của câu hỏi trước vào historyContent nếu chưa được lưu
        saveCurrentQuestionResult();

        usedIndexes.add(randomIndex);
        currentVocabulary = vocabularyList.get(randomIndex);
        txtQuestion.setText("Từ nào có nghĩa là: " + currentVocabulary.getMean());
        edtAnswer.setText("");
        txtResult.setVisibility(View.GONE);
        txtCorrectAnswer.setVisibility(View.GONE);
        btnShowAnswer.setVisibility(View.VISIBLE);
        totalQuestions++;
    }

    //Kiểm tra kết quả
    private void checkAnswer() {
        String answer = edtAnswer.getText().toString().trim();
        txtResult.setVisibility(View.VISIBLE);
        String word = currentVocabulary.getWord();

        if (answer.equalsIgnoreCase(word)) {
            //txtResult.setTextColor(getResources().getColor(R.color.green));
            txtResult.setText("✔️ Đáp án chính xác");
            if (!correctAnswerMap.containsKey(word)) {
                correctAnswers++; //Tăng số câu đúng khi lần đầu trả lời đúng
            }
            correctAnswerMap.put(word, true);
        } else {
            //txtResult.setTextColor(getResources().getColor(R.color.red));
            txtResult.setText("❌ Bạn đã trả lời sai, hãy thử lại");
            if (!correctAnswerMap.containsKey(word)) {
                correctAnswerMap.put(word, false);
            }
        }
    }

    // Lưu kết quả của câu hỏi hiện tại vào historyContent
    private void saveCurrentQuestionResult() {
        if (currentVocabulary != null) {
            String word = currentVocabulary.getWord();
            if (!correctAnswerMap.containsKey(word) || correctAnswerMap.get(word) == false) {
                historyContent.append(currentVocabulary.getMean() + ": " + word + " (❌)\n");
            } else {
                historyContent.append(currentVocabulary.getMean() + ": " + word + " (✔️)\n");
            }
        }
    }

    //Lưu lịch sử
    private void saveHistory() {
        saveCurrentQuestionResult(); //Lưu câu cuối cùng trước khi lưu lịch sử
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        History history = new History(USERNAME, "Luyện tập", historyContent.toString(), correctAnswers + "/" + totalQuestions);

        Call<ApiResponse<Void>> call = apiService.saveHistory(history);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                ApiResponse<Void> apiResponse = response.body();
                if (apiResponse != null) {
                    if (apiResponse.getStatus() == 200) {
                        Toast.makeText(PracticeActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PracticeActivity.this, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PracticeActivity.this, "Response body is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(PracticeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
