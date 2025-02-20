package kwetu_jobs.hiring.com;

public class Applicant {

    private String id; // User ID from Firestore
    private String username; // fullName from Firestore
    private String experience; // workExperience from Firestore
    private String res_url_dl;

    // Constructor
    public Applicant(String id, String username, String experience, String res_url_dl) {
        this.id = id;
        this.username = username;
        this.experience = experience;
        this.res_url_dl=res_url_dl;
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

    // Getter methods
    public String getres_url_dl() {
        return res_url_dl;
    }

    // Setter methods if needed
    public void setres_url_dl(String id) {
        this.res_url_dl = res_url_dl;
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
