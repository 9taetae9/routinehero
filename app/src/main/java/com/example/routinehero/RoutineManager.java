package com.example.routinehero;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RoutineManager {
    private static ArrayList<Routine> routines = new ArrayList<>();
    private static int currentLevel = 1;
    private static int consecutiveDays = 0;
    private static final int[] REQUIRED_ROUTINES = {1, 1, 1, 2, 2, 2, 3, 3, 4, 5};//*/{1, 2, 3, 4, 5, 5, 5, 5, 5, 5};
    public static final int[] DAYS_NEEDED ={1, 1, 1, 2, 2, 2, 3, 3, 4, 5};//{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};//*/ {7, 7, 7, 7, 7, 14, 20, 30, 40, 50};
    private static int currentId = 0;
    private static int currentTestDay = 0;
    private static boolean todayRoutinesCompleted = false;
    private static final int MAX_LEVEL = 10;
    private static boolean isMaxLevelAchieved = false;

    public static void addRoutine(String name) {
        Routine routine = new Routine(++currentId, name);
        routines.add(routine);
    }

    // 루틴 삭제 메소드 추가
    public static void removeRoutine(Routine routine) {
        routines.remove(routine);
    }

    public static ArrayList<Routine> getRoutines() {
        return new ArrayList<>(routines);
    }

    public static void saveCompletedRoutines(List<Routine> completedRoutines) {
        // 현재 루틴들의 완료 상태 업데이트
        for (Routine routine : routines) {
            routine.setCompleted(false);
        }

        // 완료된 루틴 체크
        for (Routine completed : completedRoutines) {
            for (Routine routine : routines) {
                if (routine.getId() == completed.getId()) {
                    routine.setCompleted(true);
                    break;
                }
            }
        }

        // 오늘의 목표 달성 여부 체크
        int completedCount = getCompletedRoutinesCount();
        int requiredRoutines = REQUIRED_ROUTINES[currentLevel - 1];
        todayRoutinesCompleted = completedCount >= requiredRoutines;
    }

    public static void incrementTestDay(Context context) {
        // 날짜가 변경될 때 레벨 진행상황 체크
        if (todayRoutinesCompleted) {
            consecutiveDays++;
            if (consecutiveDays >= DAYS_NEEDED[currentLevel - 1]) {
                if (currentLevel < MAX_LEVEL) {
                    levelUp();
                    Toast.makeText(context, "레벨 업!", Toast.LENGTH_SHORT).show();
                } else if (currentLevel == MAX_LEVEL && !isMaxLevelAchieved) {
                    // 처음 최대 레벨 달성 시
                    isMaxLevelAchieved = true;
                    Toast.makeText(context, "축하합니다! 최고 레벨을 달성했습니다! 🎉", Toast.LENGTH_LONG).show();
                } else {
                    // 최대 레벨이고 루틴을 잘 수행한 경우
                    Toast.makeText(context, "완벽한 하루였습니다! ⭐", Toast.LENGTH_SHORT).show();
                }
                // 최대 레벨에서는 연속 일수를 DAYS_NEEDED의 마지막 값으로 고정
                if (currentLevel == MAX_LEVEL) {
                    consecutiveDays = DAYS_NEEDED[MAX_LEVEL - 1];
                }
            }
        } else {
            if (currentLevel > 1) {
                currentLevel--;
                Toast.makeText(context, "오늘의 목표를 달성하지 못했습니다. 레벨 다운...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "오늘의 목표를 달성하지 못했습니다. 꾸준히 루틴을 달성해주세요!", Toast.LENGTH_SHORT).show();
            }
            consecutiveDays = 0;
        }

        // 새로운 날을 위한 초기화
        currentTestDay++;
        todayRoutinesCompleted = false;
        resetDayProgress();
    }

    public static void decrementTestDay() {
        if (currentTestDay > 0) {
            currentTestDay--;
            todayRoutinesCompleted = false;
            resetDayProgress();
        }
    }

    private static void resetDayProgress() {
        for (Routine routine : routines) {
            routine.setCompleted(false);
        }
    }

    private static void levelUp() {
        if (currentLevel < MAX_LEVEL) {
            currentLevel++;
            consecutiveDays = 0;
        }
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public static int getCurrentTestDay() {
        return currentTestDay;
    }

    public static int getDaysNeededForNextLevel() {
        if (currentLevel >= 1 && currentLevel <= MAX_LEVEL) {
            return DAYS_NEEDED[currentLevel - 1] - consecutiveDays;
        }else if(isMaxLevelAchieved){
            return 0;
        }
        return 0;
    }

    public static int getCompletedRoutinesCount() {
        return (int) routines.stream()
                .filter(Routine::isCompleted)
                .count();
    }

    public static int getRequiredRoutinesForCurrentLevel() {
        return REQUIRED_ROUTINES[currentLevel - 1];
    }
}