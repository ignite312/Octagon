package com.octagon.octagondu;

import static com.octagon.octagondu.MainActivity.DUREGNUM;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentPostCreate extends Fragment {
    Spinner spinnerHelp, spinnerBusType;
    TextView textViewTitle, textViewDesc;
    Button button;
    FragmentNewsFeed fragmentNewsFeed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_create, container, false);
        spinnerHelp = view.findViewById(R.id.postType);
        spinnerBusType = view.findViewById(R.id.busName);
        textViewTitle = view.findViewById(R.id.postTitle);
        textViewDesc = view.findViewById(R.id.body);
        button = view.findViewById(R.id.go);
        fragmentNewsFeed = new FragmentNewsFeed();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String helpType = spinnerHelp.getSelectedItem().toString();
                String busType = spinnerBusType.getSelectedItem().toString();
                String title = textViewTitle.getText().toString();
                String desc = textViewDesc.getText().toString();
                String time = getCurrentTime24HourFormat();
                String date = getCurrentDateFormatted();
                if (!title.isEmpty() || !desc.isEmpty()) {
                    DatabaseReference PostCountRef = FirebaseDatabase.getInstance().getReference("Feed/PostCount");
                    PostCountRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer value = mutableData.getValue(Integer.class);
                            if (value == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(value + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            if (databaseError != null) {

                            } else {
                                Integer updatedValue = dataSnapshot.getValue(Integer.class);
                                InfoNewsFeed post = new InfoNewsFeed(DUREGNUM, busType, helpType, title, desc, 0, time, date, "0", String.valueOf(updatedValue));
                                FirebaseDatabase.getInstance().getReference("Feed/Posts").child(String.valueOf(updatedValue)).setValue(post);
                                update(2, DUREGNUM);
                                showToast("Posted");
                                openFeedFragment();
                            }
                        }
                    });

                } else showToast("Something is empty");
            }
        });
        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public String getCurrentDateFormatted() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    public String getCurrentTime24HourFormat() {
        Date currentTime = new Date();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        return timeFormat.format(currentTime);
    }

    private void openFeedFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, fragmentNewsFeed)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void update(int dcc, String UID) {
        DatabaseReference UserPostCount = FirebaseDatabase.getInstance().getReference("UserInfo/" + DUREGNUM + "/postCount");
        UserPostCount.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer value = mutableData.getValue(Integer.class);
                if (value == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(value + 1);
                }
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                } else {
                    Integer updatedValue = dataSnapshot.getValue(Integer.class);
                }
            }
        });

        DatabaseReference ContributionCountRef = FirebaseDatabase.getInstance().getReference("UserInfo/" + UID + "/contributionCount");
        ContributionCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer value = mutableData.getValue(Integer.class);
                if (value == null) {
                    mutableData.setValue(dcc);
                } else {
                    mutableData.setValue(value + dcc);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                } else {
                    Integer updatedValue = dataSnapshot.getValue(Integer.class);
                }
            }
        });
    }
}