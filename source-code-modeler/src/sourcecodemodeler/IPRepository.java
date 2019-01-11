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
    private int nodeNumber;
    private final String[] IP_ADDRESS = {
            "192.168.0.100", // Node 1.
            "192.168.0.101", // Node 2.
            "192.168.0.102" // Node 3.
    };

    public IPRepository() {
        nodeNumber = 0;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }
    public String[] getIpAddress() {
        return IP_ADDRESS;
    }
    public void incrementNodeNumber() {
        nodeNumber++;
        if (nodeNumber >= 3) {
            nodeNumber = 0;
        }
    }
}
