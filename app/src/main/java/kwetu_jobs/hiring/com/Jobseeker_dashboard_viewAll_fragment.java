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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Jobseeker_dashboard_viewAll_fragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private job_view_adapter_apply jobAdapter1;
    private List<Job> jobList;

    public Jobseeker_dashboard_viewAll_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jobseeker_dashboard_view_all_fragment, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Find RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAppliedJobs);

        // Set up RecyclerView
        jobList = new ArrayList<>();
        jobAdapter1 = new job_view_adapter_apply(jobList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(jobAdapter1);

        // Load applied jobs
        loadAppliedJobs();

        return view;
    }

    private void loadAppliedJobs() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e("Auth", "User is null. Not authenticated.");
            return;
        }

        Log.d("Firestore", "Fetching applied jobs for user ID: " + userId);

        db.collectionGroup("appliedUsers")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getActivity(), "No applied jobs found", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "No applied jobs found for user: " + userId);
                    } else {
                        for (QueryDocumentSnapshot appliedJobDoc : queryDocumentSnapshots) {
                            String jobId = appliedJobDoc.getReference().getParent().getParent().getId();
                            Log.d("Firestore", "Found applied job ID: " + jobId);

                            db.collection("jobs")
                                    .document(jobId)
                                    .get()
                                    .addOnSuccessListener(jobDoc -> {
                                        if (jobDoc.exists()) {
                                            Job job = jobDoc.toObject(Job.class);
                                            if (job != null) {
                                                job.setId(jobId);
                                                jobList.add(job);
                                                jobAdapter1.notifyDataSetChanged();
                                                Log.d("Firestore", "Loaded job: " + job.getTitle());
                                            }
                                        } else {
                                            Log.e("Firestore", "Job document does not exist: " + jobId);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error loading job details", e));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load applied jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading applied jobs: ", e);
                });
    }


}
