package com.__oo__.validator;

public class Elevator {
    private int loc;//1-15
    private int state;

    public static final int DoorClose = 1;
    public static final int DoorOpen = 2;

    public Elevator() {
        this.loc = 1;
        this.state = DoorClose;
    }

    public int getState() {
        return this.state;
    }
    public int getLoc() {
        return this.loc;
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
