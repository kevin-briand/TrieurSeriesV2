package fr.firedream89.trieurseries;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class TrieurSeriesController {

    @FXML
    private TextField txtPath;
    @FXML
    private TreeView<String> avant;
    @FXML
    private TreeView<String> apres;

    @FXML
    private Button btnTrier;

    @FXML
    protected void onOpenFolder() {
        txtPath.setText(TrieurSeries.getInstance().getFolder());
        updateTreeAvant();
        updateTreeApres();
        btnTrier.setDisable(false);
    }

    @FXML
    protected void onTrier() {
        btnTrier.setDisable(true);
        try {
            TrieurSeries.getInstance().trier();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateTreeAvant() {
        TrieurSeries.getInstance().generate(avant,txtPath.getText(),"mkv|avi", false);
    }

    protected void updateTreeApres() {
        TrieurSeries.getInstance().generateAfter(apres,txtPath.getText(),new String[]{"mkv","avi"});
    }
    protected void updateTable() {
    }


}