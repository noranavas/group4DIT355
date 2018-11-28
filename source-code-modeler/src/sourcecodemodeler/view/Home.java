package sourcecodemodeler.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sourcecodemodeler.controller.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/*
    This class handles the communication between JavaFX and the rest of the system.
    For example button events.
 */
public class Home {
    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();

    private File selectedFile;
    private StringProperty fileName;

    //socket
    Receiver receiver = new Receiver();
    Sender sender = new Sender();

    //===== Constructor(s) =====//
    public Home() throws IOException {
        this.fileName = new SimpleStringProperty();
    }

    //===== Getters & Setters =====//
    public String getFileName() {
        return fileName.get();
    }
    // This method is needed to update the element in the fxml file.
    public StringProperty fileNameProperty() {
        return fileName;
    }
    public void setFileName(String text) {
        fileName.set(text);
    }


    //===== Methods =====//


    Task task1 = new Task(){
        @Override
        protected Object call() throws Exception { //start the receiver
            System.out.println("Task 1 started");
            receiver.startServer();
           /* for (int i=0; i<=10; i++){
                System.out.println(i);

            }*/
           // socketNode.startConnection("localhost",socketNode.PORT);
           // socketNode.stopConnection();
           // socketNode.stopServer();
            return null;

        }};

    Task task2= new Task(){
        @Override
        protected Object call() throws Exception{ //start the sender and send files
            System.out.println("Task 2 started");
           /* for (int i=10; i>0; i--)
                System.out.println(i);
                */
            sender.Connect(PublicData.IP_ADDRESSES[4],PublicData.PORT);
            sender.sendFiles(PublicData.outputDirectory);
            //socketNode.sendFiles(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\", socketNode.getSocket());
            //socketNode.stopConnection();
            return null;
        }
    };
    Task task3= new Task(){
        @Override
        protected Object call() throws Exception{ //start the sender and send files
            System.out.println("Task 3 started");
            receiver.receiveFiles();
            return null;
        }
    };
    Task task4= new Task(){
        @Override
        protected Object call() throws Exception{ //start the sender and send files
            System.out.println("Task 4 started");
            sender.closeSocket(sender.getSocket());
            return null;
        }
    };





    // Allows the user to select a file.
    public void selectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a code file"); // Title of the JavaFX window.

        // Defines selectable file extensions. In this case .java files.
        FileChooser.ExtensionFilter extFilters = new FileChooser.ExtensionFilter("Code files", "*.java");

        // Apply filters to the file chooser.
        fileChooser.getExtensionFilters().addAll(extFilters);

        // Open the JavaFX window.
        Node node = (Node)actionEvent.getSource();
        selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());

        // Update JavaFX to display name of selected file.
        setFileName(selectedFile.getName());
    }

    // Allows the user to select a directory.
    public void selectDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a directory");
        Node node = (Node)actionEvent.getSource();
        selectedFile = directoryChooser.showDialog(node.getScene().getWindow());
        setFileName(selectedFile.getName());
    }

    // Calls the conversion methods from the SourceCodeConverterClass.
    public void convertToXML(ActionEvent actionEvent) {
        //sourceCodeConverter.clearOutputDirectory();
        try {
            if (selectedFile.isDirectory()) {
                sourceCodeConverter.convertDirectoryToXML(selectedFile.getPath());
            } else {
                sourceCodeConverter.convertToXML(selectedFile.getName(), selectedFile.getPath());
            }
            //Thread thread1 = new Thread(task1);
            Thread thread2=new Thread(task2);
            Thread thread3=new Thread(task3);
            Thread thread4= new Thread(task4);


            //thread1.start();
            thread2.run(); //runs the thread
            thread2.join(); //waits until this thread is done
            thread3.run();
            thread3.join();
            thread4.run();
            thread4.join();
            thread4.interrupt(); //interrupts the thread. not sure it KILLS THE INSTANCE
            thread3.interrupt();
            thread2.interrupt();
            //System.out.println("Thread3 is still running: " + thread2.isAlive());
            //thread2.join();
            // thread2.join();
            /* synchronized (thread5){
                thread5.start();
            }*/
        } catch (NullPointerException e) {
            System.out.println("No file or directory selected.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //===== Temporary Test Methods =====//
    // Prints the content of the latest converted file to the console.
    public void printXMLContent(ActionEvent actionEvent) {
        if (selectedFile.isDirectory()) {
            System.out.println("Can not print a directory.");
        } else {
            xmlIterator.printXMLFile( selectedFile.getName() + ".xml");
        }
    }

    public void printFormattedXML(ActionEvent actionEvent) {
        if (selectedFile.isDirectory()) {
            System.out.println("Can not print a directory.");
        } else {
            xmlIterator.createXMLClass(selectedFile.getName() + ".xml");
        }
    }

}
