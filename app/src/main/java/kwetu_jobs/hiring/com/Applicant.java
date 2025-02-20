package kwetu_jobs.hiring.com;

public class Applicant {

    private String id; // User ID from Firestore
    private String username; // fullName from Firestore
    private String experience; // workExperience from Firestore

    // Constructor
    public Applicant(String id, String username, String experience) {
        this.id = id;
        this.username = username;
        this.experience = experience;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getExperience() {
        return experience;
    }

    // Setter methods if needed
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
