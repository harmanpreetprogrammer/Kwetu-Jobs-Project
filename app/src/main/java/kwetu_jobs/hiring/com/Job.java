package kwetu_jobs.hiring.com;
// for getting and setting the job information which will display all the jobs posted by the employer to the job seeker

public class Job {
    private String id;
    private String title;
    private String company;
    private String location;
    private String jobType;
    private double salary;
    private String Category;
    // Empty Constructor (Required for Firebase)
    public Job() {
    }

    public Job(String id, String title, String company, String location, String jobType, double salary, String Category) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.jobType = jobType;
        this.salary = salary;
        this.Category = Category;


    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getCategory() { return Category; } // ✅ Fixed return type (String)
    public void setCategory(String Category) { this.Category = Category; }
}
