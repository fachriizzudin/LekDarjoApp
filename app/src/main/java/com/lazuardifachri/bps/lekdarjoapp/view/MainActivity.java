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
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.material.navigation.NavigationView;
import com.lazuardifachri.bps.lekdarjoapp.BuildConfig;
import com.lazuardifachri.bps.lekdarjoapp.R;
import com.lazuardifachri.bps.lekdarjoapp.databinding.ActivityMainBinding;
import com.lazuardifachri.bps.lekdarjoapp.util.Constant;
import com.lazuardifachri.bps.lekdarjoapp.util.SharedPreferencesHelper;

import java.io.File;

 public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private ActivityMainBinding binding;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupDrawer();

        setupNavigation();

        // androidapp@mail.com
        // l1e2k3d4r5j6o

        SharedPreferencesHelper.getInstance(getApplicationContext()).saveAuthToken(Constant.JWT_TOKEN_API);

        File root = new File(getApplication().getFilesDir().getAbsolutePath());
        if (!root.exists()) {
            if (root.mkdirs()) {
                root.setReadable(true, false);
                root.setExecutable(true, false);
            }
        }
    }

    private void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = binding.drawer;
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

    }

    private void setupNavigation() {
        NavigationView navigationView = binding.navigationView;
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navController.navigate(R.id.graphListFragment);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.main:
                navController.navigate(R.id.graphListFragment);
                break;
            case R.id.news:
                navController.navigate(R.id.statisticalNewsListFragment);
                break;
            case R.id.publication:
                navController.navigate(R.id.publicationListFragment);
                break;
            case R.id.indicator:
                navController.navigate(R.id.indicatorPagerFragment);
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
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Unduh LekDarjo, penyedia data dan statistik sidoarjo di: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(shareIntent);
                break;
        }
        return true;
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