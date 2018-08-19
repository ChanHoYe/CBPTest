package com.ch.cbptest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ch.cbptest.DTO.DataDTO;
import com.ch.cbptest.DTO.InfoDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class App extends Application {
    private static App self;
    private static volatile FirebaseAuth sAuth;
    private static volatile FirebaseUser sUser;
    private static volatile FirebaseDatabase sDatabase;
    private ValueEventListener mInfoListener;
    private ChildEventListener mDataListener;
    private DatabaseReference mInfoRef, mDataRef;
    private ArrayList<DataDTO> mDatalist;
    private InfoDTO infoDTO;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return self;
                    }
                };
            }
        });
    }

    public static void initApp() {
        sAuth = FirebaseAuth.getInstance();
        //sUser = sAuth.getCurrentUser();
        sDatabase = FirebaseDatabase.getInstance();
    }

    public void initDB() {
        mDatalist = new ArrayList<>();
        mInfoRef = this.sDatabase.getReference("users").child(this.sUser.getUid()).child("info");
        mDataRef = this.sDatabase.getReference("users").child(this.sUser.getUid()).child("data");
        mInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                infoDTO = dataSnapshot.getValue(InfoDTO.class);
                updateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDataListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataDTO dataDTO = dataSnapshot.getValue(DataDTO.class);
                dataDTO.setId(dataSnapshot.getKey());

                mDatalist.add(dataDTO);

                //updateData();
                updateList();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataDTO toChange = null;
                DataDTO dataDTO = dataSnapshot.getValue(DataDTO.class);
                dataDTO.setId(dataSnapshot.getKey());


                for (DataDTO value : mDatalist) {
                    if(value.getId().equals(dataSnapshot.getKey())) {
                        toChange = value;
                        break;
                    }
                }

                if(toChange != null) {
                    int idx = mDatalist.indexOf(toChange);

                    mDatalist.set(idx, dataDTO);
                }

                //updateData();
                updateList();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DataDTO toRemove = null;

                for (DataDTO dataDTO : mDatalist) {
                    if(dataDTO.getId().equals(dataSnapshot.getKey())) {
                        toRemove = dataDTO;
                        break;
                    }
                }

                if(toRemove != null) {
                    mDatalist.remove(toRemove);
                }

                //updateData();
                updateList();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mInfoRef.addValueEventListener(mInfoListener);
        mDataRef.addChildEventListener(mDataListener);
    }

    private void updateData() {
        EventBus.getDefault().post(new Events(infoDTO, mDatalist));
    }

    private void updateList() {
        EventBus.getDefault().removeAllStickyEvents();
        Log.e("FUCK", mDatalist.toString());
        EventBus.getDefault().postSticky(mDatalist);
    }


    public static void updateUser() {
        sUser = sAuth.getCurrentUser();
    }

    public static FirebaseDatabase getDatabase() {
        return sDatabase;
    }

    public static FirebaseUser getUser() {
        return sUser;
    }

    public static FirebaseAuth getAuth() {
        return sAuth;
    }

    public InfoDTO getInfoDTO() {
        return infoDTO;
    }
}
