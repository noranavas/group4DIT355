package sourcecodemodeler.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/*
    This class uses srcML to convert code files to XML documents.
 */
public class SourceCodeConverter {
    // Path to srcML.
    private final String pathToSrcML = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\srcML-Win\\bin\\srcml.exe";
    // Path to directory where the converted XML files should be created.
    private final String outputDirectory = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\";

    //===== Constructor(s) =====//
    public SourceCodeConverter() {}

    //===== Methods =====//
    // Converts a selected file to a XML document in the resources/converted_xml folder.
    public void convertToXML(String fileName, String filePath) {
        /*
         Creates a process builder that contains the command prompt script
         that calls srcML to convert a code file to an XML document.
          */
        ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe",
                "/c",
                pathToSrcML + " " + filePath + " -o " + outputDirectory + fileName + ".xml"
        );
        pb.redirectErrorStream(true); // Some kind of error handler for streams.
        try {
            pb.start(); // Run the script.
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    // Creates a copy of the selected directory and converts all found files to XML documents.
    public void convertDirectoryToXML(String directoryPath) {
        System.out.println(directoryPath);
        // Create a file array for all files in the selected directory.
        File[] files = new File(directoryPath).listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getPath());
        }

        // Iterate through all the files and convert files with supported file extension to XML.
        for (int i = 0; i < files.length; i++) {

            // If the file is a directory, call this method recursively.
            if (files[i].isDirectory()) {
                convertDirectoryToXML(files[i].getPath());
            } else {
                String filter = getFileExtension(files[i].getName()).get(); // Get string value of 'Optional' object.
                if (filter.equalsIgnoreCase("java")) {
                    convertToXML(files[i].getName(), files[i].getPath());
                }
            }
        }

    }

    // Get file extension by String Handling: https://www.baeldung.com/java-file-extension
    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public void clearOutputDirectory() {
        File[] files = new File(outputDirectory).listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

}