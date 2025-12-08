// D:/Materi Kuliah/Semester 3/Mobile Development/WardrobeDigital/app/src/main/java/com/example/wardrobedigital/MainActivity.java

package com.example.wardrobedigital;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install splash screen
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // 1. Set content view
        setContentView(R.layout.activity_main);

        // 2. Temukan BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);

        // 3. Gunakan SupportFragmentManager untuk mendapatkan NavHostFragment secara eksplisit
        // Ini adalah cara paling robust untuk memastikan NavHostFragment ditemukan.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // 4. Pastikan navHostFragment tidak null sebelum melanjutkan
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 5. Hubungkan NavController dengan BottomNavigationView
            NavigationUI.setupWithNavController(navView, navController);
        }

        // Kode untuk window insets dinonaktifkan sementara untuk debugging
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
    }
}
