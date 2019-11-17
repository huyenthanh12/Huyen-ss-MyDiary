package com.example.mydiary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.Model.DiaryDetail;
import com.example.mydiary.Model.HistoryDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listView;
    private FloatingActionButton addButton;

    private DatabaseReference dbReference;
    private String userId;
    private ArrayList<DiaryDetail> diaries;
    private DiaryDetailAdapter adapter;

    private ImageButton signoutButton;

    public static int position;
    public static final int REQUEST_CODE_ADD = 10001;
    public static final int REQUEST_CODE_DELETE = 10002;
    public static final int REQUEST_CODE_EDIT = 10003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        diaries = new ArrayList<>();

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR,1900  );
        c1.set(Calendar.HOUR_OF_DAY,10);
        c1.set(Calendar.MINUTE,10);
        c1.set(Calendar.MONTH,1);
        c1.set(Calendar.DATE,1);
        Date d1 = c1.getTime();

        diaries.add(new DiaryDetail("0",d1, Color.parseColor("#FFFFFF"),"GG"));

        Intent intent = getIntent();
        if(intent != null) {
            userId = intent.getStringExtra("userId");
        }
        Log.d("USER ID",userId);
        checkForUserIdInDatabase();
        setEvent();
        adapter = new DiaryDetailAdapter(diaries,this);
        listView.setAdapter(adapter);
        getData();
    }

    public void setEvent() {

        signoutButton = findViewById(R.id.btn_signout);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginActivity.mGoogleSignInClient.signOut()
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    finish();
                                }
                            }
                        });

            }
        });
        listView = findViewById(R.id.listview);
        addButton = findViewById(R.id.add_action_fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActionActivity.class);
                intent.putExtra("requestCode",REQUEST_CODE_ADD);
                startActivityForResult(intent,REQUEST_CODE_ADD);
            }
        });
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addOnItemTouchListener(new RecyclerItemClickListener(this, listView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                try {
                    MainActivity.position = position;
                    Intent intent = new Intent(MainActivity.this,AddActionActivity.class);
                    DiaryDetail diary = diaries.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("diary",diary);
                    intent.putExtra("request",bundle);
                    intent.putExtra("requestCode",REQUEST_CODE_EDIT);
                    startActivityForResult(intent,REQUEST_CODE_EDIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }));
    }

    private void checkForUserIdInDatabase() {
        dbReference = FirebaseDatabase.getInstance().getReference().child(userId);
        dbReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            dbReference.child(userId).setValue(1)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {

                                            }
                                        }
                                    });
                            dbReference.child("Diary").setValue(diaries);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            Bundle bundle = data.getBundleExtra("response");
            final DiaryDetail item = (DiaryDetail) bundle.getSerializable("diary");
            String diaryId = dbReference.child("Diary").push().getKey();
            item.setId(diaryId);
            diaries.add(item);
            adapter.notifyDataSetChanged();
            dbReference.child("Diary").child(diaryId).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
        if(requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            int command = data.getIntExtra("command",0);
            switch (command) {
                case REQUEST_CODE_EDIT: {
                    Bundle bundle = data.getBundleExtra("package");
                    final DiaryDetail diary = (DiaryDetail)bundle.getSerializable("diary");
                    dbReference.child("Diary").child(diary.getId()).setValue(diary).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                diaries.remove(position);
                                diaries.add(position,diary);
                                sortJournal(diaries);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }break;
                case REQUEST_CODE_DELETE: {
                    final DiaryDetail diary = diaries.get(position);
                    dbReference.child("Diary").child(diary.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                diaries.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }break;
                default:
                    break;
            }

        }
    }

    public void getData() {
        diaries.clear();
        FirebaseDatabase.getInstance().getReference().child(userId).child("Diary").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    DiaryDetail tD = d.getValue(DiaryDetail.class);
                    if(tD.getHistory()==null)
                        tD.setHistory(new ArrayList<HistoryDetail>());
                    diaries.add(tD);
                }
                sortJournal(diaries);
                dbReference.child("Diary").removeEventListener(this);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void sortJournal(List<DiaryDetail> diaries) {
        Collections.sort(diaries, new Comparator<DiaryDetail>() {
            @Override
            public int compare(DiaryDetail o1, DiaryDetail o2) {
                if (o1.getDateCreate().equals(o2.getDateCreate())) {
                    return 0;
                } else if (o1.getDateCreate().before(o2.getDateCreate())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
}
