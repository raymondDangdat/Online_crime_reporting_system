package comq.example.raymond.crimereport2.Model;

public class ApproveModel {
    private String name, phone,lg, profilePic, status, email, img_id, address, occupation;

    public ApproveModel() {
    }

    public ApproveModel(String name, String phone, String lg, String profilePic, String status, String
            email, String img_id, String address, String occupation) {
        this.name = name;
        this.phone = phone;
        this.lg = lg;
        this.profilePic = profilePic;
        this.status = status;
        this.email = email;
        this.img_id = img_id;
        this.address = address;
        this.occupation = occupation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
