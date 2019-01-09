package sourcecodemodeler;

import java.io.Serializable;
/*
    This class acts as a storage for hardcoded IP addresses.
 */
public class IPRepository implements Serializable {
    private int nodeNumber = 1;
    private static String[] IP_ADDRESS = {
            "192.168.0.103",
            "192.168.0.100",
            "192.168.0.102"
    };

    public static String[] getIpAddress() {
        return IP_ADDRESS;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public void incrementNodeNumber() {
        if (nodeNumber >= 4) {
            nodeNumber = 0;
        } else {
            this.nodeNumber++;
        }
    }
}
