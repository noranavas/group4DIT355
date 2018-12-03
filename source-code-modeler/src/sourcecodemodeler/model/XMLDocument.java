package sourcecodemodeler.model;

import java.io.Serializable;

public class XMLDocument implements Serializable {
    private String XMLData;

    //===== Constructor(s) =====//
    public XMLDocument(String data) {
        this.XMLData = data;
    }

    //===== Getters & Setters =====//
    public String getXMLData() {
        return XMLData;
    }
}
