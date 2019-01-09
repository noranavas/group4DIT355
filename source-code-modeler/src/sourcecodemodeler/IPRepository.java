package sourcecodemodeler;

import java.io.Serializable;
/*
    This class acts as a storage for hardcoded IP addresses.
 */
public class IPRepository implements Serializable {
    private static String[] IP_ADDRESS = {
            "192.168.0.103",
            "192.168.0.100",
            "192.168.0.101"
    };

    public static String[] getIpAddress() {
        return IP_ADDRESS;
    }
}
