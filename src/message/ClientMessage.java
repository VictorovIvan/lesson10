package message;

import java.io.Serializable;

/**
 * Class ClientMessage. This is class contain desription about object
 */
public class ClientMessage implements Serializable {
    public enum Condition {ENTER,WORK,QUIT};
    public String name;
    public String message;
    public Condition conditionClient;
    public String clientPrivate;
}
