package com.example.routinehero;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class ManageRoutinesActivity extends AppCompatActivity {
    private ListView routineListView;
    private EditText newRoutineText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_routines);

        routineListView = findViewById(R.id.manage_routine_list);
        newRoutineText = findViewById(R.id.new_routine_text);

        loadRoutines();

        findViewById(R.id.add_routine_button).setOnClickListener(v -> {
            String routineText = newRoutineText.getText().toString();
            if (!routineText.isEmpty()) {
                RoutineManager.addRoutine(routineText);
                newRoutineText.setText("");
                loadRoutines();
            }
        });
    }

    private void loadRoutines() {
        List<Routine> routines = RoutineManager.getRoutines();
        RoutineAdapter adapter = new RoutineAdapter(this, routines, false, true);
        routineListView.setAdapter(adapter);
    }
}