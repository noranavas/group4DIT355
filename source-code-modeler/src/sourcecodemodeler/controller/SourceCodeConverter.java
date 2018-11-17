package sourcecodemodeler.controller;

import java.io.IOException;

/*
    This class uses srcML to convert code files to XML documents.
 */
public class SourceCodeConverter {
    // Path to srcML.
    private final String pathToSrcML = "resources\\srcML-Win\\bin\\srcml";
    // Path to directory where the created XML files are stored.
    private final String pathToXMLDirectory = "resources\\converted_xml\\";

    //===== Constructor(s) =====//
    public SourceCodeConverter() {}

    //===== Methods =====//
    public void convertToXML(String fileName, String filePath) throws NullPointerException {
        /*
         Creates a process builder that contains the command prompt script
         that calls srcML to convert a code file to an XML document.
          */
        ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe",
                "/c",
                pathToSrcML + " " + filePath + " -o " + pathToXMLDirectory + fileName + ".xml"
        );
        pb.redirectErrorStream(true); // Not sure. Some kind of error handler for streams.
        try {
            pb.start(); // Run the script.
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}