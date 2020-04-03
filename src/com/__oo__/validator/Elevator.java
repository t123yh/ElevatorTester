package com.__oo__.validator;


public class Elevator {
    private int loc;//-3 - -1 1 - 15
    private int state;
    private String id;
    private double time;
    private boolean start;

    public static final int DoorClose = 1;
    public static final int DoorOpen = 2;

    public Elevator(String id) {
        this.loc = 1;
        this.state = DoorClose;
        this.id = id;
        this.time = 0;
        this.start = false;
    }

    public String getId() {
        return this.id;
    }
    public int getState() {
        return this.state;
    }

    public int getLoc() {
        return this.loc;
    }

    public double getTime() {
        return this.time;
    }

    public boolean ifStart() {
        return this.start;
    }

    public void start() {
        this.start = true;
    }
    public void setTime(double time) {
        this.time = time;
    }



    public void setLoc(int xxx) {
        this.loc = xxx;
    }

    public void changeState() {
        if(this.state == DoorClose) {
            this.state = DoorOpen;
        } else {
            this.state = DoorClose;
        }
    }
}
