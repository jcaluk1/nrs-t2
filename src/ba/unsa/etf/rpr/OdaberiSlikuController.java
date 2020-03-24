package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.File;

public class OdaberiSlikuController {
    public TextField fldPath;
    private String path = "";

    public TextField fldPattern;

    public ListView<String> listVievPutanje;
    private ObservableList<String> putanjeObs;
    private static volatile boolean exit = false;

    private class ThreadFind extends Thread {
        private String pattern, directory;

        public ThreadFind(String pattern, String directory) {
            this.pattern = pattern;
            this.directory = directory;
        }

        public void stopThread() {
            exit = true;
        }

        @Override
        public void run() {
            searchAndUpdate(pattern,new File(directory));
        }
    }

    ThreadFind threadFind;


    @FXML
    public void initialize () {
        putanjeObs = FXCollections.observableArrayList();
        listVievPutanje.setItems(putanjeObs);

        listVievPutanje.getSelectionModel().selectedItemProperty().addListener((obs, oldPath, newPath) -> {
            threadFind.stopThread();
            fldPath.setText(newPath);
        });
    }

    public void searchAndUpdate(String pattern, File startDirectory) {
        if (!exit) {
            File[] files = startDirectory.listFiles();
            String filePath = startDirectory.getAbsolutePath();
            if (files != null && files.length > 0) {
                // startDirectory nije skriveni folder, niti fajl
                for (File f : files)
                    searchAndUpdate(pattern, f);
            }
            else if (startDirectory.isFile() && filePath.toLowerCase().contains(pattern.toLowerCase()) &&
                     (filePath.endsWith("jpg") || filePath.endsWith("png") || filePath.endsWith("jpeg"))) {
                Platform.runLater(() -> {
                    putanjeObs.add(filePath);
                    listVievPutanje.refresh();
                });
            }
        }
    }

    public void clickOK(ActionEvent actionEvent) {
        threadFind.stopThread();
        path = fldPath.getText();
        Stage stage = (Stage) fldPath.getScene().getWindow();
        stage.close();
    }

    public void clickTrazi (ActionEvent actionEvent) {
        exit = false;
        putanjeObs.clear();
        String pattern = fldPattern.getText();
        String directory = System.getProperty("user.home");
        threadFind = new ThreadFind(pattern,directory);
        threadFind.start();
    }

    public void tStop (ActionEvent actionEvent) {
        threadFind.stopThread();
    }

    String getPath () {
        return path;
    }
}
