package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Employer_dashboard_home extends Fragment {
    private DrawerLayout drawerLayout_emp;
    private NavigationView navigationView_emp;
    private MaterialToolbar toolbar_emp;
    private FirebaseAuth auth;
    private FirebaseUser log_user_emp;
    private FirebaseFirestore db;
    private TextView usernameTextView;
Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.employer_dashboard_home_fg, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        log_user_emp = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        toolbar_emp = view.findViewById(R.id.topAppBar);
        drawerLayout_emp = view.findViewById(R.id.drawer_layout);
        navigationView_emp = view.findViewById(R.id.navigation_view);
        usernameTextView = view.findViewById(R.id.user_greeting);


        // Set up toolbar navigation click
        toolbar_emp.setNavigationOnClickListener(v -> drawerLayout_emp.openDrawer(GravityCompat.START));

        // Set up navigation item selection listener
        navigationView_emp.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout_emp.closeDrawer(GravityCompat.START);

            if (id == R.id.view_profile_emp) {
                Toast.makeText(getActivity(), "View Profile is clicked", Toast.LENGTH_SHORT).show();
                 intent = new Intent(getActivity(), Viewing_employer_profile.class);
                startActivity(intent);
                getActivity().finish();
            } else if (id == R.id.logout_emp) {
                auth.signOut(); // Logout
                Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

                 intent = new Intent(getActivity(), JobSeeker_login.class);
                startActivity(intent);
                getActivity().finish(); // Close current activity
                // Redirect to Login (Optional)
            }

            return true;
        });

        // Fetch Username if User is Logged In
        if (log_user_emp != null) {
            String userId = log_user_emp.getUid();
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String username = document.getString("Username");
                            if (username != null) {
                                usernameTextView.setText("Welcome, " + username);
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

