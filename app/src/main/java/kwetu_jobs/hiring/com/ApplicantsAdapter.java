package kwetu_jobs.hiring.com;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.ViewHolder> {

    private List<Applicant> applicantsList;

    public ApplicantsAdapter(List<Applicant> applicantsList) {
        this.applicantsList = (applicantsList != null) ? applicantsList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applicant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Applicant applicant = applicantsList.get(position);
        holder.fullName.setText("Name: " + applicant.getUsername()); // fullName from Firestore
        holder.workExperience.setText("Experience: " + applicant.getExperience());

        String url = applicant.getres_url_dl();
        if (!url.equals("Not Provided")) {
            holder.resume_id.setText(android.text.Html.fromHtml("<a href=\"" + url + "\">Download Resume</a>"));
        } else {
            holder.resume_id.setText("Resume URL: Not Provided");
        }
    }

    @Override
    public int getItemCount() {
        return applicantsList.size();
    }

    // Method to update applicants dynamically
    public void updateApplicants(List<Applicant> newApplicants) {
        this.applicantsList = newApplicants;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, workExperience, resume_id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName); // Updated ID
            workExperience = itemView.findViewById(R.id.workExperience); // Updated ID
            resume_id = itemView.findViewById(R.id.resume_url_dl);

            resume_id.setClickable(true);
            resume_id.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        }
    }
}
