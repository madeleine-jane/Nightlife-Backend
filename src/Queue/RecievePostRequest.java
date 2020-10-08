package Queue;

import models.User;

import java.math.BigInteger;

public class RecievePostRequest {
    public BigInteger timestamp;
    public User user;
    public String message;
    public String textAttachment;
    public String imageAttachment;
}
