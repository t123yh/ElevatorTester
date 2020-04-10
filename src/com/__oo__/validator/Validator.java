package com.__oo__.validator;

import com.__oo__.runner.InputSequence;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Validator {
    public static final int DoorClose = 1;

    public static final int DoorOpen = 2;

    public static boolean validate(InputSequence is, String[] logLines) {
        TreeMap<Integer, PersonReq> personReqs = new TreeMap<>();
        boolean judge = true;
        int numOfEle = 3;

        HashMap<String, Elevator> elevators = new HashMap<>();
        Elevator elevator1 = new Elevator("A","A",0);
        Elevator elevator2 = new Elevator("B","B",0);
        Elevator elevator3 = new Elevator("C","C",0);
        elevators.put(elevator1.getId(),elevator1);
        elevators.put(elevator2.getId(),elevator2);
        elevators.put(elevator3.getId(),elevator3);

        //get_id_list
        for (InputSequence.RequestWithTime requestWithTime : is.getRequests()) {
            double time = requestWithTime.time;
            Request req = requestWithTime.content;
            if(req instanceof PersonRequest) {
                PersonRequest temp = (PersonRequest)req;
                PersonReq pr = new PersonReq(temp);
                personReqs.put(pr.getId(), pr);
            } else {
                ElevatorRequest temp = (ElevatorRequest)req;
                if(numOfEle == 3) {
                    Elevator elevator4 = new Elevator(temp.getElevatorId(),temp.getElevatorType(),time);
                    elevators.put(elevator4.getId(),elevator4);
                } else if(numOfEle == 4) {
                    Elevator elevator5 = new Elevator(temp.getElevatorId(),temp.getElevatorType(),time);
                    elevators.put(elevator5.getId(),elevator5);
                } else if(numOfEle == 5) {
                    Elevator elevator6 = new Elevator(temp.getElevatorId(),temp.getElevatorType(),time);
                    elevators.put(elevator6.getId(),elevator6);
                } else {
                    judge = false;
                }
            }
        }

        //judge_begin

        Elevator elevator = null;
        for (String str : logLines) {
            String temp = CopeString(str);
            // System.out.println("Processing line " + temp);
            String[] op = temp.split(" ");
            double getTime = Double.parseDouble(op[0]);

            String eleId = op[op.length-1];
            if(!elevators.containsKey(eleId)) {
                judge  = false;
                break;
            } else {
                elevator = elevators.get(eleId);
            }

            if(!elevator.ifStart()) {
                elevator.setTime(getTime - 0.4);
                elevator.start();
            }

            String move = op[1];
            if(move.equals("ARRIVE")) {
                int arriveFloor = Integer.parseInt(op[2]);
                //time
                if(getTime - elevator.getTime() < elevator.getSpeed()-0.000001) {
                    judge = false;
                }
                //wrong floor
                if(arriveFloor < -3 || arriveFloor > 20 || arriveFloor == 0) {
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
                if(openFloor != elevator.getLoc() || !elevator.canOpen(openFloor)){
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
}
