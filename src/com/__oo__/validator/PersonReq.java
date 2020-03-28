package com.__oo__.validator;

import com.oocourse.elevator1.PersonRequest;

public class PersonReq {
    private int id;
    private int fromFloor;
    private int toFloor;
    private boolean fromRight;
    private boolean toRight;
    private boolean finished;

    public PersonReq(PersonRequest req) {
        this.id = req.getPersonId();
        this.fromFloor = req.getFromFloor();
        this.toFloor = req.getToFloor();
        this.fromRight = false;
        this.toRight = false;
        this.finished = false;
    }

    public boolean getFromRight() {
        return this.fromRight;
    }

    public boolean getToRight() {
        return this.toRight;
    }

    public void judgeFrom(int fromFloor) {
        if(!this.finished) {
            if (this.fromFloor != fromFloor) {
                this.fromRight = false;
                return;
            }
            this.fromRight = true;
        } else {
            this.fromRight = false;
        }

    }

    public void judgeTo(int toFloor) {
        if(!this.finished) {
            if (!this.fromRight) {
                this.toRight = false;
                return;
            }
            if (this.toFloor != toFloor) {
                this.toRight = false;
                return;
            }
            this.toRight = true;
            this.finished = true;
        } else {
            this.toRight = false;
        }
    }
}
