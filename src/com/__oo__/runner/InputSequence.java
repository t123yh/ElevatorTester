package com.__oo__.runner;

import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.EndOfRequest;
import com.oocourse.elevator2.PersonRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class InputSequence {
    public static class StringCommand {
        public StringCommand(PersonRequest request) {
            this.request = request;
        }

        public PersonRequest request;

        public String toString() {
            return request.toString();
        }
    }

    public static class DelayCommand {
        public DelayCommand(int millis) {
            this.millis = millis;
        }

        public int millis;
    }

    public int getElevatorNum() {
        return elevatorNum;
    }

    private int elevatorNum;
    private List<Object> commands;

    public InputSequence(List<Object> cmd, int num) {
        commands = new ArrayList<>(cmd);
        elevatorNum = num;
    }

    public static InputSequence parseFromText(List<String> lines) {
        double currentTime = 0;
        List<Object> cmdList = new LinkedList<>();
        int number = -1;

        for (String str : lines) {
            if (str.isEmpty())
                continue;
            str = str.trim();
            int i = 0;
            String temp = "";
            double time;
            int id;
            int fromFloor;
            int toFloor;
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
            temp = "";
            if(number == -1) {
                temp = str.substring(i);
                number = Integer.parseInt(temp);
            } else {
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
                fromFloor = Integer.parseInt(temp.substring(0,temp.length()-1));//////////fromFloor
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
                if (time - currentTime > 0.00001) {
                    cmdList.add(new DelayCommand((int) ((time - currentTime) * 1000)));
                    currentTime = time;
                }
                cmdList.add(new StringCommand(new PersonRequest(fromFloor, toFloor, id)));
            }
        }

        return new InputSequence(cmdList, number);
    }

    public String toString() {
        double currentTime = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("[0.000]%d\n", elevatorNum));
        for (Object obj : commands) {
            if (obj instanceof StringCommand) {
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
                ElevatorInput.numberOfElevators = elevatorNum;
                for (Object obj : commands) {
                    if (obj instanceof StringCommand) {
                        try {
                            if (Main.verbose)
                                System.out.println("Feed in: " + obj.toString());
                            ElevatorInput.InputQueue.put(((StringCommand) obj).request);
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
    static final int minFloor = -3, maxFloor = 15;

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
        boolean started = false;
        while (totalTime < maxTime) {
            if (!started || rnd.nextDouble() > 0.4) {
                started = true;
                int count = rnd.nextDouble() > 0.3 ? 1 : rnd.nextInt(4);
                for (int i = 0; i < count; i++) {
                    StringCommand cmd = new StringCommand(generateRequest(idList));
                    cmds.add(cmd);
                }
            } else if (rnd.nextDouble() > 0.9) {
                break;
            } else {
                int maxDelay = rnd.nextDouble() > 0.5 ? 200 : 50;
                int delay = rnd.nextInt(maxDelay) * 10 + 100;
                DelayCommand cmd = new DelayCommand(delay);
                totalTime += cmd.millis;
                cmds.add(cmd);
            }
        }
        StringCommand cmd = new StringCommand(generateRequest(idList));
        cmds.add(cmd);

        return new InputSequence(cmds, rnd.nextInt(5) + 1);
    }

    public List<PersonRequest> getRequests() {
        return commands.stream().filter(x -> x instanceof StringCommand).map(x -> ((StringCommand) x).request).collect(Collectors.toList());
    }
}
