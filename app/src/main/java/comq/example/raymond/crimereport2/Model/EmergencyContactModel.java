package comq.example.raymond.crimereport2.Model;

public class EmergencyContactModel {
    private String lg, district, phone;

    public EmergencyContactModel() {
    }

    public EmergencyContactModel(String lg, String district, String phone) {
        this.lg = lg;
        this.district = district;
        this.phone = phone;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
