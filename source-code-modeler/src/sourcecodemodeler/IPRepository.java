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
    private String[] ipAddress = {
            "", // Node 1.
            "", // Node 2.
            "" // Node 3.
    };

    public IPRepository() {
        nodeNumber = 0;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }
    public String[] getIpAddress() {
        return ipAddress;
    }
    public void setIPAddress(String[] ipAddresses) {
        this.ipAddress = ipAddresses;
    }
    public void incrementNodeNumber() {
        nodeNumber++;
        if (nodeNumber >= 3) {
            nodeNumber = 0;
        }
    }
}
