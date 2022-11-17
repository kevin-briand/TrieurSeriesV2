module fr.firedream.trieurseries {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.firedream89.trieurseries to javafx.fxml;
    exports fr.firedream89.trieurseries;
}