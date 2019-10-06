package comq.example.raymond.crimereport2.Model;

public class FeedbackModel {
    private String feedback;
    private String crimeId;
    private long dateGiven;


    public FeedbackModel() {
    }

    public FeedbackModel(String feedback, String crimeId, long dateGiven) {
        this.feedback = feedback;
        this.crimeId = crimeId;
        this.dateGiven = dateGiven;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCrimeId() {
        return crimeId;
    }

    public void setCrimeId(String crimeId) {
        this.crimeId = crimeId;
    }

    public long getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(long dateGiven) {
        this.dateGiven = dateGiven;
    }
}
