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

//        if (getArguments() != null) {
            String jobId = getArguments().getString("jobId");
            if (jobId != null) {
                Toast.makeText(context, "Loading applicants for job ID: " + jobId, Toast.LENGTH_SHORT).show();
                loadApplicants(jobId);
            } else {
                Toast.makeText(context, "Error: Job ID is null!", Toast.LENGTH_SHORT).show();
                Log.e("EmployerDashboard", "Job ID is null");
            }
//        } else {
//            Toast.makeText(context, "Error: No arguments received!", Toast.LENGTH_SHORT).show();
//        }
    }


    private void loadApplicants(String jobId) {
        db.collection("applications").document(jobId).collection("appliedUsers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(context, "No applicants found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    applicantsList.clear(); // Clear the list before adding new data

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String userId = document.getId(); // Get user ID

                        // Fetch user details from the profiles collection
                        db.collection("profiles").document(userId)
                                .get()
                                .addOnSuccessListener(profileDoc -> {
                                    if (profileDoc.exists()) {
                                        String username = profileDoc.getString("fullName");
                                        String experience = profileDoc.getString("workExperience");

                                        if (username != null && experience != null) {
                                            Applicant applicant = new Applicant(username, experience,userId);
                                            applicantsList.add(applicant);
                                            applicantsAdapter.notifyDataSetChanged();
                                        }
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
