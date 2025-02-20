package kwetu_jobs.hiring.com;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Employ_Login_page extends AppCompatActivity {
    TextInputEditText email_login_emp, password_login_emp;
    TextView textView_log_emp;
    Button buttonlogin_emp;
    FirebaseAuth logAuth_emp;

    Intent intent_log_emp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.employ_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        textView_log_emp = findViewById(R.id.signup_link_emp);

        textView_log_emp.setOnClickListener(v -> {
            intent_log_emp = new Intent(getApplicationContext(), Employ_signup_page.class);
            startActivity(intent_log_emp);
        });


        // Initialize FirebaseAuth instance
        logAuth_emp = FirebaseAuth.getInstance();

        // Binding UI elements
        email_login_emp = findViewById(R.id.email_box_log_emp);
        password_login_emp = findViewById(R.id.password_box_log_emp);
        buttonlogin_emp = findViewById(R.id.login_button_emp);
        textView_log_emp = findViewById(R.id.signup_link_emp);
        TextView forgotPassword = findViewById(R.id.fg_password);


        // Navigate to signup page when the user clicks on the "Sign Up" link
        textView_log_emp.setOnClickListener(v -> {
            Intent intent_log = new Intent(getApplicationContext(), Employ_signup_page.class);
            startActivity(intent_log);
        });

        // Handle login button click
        buttonlogin_emp.setOnClickListener(v -> {
            String email = email_login_emp.getText().toString().trim();
            String password = password_login_emp.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }


            // Firebase authentication
            logAuth_emp.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = logAuth_emp.getCurrentUser();
                            if (user != null) {
                                checkUserRole(user.getUid()); // Validate role after login
                            }
                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        forgotPassword.setOnClickListener(v -> resetPassword_emp());
    }

    private void resetPassword_emp() {
        String email = email_login_emp.getText().toString().trim(); // Fix: Correct variable name

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Employ_Login_page.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        logAuth_emp.sendPasswordResetEmail(email).addOnCompleteListener(task -> { // Fix: Use logAuth instead of mAuth
            if (task.isSuccessful()) {
                Toast.makeText(Employ_Login_page.this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Employ_Login_page.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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

                        if ("Employer".equals(role)) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();  // Navigate to dashboard if the role is valid
                        } else {
                            Toast.makeText(this, "Access Denied. Only Employers can log in.", Toast.LENGTH_LONG).show();
                            logAuth_emp.signOut();  // Sign out user to prevent access
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
        Intent intent = new Intent(getApplicationContext(), Employer_dashboard.class);
        startActivity(intent);
        finish();  // Close login activity to prevent going back to login
    }

}