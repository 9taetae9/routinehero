package com.example.routinehero;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private TextView levelText;
    private TextView daysLeftText;
    private TextView requiredRoutinesText;
    private TextView currentDayText;
    private ImageView characterImage;
    private ImageView previousCharacterImage;
    private ImageView nextCharacterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        levelText = findViewById(R.id.level_text);
        daysLeftText = findViewById(R.id.days_left_text);
        requiredRoutinesText = findViewById(R.id.required_routines_text);
        currentDayText = findViewById(R.id.current_day_text);
        characterImage = findViewById(R.id.character_image);
        previousCharacterImage = findViewById(R.id.previous_character_image);
        nextCharacterImage = findViewById(R.id.next_character_image);

        findViewById(R.id.decrease_day_button).setOnClickListener(v -> {
            RoutineManager.decrementTestDay();
            updateUI();
        });

        findViewById(R.id.increase_day_button).setOnClickListener(v -> {
            RoutineManager.incrementTestDay(this);
            updateUI();
        });

        findViewById(R.id.check_routines_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChecklistActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.manage_routines_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageRoutinesActivity.class);
            startActivity(intent);
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        int currentLevel = RoutineManager.getCurrentLevel();
        int completedToday = RoutineManager.getCompletedRoutinesCount();
        int requiredRoutines = RoutineManager.getRequiredRoutinesForCurrentLevel();
        int currentDay = RoutineManager.getCurrentTestDay();

        // 현재 레벨 캐릭터 업데이트
        characterImage.setImageResource(getCharacterResourceForLevel(currentLevel));

        // 이전 레벨 캐릭터 업데이트 (레벨 1이면 이전 레벨 이미지 숨김)
        if (currentLevel > 1) {
            previousCharacterImage.setVisibility(View.VISIBLE);
            previousCharacterImage.setImageResource(getCharacterResourceForLevel(currentLevel - 1));
        } else {
            previousCharacterImage.setVisibility(View.INVISIBLE);
        }

        // 다음 레벨 캐릭터 업데이트 (최대 레벨이면 다음 레벨 이미지 숨김)
        if (currentLevel < CHARACTER_RESOURCES.length) {
            nextCharacterImage.setVisibility(View.VISIBLE);
            nextCharacterImage.setImageResource(getCharacterResourceForLevel(currentLevel + 1));

            // ColorMatrix 흑백 효과 적용
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0); // 채도(0)설정 (흑백)
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            nextCharacterImage.setColorFilter(filter);
        } else {
            nextCharacterImage.setVisibility(View.INVISIBLE);
        }

        // 레벨 텍스트 업데이트
        levelText.setText("Level " + currentLevel);

        // 현재 날짜 텍스트 업데이트
        currentDayText.setText("Day " + currentDay);

        // 남은 일수 텍스트 업데이트
        int daysNeeded = RoutineManager.getDaysNeededForNextLevel();
        daysLeftText.setText(String.format("다음 레벨까지 %d일 남음", daysNeeded));

        // 필요 루틴 개수 텍스트 업데이트
        String routineStatus = String.format("오늘 달성한 루틴: %d개 / 필요한 루틴: %d개 이상",
                completedToday, requiredRoutines);
        requiredRoutinesText.setText(routineStatus);
    }

    private static final int[] CHARACTER_RESOURCES = {
            R.drawable.character_level1, R.drawable.character_level2, R.drawable.character_level3,
            R.drawable.character_level4, R.drawable.character_level5, R.drawable.character_level6,
            R.drawable.character_level7, R.drawable.character_level8, R.drawable.character_level9,
            R.drawable.character_level10
    };

    private int getCharacterResourceForLevel(int level) {
        if (level >= 1 && level <= CHARACTER_RESOURCES.length) {
            return CHARACTER_RESOURCES[level - 1];
        }
        return R.drawable.character_level1;
    }
}