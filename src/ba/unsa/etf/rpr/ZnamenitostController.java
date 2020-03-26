package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.util.ResourceBundle;


public class ZnamenitostController implements Initializable {

    public TextField fldNaziv;
    public ImageView imgFrame;

    private Grad grad;
    private Znamenitost znamenitost;
    private String putanja = "";
    private ResourceBundle resourceBundle;

    public ZnamenitostController (Grad g) {
        this.grad = g;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        try {
            Image img = new Image(new FileInputStream("C:/Users/hp/Downloads/rpr19-2parc-1t/rpr19-2parc-1t/resources/imgs/upload.png"));
            imgFrame.setImage(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnOdaberiSliku(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/odaberiSliku.fxml"),resourceBundle);
            OdaberiSlikuController odaberiSlikuController = new OdaberiSlikuController();
            loader.setController(odaberiSlikuController);
            root = loader.load();
            stage.setTitle(resourceBundle.getString("window.choose.picture"));
            stage.setScene(new Scene(root, PopupControl.USE_COMPUTED_SIZE, PopupControl.USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                String putanja = odaberiSlikuController.getPath();
                File putanjaFile = new File (putanja);
                if (putanjaFile.exists() && (putanja.endsWith("png") || putanja.endsWith("jpg"))) {
                    try {
                        imgFrame.setImage(new Image(new FileInputStream(putanja)));
                        this.putanja = putanja;
                    } catch (FileNotFoundException e) {
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
        if (!sveOk && putanja.isEmpty())
            return;

        // Znamenitost je ok
        znamenitost = new Znamenitost();
        znamenitost.setSlika(putanja);
        znamenitost.setNaziv(fldNaziv.getText());
        znamenitost.setGrad(grad);

        Stage stage = (Stage) fldNaziv.getScene().getWindow();
        stage.close();
    }
    public Znamenitost getZnamenitost () {return znamenitost;}
}
