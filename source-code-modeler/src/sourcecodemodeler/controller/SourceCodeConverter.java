package sourcecodemodeler.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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

    public void convertDirectoryToXML(String directoryPath) throws NullPointerException {
        // Create a file array for all files in the directory passed as parameter.
        File[] files = new File(directoryPath).listFiles();

        // Iterate through all the files and filter out unsupported file extensions.
        for (int i = 0; i < files.length; i++) {

            // If the file is a directory, call this method recursively.
            if (files[i].isDirectory()) {
                convertDirectoryToXML(files[i].getPath());
            } else {
                String filter = getFileExtension(files[i].getName()).get();
                if (
                        filter.equalsIgnoreCase("c") ||
                                filter.equalsIgnoreCase("cpp") ||
                                filter.equalsIgnoreCase("java"))
                {
                    convertToXML(files[i].getName(), files[i].getPath());
                }
            }
        }

    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}