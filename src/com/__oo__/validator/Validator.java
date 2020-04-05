package com.__oo__.validator;

import com.oocourse.elevator2.PersonRequest;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Validator {
    public static final int DoorClose = 1;

    public static final int DoorOpen = 2;

    public static boolean validate(List<PersonRequest> requests, int num, String[] logLines) {
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


        //judge_begin
        HashMap<String, Elevator> elevators = new HashMap<>();
        Elevator elevatorA = new Elevator("A");
        Elevator elevatorB = new Elevator("B");
        Elevator elevatorC = new Elevator("C");
        Elevator elevatorD = new Elevator("D");
        Elevator elevatorE = new Elevator("E");
        elevators.put(elevatorA.getId(),elevatorA);
        elevators.put(elevatorB.getId(),elevatorB);
        elevators.put(elevatorC.getId(),elevatorC);
        elevators.put(elevatorD.getId(),elevatorD);
        elevators.put(elevatorE.getId(),elevatorE);

        judge = true;
        Elevator elevator = null;
        for (String str : logLines) {
            String temp = CopeString(str);
            String[] op = temp.split(" ");
            double getTime = Double.parseDouble(op[0]);
            String move = op[1];

            if(temp.charAt(temp.length()-1) == 'A'){
                elevator = elevatorA;
            } else if(temp.charAt(temp.length()-1) == 'B') {
                elevator = elevatorB;
            } else if(temp.charAt(temp.length()-1) == 'C') {
                elevator = elevatorC;
            } else if(temp.charAt(temp.length()-1) == 'D') {
                elevator = elevatorD;
            } else {
                elevator = elevatorE;
            }

            if(temp.charAt(temp.length()-1) - 'A' > num) {
                judge = false;
                break;
            }

            if(!elevator.ifStart()) {
                elevator.setTime(getTime - 0.4);
                elevator.start();
            }

            if(move.equals("ARRIVE")) {
                int arriveFloor = Integer.parseInt(op[2]);
                //time
                if(getTime - elevator.getTime() < 0.39999) {
                    judge = false;
                }
                //wrong floor
                if(arriveFloor < -3 || arriveFloor > 16 || arriveFloor == 0) {
                    judge = false;
                }
                //about move
                if(elevator.getState() == DoorOpen) {
                    judge = false;
                } else {
                    if(elevator.getLoc() - arriveFloor == 1||elevator.getLoc() - arriveFloor == -1) {
                        elevator.setLoc(arriveFloor);
                    } else if(elevator.getLoc() == 1 && arriveFloor == -1) {
                        elevator.setLoc(arriveFloor);
                    } else if(elevator.getLoc() == -1 && arriveFloor == 1) {
                        elevator.setLoc(arriveFloor);
                    } else {
                        judge = false;
                    }
                }
                elevator.setTime(getTime);
            } else if(move.equals("IN")) {
                int id = Integer.parseInt(op[2]);
                int fromFloor = Integer.parseInt(op[3]);
                //time
                if(getTime - elevator.getTime() < 0) {
                    judge = false;
                }
                //open in wrong floor
                if(fromFloor != elevator.getLoc()) {
                    judge = false;
                }
                //the door is closed but someone come in.
                if(elevator.getState() == DoorClose){
                    judge = false;
                }
                //judge if fromfloor is right
                if(personReqs.containsKey(id)) {
                    personReqs.get(id).in(elevator.getLoc(), elevator.getId());
                } else {//wrong id
                    judge = false;
                }
            } else if(move.equals("OUT")) {
                int id = Integer.parseInt(op[2]);
                int toFloor = Integer.parseInt(op[3]);
                //time
                if(getTime - elevator.getTime() < 0) {
                    judge = false;
                }
                //close in wrong floor
                if(toFloor != elevator.getLoc()) {
                    judge = false;
                }
                //the door is closed but someone go out.
                if(elevator.getState() == DoorClose){
                    judge = false;
                }
                //judge if tofloor is right
                if(personReqs.containsKey(id)) {
                    personReqs.get(id).out(toFloor, elevator.getId());
                } else {//wrong id
                    judge = false;
                }
            } else if(move.equals("OPEN")) {
                int openFloor = Integer.parseInt(op[2]);
                //time
                if(getTime - elevator.getTime() < 0) {
                    judge = false;
                }
                //open in wrong floor
                if(openFloor != elevator.getLoc()){
                    judge = false;
                }
                //door already is open
                if(elevator.getState() != DoorClose) {
                    judge = false;
                }
                elevator.changeState();
                elevator.setTime(getTime);
            } else if(move.equals("CLOSE")) {
                int closeFloor = Integer.parseInt(op[2]);
                if(getTime - elevator.getTime() < 0.399999) {
                    judge = false;
                }
                if(closeFloor != elevator.getLoc()){
                    judge = false;
                }
                if(elevator.getState() != DoorOpen) {
                    judge = false;
                }
                elevator.changeState();
                elevator.setTime(getTime);
            } else {
                judge = false;
            }
        }

        if(judge == false) {
            return false;
        }
        for(PersonReq pr: personReqs.values()){
            if(!pr.isRight()) {
                judge = false;
            }
        }
        return judge;
    }


    public static String CopeString(String str) {
        String temp = str;
        String temp1 = temp.replace(" ","");
        String temp2 = temp1.replace("[","");
        String temp3 = temp2.replace("]"," ");
        String temp4 = temp3.replace("-"," ");
        int i = 1;
        while(i < temp4.length()) {
            if(temp4.charAt(i - 1) == ' ' && temp4.charAt(i) == ' ') {
                temp4 = temp4.substring(0, i) + "-" + temp4.substring(i + 1);
            }
            i++;
        }
        return temp4;
    }

    public static String getReq(String str) {
        String temp = str;
        String blank = " ";
        int i = 0;
        while(i < temp.length()) {
            if(temp.charAt(i) == '-' && temp.charAt(i + 1) == 'F') {
                temp = temp.substring(0, i) + blank + temp.substring(i + 1);
            }
            if(temp.charAt(i) == 'M' && str.charAt(i + 1) == '-') {
                temp = temp.substring(0, i + 1) + blank + temp.substring(i + 2);
            }
            if(temp.charAt(i) == '-' && temp.charAt(i + 1) == 'T') {
                temp = temp.substring(0, i) + blank + temp.substring(i + 1);
            }
            if(temp.charAt(i) == 'O' && str.charAt(i + 1) == '-') {
                temp = temp.substring(0, i + 1) + blank + temp.substring(i + 2);
            }
            i++;
        }
        return temp;
    }
}
