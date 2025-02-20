package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Jobseeker_dashboard_home_fragment extends Fragment {

    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private FirebaseAuth logAuth_js;
    private FirebaseUser log_user_js;
    private TextView usernameTextView_js;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.jobseeker_dashboard_home_fragment, container, false);

        // Initialize Firebase Auth
        logAuth_js = FirebaseAuth.getInstance();
        log_user_js = logAuth_js.getCurrentUser();

        // Initialize views using 'view.findViewById()'
        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.navigation_view);
        usernameTextView_js = view.findViewById(R.id.user_greet_seeker); // Ensure this ID exists in XML

        // Set up toolbar navigation click
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set up navigation item selection listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.view_profile) {
               // Toast.makeText(getActivity(), "View Profile is clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), Job_seeker_viewing_the_profile.class);
                startActivity(intent);
                getActivity().finish();
            } else if (id == R.id.logout) {
                logAuth_js.signOut(); // Firebase sign-out
                Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Redirect to Login Screen
                Intent intent = new Intent(getActivity(), JobSeeker_login.class);
                startActivity(intent);
                getActivity().finish(); // Close current activity
            }

            return true;
        });

        // Fetch Username if User is Logged In
        if (log_user_js != null) {
            String userId = log_user_js.getUid();
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String username = document.getString("Username");
                            if (username != null) {
                                usernameTextView_js.setText("Welcome, " + username);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Error fetching username", Toast.LENGTH_SHORT).show()
                    );
        }

        return view;
    }
}
