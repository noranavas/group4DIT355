package sourcecodemodeler.model;

import java.util.ArrayList;
import java.util.List;

public class XMLClass {
    private String name;
    private List<String> attributes;
    private List<String> methods;
    private List<String> relationships;

    //===== Constructor(s) =====//
    public XMLClass() {
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }

    //===== Getters & Setters =====//
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void addAttribute(String attribute) {
        this.attributes.add(attribute);
    }
    public List<String> getAttributes() {
        return this.attributes;
    }
    public void addMethod(String method) {
        this.methods.add(method);
    }
    public List<String> getMethods() {
        return this.methods;
    }
    public void addRelationship(String relationship) {
        this.relationships.add(relationship);
    }
}
