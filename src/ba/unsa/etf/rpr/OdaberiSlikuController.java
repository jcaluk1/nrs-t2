package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

public class OdaberiSlikuController {
    public TextField fldPath;
    public String path;

    public void clickOK(ActionEvent actionEvent) {
        path = fldPath.getText();
        Stage stage = (Stage) fldPath.getScene().getWindow();
        stage.close();
    }

    String getPath () {
        return path;
    }


}
