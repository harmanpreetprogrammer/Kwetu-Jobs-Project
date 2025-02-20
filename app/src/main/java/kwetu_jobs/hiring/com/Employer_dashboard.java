package kwetu_jobs.hiring.com;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kwetu_jobs.hiring.com.databinding.EmployerDashboardFileBinding;

public class Employer_dashboard extends AppCompatActivity {

    EmployerDashboardFileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.employer_dashboard_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Inflate the binding
        binding = EmployerDashboardFileBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);

        // Correctly set the content view
        setContentView(binding.getRoot());

        // Load default fragment
        ReplaceFragment(new Employer_dashboard_home());

        // Set the bottom navigation listener
        binding.bottomNavigationViewEmploy.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                ReplaceFragment(new Employer_dashboard_home());
            } else if (itemId == R.id.post) {
                ReplaceFragment(new Employer_dashboard_post_jobs());
            } else if (itemId == R.id.review) {
                ReplaceFragment(new Employer_dashboard_review_jobs());
            } else if (itemId == R.id.screening) {
                ReplaceFragment(new Employer_dashboard_screening());
            }

            //   Toast.makeText(this, "Fragment changed!", Toast.LENGTH_SHORT).show();
            return true;
        });

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_layout_employ, fragment);
        transaction.commit();


    }
}