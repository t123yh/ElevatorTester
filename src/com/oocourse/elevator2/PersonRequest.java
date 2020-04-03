package com.oocourse.elevator2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PersonRequest {
    private static final int MAX_FLOOR = 15;
    private static final int MIN_FLOOR = 1;
    private final int fromFloor;
    private final int toFloor;
    private final int personId;
    private static final String PARSE_PATTERN_STRING = "^(?<personId>\\d+)-FROM-(?<fromFloor>\\d+)-TO-(?<toFloor>\\d+)\\s*$";
    private static final Pattern PARSE_PATTERN = Pattern.compile("^(?<personId>\\d+)-FROM-(?<fromFloor>\\d+)-TO-(?<toFloor>\\d+)\\s*$");
    private static final BigInteger INT_MAX = BigInteger.valueOf(2147483647L);
    private static final BigInteger INT_MIN = BigInteger.valueOf(-2147483648L);

    private static boolean isValidFloor(Integer floor) {
        return floor != null && floor >= 1 && floor <= 15;
    }

    public PersonRequest(int fromFloor, int toFloor, int personId) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.personId = personId;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public int getPersonId() {
        return this.personId;
    }

    public String toString() {
        return String.format("%d-FROM-%d-TO-%d", this.personId, this.fromFloor, this.toFloor);
    }

    public int hashCode() {
        return Arrays.hashCode(new int[]{this.personId, this.fromFloor, this.toFloor});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof PersonRequest)) {
            return false;
        } else {
            return ((PersonRequest)obj).fromFloor == this.fromFloor && ((PersonRequest)obj).toFloor == this.toFloor && ((PersonRequest)obj).personId == this.personId;
        }
    }

    private static boolean isValidInteger(String string) {
        try {
            BigInteger integer = new BigInteger(string);
            return integer.compareTo(INT_MAX) <= 0 && integer.compareTo(INT_MIN) >= 0;
        } catch (Exception var2) {
            return false;
        }
    }

    private static Integer toValidInteger(String string) {
        return isValidInteger(string) ? (new BigInteger(string)).intValue() : null;
    }
}
