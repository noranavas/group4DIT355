package sourcecodemodeler;

public class Globals {
    public static final int PORT = 5991;
    public static final String[] IP_ADDRESSES = {
            "10.0.30.202",          //0
            "10.132.178.107",       //1
            "PC3",                  //2
            "PC4",                  //3
            "localhost"             //4
    };
    // Path to srcML.
    public static final String pathToSrcML = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\srcML-Win\\bin\\srcml.exe";
    // Path to directory where the converted XML files should be created.
    public static final String outputDirectory = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\";
    public static final int [] nodeNumber={
            1, //main
            2, //xml parser
            3, //xml formatter
            4 //visualization maker"
    };
    public Globals(){}
}
