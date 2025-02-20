package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JobSeeker_signup extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, editTextUsername;
    private Button buttonSingup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database1;

    private TextView textView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.job_seeker_signup_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        database1 = FirebaseFirestore.getInstance();

        // Bind views
        editTextEmail = findViewById(R.id.email_box);
        editTextPassword = findViewById(R.id.password_box);
        editTextUsername = findViewById(R.id.username_box);
        buttonSingup = findViewById(R.id.signup_button);

        textView = findViewById(R.id.login_link);

        // Navigate to login activity if already have an account
        textView.setOnClickListener(v -> {
            intent = new Intent(this, JobSeeker_login.class);
            startActivity(intent);
            finish();
        });

        // Sign-up process
        buttonSingup.setOnClickListener(v -> {
            if (validateInputs()) {
//                Toast.makeText(this, "All good", Toast.LENGTH_SHORT).show();

                String email = Objects.requireNonNull(editTextEmail.getText()).toString();
                String password = Objects.requireNonNull(editTextPassword.getText()).toString();
                String username = Objects.requireNonNull(editTextUsername.getText()).toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {

                            if (task.isSuccessful()) {
//                                Toast.makeText(this, "now, turn to save username", Toast.LENGTH_SHORT).show();
                                handleUserSignUp(username, email);
                            } else {
                                Toast.makeText(JobSeeker_signup.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    // Method to validate inputs
    private boolean validateInputs() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String username = editTextUsername.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 3 || username.length() > 20 || !username.matches("[a-zA-Z0-9_]+")) {
            Toast.makeText(this, "Please try another Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Handle successful user sign-up
    private void handleUserSignUp(String username, String email) {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Username", username);
        userMap.put("Email", email);
        userMap.put("Role","Job Seeker");

        database1.collection("Users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Account Created.", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save username. Please try again later.", Toast.LENGTH_SHORT).show();
                });

    }


    // Navigate to the dashboard
    private void navigateToDashboard() {
        intent = new Intent(this,JobSeeker_profile.class);
        startActivity(intent);
        finish();

    }
}