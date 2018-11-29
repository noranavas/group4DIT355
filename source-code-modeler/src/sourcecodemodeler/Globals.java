package sourcecodemodeler;

public class Globals {
    // Port that the nodes(PCs) communicates through.
    public static final int PORT = 5991;

    // Indexer to keep track of the nodes(PCs).
    public static final int[] NODE_NUMBER = {
            1,  // Main.
            2,  // XML Parser.
            3,  // XML Formatter.
            4   // Visualizer.
    };

    // A list of ip addresses belonging to the different nodes(PCs).
    public static final String[] IP_ADDRESS = {
            "95.80.14.65",
            "PC2",
            "PC3",
            "PC4",
            "127.0.0.1"
    };

    // Path to srcML.
    public static final String PATH_TO_SRCML = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\srcML-Win\\bin\\srcml.exe";

    // Path to directory where the converted XML files should be created.
    public static final String PATH_TO_XML_FILES = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\";

}