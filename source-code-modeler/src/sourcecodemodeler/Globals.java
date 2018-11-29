package sourcecodemodeler;

public class Globals {
    public static final int PORT = 5991;
    public static final int[] NODE_NUMBER = {
            1, //main
            2, //xml parser
            3, //xml formatter
            4 //visualization maker"
    };
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