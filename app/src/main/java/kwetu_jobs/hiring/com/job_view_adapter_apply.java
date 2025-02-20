package kwetu_jobs.hiring.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class job_view_adapter_apply extends RecyclerView.Adapter<job_view_adapter_apply.JobViewHolder> {
    private List<Job> jobList;
    private Context context;

    // Constructor
    public job_view_adapter_apply(List<Job> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_viewing_applied_jobs_page, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.companyName.setText(job.getCompany());
        holder.jobLocation.setText(job.getLocation());
        holder.jobSalary.setText("Salary: " + job.getSalary());
        holder.jobType.setText("Type: " + job.getJobType());
        holder.jobCategory.setText("Category: " + job.getCategory());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, companyName, jobLocation, jobSalary, jobType, jobCategory;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            companyName = itemView.findViewById(R.id.companyName);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            jobType = itemView.findViewById(R.id.jobType);
            jobCategory = itemView.findViewById(R.id.jobCategory);
        }
    }
}
