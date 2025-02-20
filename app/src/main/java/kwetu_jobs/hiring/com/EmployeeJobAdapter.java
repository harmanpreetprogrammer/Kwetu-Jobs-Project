package kwetu_jobs.hiring.com;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EmployeeJobAdapter extends RecyclerView.Adapter<EmployeeJobAdapter.JobViewHolder> {
    private List<Job> jobList;

    public EmployeeJobAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
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
            holder.jobCategory.setText("Category: " + job.getCategory());

            // Delete Button Click Listener
            holder.deleteJobBtn.setOnClickListener(view -> {
                String jobId = job.getId(); // Get job ID

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("jobs").document(jobId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            jobList.remove(position); // Remove from list
                            notifyItemRemoved(position); // Notify adapter
                            Toast.makeText(view.getContext(), "Job deleted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(view.getContext(), "Failed to delete job: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView title, company, location, salary, jobType, jobCategory;
        MaterialButton deleteJobBtn;

        public JobViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.jobTitle);
            company = itemView.findViewById(R.id.companyName);
            location = itemView.findViewById(R.id.jobLocation);
            salary = itemView.findViewById(R.id.jobSalary);
            jobType = itemView.findViewById(R.id.jobType);
            jobCategory = itemView.findViewById(R.id.jobCategory);
            deleteJobBtn = itemView.findViewById(R.id.deleteJobBtn); // ✅ Correctly initialized
        }
    }
}
