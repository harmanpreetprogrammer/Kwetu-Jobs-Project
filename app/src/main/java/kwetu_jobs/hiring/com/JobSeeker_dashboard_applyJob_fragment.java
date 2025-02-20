package kwetu_jobs.hiring.com;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class JobSeeker_dashboard_applyJob_fragment extends Fragment {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private job_adapter_apply jobAdapter;
    private FirebaseAuth logAuth_emp1;
    private FirebaseUser user;
    private List<Job> jobList;  // ✅ Ensure proper initialization

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_seeker_dashboard_applyjob_fragment, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Auth and get logged-in user
        logAuth_emp1 = FirebaseAuth.getInstance();
        user = logAuth_emp1.getCurrentUser();

        // Find RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAllJobs);

        // Initialize jobList BEFORE using it in adapter
        jobList = new ArrayList<>();  // ✅ Ensuring proper initialization

        // Initialize adapter AFTER jobList is initialized
        jobAdapter = new job_adapter_apply(jobList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(jobAdapter);

        // Load all jobs
        if (user != null) {
            loadAllJobs();
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    // ✅ Method to fetch all jobs from Firestore
    private void loadAllJobs() {
        db.collection("applications")
                .whereEqualTo("userID", user.getUid()) // Fetch jobs applied by the logged-in user
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getActivity(), "No applied jobs found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Job job = document.toObject(Job.class);
                            job.setId(document.getId()); // Set job ID
                            job.setUserID(user.getUid()); // ✅ Store the logged-in user's ID in the Job object
                            jobList.add(job);
                        }
                        jobAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load applied jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading applied jobs: ", e);
                });
    }
}
