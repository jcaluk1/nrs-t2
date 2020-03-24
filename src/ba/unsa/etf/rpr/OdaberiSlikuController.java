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
            searchAndUpdate(pattern,directory);
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

    public void searchAndUpdate(String pattern, String startDirectory) {
        if (!exit) {
            File file = new File(startDirectory);
            String filePath = file.getAbsolutePath();
            if (file.isDirectory() && !file.isHidden()) {
                try {
                    for (File f : file.listFiles()) {
                        searchAndUpdate(pattern, f.getAbsolutePath());
                    }
                } catch (NullPointerException e) {}
            } else if (
                    (file.isFile() && filePath.toLowerCase().contains(pattern)) &&
                            (filePath.endsWith("jpg") || filePath.endsWith("png") || filePath.endsWith("jpeg"))
            ) {
                Platform.runLater(() -> {
                    putanjeObs.add(filePath);
                    listVievPutanje.refresh();
                });
            }
        }
    }

    public void clickOK(ActionEvent actionEvent) {
        path = fldPath.getText();
        Stage stage = (Stage) fldPath.getScene().getWindow();
        stage.close();
    }

    public void clickTrazi (ActionEvent actionEvent) {
        exit = false;
        putanjeObs.clear();
        String pattern = fldPattern.getText().toLowerCase();
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
