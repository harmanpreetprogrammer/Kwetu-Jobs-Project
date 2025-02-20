package kwetu_jobs.hiring.com;

    public class UserProfile {
        private String fullName;
        private String email;
        private String phone;
        private String location;
        private String skills;
        private String workExperience;
        private String url;
        private String resume;

        // Constructor
        public UserProfile(String fullName, String email, String phone, String location,
                           String skills, String workExperience, String url, String resume) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.location = location;
            this.skills = skills;
            this.workExperience = workExperience;
            this.url = url;
            this.resume=resume;
        }

        // Getters and Setters
        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSkills() {
            return skills;
        }

        public void setSkills(String skills) {
            this.skills = skills;
        }

        public String getWorkExperience() {
            return workExperience;
        }

        public void setWorkExperience(String workExperience) {
            this.workExperience = workExperience;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public String getResume() {
            return resume;
        }

        public void setResume(String resume) {
            this.resume = resume;
        }
    }


