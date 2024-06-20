package com.example.levalens.Helper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.levalens.Activity.SettingsActivity;
import com.example.levalens.Activity.TermsOfUseActivity;
import com.example.levalens.R;
import com.google.android.material.navigation.NavigationView;

public class NavigationHelper implements NavigationView.OnNavigationItemSelectedListener {
    private final Context context;
    private final DrawerLayout drawerLayout;

    public NavigationHelper(Context context, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.context = context;
        this.drawerLayout = drawerLayout;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            context.startActivity(new Intent(context, SettingsActivity.class));
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(context, TermsOfUseActivity.class);
            context.startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
