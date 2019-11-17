package com.example.mydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.Model.DiaryDetail;
import com.example.mydiary.Model.HistoryDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddActionActivity extends AppCompatActivity {
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mColor = Color.parseColor("#FFFFFF");
    private String content;
    private ImageButton datePickerButton, timePickerButton, colorPickerButton, deleteButton, doneButton, cancelButton;
    private EditText mEditContent, mEditTitle;
    private DiaryDetail editDiary;
    private TextView colorDetail;
    private int command;
    private ArrayList<HistoryDetail> history = new ArrayList<HistoryDetail>();
    private HistoryDetailAdapter adapter;
    private ListView mListviewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);
        setEvent();
        Intent intent = getIntent();
        boolean signal = false;
        if (intent != null) {
            command = intent.getIntExtra("requestCode", 0);
            Bundle bundle = intent.getBundleExtra("request");
            if (bundle != null) {
                editDiary = (DiaryDetail) bundle.getSerializable("diary");
                mEditContent.setText(editDiary.getContent());
                colorDetail.setBackgroundColor(editDiary.getColor());
                colorDetail.setText("                      ");
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(editDiary.getDateCreate());
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mColor = editDiary.getColor();
                history = editDiary.getHistory();
                signal = true;
            }
        }
        setHistoryAdapter();
        dataInit(signal);
    }

    public void dataInit(boolean signal) {
        Date date = new Date();
        if (signal)
            date = editDiary.getDateCreate();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    protected void setHistoryAdapter() {
        adapter = new HistoryDetailAdapter(this, R.layout.history_detail_component, history);
        mListviewHistory.setAdapter(adapter);
    }

    protected void setEvent() {
        mListviewHistory = findViewById(R.id.history_listview);
        colorDetail = findViewById(R.id.color_view_detail);
        colorDetail.setText("                      ");
        datePickerButton = findViewById(R.id.btn_date_picker);
        timePickerButton = findViewById(R.id.btn_time_picker);
        colorPickerButton = findViewById(R.id.btn_color_picker);
        deleteButton = findViewById(R.id.btn_delete_diary);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DELETE", "DELETE");
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActionActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Confirm to delete this diary?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (command == MainActivity.REQUEST_CODE_ADD) {
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("command", MainActivity.REQUEST_CODE_DELETE);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }
                });
                builder.show();
            }
        });
        mEditTitle = findViewById(R.id.edit_title);
        mEditContent = findViewById(R.id.edit_content);
        doneButton = findViewById(R.id.btn_done);
        cancelButton = findViewById(R.id.btn_back);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                switch (command) {
                    case MainActivity.REQUEST_CODE_ADD: {
                        DiaryDetail diary = new DiaryDetail();
                        diary.setContent(mEditContent.getText().toString());
                        diary.setColor(mColor);
                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.YEAR, mYear);
                        c1.set(Calendar.MONTH, mMonth);
                        c1.set(Calendar.DAY_OF_MONTH, mDay);
                        c1.set(Calendar.HOUR_OF_DAY, mHour);
                        c1.set(Calendar.MINUTE, mMinute);
                        Date d1 = c1.getTime();
                        diary.setHistory(new ArrayList<HistoryDetail>());
                        diary.setDateCreate(d1);
                        bundle.putSerializable("diary", diary);
                        intent.putExtra("response", bundle);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    break;
                    case MainActivity.REQUEST_CODE_EDIT: {
                        HistoryDetail history = new HistoryDetail();
                        history.setContent(editDiary.getContent());
                        Date date = new Date();
                        history.setDateEditted(date);
                        editDiary.setContent(mEditContent.getText().toString());
                        editDiary.setColor(mColor);
                        editDiary.addHistory(history);
                        bundle.putSerializable("diary", editDiary);
                        intent.putExtra("command", command);
                        intent.putExtra("package", bundle);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddActionActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    mEditTitle.append(" " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    Calendar calendar = Calendar.getInstance();
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker colorPicker = new ColorPicker(AddActionActivity.this);
                ArrayList<String> colorlist = new ArrayList<>();
                colorlist.add("#d9d9d9");
                colorlist.add("#ffcdd2");
                colorlist.add("#f8bbd0");
                colorlist.add("#e1bee7");
                colorlist.add("#bbdefb");
                colorlist.add("#d7ccc8");
                colorlist.add("#ffe0b2");
                colorlist.add("#fff9c4");
                colorlist.add("#c8e6c9");
                colorlist.add("#b2dfdb");
                colorPicker.setColors(colorlist);
                colorPicker.setDefaultColorButton(R.color.colorPrimary);
                colorPicker.setRoundColorButton(true)
                        .setColumns(5)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                //colorDetail.setText(Integer.toHexString(color));
                                colorDetail.setBackgroundColor(color);
                                mColor = color;
                            }

                            @Override
                            public void onCancel() {

                            }
                        })
                        .show();
            }
        });
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePicker = new TimePickerDialog(AddActionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mEditTitle.append((" " + hourOfDay + " " + minute));
                    }
                }, mHour, mMinute, false);
                timePicker.show();
            }
        });
    }
}
