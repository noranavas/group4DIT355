package sourcecodemodeler.controller;

import javafx.concurrent.Task;
import sourcecodemodeler.Globals;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


/*
    This class uses srcML to convert code files to XML documents.
 */
public class SourceCodeConverter {
    //===== Constructor(s) =====//
    public SourceCodeConverter() {}

    //===== Methods =====//
    // Converts a selected file to a XML document in the resources/converted_xml folder.
    public void convertToXML(String fileName, String filePath) throws InterruptedException {
        //create a new runnable task
        Task srcmlTask = new Task(){
            /*Creates a process builder that contains the command prompt script
            that calls srcML to convert a code file to an XML document.*/
            @Override
            protected Object call() throws Exception { //start the receiver
                ProcessBuilder pb = new ProcessBuilder(
                        "cmd.exe",
                        "/c",
                        Globals.PATH_TO_SRCML + " " + filePath + " -o " + Globals.PATH_TO_XML_FILES + fileName + ".xml"
                );
                pb.redirectErrorStream(true); // Some kind of error handler for streams.
                try {
                    pb.start(); // Run the script.
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

            return null;}};

        //assign the task to a thread. run it and wait until it finishes
        Thread thread1= new Thread(srcmlTask);
        thread1.run();
        thread1.join();
        thread1.interrupt();
    }

    // Creates a copy of the selected directory and converts all found files to XML documents.
    public void convertDirectoryToXML(String directoryPath) throws InterruptedException {
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
        File[] files = new File(Globals.PATH_TO_XML_FILES).listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

}