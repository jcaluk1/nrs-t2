package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ZnamenitostController {

    public TextField fldNaziv;
    public ImageView imgFrame;

    private Grad grad;
    private Znamenitost znamenitost;
    private final String putanjaError = "C:/Users/hp/Downloads/rpr19-2parc-1t/rpr19-2parc-1t/resources/imgs/upload.png";

    public ZnamenitostController (Grad g) {
        this.grad = g;
    }

    @FXML
    public void initialize() {
        try {
            Image img = new Image(new FileInputStream(putanjaError));
            imgFrame.setImage(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnOdaberiSliku(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/odaberiSliku.fxml"));
            OdaberiSlikuController odaberiSlikuController = new OdaberiSlikuController();
            loader.setController(odaberiSlikuController);
            root = loader.load();
            stage.setTitle("Znamenitost");
            stage.setScene(new Scene(root, PopupControl.USE_COMPUTED_SIZE, PopupControl.USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                String putanja = odaberiSlikuController.getPath();
                File putanjaFile = new File (putanja);
                if (putanjaFile.exists() && (putanja.endsWith("png") || putanja.endsWith("jpg"))) {
                    try {
                        imgFrame.setImage(new Image(new FileInputStream(putanja)));
                        znamenitost = new Znamenitost();
                        znamenitost.setSlika(putanja);
                        System.out.println("Sve ok");
                    } catch (FileNotFoundException e) {
                        znamenitost = null;
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnSacuvajPromjene(ActionEvent actionEvent) {
        boolean sveOk = true;
        if (fldNaziv.getText().trim().isEmpty()) {
            fldNaziv.getStyleClass().removeAll("poljeIspravno");
            fldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fldNaziv.getStyleClass().add("poljeIspravno");
        }

        if (znamenitost == null)
            sveOk = false;

        if (!sveOk)
            return;

        // Znamenitost je ok
        znamenitost.setNaziv(fldNaziv.getText());
        znamenitost.setGrad(grad);

        Stage stage = (Stage) fldNaziv.getScene().getWindow();
        stage.close();
    }
    public Znamenitost getZnamenitost () {return znamenitost;}
}
