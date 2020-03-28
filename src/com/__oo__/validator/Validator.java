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
            String move = op[0];
            if (move.equals("ARRIVE")) {
                int arriveFloor = Integer.parseInt(op[1]);
                if (elevator.getState() == DoorOpen) {
                    judge = false;
                } else {
                    if (elevator.getLoc() - arriveFloor == 1 || elevator.getLoc() - arriveFloor == -1) {
                        elevator.setLoc(arriveFloor);
                    } else {
                        judge = false;
                    }
                }
            } else if (move.equals("IN")) {
                int id = Integer.parseInt(op[1]);
                int fromFloor = Integer.parseInt(op[2]);
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
                int id = Integer.parseInt(op[1]);
                int toFloor = Integer.parseInt(op[2]);
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
                int openFloor = Integer.parseInt(op[1]);
                if (openFloor != elevator.getLoc()) {
                    judge = false;
                }
                if (elevator.getState() != DoorClose) {
                    judge = false;
                }
                elevator.changeState();
            } else if (move.equals("CLOSE")) {
                int closeFloor = Integer.parseInt(op[1]);
                if (closeFloor != elevator.getLoc()) {
                    judge = false;
                }
                if (elevator.getState() != DoorOpen) {
                    judge = false;
                }
                elevator.changeState();
            } else {
                judge = false;
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
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ']') {
                break;
            }
            i++;
        }
        return str.substring(i + 1);
    }

}
