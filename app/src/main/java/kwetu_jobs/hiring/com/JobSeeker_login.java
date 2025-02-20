package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class JobSeeker_login extends AppCompatActivity {

    TextInputEditText email_login, password_login;
    TextView textView_log;
    Button buttonlogin;
    FirebaseAuth logAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.job_seeker_login_page);

        // Apply padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth instance
        logAuth = FirebaseAuth.getInstance();

        // Binding UI elements
        email_login = findViewById(R.id.email_box_log);
        password_login = findViewById(R.id.password_box_log);
        buttonlogin = findViewById(R.id.login_button);
        textView_log = findViewById(R.id.signup_link_jobseeker);
        TextView forgotPassword = findViewById(R.id.job_fg_password);

        // Navigate to signup page when the user clicks on the "Sign Up" link
        textView_log.setOnClickListener(v -> {
            Intent intent_log = new Intent(getApplicationContext(), JobSeeker_signup.class);
            startActivity(intent_log);
        });

        // Handle login button click
        buttonlogin.setOnClickListener(v -> {
            String email = email_login.getText().toString().trim();
            String password = password_login.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase authentication
            logAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = logAuth.getCurrentUser();
                            if (user != null) {
                                checkUserRole(user.getUid()); // Validate role after login
                            }
                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle Forgot Password
        forgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = email_login.getText().toString().trim(); // Fix: Correct variable name

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(JobSeeker_login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        logAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> { // Fix: Use logAuth instead of mAuth
            if (task.isSuccessful()) {
                Toast.makeText(JobSeeker_login.this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(JobSeeker_login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Check user role after successful login
    private void checkUserRole(String userId) {
        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("Role");

                        if ("Job Seeker".equals(role)) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();  // Navigate to dashboard if the role is valid
                        } else {
                            Toast.makeText(this, "Access Denied. Only Job Seekers can log in.", Toast.LENGTH_LONG).show();
                            logAuth.signOut();  // Sign out user to prevent access
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user role.", Toast.LENGTH_SHORT).show();
                });
    }

    // Navigate to the Job Seeker Dashboard
    private void navigateToDashboard() {
        Intent intent = new Intent(getApplicationContext(), New_jobseeker_dashboard.class);
        startActivity(intent);
        finish();  // Close login activity to prevent going back to login
    }
}
