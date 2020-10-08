package models;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

public class Status {
    public Status(String message, User user, BigInteger timestamp) {
        this.message = message;
        this.user = user;
        this.timestamp = timestamp;
    }
    public String message;
    public BigInteger timestamp;
    public String imageAttachment = "";
    public String textAttachment = "";
    public User user;
}