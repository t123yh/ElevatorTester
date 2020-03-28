package com.__oo__.validator;

import com.oocourse.elevator1.PersonRequest;

public class PersonReq {
    private int id;
    private int fromFloor;
    private int toFloor;
    private boolean fromRight;
    private boolean toRight;

    public PersonReq(PersonRequest req) {
        this.id = req.getPersonId();
        this.fromFloor = req.getFromFloor();
        this.toFloor = req.getToFloor();
        this.fromRight = false;
        this.toRight = false;
    }

    public boolean getFromRight() {
        return this.fromRight;
    }

    public boolean getToRight() {
        return this.toRight;
    }

    public void judgeFrom(int fromFloor) {
        if (this.fromFloor != fromFloor) {
            this.fromRight = false;
            return;
        }
        this.fromRight = true;
    }

    public void judgeTo(int toFloor) {
        if (this.toFloor != toFloor) {
            this.toRight = false;
            return;
        }
        this.toRight = true;
    }
}
