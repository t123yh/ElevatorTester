package com.__oo__.validator;

import com.oocourse.elevator1.PersonRequest;

import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class Validator {
    public static final int DoorClose = 1;

    public static final int DoorOpen = 2;

    public static boolean validate(List<PersonRequest> requests, String[] logLines) {
        TreeMap<Integer, PersonReq> personReqs = new TreeMap<>();
        boolean judge;
        double time = 0;
        boolean begin = false;

        //get_id_list
        for (PersonRequest req : requests) {
            PersonReq pr = new PersonReq(req);
            personReqs.put(req.getPersonId(), pr);
        }

        //judge_begin
        Elevator elevator = new Elevator();
        judge = true;
        for (String str : logLines) {
            String temp = CopeString(str);
            String[] op = temp.split("-");
            double getTime = Double.parseDouble(op[0]);
            if (!begin) {
                time = getTime;
            }
            String move = op[1];
            if (move.equals("ARRIVE")) {
                int arriveFloor = Integer.parseInt(op[2]);
                //time
                if(getTime - time < 0.399999 && begin) {
                    judge = false;
                }
                if (elevator.getState() == DoorOpen) {
                    judge = false;
                } else {
                    if (elevator.getLoc() - arriveFloor == 1 || elevator.getLoc() - arriveFloor == -1) {
                        elevator.setLoc(arriveFloor);
                    } else {
                        judge = false;
                    }
                }
                time = getTime;
            } else if (move.equals("IN")) {
                int id = Integer.parseInt(op[2]);
                int fromFloor = Integer.parseInt(op[3]);
                //time
                if(getTime < time) {
                    judge = false;
                }
                //open in wrong floor
                if (fromFloor != elevator.getLoc()) {
                    judge = false;
                }
                //the door is closed but someone come in.
                if (elevator.getState() == DoorClose) {
                    judge = false;
                }
                //judge if fromfloor is right
                if (personReqs.containsKey(id)) {
                    personReqs.get(id).judgeFrom(fromFloor);
                } else {//wrong id
                    judge = false;
                }
            } else if (move.equals("OUT")) {
                int id = Integer.parseInt(op[2]);
                int toFloor = Integer.parseInt(op[3]);
                //time
                if(getTime < time) {
                    judge = false;
                }
                //close in wrong floor
                if (toFloor != elevator.getLoc()) {
                    judge = false;
                }
                //the door is closed but someone go out.
                if (elevator.getState() == DoorClose) {
                    judge = false;
                }
                //judge if tofloor is right
                if (personReqs.containsKey(id)) {
                    personReqs.get(id).judgeTo(toFloor);
                } else {//wrong id
                    judge = false;
                }
            } else if (move.equals("OPEN")) {
                int openFloor = Integer.parseInt(op[2]);
                //time
                if(getTime < time) {
                    judge = false;
                }
                if (openFloor != elevator.getLoc()) {
                    judge = false;
                }
                if (elevator.getState() != DoorClose) {
                    judge = false;
                }
                elevator.changeState();
                time = getTime;
            } else if (move.equals("CLOSE")) {
                int closeFloor = Integer.parseInt(op[2]);
                if(getTime - time < 0.399999) {
                    judge = false;
                }
                if (closeFloor != elevator.getLoc()) {
                    judge = false;
                }
                if (elevator.getState() != DoorOpen) {
                    judge = false;
                }
                elevator.changeState();
                time = getTime;
            } else {
                judge = false;
            }
            if(!begin) {
                begin = true;
            }
        }

        if (judge == false) {
            return false;
        }
        for (PersonReq pr : personReqs.values()) {
            if (!pr.getFromRight() || !pr.getToRight()) {
                judge = false;
            }
        }
        return judge;
    }

    public static String CopeString(String str) {
        String temp = str;
        String temp1 = temp.replace("[","");
        String temp2 = temp1.replace("]","-");
        String temp3 = temp2.replace(" ","");
        return temp3;
    }

}
