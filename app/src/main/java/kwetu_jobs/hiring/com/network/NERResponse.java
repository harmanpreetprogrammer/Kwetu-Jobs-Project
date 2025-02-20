package kwetu_jobs.hiring.com.network;
import java.util.List;

public class NERResponse {
    private List<Entity> entities;

    public List<Entity> getEntities() {
        return entities;
    }

    public static class Entity {
        private String text;
        private String type;

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return text + " (" + (type != null ? type : "Unknown") + ")";
        }
    }

    @Override
    public String toString() {
        return "NERResponse{" +
                "entities=" + entities +
                '}';
    }
}
