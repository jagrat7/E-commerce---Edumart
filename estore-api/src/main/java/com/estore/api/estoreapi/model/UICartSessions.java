package com.estore.api.estoreapi.model;

public class UICartSessions {
    private int time;
    private String name;
    private String status;

    public UICartSessions(int time, String name, String status) {
        this.time = time;
        this.name = name;
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public Object getStatus() {
        return status;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String status) {
        this.status = status;
    }

}
