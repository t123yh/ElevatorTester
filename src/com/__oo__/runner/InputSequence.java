package com.__oo__.runner;

import com.__oo__.validator.PersonReq;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.EndOfRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class InputSequence {
    public static class DelayCommand {
        public DelayCommand(int millis) {
            this.millis = millis;
        }

        public int millis;
    }

    private List<Object> commands;

    public InputSequence(List<Object> cmd) {
        commands = new ArrayList<>(cmd);
    }

    public static InputSequence parseFromText(List<String> lines) {
        double currentTime = 0;
        List<Object> cmdList = new LinkedList<>();

        for (String str : lines) {
            if (str.isEmpty())
                continue;
            str = str.trim();
            int i = 0;
            String temp = "";
            double time;
            if (str.charAt(i) == '[') {
                i++;
            }
            while (str.charAt(i) == ' ') {
                i++;
            }
            while (str.charAt(i) != ']') {
                temp += str.charAt(i);
                i++;
            }
            i++;
            time = Double.parseDouble(temp);///////////time
            if (time - currentTime > 0.00001) {
                cmdList.add(new DelayCommand((int) ((time - currentTime) * 1000)));
                currentTime = time;
            }
            if(str.charAt(i) == 'X'){
                ElevatorRequest eleR = parseForEle(str, i);
                cmdList.add(eleR);
            } else {
                PersonRequest pR = parseForPerson(str, i);
                cmdList.add(pR);
            }
        }
        return new InputSequence(cmdList);
    }

    public static ElevatorRequest parseForEle(String str, int i) {
        String eleId = "";
        String eleType = "";
        while (str.charAt(i) != '-') {
            eleId += str.charAt(i);
            i++;
        }
        eleType = "" + str.charAt(str.length()-1);
        ElevatorRequest eleR = new ElevatorRequest(eleId, eleType);
        return eleR;
    }

    public static PersonRequest parseForPerson(String str, int i) {
        int id;
        int fromFloor;
        int toFloor;
        String temp = "";
        while (str.charAt(i) != '-') {
            temp += str.charAt(i);
            i++;
        }
        id = Integer.parseInt(temp);//////////////id
        i++;
        while (str.charAt(i) != '-') {
            i++;
        }
        i++;
        temp = "";
        while (str.charAt(i) != 'T') {
            temp += str.charAt(i);
            i++;
        }
        fromFloor = Integer.parseInt(temp.substring(0, temp.length() - 1));//////////fromFloor
        while (str.charAt(i) != '-') {
            temp += str.charAt(i);
            i++;
        }
        i++;
        temp = "";
        while (i < str.length()) {
            temp += str.charAt(i);
            i++;
        }
        toFloor = Integer.parseInt(temp);
        PersonRequest pr = new PersonRequest(fromFloor, toFloor, id);
        return pr;
    }
    public String toString() {
        double currentTime = 0;
        StringBuilder builder = new StringBuilder();
        for (Object obj : commands) {
            if (obj instanceof Request) {
                builder.append(String.format("[%.3f]%s\n", currentTime, obj.toString()));
            } else if (obj instanceof DelayCommand) {
                currentTime += ((DelayCommand) obj).millis / 1000.0;
            }
        }
        return builder.toString();
    }

    public Runnable feed() {
        return new Runnable() {
            @Override
            public void run() {
                ElevatorInput.InputQueue.clear();
                for (Object obj : commands) {
                    if (obj instanceof Request) {
                        try {
                            if (Main.verbose)
                                System.out.println("Feed in: " + obj.toString());
                            ElevatorInput.InputQueue.put(((Request) obj));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (obj instanceof DelayCommand) {
                        try {
                            Thread.sleep(((DelayCommand) obj).millis);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    ElevatorInput.InputQueue.put(new EndOfRequest());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static int generateInt(HashSet<Integer> current) {
        int result;
        do {
            result = rnd.nextInt(100000);
        } while (current.contains(result));
        current.add(result);
        return result;
    }

    static final Random rnd = new Random();
    static final int minFloor = -3, maxFloor = 20;

    private static int generateFloor() {
        int floor;
        do {
            floor = rnd.nextInt(maxFloor - minFloor + 1) + minFloor;
        } while (floor == 0);
        return floor;
    }

    private static PersonRequest generateRequest(HashSet<Integer> idList) {
        int pid = generateInt(idList);
        int fromFloor, toFloor;
        do {
            fromFloor = generateFloor();
            toFloor = generateFloor();
        } while (fromFloor == toFloor);
        return new PersonRequest(fromFloor, toFloor, pid);
    }

    static final int maxTime = 10 * 1000;

    public static InputSequence generate() {
        HashSet<Integer> idList = new HashSet<>();

        List<Object> cmds = new ArrayList<>();
        int totalTime = 0;
        int elevatorsAdded = 0;
        boolean started = false;
        while (totalTime < maxTime && cmds.size() < 50) {
            if (!started || rnd.nextDouble() > 0.4) {
                started = true;
                int count = rnd.nextDouble() > 0.5 ? 1 : rnd.nextInt(5);
                for (int i = 0; i < count; i++) {
                    Request cmd = generateRequest(idList);
                    cmds.add(cmd);
                }
            } else if (rnd.nextDouble() > 0.7 && elevatorsAdded < 3) {
                elevatorsAdded++;
                double n = rnd.nextDouble();
                String type;
                if (n > 0.666) {
                    type = "A";
                } else if (n > 0.333) {
                    type = "B";
                } else {
                    type = "C";
                }
                Request cmd = new ElevatorRequest("X" + Integer.toString(elevatorsAdded), type);
                cmds.add(cmd);
            } else if (rnd.nextDouble() > 0.95) {
                break;
            } else {
                int maxDelay = rnd.nextDouble() > 0.5 ? 200 : 50;
                int delay = rnd.nextInt(maxDelay) * 10 + 100;
                DelayCommand cmd = new DelayCommand(delay);
                totalTime += cmd.millis;
                cmds.add(cmd);
            }
        }
        Request cmd = generateRequest(idList);
        cmds.add(cmd);

        return new InputSequence(cmds);
    }

    public static class RequestWithTime {
        public RequestWithTime(int time, Request content) {
            this.time = time;
            this.content = content;
        }

        public int time;
        public Request content;
    }

    public List<RequestWithTime> getRequests() {
        List<RequestWithTime> result = new ArrayList<>();
        int time = 0;
        for (Object obj : commands) {
            if (obj instanceof Request) {
                result.add(new RequestWithTime(time, (Request)obj));
            } else if (obj instanceof DelayCommand) {
                time += ((DelayCommand) obj).millis;
            }
        }
        return result;
    }

}
