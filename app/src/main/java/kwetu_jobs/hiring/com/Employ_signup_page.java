package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Employ_signup_page extends AppCompatActivity {
    private TextInputEditText email_emp, password_emp, username_emp;
    private Button buttonSingup_emp;
    private FirebaseAuth mAuth_emp;
    private FirebaseFirestore database_emp;

    private TextView textView_emp;
    Intent intent_emp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.employ_signup_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth and Firestore
        mAuth_emp = FirebaseAuth.getInstance();
        database_emp = FirebaseFirestore.getInstance();

        // Bind views
        email_emp = findViewById(R.id.emply_email_box);
        password_emp = findViewById(R.id.emply_password_box);
        username_emp = findViewById(R.id.emply_username_box);
        buttonSingup_emp = findViewById(R.id.emply_signup_button);

        textView_emp = findViewById(R.id.emply_login_link);

        // Navigate to login activity if already have an account
        textView_emp.setOnClickListener(v -> {
            intent_emp = new Intent(this, Employ_Login_page.class);
            startActivity(intent_emp);
            finish();
        });

        // Sign-up process
        buttonSingup_emp.setOnClickListener(v -> {
            if (validateInputs()) {
//                Toast.makeText(this, "All good", Toast.LENGTH_SHORT).show();

                String email = Objects.requireNonNull(email_emp.getText()).toString();
                String password = Objects.requireNonNull(password_emp.getText()).toString();
                String username = Objects.requireNonNull(username_emp.getText()).toString();

                mAuth_emp.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {

                            if (task.isSuccessful()) {
//                                Toast.makeText(this, "now, turn to save username", Toast.LENGTH_SHORT).show();
                                handleUserSignUp(username, email);
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(Employ_signup_page.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

//                                Toast.makeText(Employ_signup_page.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    // Method to validate inputs
    private boolean validateInputs() {
        String email = email_emp.getText().toString();
        String password = password_emp.getText().toString();
        String username = username_emp.getText().toString();

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
        String userId = Objects.requireNonNull(mAuth_emp.getCurrentUser()).getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Username", username);
        userMap.put("Email", email);
        userMap.put("Role","Employer");

        database_emp.collection("Users").document(userId)
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
        intent_emp = new Intent(this,Employer_create_profile.class);
        startActivity(intent_emp);

        finish();

    }
}