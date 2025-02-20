package kwetu_jobs.hiring.com;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Jobseeker_dashboard_applying_jobs extends Fragment  {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private job_adapter_apply jobAdapter;
    private List<Job> jobList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_seeker_dashboard_applyjob_fragment, container, false);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Find RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAllJobs);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        jobList = new ArrayList<>();
        jobAdapter = new job_adapter_apply(jobList, getContext());  // Pass listener
        recyclerView.setAdapter(jobAdapter);

        // Load all jobs
        loadAllJobs();

        return view;
    }

    private void loadAllJobs() {
        if (user == null) {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("jobs") // ✅ Fetch all jobs posted by all employers
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear(); // ✅ Ensure list is cleared before adding new jobs
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getActivity(), "No jobs found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) { // ✅ Iterate properly
                            Job job = document.toObject(Job.class);
                            if (job != null) {
                                job.setId(document.getId()); // ✅ Set job ID correctly
                                jobList.add(job);
                            }
                        }
                        jobAdapter.notifyDataSetChanged(); // ✅ Refresh RecyclerView
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
