package sourcecodemodeler;

import java.io.Serializable;
/*
    Middleware class for IP addresses.
    Indexes:
    0 = Node/PC 1.
    1 = Node/PC 2.
    2 = Node/PC 3.
 */
public class IPRepository implements Serializable {
    private static int nodeNumber = 0;
    private static final String[] IP_ADDRESS = {
            "192.168.0.103",
            "192.168.0.100",
            "192.168.0.102"
    };
    public static int getNodeNumber() {
        return nodeNumber;
    }
    public static String[] getIpAddress() {
        return IP_ADDRESS;
    }
    public void incrementNodeNumber() {
        if (nodeNumber >= 3) {
            nodeNumber = 0;
        } else {
            nodeNumber++;
        }
    }
}
