package studio.golden.adminapp;

public class ServiceProviderClass {
    private String name;
    private String phone;
    private String priority;

    private String uid;

    public ServiceProviderClass() {
        //empty constructor needed
    }

    public ServiceProviderClass(String name, String phone, String uid, String priority) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.priority = priority;

    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }

    public String getPriority() { return priority; }
}

