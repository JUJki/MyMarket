package com.mymarket.gcm.julien.modeles;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by julien on 24/06/2016.
 */
public class Course {
    private Long id;
    private String name;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Course(String name) {
        this.name = name;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        this.date = formattedDate;

    }

    public Course(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
