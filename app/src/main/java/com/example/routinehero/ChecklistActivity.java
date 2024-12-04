package com.example.routinehero;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChecklistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        ListView checklistRoutineList = findViewById(R.id.checklist_routine_list);
        List<Routine> todayRoutines = RoutineManager.getRoutines();
        RoutineAdapter adapter = new RoutineAdapter(this, todayRoutines, true, false);
        checklistRoutineList.setAdapter(adapter);

        findViewById(R.id.save_button).setOnClickListener(v -> {
            List<Routine> completedRoutines = adapter.getCompletedRoutines();
            RoutineManager.saveCompletedRoutines(completedRoutines);
            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
