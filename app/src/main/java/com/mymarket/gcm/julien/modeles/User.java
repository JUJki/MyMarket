package com.mymarket.gcm.julien.modeles;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class User {
    private Long id;
    private String teokendevise;
    private String date;

    public User(String teokendevise) {
        this.teokendevise = teokendevise;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        this.date = formattedDate;
    }

    public User(String teokendevise, String date) {
        this.teokendevise = teokendevise;
        this.date = date;
    }

    public String getTeokendevise() {
        return teokendevise;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTeokendevise(String teokendevise) {
        this.teokendevise = teokendevise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
