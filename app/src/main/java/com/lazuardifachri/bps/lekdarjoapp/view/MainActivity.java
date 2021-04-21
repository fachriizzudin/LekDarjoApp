 package com.lazuardifachri.bps.lekdarjoapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.lazuardifachri.bps.lekdarjoapp.BuildConfig;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ActivityMainBinding;
import com.lazuardifachri.bps.lekdarjoapp.model.api.LoginApi;
import com.lazuardifachri.bps.lekdarjoapp.model.request.LoginRequest;
import com.lazuardifachri.bps.lekdarjoapp.model.response.LoginResponse;
import com.lazuardifachri.bps.lekdarjoapp.util.ServiceGenerator;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.io.File;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private ActivityMainBinding binding;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private NavController navController;
    private final LoginApi loginApi = ServiceGenerator.createService(LoginApi.class, this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupDrawer();

        setupNavigation();

        // androidapp@mail.com
        // l1e2k3d4r5j6o

        // loginApp();
        SharedPreferencesHelper.getInstance(getApplicationContext()).saveAuthToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsZWtkYXJqb3B5QGVtYWlsLmNvbSIsImlhdCI6MTYxODU4ODU2MiwiZXhwIjoyNjE4NTg4NTYyfQ.oLZc8sVSZIZMX1MkxVTWSlHhhGkmbLE4B6teQV1NTtMfcCN8A7lvXj6OOtLELuZK43imKiDy8lheMlHNhNDwEw");

        File root = new File(getApplication().getFilesDir().getAbsolutePath());
        if (!root.exists()) {
            if (root.mkdirs()) {
                root.setReadable(true, false);
                root.setExecutable(true, false);
            }
        }
    }

    private void setupDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = binding.drawer;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

    }

    private void setupNavigation() {
        navigationView = binding.navigationView;
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navController.navigate(R.id.mainFragment);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.main:
                navController.navigate(R.id.mainFragment);
                break;
            case R.id.news:
                navController.navigate(R.id.statisticalNewsListFragment);
                break;
            case R.id.publication:
                navController.navigate(R.id.publicationListFragment);
                break;
            case R.id.indicator:
                navController.navigate(R.id.indicatorListFragment);
                break;
            case R.id.infographic:
                navController.navigate(R.id.infographicListFragment);
                break;
            case R.id.about:
                navController.navigate(R.id.aboutFragment);
                break;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(shareIntent);
                break;
        }
        return true;
    }

    private void loginApp() {
        loginApi.login(new LoginRequest("bpskabupatensidoarjo@gmail.com", "@Hero140"))
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull LoginResponse loginResponse) {
                        SharedPreferencesHelper.getInstance(getApplicationContext()).saveAuthToken(loginResponse.getToken());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("onError", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public NavController getNavController() {
        return navController;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}