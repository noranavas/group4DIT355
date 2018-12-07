package sourcecodemodeler;

public class Globals {
    // Port that the nodes (PCs) communicates through.
    public static final int PORT = 5991;

    // A list of ip addresses belonging to the different nodes(PCs).
    public static final String[] IP_ADDRESS = {
            "95.80.14.65",
            "95.80.14.65",
            "95.80.14.65",
            "95.80.14.65"
    };

    // Path to directory where the converted XML files should be created.
    public static final String PATH_TO_XML_FILES = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\";

}