package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Viewing_employer_profile extends AppCompatActivity {

    private Button updateButton;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private TextView fullNameView, compNameView, compLocationView, compDescView, UrlView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewing_employer_profile_page);

        // Initialize Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        fullNameView = findViewById(R.id.full_name_view);
        compNameView = findViewById(R.id.comp_name_view);
        compLocationView = findViewById(R.id.comp_location_view);
        compDescView = findViewById(R.id.company_desc_view);
        UrlView = findViewById(R.id.company_url_view);
        updateButton = findViewById(R.id.update_button_view);

        // Load existing profile data
        loadProfileData();

        updateButton.setOnClickListener(view -> updateProfile());
    }

    private void loadProfileData() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user data from "employer_profile" collection
        db.collection("employer_profile").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve user details
                        String person_name = documentSnapshot.getString("full_name");
                        String company_name = documentSnapshot.getString("company_name");
                        String company_location = documentSnapshot.getString("com_location");
                        String company_descp = documentSnapshot.getString("com_desc");
                        String company_logo = documentSnapshot.getString("com_logo");

                        // Set values to UI fields
                        fullNameView.setText(person_name != null ? person_name : "N/A");
                        compNameView.setText(company_name != null ? company_name : "N/A");
                        compLocationView.setText(company_location != null ? company_location : "N/A");
                        compDescView.setText(company_descp != null ? company_descp : "N/A");
                        UrlView.setText(company_logo != null ? company_logo : "N/A");

                    } else {
                        Toast.makeText(Viewing_employer_profile.this, "No profile data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Viewing_employer_profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateProfile() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get updated data from TextViews
        String fullName_v = fullNameView.getText().toString();
        String compName_v = compNameView.getText().toString();
        String compLocation_v = compLocationView.getText().toString();
        String compDesc_v = compDescView.getText().toString();
        String comUrl_v = UrlView.getText().toString();

        // Create a map to update Firestore
        Map<String, Object> profileUpdates = new HashMap<>();
        profileUpdates.put("full_name", fullName_v);
        profileUpdates.put("company_name", compName_v);
        profileUpdates.put("phone", compLocation_v);
        profileUpdates.put("com_location", compDesc_v);
        profileUpdates.put("skills", comUrl_v);

        // Update Firestore document
        // Update Firestore document
        db.collection("employer_profile").document(user.getUid())
                .update(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Viewing_employer_profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Viewing_employer_profile.this, Employer_dashboard.class);
                    startActivity(intent);
                    Viewing_employer_profile.this.finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Viewing_employer_profile.this, "Update failed", Toast.LENGTH_SHORT).show());
    }
    }




