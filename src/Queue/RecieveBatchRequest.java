package Queue;

import models.User;

import java.math.BigInteger;
import java.util.ArrayList;

public class RecieveBatchRequest {
    public ArrayList<String> followers;
    public BigInteger timestamp;
    public User user;
    public String message;
    public String textAttachment;
}
