package sourcecodemodeler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
    This class is an information holder that stores parsed XML data.
    The instances of these classes are temporary.
 */
public class XMLClass implements Serializable {
    private String name;
    private List<String> attributes;
    private List<String> methods;
    private List<XMLClass> relationships;

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
    public void addRelationship(XMLClass relationship) {
        this.relationships.add(relationship);
    }
    public List<XMLClass> getRelationships() {
        return this.relationships;
    }

    @Override
    public String toString() {
        String s = "class " + this.getName() + " {\n";
        for (String attribute: this.getAttributes()) {
            s += attribute + "\n";
        }
        for (String method: this.getMethods()) {
            s += method + "\n";
        }

        s += "}\n";
        return s;
    }

    public ArrayList<String> relationsToString(){
        ArrayList<String> relations = new ArrayList<>();

        for (XMLClass relation: this.getRelationships()){
            relations.add(relation.getName());
        }

        return relations;
    }

}