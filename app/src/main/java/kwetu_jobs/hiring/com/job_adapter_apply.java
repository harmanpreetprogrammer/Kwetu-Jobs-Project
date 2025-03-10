package kwetu_jobs.hiring.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class job_adapter_apply extends RecyclerView.Adapter<job_adapter_apply.JobViewHolder> {

    private List<Job> jobList;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public job_adapter_apply(List<Job> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_apply_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        if (job != null) {
            holder.title.setText(job.getTitle());
            holder.company.setText(job.getCompany());
            holder.location.setText(job.getLocation());
            holder.salary.setText("Salary: $" + job.getSalary());
            holder.jobType.setText("Type: " + job.getJobType());
            holder.jobCategory.setText("Category: " + (job.getCategory() != null ? job.getCategory() : "N/A"));

            // ✅ Check if the user already applied for this job
            if (currentUser != null) {
                String userId = currentUser.getUid();
                db.collection("applications")
                        .document(job.getId()) // Job ID as document
                        .collection("appliedUsers")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                holder.applyJobBtn.setText("Applied");
                              //  holder.applyJobBtn.setEnabled(false);
                            }
                        });
            }

            // ✅ Apply Button Click Handler
            holder.applyJobBtn.setOnClickListener(v -> applyForJob(holder, job));
        }
    }

    private void applyForJob(JobViewHolder holder, Job job) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "Please log in to apply.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String jobId = job.getId();  // Ensure job ID exists

        // Create application data
        Map<String, Object> applicationData = new HashMap<>();
        applicationData.put("userId", userId);
        applicationData.put("appliedAt", System.currentTimeMillis());


        // Save in "applications" collection under job ID
        db.collection("applications")
                .document(jobId) // Job ID as document
                .collection("appliedUsers")
                .document(userId) // User ID as sub-document
                .set(applicationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Applied successfully!", Toast.LENGTH_SHORT).show();
                    holder.applyJobBtn.setText("Applied");
                  //  holder.applyJobBtn.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to apply: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return (jobList != null) ? jobList.size() : 0;
    }

    // ✅ ViewHolder Class
    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView title, company, location, salary, jobType, jobCategory;
        MaterialButton applyJobBtn;

        public JobViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.jobTitle);
            company = itemView.findViewById(R.id.companyName);
            location = itemView.findViewById(R.id.jobLocation);
            salary = itemView.findViewById(R.id.jobSalary);
            jobType = itemView.findViewById(R.id.jobType);
            jobCategory = itemView.findViewById(R.id.jobCategory);
            applyJobBtn = itemView.findViewById(R.id.applyJobBtn); // Apply Button
        }
    }
}
