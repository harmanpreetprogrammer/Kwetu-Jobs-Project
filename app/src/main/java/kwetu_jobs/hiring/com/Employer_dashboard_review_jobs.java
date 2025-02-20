package kwetu_jobs.hiring.com;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class Employer_dashboard_review_jobs extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser log_user_emp;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private EmployeeJobAdapter jobAdapter;
    private List<Job> jobList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employer_dashboard_review_jobs_fg, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        log_user_emp = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // ✅ Find RecyclerView AFTER inflating the view
        recyclerView = view.findViewById(R.id.recyclerViewPostedJobs);

        // ✅ Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        jobList = new ArrayList<>();
        jobAdapter = new EmployeeJobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        // Load employer's jobs
        loadEmployerJobs();

        return view;
    }


    // ✅ Method to fetch employer's jobs from Firestore
    private void loadEmployerJobs() {
        if (log_user_emp == null) {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String employerId = log_user_emp.getUid();

        db.collection("jobs")
                .whereEqualTo("employerId", employerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getActivity(), "No jobs found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Job job = document.toObject(Job.class);
                            job.setId(document.getId());
                            jobList.add(job);
                        }
                        jobAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
