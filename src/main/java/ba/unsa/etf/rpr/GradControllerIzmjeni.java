package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;
public class GradControllerIzmjeni implements Initializable {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public TextField fieldPBroj;

    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    private Grad grad;

    public ListView<Znamenitost> listViewZnam;
    public ObservableList<Znamenitost> znamenitostiObs;
    private ResourceBundle resourceBundle;

    public GradControllerIzmjeni(Grad grad, ArrayList<Drzava> drzave, ArrayList<Znamenitost> znamenitosti) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
        znamenitostiObs = FXCollections.observableArrayList(znamenitosti);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        choiceDrzava.setItems(listDrzave);
        listViewZnam.setItems(znamenitostiObs);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            fieldPBroj.setText(String.valueOf(grad.getPbroj()));
            for (Drzava drzava : listDrzave)
                if (drzava.getId() == grad.getDrzava().getId())
                    choiceDrzava.getSelectionModel().select(drzava);
        } else {
            choiceDrzava.getSelectionModel().selectFirst();
        }
    }

    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }

    public void btnDodajZnam (ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/znamenitost.fxml"), resourceBundle);
            ZnamenitostController znamenitostController = new ZnamenitostController(grad);
            loader.setController(znamenitostController);
            root = loader.load();
            stage.setTitle(resourceBundle.getString("window.attraction"));
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(true);
            stage.show();

            stage.setOnHiding(event -> {
                Znamenitost novaZnamenitost = znamenitostController.getZnamenitost();
                if (novaZnamenitost != null) {
                    GeografijaDAO dao = GeografijaDAO.getInstance();
                    dao.dodajZnamenitost(novaZnamenitost);
                    znamenitostiObs.add(novaZnamenitost);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;
        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }
        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            //
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }
        if (!sveOk) return;
        // Poštanski broj
        new Thread(() -> {
            String adress = "http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + fieldPBroj.getText().trim();
            try {
                URL url = new URL(adress);
                BufferedReader ulaz = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
                if (ulaz.readLine().equals("OK")) {
                    Platform.runLater(() -> {
                        fieldPBroj.getStyleClass().removeAll("poljeNijeIspravno");
                        fieldPBroj.getStyleClass().add("poljeIspravno");
                    });
                    if (grad == null)
                        grad = new Grad();
                    grad.setNaziv(fieldNaziv.getText());
                    grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
                    grad.setDrzava(choiceDrzava.getValue());
                    grad.setPbroj(Integer.parseInt(fieldPBroj.getText()));
                    Stage stage = (Stage) fieldNaziv.getScene().getWindow();
                    stage.close();
                } else {
                    Platform.runLater(() -> {
                        fieldPBroj.getStyleClass().removeAll("poljeNijeIspravno");
                        fieldPBroj.getStyleClass().add("poljeIspravno");
                    });
                }
            } catch (IOException e) {
                // ...
            }
        }).start();
    }
}
