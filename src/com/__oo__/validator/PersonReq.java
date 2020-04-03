package com.__oo__.validator;

import com.oocourse.elevator2.PersonRequest;

public class PersonReq {
    private int id;
    private int fromFloor;
    private int toFloor;

    private int inFloor;
    private int loc;
    private boolean ifInEle;
    private String inEle;
    private boolean judge;


    public PersonReq(int id, int fromFloor, int toFloor){
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;

        this.ifInEle = false;
        this.loc = fromFloor;
        this.judge = true;
    }

    public PersonReq(PersonRequest req) {
        this(req.getPersonId(), req.getFromFloor(), req.getToFloor());
    }

    public void in(int inFloor, String inEle) {
        if(judge) {
            if(inFloor != loc) {
                this.judge = false;
                return;
            }
            if(this.ifInEle) {
                this.judge = false;
                return;
            }
            this.ifInEle = true;
            this.inEle = inEle;
        }
    }

    public void out(int outFloor, String outEle) {
        if (judge) {
            if(!ifInEle) {
                judge = false;
                return;
            }
            if(!outEle.equals(inEle)) {
                judge = false;
                return;
            }
            loc = outFloor;
            this.ifInEle = false;
        }
    }

    public boolean isRight() {
        if(this.judge && this.loc == this.toFloor) {
            return true;
        }
        return false;
    }
}
