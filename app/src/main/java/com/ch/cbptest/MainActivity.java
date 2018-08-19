package com.ch.cbptest;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ch.cbptest.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private ActivityMainBinding mBinding;
    private ActionBarDrawerToggle mToggle;
    private MainPageAdapter mainPageAdapter;
    private TextView userName, userEmail;
    private CircleImageView profileImg;
    private FirebaseAuth.AuthStateListener mListener;
    private volatile boolean isQuit = true;

    /**
     * @brief onCreate
     * @details Called when the activity is first created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);    // Layout을 바인딩해준다.

        setFirebase();

        ((App)getApplication()).initDB();

        initView();
    }

    /**
     * @brief initView
     * @details Initialize View Components and set up listener
     */
    private void initView() {
        setSupportActionBar(mBinding.toolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

        mToggle = new ActionBarDrawerToggle(this, mBinding.drawer, R.string.open, R.string.close);
        mBinding.drawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);
        mainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mBinding.mainPager.setAdapter(mainPageAdapter);
        mBinding.mainTab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        mBinding.mainPager.setCurrentItem(0);
                        return true;
                    case R.id.bottom_info:
                        mBinding.mainPager.setCurrentItem(1);
                        return true;
                    case R.id.bottom_measure:
                        mBinding.mainPager.setCurrentItem(2);
                        return true;
                    case R.id.bottom_analysis:
                        mBinding.mainPager.setCurrentItem(3);
                        return true;
                    case R.id.bottom_setting:
                        mBinding.mainPager.setCurrentItem(4);
                        return true;
                }
                return false;
            }
        });
        //mBinding.mainTab.setSelectedItemId(R.id.bottom_home);
    }

    /**
     * @brief setFirebase
     * @details Notify to Application Class to set up firebase objects
     */
    public void setFirebase() {
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                App.updateUser();

                // 유저가 없다고 확인될 경우, 액티비티를 종료하고 로그인 액티비티로 넘어감.
                if (App.getUser() == null) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                } else {    // 유저가 존재할 경우, Drawer 헤더에 유저 정보 표시.
                    initProfileImage();
                    userName = (TextView) findViewById(R.id.drawer_name);
                    userEmail = (TextView) findViewById(R.id.drawer_email);
                    userName.setText(App.getUser().getDisplayName());
                    userEmail.setText(App.getUser().getEmail());
                }
            }
        };
    }

    /**
     * @brief onStart
     * @details Called when the activity becomes visible to the user
     */
    @Override
    public void onStart() {
        super.onStart();

        App.getAuth().addAuthStateListener(mListener);  // 인증 상태 리스너 등록.
    }

    /**
     * @brief onStop
     * @details Called when the activity is no longer visible to the user
     */
    @Override
    public void onStop() {
        if (mListener != null) {
            App.getAuth().removeAuthStateListener(mListener);   // 인증 상태 리스너 해제.
        }

        super.onStop();
    }

    /**
     * @brief onDestroy
     * @details Called before the activity is destroyed by the system
     */
    @Override
    public void onDestroy() {
        if (isQuit) {
            freeValue();
        }

        super.onDestroy();
    }

    /**
     * @brief freeValue
     * @details When MainActivity is terminated, notify to Application Class
     */
    private void freeValue() {
        //((InitApp) getApplication()).terminate();
    }

    /**
     * @brief onBackPressed
     * @details Called when user press back button
     */
    @Override
    public void onBackPressed() {
        if (mBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                isQuit = false;
                freeValue();

                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @brief onNavigationItemSelected
     * @details Called when user selects navigation item
     * @param item
     * @return itemSelected Result
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_info:
                //startActivity(new Intent(MainActivity.this, InfoActivity.class));
                break;
            case R.id.nav_budget:
                //startActivity(new Intent(MainActivity.this, BudgetSettingActivity.class));
                break;
            case R.id.nav_category:
                //startActivity(new Intent(MainActivity.this, CategoryAddingActivity.class));
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
        }

        mBinding.drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * @brief onOptionsItemSelected
     * @details Called when user selects option item
     * @param item
     * @return itemSelected Result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBinding.drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief initProfileImage
     * @details Bind Image to Profile ImageView
     */
    private void initProfileImage() {
        if (App.getUser().getPhotoUrl() != null) {
            profileImg = (CircleImageView) findViewById(R.id.drawer_image);
            final LinearLayout back = (LinearLayout) findViewById(R.id.drawer_back);
            Glide.with(this).load(App.getUser().getPhotoUrl()).into(profileImg);
            Glide.with(this).load(App.getUser().getPhotoUrl()).apply(bitmapTransform(new BlurTransformation(25, 3))).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    back.setBackground(resource);
                }
            });
        }
    }

    /**
     * @brief signOut
     * @details Called when user selects signOut
     */
    private void signOut() {
        List<? extends UserInfo> providerData = App.getUser().getProviderData();

        // Firebase sign out
        App.getAuth().signOut();

        for (UserInfo info : providerData) {
            switch (info.getProviderId()) {
                case "google.com":
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                    mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                    break;

                case "firebase":
                    if (info.getUid().startsWith("kakao")) {
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                            }
                        });
                    }
                    break;
            }
        }
    }

    /**
     @class MainPageAdapter
     @brief FragmentPagerAdapter for ViewPager
     @details Home, Housekeeping, Saving Fragment pager for Main ViewPager
     @date 2018
     */
    class MainPageAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_NUMBER = 5;

        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * @brief getItem
         * @details Return Fragment by position
         * @param position
         * @return Fragment
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeTab.newInstance();
                case 1:
                    return InfoTab.newInstance();
                case 2:
                    return MeasureTab.newInstance();
                case 3:
                    return AnalysisTab.newInstance();
                case 4:
                    return SettingTab.newInstance();
                default:
                    return null;
            }
        }

        /**
         * @brief getItemPosition
         * @details Return position of Fragment
         * @param object
         * @return position
         */
        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        /**
         * @brief getCount
         * @details Return the number of Fragments
         * @return the number of Fragments
         */
        @Override
        public int getCount() {
            return PAGE_NUMBER;
        }
    }
}
