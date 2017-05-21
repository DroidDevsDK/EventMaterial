package com.example.androidthings.myproject;

import java.util.Date;

import io.realm.RealmObject;


/**
 * Created by kneth on 17/04/2017.
 */

public class Measurement extends RealmObject {
    private Date timestamp;
    private boolean led_state;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLed_state() {
        return led_state;
    }

    public void setLed_state(boolean led_state) {
        this.led_state = led_state;
    }
}
