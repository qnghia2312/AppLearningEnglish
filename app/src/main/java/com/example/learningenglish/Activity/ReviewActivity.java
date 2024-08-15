package com.example.learningenglish.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglish.DataLogin;
import com.example.learningenglish.R;
import com.example.learningenglish.api.ApiClient;
import com.example.learningenglish.api.ApiResponse;
import com.example.learningenglish.api.ApiService;
import com.example.learningenglish.model.History;
import com.example.learningenglish.model.Vocabulary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private static final String USERNAME = DataLogin.username;
    private TextView txtQuestion, txtResult, txtNextQuestion, txtNoVocabulary, txtCorrectAnswer, txtAttemptCount, txtStatistics;
    private EditText edtAnswer;
    private ImageButton btnBack, btnNext;
    private Button btnCheck, btnShowAnswer, btnShowStatistics;
    private ArrayList<Vocabulary> favoriteVocabularies;
    private Vocabulary currentVocabulary;
    private List<Integer> usedIndexes = new ArrayList<>();
    private Set<Integer> reviewedIndices;
    private int attemptCount = 2;
    private int correctAnswers = 0;
    private List<String> incorrectAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        favoriteVocabularies = (ArrayList<Vocabulary>) getIntent().getSerializableExtra("favoriteVocabularies");
        reviewedIndices = new HashSet<>();

        reviewedIndices = new HashSet<>();
        incorrectAnswers = new ArrayList<>();

        control();
        event();

        showNextQuestion();

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
        txtAttemptCount = findViewById(R.id.txtAttemptCount);
        btnShowStatistics = findViewById(R.id.btnShowStatistics);
        txtStatistics = findViewById(R.id.txtStatistics);
    }

    private void event() {
        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(ReviewActivity.this)
                    .setTitle("Lưu lịch sử")
                    .setMessage("Bạn có muốn lưu lại lịch sử của lần ôn tập này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        saveReviewHistory();

                        finish();
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        finish();
                    })
                    .show();
        });

        btnNext.setOnClickListener(v -> showNextQuestion());

        btnCheck.setOnClickListener(v -> checkAnswer());

        btnShowAnswer.setOnClickListener(v -> {
            txtCorrectAnswer.setText("Đáp án chính xác là: " + currentVocabulary.getWord());
            txtCorrectAnswer.setVisibility(View.VISIBLE);
            btnShowAnswer.setVisibility(View.GONE);
        });

        btnShowStatistics.setOnClickListener(v -> showStatistics());
    }

    private void showNextQuestion() {
        if (reviewedIndices.size() >= favoriteVocabularies.size()) {
            Toast.makeText(ReviewActivity.this, "Đã hết từ vựng trong danh sách yêu thích", Toast.LENGTH_LONG).show();
            btnNext.setVisibility(View.GONE);
            txtNextQuestion.setVisibility(View.GONE);
            btnShowStatistics.setVisibility(View.VISIBLE);
            return;
        }

        int randomIndex;
        do {
            Random random = new Random();
            randomIndex = random.nextInt(favoriteVocabularies.size());
        } while (reviewedIndices.contains(randomIndex));

        reviewedIndices.add(randomIndex);
        currentVocabulary = favoriteVocabularies.get(randomIndex);
        txtQuestion.setText("Từ nào có nghĩa là: " + currentVocabulary.getMean());
        edtAnswer.setText("");
        edtAnswer.setEnabled(true);
        txtResult.setVisibility(View.GONE);
        txtCorrectAnswer.setVisibility(View.GONE);
        btnShowAnswer.setVisibility(View.GONE);
        txtAttemptCount.setText("Số cơ hội còn lại: 2");
        txtAttemptCount.setVisibility(View.VISIBLE);
        attemptCount = 2;
        btnCheck.setEnabled(true);
        btnNext.setVisibility(View.GONE);
        txtNextQuestion.setVisibility(View.GONE);
    }

    private void checkAnswer() {
        String answer = edtAnswer.getText().toString().trim();
        txtResult.setVisibility(View.VISIBLE);

        if (answer.equalsIgnoreCase(currentVocabulary.getWord())) {
            txtResult.setText("✔️ Đáp án chính xác");
            correctAnswers++;
            disableAnswerInput();
        } else {
            attemptCount--;
            if (attemptCount > 0) {
                txtResult.setText("❌ Bạn đã trả lời sai, hãy thử lại. Số cơ hội còn lại: " + attemptCount);
            } else {
                txtResult.setText("❌ Bạn đã trả lời sai, hết cơ hội trả lời");
                incorrectAnswers.add(currentVocabulary.getWord() + ": " + currentVocabulary.getMean());
                disableAnswerInput();
                btnShowAnswer.setVisibility(View.VISIBLE);
            }
            txtAttemptCount.setText("Số cơ hội còn lại: " + attemptCount);
        }
    }

    private void disableAnswerInput() {
        edtAnswer.setEnabled(false);
        btnCheck.setEnabled(false);
        btnNext.setVisibility(View.VISIBLE);
        txtNextQuestion.setVisibility(View.VISIBLE);
    }

    //Thống kê
    private void showStatistics() {
        txtQuestion.setVisibility(View.GONE);
        edtAnswer.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);
        txtResult.setVisibility(View.GONE);
        btnShowAnswer.setVisibility(View.GONE);
        txtAttemptCount.setVisibility(View.GONE);
        btnShowStatistics.setVisibility(View.GONE);
        txtNextQuestion.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        txtCorrectAnswer.setVisibility(View.GONE);

        if (incorrectAnswers.isEmpty()) {
            txtStatistics.setText("Số câu trả lời đúng: " + correctAnswers + "/" + favoriteVocabularies.size() + "\n\nChúc mừng bạn đã trả lời đúng tất cả");
        } else {
            txtStatistics.setText("Số câu trả lời đúng: " + correctAnswers + "/" + favoriteVocabularies.size() + "\n\nCác câu trả lời sai và đáp án:\n" + String.join("\n", incorrectAnswers));
        }
        txtStatistics.setVisibility(View.VISIBLE);
    }

    //Lưu lịch sử
    private void saveReviewHistory() {
        String content = generateHistoryContent();
        String result = correctAnswers + "/" + favoriteVocabularies.size();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Tạo đối tượng History
        History history = new History(USERNAME, "Ôn tập", content, result);

        // Gửi đối tượng History đến API
        Call<ApiResponse<Void>> call = apiService.saveHistory(history);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                ApiResponse<Void> apiResponse = response.body();
                if (apiResponse != null) {
                    if (apiResponse.getStatus() == 200) {
                        Toast.makeText(ReviewActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ReviewActivity.this, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ReviewActivity.this, "Response body is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ReviewActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String generateHistoryContent() {
        StringBuilder contentBuilder = new StringBuilder();
        for (Vocabulary vocabulary : favoriteVocabularies) {
            if (reviewedIndices.contains(favoriteVocabularies.indexOf(vocabulary))) {
                String resultSymbol = incorrectAnswers.contains(vocabulary.getWord() + ": " + vocabulary.getMean()) ? "❌" : "✔️";
                contentBuilder.append(vocabulary.getMean()).append(": ").append(vocabulary.getWord()).append(" ").append(resultSymbol).append("\n");
            }
        }
        return contentBuilder.toString();
    }
}
