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

import kwetu_jobs.hiring.com.databinding.NewJobseekerDashboardFileBinding;

public class New_jobseeker_dashboard extends AppCompatActivity {
 NewJobseekerDashboardFileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_jobseeker_dashboard_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = NewJobseekerDashboardFileBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);

        // Correctly set the content view
        setContentView(binding.getRoot());

        // Load default fragment
        ReplaceFragment(new Jobseeker_dashboard_home_fragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.jobseeker_home) {
                        ReplaceFragment(new Jobseeker_dashboard_home_fragment());
                    } else if (itemId == R.id.jobseeker_apply) {
                        ReplaceFragment(new JobSeeker_dashboard_applyJob_fragment());
                    } else if (itemId == R.id.jobseeker_view) {
                        ReplaceFragment(new Jobseeker_dashboard_viewAll_fragment());
                    }

    //    Toast.makeText(this, "Fragment changed!", Toast.LENGTH_SHORT).show();
        return true;
    });

}
private void ReplaceFragment(
        Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();

    transaction.replace(R.id.frame_layout_dashboard, fragment);
    transaction.commit();

  //  Toast.makeText(this, "Fragment changed to: " + fragment.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
}


}