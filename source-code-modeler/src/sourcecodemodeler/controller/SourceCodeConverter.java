package sourcecodemodeler.controller;

import sourcecodemodeler.Globals;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

/*
    This class uses srcML to convert code files to XML documents.
 */
public class SourceCodeConverter {
    public static final String PATH_TO_SRCML = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\srcML-Win\\bin\\srcml.exe";
    private static final String PATH_TO_XML_DIRECTORY = Globals.PATH_TO_XML_DIRECTORY;

    //===== Constructor(s) =====//
    public SourceCodeConverter() {}

    //===== Methods =====//
    // Converts a selected file to a XML document in the resources/converted_xml folder.
    public void convertToXML(String fileName, String filePath) {
        /*
         Creates a process builder that contains the command prompt script
         that calls srcML to convert a code file to a XML document.
          */
        ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe",
                "/c",
                PATH_TO_SRCML + " " + filePath + " -o " + PATH_TO_XML_DIRECTORY + fileName + ".xml"
        );
        pb.redirectErrorStream(true); // Some kind of error handler for streams.
        try {
            pb.start(); // Run the script.
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    // Converts all the files contained in the selected directory to XML documents.
    public void convertDirectoryToXML(String directoryPath) {
        // Create a file array for all files in the selected directory.
        File[] files = new File(directoryPath).listFiles();

        // Iterate through all the files and convert files with supported file extension to XML.
        for (int i = 0; i < files.length; i++) {

            // If the file is a directory (a sub-directory), call this method recursively to examinate all the subfolders
            if (files[i].isDirectory()) {
                convertDirectoryToXML(files[i].getPath());
            } else {
                // Get string value of 'Optional' object.
                String filter = "";

                // This try-catch is to prevent files without extensions from throwing an exception.
                try {
                    filter = getFileExtension(files[i].getName()).get();
                } catch (NoSuchElementException e) {
                    //System.out.println("Unspecified extension found for file: " + files[i].getName() + " at: " + files[i].getPath()); // DEBUGGING.
                }

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

    // Clears the output directory.
    public void clearOutputDirectory() {
        File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

}