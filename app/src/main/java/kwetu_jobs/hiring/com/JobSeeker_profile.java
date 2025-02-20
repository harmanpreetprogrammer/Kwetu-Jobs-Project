package kwetu_jobs.hiring.com;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import kwetu_jobs.hiring.com.JobSeeker_login;
import kwetu_jobs.hiring.com.R;
import kwetu_jobs.hiring.com.UserProfile;

public class JobSeeker_profile extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private TextView selectedFileName;
    private EditText fullNameInput, emailInput, phoneInput, locationInput, skillsInput, workExpInput, urlInput;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    Intent intent1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_seeker_profile_page);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            startActivity(new Intent(this, JobSeeker_login.class));
            finish();
            return;
        }

        selectedFileName = findViewById(R.id.selected_file_name);
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        locationInput = findViewById(R.id.location_input);
        skillsInput = findViewById(R.id.skills_input);
        workExpInput = findViewById(R.id.work_exp);
        urlInput = findViewById(R.id.Url_input);

        Button selectFileButton = findViewById(R.id.select_file_button);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        Button uploadResumeButton = findViewById(R.id.upload_resume_button);
        uploadResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            selectedFileName.setText(getFileName(fileUri)); // Set actual file name
        }
    }

    // Method to get file name from URI
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

    private void uploadFile() {
        if (fileUri != null) {
            String userId = currentUser.getUid();
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("resumes/").child(userId + ".pdf");
            fileRef.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(JobSeeker_profile.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(JobSeeker_profile.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserProfile() {
        String userId = currentUser.getUid();
        DocumentReference profileRef = firestore.collection("profiles").document(userId);

        UserProfile userProfile = new UserProfile(
                fullNameInput.getText().toString(),
                emailInput.getText().toString(),
                phoneInput.getText().toString(),
                locationInput.getText().toString(),
                skillsInput.getText().toString(),
                workExpInput.getText().toString(),
                urlInput.getText().toString(),
                fileUri != null ? getFileName(fileUri) :""
        );

        profileRef.set(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(JobSeeker_profile.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    navigateToBoard();
                } else {
                    Log.d("PROFILE_SAVE_ERROR", "Error saving profile", task.getException());
                    Toast.makeText(JobSeeker_profile.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void navigateToBoard() {
        intent1 = new Intent(this,New_jobseeker_dashboard.class);
        startActivity(intent1);
        finish();

    }
}