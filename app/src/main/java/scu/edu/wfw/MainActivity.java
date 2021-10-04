package scu.edu.wfw;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import scu.edu.wfw.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Map<Integer, String> methods = new HashMap<>();
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        methods.put(R.id.logout, "logout_click");
        methods.put(R.id.load_data, "load_click");
        methods.put(R.id.custom_location, "custom_click");
        methods.put(R.id.custom_switch, "custom_location_switch");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ((SwitchCompat) ((RelativeLayout) menu.findItem(R.id.app_bar_switch).getActionView())
                .findViewById(R.id.custom_switch))
                .setChecked(preferences.getBoolean("isIntercept", false));
        return true;
    }

    public void menuItemClick(MenuItem item) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        menuItemClick_imp(item);
    }

    public void menuItemClick(View view) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        menuItemClick_imp(view);
    }

    public <T> void menuItemClick_imp(T item) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof FirstFragment) {
            int id = 0;
            if (item instanceof MenuItem)
                id = ((MenuItem) item).getItemId();
            else if (item instanceof View)
                id = ((View) item).getId();
            else
                return;
            Method method = fragment.getClass().getMethod(methods.get(id), Object.class);
            method.invoke(fragment, item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}