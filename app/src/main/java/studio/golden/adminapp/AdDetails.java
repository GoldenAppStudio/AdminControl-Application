package studio.golden.adminapp;

public class AdDetails {
    private String name;
    private Long priority;


    private String uid;

    public AdDetails() {
        //empty constructor needed
    }

    public AdDetails(String name, Long priority, String uid) {
        this.name = name;
        this.priority = priority;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public Long getPriority() {
        return priority;
    }

}


