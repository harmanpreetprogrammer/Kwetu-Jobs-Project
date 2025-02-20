package kwetu_jobs.hiring.com;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Employer_dashboard_post_jobs extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextInputEditText jobTitle, jobDescription, jobSalary, jobLocation, companyName;
    private MultiAutoCompleteTextView skillsRequired;
    private Spinner jobTypeSpinner, jobCategorySpinner;
    private Button submitJobBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employer_dashboard_post_jobs_fg, container, false);

        // Initialize Firestore & Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        jobTitle = view.findViewById(R.id.jobTitle);
        jobDescription = view.findViewById(R.id.jobDescription);
        jobSalary = view.findViewById(R.id.jobSalary);
        jobLocation = view.findViewById(R.id.jobLocation);
        companyName = view.findViewById(R.id.companyName);
        skillsRequired = view.findViewById(R.id.skillsRequired);
        jobTypeSpinner = view.findViewById(R.id.jobTypeSpinner);
        jobCategorySpinner = view.findViewById(R.id.jobCategorySpinner);
        submitJobBtn = view.findViewById(R.id.submitJobBtn);

        // Set Up Job Type Spinner
        String[] jobTypes = {"Full-Time", "Part-Time", "Contract", "Internship"};
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, jobTypes);
        jobTypeSpinner.setAdapter(jobTypeAdapter);

        // Set Up Job Category Spinner
        String[] jobCategories = {"IT & Software", "Finance", "Marketing", "Healthcare", "Engineering"};
        ArrayAdapter<String> jobCategoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, jobCategories);
        jobCategorySpinner.setAdapter(jobCategoryAdapter);

        // Set click listener on the submit button
        submitJobBtn.setOnClickListener(v -> postJobToFirebase());

        return view;
    }

    private void postJobToFirebase() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get logged-in employer ID
        String employerId = user.getUid();

        // Get data from input fields
        String title = jobTitle.getText().toString().trim();
        String description = jobDescription.getText().toString().trim();
        String location = jobLocation.getText().toString().trim();
        String company = companyName.getText().toString().trim();
        String skills = skillsRequired.getText().toString().trim();
        String jobType = jobTypeSpinner.getSelectedItem().toString();
        String jobCategory = jobCategorySpinner.getSelectedItem().toString();

        double salary = 0.0; // Default value
        String salaryInput = jobSalary.getText().toString().trim();
        if (!salaryInput.isEmpty()) {
            try {
                salary = Double.parseDouble(salaryInput);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid salary format!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate input
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location)) {
            Toast.makeText(getContext(), "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a job data map
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("title", title);
        jobData.put("description", description);
        jobData.put("salary", salary);
        jobData.put("location", location);
        jobData.put("company", company);
        jobData.put("skillsRequired", skills);
        jobData.put("jobType", jobType);
        jobData.put("jobCategory", jobCategory);
        jobData.put("employerId", employerId);

        // Save data to Firestore
        db.collection("jobs")
                .add(jobData)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Job posted successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error posting job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
