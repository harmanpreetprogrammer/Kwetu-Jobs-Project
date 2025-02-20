package kwetu_jobs.hiring.com;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Employer_dashboard_screening extends Fragment {
    private RecyclerView recyclerView;
    private ApplicantsAdapter applicantsAdapter;
    private List<Applicant> applicantsList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context; // Store context for Toast messages
    private boolean anyApplicantsFound = false; // Track if any applicants exist

    public Employer_dashboard_screening() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.employer_dashboard_screening_fg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Store the context
        context = requireContext();

        recyclerView = view.findViewById(R.id.recyclerViewApplicants);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        applicantsList = new ArrayList<>();
        applicantsAdapter = new ApplicantsAdapter(applicantsList);
        recyclerView.setAdapter(applicantsAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadApplicants();
    }

    private void loadApplicants() {
        String currentEmployerId = auth.getCurrentUser().getUid(); // Get logged-in employer ID

        db.collection("jobs")
                .whereEqualTo("employerId", currentEmployerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot jobDoc : queryDocumentSnapshots.getDocuments()) {
                            String jobId = jobDoc.getId(); // Get jobId

                            // Now fetch applicants for this job
                            fetchApplicants(jobId);
                        }
                    } else {
                        Toast.makeText(context, "No jobs found for this employer.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error fetching jobs!", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error fetching employer jobs", e);
                });
    }

    private void fetchApplicants(String jobId) {
        db.collection("applications").document(jobId).collection("appliedUsers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        return; // Do nothing, avoid multiple toasts
                    }

                    if (!anyApplicantsFound) {
                        applicantsList.clear(); // Clear only once before adding new applicants
                        anyApplicantsFound = true;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String userId = document.getId(); // Get user ID

                        // Fetch user details from profiles collection
                        db.collection("profiles").document(userId)
                                .get()
                                .addOnSuccessListener(profileDoc -> {
                                    if (profileDoc.exists()) {
                                        String username = profileDoc.getString("fullName");
                                        String experience = profileDoc.getString("workExperience");
                                        String urlDwld = profileDoc.getString("url_dwld");

                                        // Handle null values
                                        username = (username != null) ? username : "Unknown";
                                        experience = (experience != null) ? experience : "Not Provided";
                                        urlDwld = (urlDwld != null) ? urlDwld : "Not Provided";


                                        Applicant applicant = new Applicant(userId, username, experience,urlDwld);
                                        applicantsList.add(applicant);
                                        applicantsAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e("Firestore", "Profile not found for user ID: " + userId);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching profile", e));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error fetching applicants!", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error fetching applicants", e);
                });
    }
}
