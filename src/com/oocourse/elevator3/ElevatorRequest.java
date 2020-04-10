package com.oocourse.elevator3;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElevatorRequest extends Request {
    private final String elevatorId;
    private final String elevatorType;

    public ElevatorRequest(String elevatorId, String elevatorType) {
        this.elevatorId = elevatorId;
        this.elevatorType = elevatorType;
    }

    public String getElevatorId() {
        return this.elevatorId;
    }

    public String getElevatorType() {
        return this.elevatorType;
    }

    public String toString() {
        return String.format("%s-ADD-ELEVATOR-%s", this.elevatorId, this.elevatorType);
    }

    public int hashCode() {
        return Arrays.hashCode(new String[]{this.elevatorId, this.elevatorType});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ElevatorRequest)) {
            return false;
        } else {
            return ((ElevatorRequest)obj).elevatorId.equals(this.elevatorId) && ((ElevatorRequest)obj).elevatorType.equals(this.elevatorType);
        }
    }
}
