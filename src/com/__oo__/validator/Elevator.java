package com.__oo__.validator;


public class Elevator {
    private int loc;//-3 - -1 1 - 15
    private int state;
    private String id;
    private String type;
    private double time;
    private boolean start;
    private double movePerF;
    private int maxNumIn;
    private int count;
    private boolean judge;
    private double startTime;

    public static final int DoorClose = 1;
    public static final int DoorOpen = 2;

    public Elevator(String id, String type, double startTime) {
        this.loc = 1;
        this.state = DoorClose;
        this.id = id;
        this.time = 0;
        this.start = false;
        this.type = type;
        this.count = 0;
        this.judge = true;
        this.startTime = startTime;
        if(type.equals("A")) {
            this.movePerF = 0.4;
            this.maxNumIn = 6;
        } else if(type.equals("B")) {
            this.movePerF = 0.5;
            this.maxNumIn = 8;
        } else {
            this.movePerF = 0.6;
            this.maxNumIn = 7;
        }
    }

    public boolean canOpen(int num) {
        if(type.equals("A")) {
            if(num > 1 && num < 15) {
                return false;
            }
            return true;
        } else if(type.equals("B")) {
            if(num >= -2 && num <= 15 && num != 3) {
                return true;
            }
            return false;
        } else {
            if(num >= 1 && num <= 15 && num % 2 == 1) {
                return true;
            }
            return false;
        }
    }

    public double getSpeed() {
        return this.movePerF;
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

    public void inPerson() {
        this.count++;
        if(this.count > maxNumIn) {
            this.judge = false;
        }
    }

    public void outPerson() {
        this.count--;
        if(this.count < 0) {
            judge = false;
        }
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
