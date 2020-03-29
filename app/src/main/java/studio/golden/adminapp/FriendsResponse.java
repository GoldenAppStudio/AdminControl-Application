package studio.golden.adminapp;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FriendsResponse {
    private String name;
    private String state;
    private String service;
    private String image;
    private String uid;
    private String sub;
    private String login;
    private String district;

    public FriendsResponse() {
    }

    public FriendsResponse(String name, String state, String service, String image, String uid, String district, String login, String sub) {
        this.name = name;
        this.sub = sub;
        this.state = state;
        this.service = service;
        this.image = image;
        this.district = district;
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public String getSub() {
        return sub;
    }

    public String getLogin() {
        return login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDistrict(){
        return district;
    }

    public void setDistrict(String district){
        this.district = district;
    }

}