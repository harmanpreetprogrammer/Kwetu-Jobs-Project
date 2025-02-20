package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Employer_create_profile extends AppCompatActivity {

    private EditText fullName_pf, comp_name, comp_location, comp_desc, comp_logo;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employer_create_profile_page); // Ensure you are using the correct layout

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            startActivity(new Intent(this, JobSeeker_login.class));
            finish();
            return;
        }

        fullName_pf = findViewById(R.id.full_name_com);
        comp_name = findViewById(R.id.company_name);
        comp_desc = findViewById(R.id.comp_desc);
        comp_location = findViewById(R.id.com_location);
        comp_logo = findViewById(R.id.Url_company);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String userId = currentUser.getUid();
        DocumentReference profileRef = firestore.collection("employer_profile").document(userId);

        // Creating a HashMap for the employer profile
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("full_name", fullName_pf.getText().toString());
        userProfile.put("company_name", comp_name.getText().toString());
        userProfile.put("com_location", comp_location.getText().toString());
        userProfile.put("com_desc", comp_desc.getText().toString());
        userProfile.put("com_logo", comp_logo.getText().toString());

        profileRef.set(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Employer_create_profile.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    navigateToBoard();
                } else {
                    Log.e("PROFILE_SAVE_ERROR", "Error saving profile", task.getException());
                    Toast.makeText(Employer_create_profile.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToBoard() {
        intent1 = new Intent(this, Employer_dashboard.class);
        startActivity(intent1);
        finish();
    }
}
