package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController implements Initializable {

    public TableView<Grad> tableViewGradovi;
    public TableColumn colGradId;
    public TableColumn colGradNaziv;
    public TableColumn colGradStanovnika;
    public TableColumn colPBroj;    // added
    public TableColumn<Grad,String> colGradDrzava;
    private GeografijaDAO dao;
    private ObservableList<Grad> listGradovi;

    public ResourceBundle resourceBundle;

    public GlavnaController() {
        dao = GeografijaDAO.getInstance();
        listGradovi = FXCollections.observableArrayList(dao.gradovi());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        tableViewGradovi.setItems(listGradovi);
        colGradId.setCellValueFactory(new PropertyValueFactory("id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory("brojStanovnika"));
        colPBroj.setCellValueFactory(new PropertyValueFactory("pbroj"));
        colGradDrzava.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDrzava().getNaziv()));
    }


    public void actionDodajGrad(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
            GradControllerDodaj gradControllerDodaj = new GradControllerDodaj(null, dao.drzave());
            loader.setController(gradControllerDodaj);
            root = loader.load();
            stage.setTitle("Grad");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding( event -> {
                Grad grad = gradControllerDodaj.getGrad();
                if (grad != null) {
                    dao.dodajGrad(grad);
                    listGradovi.setAll(dao.gradovi());
                }
            } );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void actionDodajDrzavu(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"));
            DrzavaController drzavaController = new DrzavaController(null, dao.gradovi());
            loader.setController(drzavaController);
            root = loader.load();
            stage.setTitle("Država");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding( event -> {
                Drzava drzava = drzavaController.getDrzava();
                if (drzava != null) {
                    dao.dodajDrzavu(drzava);
                    listGradovi.setAll(dao.gradovi());
                }
            } );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionIzmijeniGrad(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        if (grad == null) return;
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
            GradControllerIzmjeni gradController = new GradControllerIzmjeni(grad, dao.drzave(), dao.dajZnamenostiZaGrad(grad));
            loader.setController(gradController);
            root = loader.load();
            stage.setTitle("Grad");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding( event -> {
                Grad noviGrad = gradController.getGrad();
                if (noviGrad != null) {
                    dao.izmijeniGrad(noviGrad);
                    listGradovi.setAll(dao.gradovi());
                }
            } );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionObrisiGrad(ActionEvent actionEvent) {
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        if (grad == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda brisanja");
        alert.setHeaderText("Brisanje grada "+grad.getNaziv());
        alert.setContentText("Da li ste sigurni da želite obrisati grad " +grad.getNaziv()+"?");
        alert.setResizable(true);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            dao.obrisiGrad(grad);
            listGradovi.setAll(dao.gradovi());
        }
    }

    public void actionNapraviIzvjestaj(ActionEvent actionEvent) {
        try {
            new Izvjestaj().showReport(dao.getConn());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void actionPromijeniJezik (ActionEvent actionEvent) {
        String engleski = resourceBundle.getString("english");
        String bosanski = resourceBundle.getString("bosnian");

        String[] jezici = {engleski, bosanski};
        ChoiceDialog<String> dialog = new ChoiceDialog<>(jezici[0], jezici[1]);
        dialog.setTitle(resourceBundle.getString("dialog.title"));
        dialog.setHeaderText(resourceBundle.getString("dialog.message"));
        Optional<String> result = dialog.showAndWait();

        // Da li je selektovan razlicit jezik? Ako jest resetujemo pocetni stage
        result.ifPresent(jezik -> {
            String language = "en";
            String country = "UK";
            if (jezik.equals(jezici[1])) {
                language = "bs";
                country = "BA";
            }
            if (!language.equals(resourceBundle.getLocale().getLanguage())) {
                // Pokrecemo novi stage
                Stage stageNovi = new Stage();
                Locale locale = new Locale(language, country);
                ResourceBundle bundle = ResourceBundle.getBundle("Translation", locale);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle);
                GlavnaController ctrl = new GlavnaController();
                loader.setController(ctrl);
                try {
                    Parent root = loader.load();
                    stageNovi.setTitle(bundle.getString("window.main"));
                    stageNovi.setScene(new Scene(root, 600, 400));
                    stageNovi.show();

                    // Zatvaramo stari stage
                    Stage stage = (Stage) tableViewGradovi.getScene().getWindow();
                    stage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Metoda za potrebe testova, vraća bazu u polazno stanje
    public void resetujBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        dao = GeografijaDAO.getInstance();
    }
}
