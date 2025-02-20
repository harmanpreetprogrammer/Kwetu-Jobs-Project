package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Job_seeker_viewing_the_profile extends AppCompatActivity {

    private TextView fileNameTextView;
    private Button selectResumeButton, updateButton;
    private Uri resumeUri;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String oldResumeUrl; // Store old resume URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_seeker_viewing_the_profile_page);

        // Firebase initialization
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        fileNameTextView = findViewById(R.id.selected_file_name);
        selectResumeButton = findViewById(R.id.select_new_resume_button);
        updateButton = findViewById(R.id.update_button);


        // Load existing profile data including resume name
        loadProfileData();

        // Select new resume
        selectResumeButton.setOnClickListener(view -> selectNewResume());
        updateButton.setOnClickListener(view -> updateProfile());
    }

    private void loadProfileData() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user data from "profiles" collection
        db.collection("profiles").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve user details
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");
                        String location = documentSnapshot.getString("location");
                        String skills = documentSnapshot.getString("skills");
                        String workExperience = documentSnapshot.getString("workExperience");
                        String portfolioUrl = documentSnapshot.getString("url"); // Adjusted key
                        String resumeName = documentSnapshot.getString("resume"); // Fetching resume name
                        oldResumeUrl = documentSnapshot.getString("resumeUrl"); // Store old resume URL

                        // Set values to UI fields
                        ((TextView) findViewById(R.id.full_name)).setText(fullName != null ? fullName : "N/A");
                        ((TextView) findViewById(R.id.email)).setText(email != null ? email : "N/A");
                        ((TextView) findViewById(R.id.phone)).setText(phone != null ? phone : "N/A");
                        ((TextView) findViewById(R.id.location)).setText(location != null ? location : "N/A");
                        ((TextView) findViewById(R.id.skills)).setText(skills != null ? skills : "N/A");
                        ((TextView) findViewById(R.id.work_exp)).setText(workExperience != null ? workExperience : "N/A");
                        ((TextView) findViewById(R.id.url)).setText(portfolioUrl != null ? portfolioUrl : "N/A");

                        // Display resume file name
                        fileNameTextView.setText(resumeName != null ? resumeName : "No resume uploaded");

                    } else {
                        Toast.makeText(Job_seeker_viewing_the_profile.this, "No profile data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Job_seeker_viewing_the_profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void selectNewResume() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1);
    }

    private void uploadFile() {
        if (resumeUri != null) {
            String userId = user.getUid();
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("resumes/").child(userId + ".pdf");
            String actualFileName = getFileName(resumeUri);

            // Delete the previous resume if it exists
            if (oldResumeUrl != null && !oldResumeUrl.isEmpty()) {
                FirebaseStorage.getInstance().getReferenceFromUrl(oldResumeUrl).delete()
                        .addOnSuccessListener(aVoid -> uploadNewFile(fileRef, actualFileName))
                        .addOnFailureListener(e -> {
                            Toast.makeText(Job_seeker_viewing_the_profile.this, "Failed to delete old resume", Toast.LENGTH_SHORT).show();
                            uploadNewFile(fileRef, actualFileName); // Proceed even if deletion fails
                        });
            } else {
                uploadNewFile(fileRef, actualFileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (fileName.isEmpty()) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }

    private void uploadNewFile(StorageReference fileRef, String actualFileName) {
        fileRef.putFile(resumeUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String newResumeUrl = uri.toString();

                    db.collection("profiles").document(user.getUid())
                            .update("resume", actualFileName, "resumeUrl", newResumeUrl)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(Job_seeker_viewing_the_profile.this, "Profile and resume updated successfully", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(Job_seeker_viewing_the_profile.this, "Failed to update resume details", Toast.LENGTH_SHORT).show()
                            );
                });
            } else {
                Toast.makeText(Job_seeker_viewing_the_profile.this, "File upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            resumeUri = data.getData();
            fileNameTextView.setText(getFileName(resumeUri));
        }
    }

    private void updateProfile() {
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = ((TextView) findViewById(R.id.full_name)).getText().toString();
        String email = ((TextView) findViewById(R.id.email)).getText().toString();
        String phone = ((TextView) findViewById(R.id.phone)).getText().toString();
        String location = ((TextView) findViewById(R.id.location)).getText().toString();
        String skills = ((TextView) findViewById(R.id.skills)).getText().toString();
        String workExperience = ((TextView) findViewById(R.id.work_exp)).getText().toString();
        String portfolioUrl = ((TextView) findViewById(R.id.url)).getText().toString();

        db.collection("profiles").document(user.getUid())
                .update("fullName", fullName, "email", email, "phone", phone, "location", location, "skills", skills, "workExperience", workExperience, "url", portfolioUrl)
                .addOnSuccessListener(aVoid -> {
                    if (resumeUri != null) {
                        uploadFile();
                    } else {
                        Toast.makeText(Job_seeker_viewing_the_profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Job_seeker_viewing_the_profile.this, New_jobseeker_dashboard.class);
                        startActivity(intent);
                        Job_seeker_viewing_the_profile.this.finish();
                    }
                });
    }
}
