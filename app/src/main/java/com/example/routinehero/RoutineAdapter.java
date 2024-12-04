package com.example.routinehero;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RoutineAdapter extends ArrayAdapter<Routine> {
    private final boolean showCheckBox; // 체크박스 - ChecklistActivity에서 사용
    private final boolean showDeleteButton; // 삭제 버튼 - ManageRoutinesActivity에서 사용

    public RoutineAdapter(Context context, List<Routine> routines, boolean showCheckBox, boolean showDeleteButton) {
        super(context, R.layout.item_routine, routines);
        this.showCheckBox = showCheckBox;
        this.showDeleteButton = showDeleteButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_routine, parent, false);
        }

        Routine routine = getItem(position);
        TextView textView = convertView.findViewById(R.id.routine_name);
        CheckBox checkBox = convertView.findViewById(R.id.routine_checkbox);
        ImageButton deleteButton = convertView.findViewById(R.id.routine_delete_button);

        assert routine != null;
        textView.setText(routine.getName());
        checkBox.setVisibility(showCheckBox ? View.VISIBLE : View.GONE);

        if(showCheckBox) {
            checkBox.setChecked(routine.isCompleted());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    routine.setCompleted(isChecked));
        }

        // 삭제 버튼의 가시성 설정
        if (deleteButton != null) {
            deleteButton.setVisibility(showDeleteButton ? View.VISIBLE : View.GONE);

            if (showDeleteButton) {
                deleteButton.setOnClickListener(v -> {
                    // 삭제 확인 다이얼로그 생성
                    new AlertDialog.Builder(getContext())
                            .setMessage("삭제하시겠습니까?")
                            .setCancelable(false) // 배경을 클릭해도 다이얼로그가 닫히지 않게 함
                            .setPositiveButton("예", (dialog, id) -> {
                                // "예"클릭 시 루틴 삭제
                                remove(routine); // 리스트에서 삭제
                                RoutineManager.removeRoutine(routine); // RoutineManager에서도 삭제
                                notifyDataSetChanged(); // UI 업데이트
                                Toast.makeText(getContext(), "루틴이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("아니오", (dialog, id) -> {
                                dialog.dismiss(); //변화 x
                            })
                            .create()
                            .show();
                });
            }
        }

        return convertView;
    }

    public List<Routine> getCompletedRoutines() {
        List<Routine> completed = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            Routine routine = getItem(i);
            if (routine != null && routine.isCompleted()) {
                completed.add(routine);
            }
        }
        return completed;
    }
}
