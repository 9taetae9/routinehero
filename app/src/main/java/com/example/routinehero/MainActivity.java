package com.example.routinehero;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private ProgressBar levelProgressBar;
    private ImageView progressIndicator;
    
    // 애니메이션 관련 변수
    private AnimatorSet characterBounceAnimation;
    private AnimatorSet levelUpAnimation;
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RoutineManager 초기화
        RoutineManager.initialize(this);
        RoutineManager.checkDateChange();

        levelText = findViewById(R.id.level_text);
        daysLeftText = findViewById(R.id.days_left_text);
        requiredRoutinesText = findViewById(R.id.required_routines_text);
        currentDayText = findViewById(R.id.current_day_text);
        characterImage = findViewById(R.id.character_image);
        previousCharacterImage = findViewById(R.id.previous_character_image);
        nextCharacterImage = findViewById(R.id.next_character_image);
        levelProgressBar = findViewById(R.id.level_progress_bar);
        progressIndicator = findViewById(R.id.progress_indicator);

        // 실제 날짜 시스템으로 변경 - 테스트 버튼 제거
        // findViewById(R.id.decrease_day_button).setOnClickListener(v -> {
        //     RoutineManager.decrementTestDay();
        //     updateUI();
        // });

        // findViewById(R.id.increase_day_button).setOnClickListener(v -> {
        //     RoutineManager.incrementTestDay(this);
        //     updateUI();
        // });

        findViewById(R.id.check_routines_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChecklistActivity.class);
            startActivityForResult(intent, 1);
        });

        findViewById(R.id.manage_routines_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageRoutinesActivity.class);
            startActivity(intent);
        });

        updateUI();
        startCharacterBounceAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        startCharacterBounceAnimation();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopCharacterAnimations();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 루틴 완료 시 애니메이션 트리거
            triggerRoutineCompleteAnimation();
        }
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

        // 실제 날짜 텍스트 업데이트
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy년 MM월 dd일", java.util.Locale.KOREA);
        String currentDate = sdf.format(new java.util.Date());
        currentDayText.setText(currentDate);

        // 남은 일수 텍스트 업데이트
        int daysNeeded = RoutineManager.getDaysNeededForNextLevel();
        daysLeftText.setText(String.format("다음 레벨까지 %d일 남음", daysNeeded));

        // 필요 루틴 개수 텍스트 업데이트
        String routineStatus = String.format("오늘 달성한 루틴: %d개 / 필요한 루틴: %d개 이상",
                completedToday, requiredRoutines);
        requiredRoutinesText.setText(routineStatus);

        // 루틴 달성 일수
        int completedDays = 0;

        // 루틴 달성 총 일수 계산
        for (int i = 0; i < currentLevel - 1; i++) {
            completedDays += RoutineManager.DAYS_NEEDED[i];
        }

        // 현재 레벨까지의 루틴 달성 일수를 completedDays에 추가
        int daysInCurrentLevel = RoutineManager.DAYS_NEEDED[Math.min(currentLevel - 1, 9)]; // 배열 범위 초과 방지
        int daysCompletedInCurrentLevel = daysInCurrentLevel - RoutineManager.getDaysNeededForNextLevel();
        completedDays += daysCompletedInCurrentLevel;

        // 루틴 달성 최대 일수를 totalPossibleDays로 설정
        int totalPossibleDays = 0;
        for (int days : RoutineManager.DAYS_NEEDED) {
            totalPossibleDays += days;
        }
        levelProgressBar.setMax(totalPossibleDays);

        // 최대 진행도를 totalPossibleDays로 제한
        completedDays = Math.min(completedDays, totalPossibleDays);
        
        // 진행도 바 애니메이션
        animateProgressBar(completedDays);

        // 진행도 표시 위치 업데이트
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) progressIndicator.getLayoutParams();
        float progressWidth = levelProgressBar.getWidth() - progressIndicator.getWidth();
        float position = Math.min((progressWidth * completedDays) / totalPossibleDays, progressWidth);
        params.leftMargin = (int) position;
        progressIndicator.setLayoutParams(params);

        // 진행도 표시 이미지 업데이트
        progressIndicator.setImageResource(getCharacterResourceForLevel(currentLevel));
        progressIndicator.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
    
    // 캐릭터 바운스 애니메이션 시작
    private void startCharacterBounceAnimation() {
        if (isAnimating) return;
        
        stopCharacterAnimations();
        
        // 메인 캐릭터 바운스 애니메이션
        ObjectAnimator bounceY = ObjectAnimator.ofFloat(characterImage, "translationY", 0f, -20f, 0f);
        bounceY.setDuration(1000);
        bounceY.setInterpolator(new BounceInterpolator());
        bounceY.setRepeatCount(ValueAnimator.INFINITE);
        bounceY.setRepeatMode(ValueAnimator.REVERSE);
        
        // 약간의 회전 애니메이션 추가
        ObjectAnimator rotate = ObjectAnimator.ofFloat(characterImage, "rotation", -2f, 2f);
        rotate.setDuration(2000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(ValueAnimator.INFINITE);
        rotate.setRepeatMode(ValueAnimator.REVERSE);
        
        // 스케일 애니메이션 (호흡하는 느낌)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(characterImage, "scaleX", 1.0f, 1.05f, 1.0f);
        scaleX.setDuration(3000);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(characterImage, "scaleY", 1.0f, 1.05f, 1.0f);
        scaleY.setDuration(3000);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        
        characterBounceAnimation = new AnimatorSet();
        characterBounceAnimation.playTogether(bounceY, rotate, scaleX, scaleY);
        characterBounceAnimation.start();
        
        isAnimating = true;
    }
    
    // 레벨업 애니메이션
    private void startLevelUpAnimation() {
        if (isAnimating) return;
        
        stopCharacterAnimations();
        
        // 큰 바운스 애니메이션
        ObjectAnimator bigBounce = ObjectAnimator.ofFloat(characterImage, "translationY", 0f, -50f, 0f);
        bigBounce.setDuration(800);
        bigBounce.setInterpolator(new BounceInterpolator());
        
        // 스케일 애니메이션 (커졌다 작아지는 효과)
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(characterImage, "scaleX", 1.0f, 1.3f, 1.0f);
        scaleUp.setDuration(800);
        scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());
        
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(characterImage, "scaleY", 1.0f, 1.3f, 1.0f);
        scaleUpY.setDuration(800);
        scaleUpY.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // 회전 애니메이션
        ObjectAnimator spin = ObjectAnimator.ofFloat(characterImage, "rotation", 0f, 360f);
        spin.setDuration(800);
        spin.setInterpolator(new AccelerateDecelerateInterpolator());
        
        levelUpAnimation = new AnimatorSet();
        levelUpAnimation.playTogether(bigBounce, scaleUp, scaleUpY, spin);
        
        levelUpAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 레벨업 애니메이션 후 일반 바운스 애니메이션 재시작
                startCharacterBounceAnimation();
            }
        });
        
        levelUpAnimation.start();
    }
    
    // 애니메이션 중지
    private void stopCharacterAnimations() {
        if (characterBounceAnimation != null) {
            characterBounceAnimation.cancel();
            characterBounceAnimation = null;
        }
        if (levelUpAnimation != null) {
            levelUpAnimation.cancel();
            levelUpAnimation = null;
        }
        isAnimating = false;
    }
    
    // 루틴 완료 시 특별 애니메이션
    public void triggerRoutineCompleteAnimation() {
        // 작은 축하 애니메이션
        ObjectAnimator celebrate = ObjectAnimator.ofFloat(characterImage, "translationY", 0f, -15f, 0f);
        celebrate.setDuration(300);
        celebrate.setInterpolator(new BounceInterpolator());
        
        ObjectAnimator celebrateScale = ObjectAnimator.ofFloat(characterImage, "scaleX", 1.0f, 1.1f, 1.0f);
        celebrateScale.setDuration(300);
        celebrateScale.setInterpolator(new AccelerateDecelerateInterpolator());
        
        ObjectAnimator celebrateScaleY = ObjectAnimator.ofFloat(characterImage, "scaleY", 1.0f, 1.1f, 1.0f);
        celebrateScaleY.setDuration(300);
        celebrateScaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        
        AnimatorSet celebrateAnimation = new AnimatorSet();
        celebrateAnimation.playTogether(celebrate, celebrateScale, celebrateScaleY);
        celebrateAnimation.start();
    }
    
    // 진행도 바 애니메이션
    private void animateProgressBar(int targetProgress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(levelProgressBar, "progress", 
                levelProgressBar.getProgress(), targetProgress);
        progressAnimator.setDuration(1000);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.start();
    }
}