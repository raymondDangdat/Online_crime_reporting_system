package comq.example.raymond.crimereport2.Model;

public class ReportModel {
    private String crimeType;
    private String crimeDescription;
    private String crimeLocation;
    private String crimeScene;
    private String reporterId;
    private String reporterName;
    private String reporterPhone;
    private String reporterProfile;
    private long reportDate;


    public ReportModel() {
    }

    public ReportModel(String crimeType, String crimeDescription, String crimeLocation, String crimeScene, String reporterId, String reporterName,
                       String reporterPhone, String reporterProfile, long reportDate) {
        this.crimeType = crimeType;
        this.crimeDescription = crimeDescription;
        this.crimeLocation = crimeLocation;
        this.crimeScene = crimeScene;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reporterPhone = reporterPhone;
        this.reporterProfile = reporterProfile;
        this.reportDate = reportDate;
    }

    public String getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public String getCrimeDescription() {
        return crimeDescription;
    }

    public void setCrimeDescription(String crimeDescription) {
        this.crimeDescription = crimeDescription;
    }

    public String getCrimeLocation() {
        return crimeLocation;
    }

    public void setCrimeLocation(String crimeLocation) {
        this.crimeLocation = crimeLocation;
    }

    public String getCrimeScene() {
        return crimeScene;
    }

    public void setCrimeScene(String crimeScene) {
        this.crimeScene = crimeScene;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public void setReporterPhone(String reporterPhone) {
        this.reporterPhone = reporterPhone;
    }

    public String getReporterProfile() {
        return reporterProfile;
    }

    public void setReporterProfile(String reporterProfile) {
        this.reporterProfile = reporterProfile;
    }

    public long getReportDate() {
        return reportDate;
    }

    public void setReportDate(long reportDate) {
        this.reportDate = reportDate;
    }
}
