package fr.firedream89.trieurseries;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class TrieurSeries extends Application {
    private static Stage stage;
    private static TrieurSeries ts;
    private SeriesManager series;

    private String currentPath;
    private List<List<String>> listSeries;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TrieurSeries.class.getResource("trieurseries-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Trieur Series");
        stage.setScene(scene);
        stage.show();

        TrieurSeries.stage = stage;
        ts = this;
        series = new SeriesManager();
    }

    public static void main(String[] args) {
        launch();
    }

    public static TrieurSeries getInstance() {
        if(ts == null) {
            ts = new TrieurSeries();
        }
        return ts;
    }

    public String getFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Selectionner un dossier");
        File directory = new File("D:/Film attente");
        chooser.setInitialDirectory(directory);
        File finalDirectory = chooser.showDialog(stage);
        System.out.println(finalDirectory.getAbsolutePath());

        currentPath = finalDirectory.getAbsolutePath();
        return finalDirectory.getAbsolutePath();
    }

    public void generate(TreeView<String> tree, String path, String extensionFilter, boolean directoryOnly) {
        ArrayList<File> directoryList = Outils.getItemsInDirectory(path, extensionFilter.split("\\|"));
        System.out.println("Count : " + directoryList.size());

        //création racine
        tree.setRoot(new TreeItem<>(path));
        tree.getRoot().setExpanded(true);

        for(File f : directoryList) {
            if(f.isDirectory() || !directoryOnly)
                tree.getTreeItem(0).getChildren().add(addTreeChild(path + "\\\\" + f.getName() + "\\\\", extensionFilter.split("\\|"),directoryOnly));
        }
    }

    private TreeItem<String> addTreeChild(String path, String[] filter, boolean directoryOnly) {
        ArrayList<File> directoryList = Outils.getItemsInDirectory(path, filter);
        TreeItem<String> tree = new TreeItem<>(path.split("\\\\")[path.split("\\\\").length-1]);
        for(File f : directoryList) {
            TreeItem<String> cTree = new TreeItem<>(f.getName());
            ArrayList<File> sousDirectoryList = Outils.getItemsInDirectory(path + f.getName() + "\\\\", filter);
            if(f.isFile() && !directoryOnly) {
                tree.getChildren().add(cTree);
            } else if(f.isDirectory()) {
                tree.getChildren().add(addTreeChild(path + f.getName() + "\\\\", filter, directoryOnly));
            }
        }
        return tree;
    }

    public void generateAfter(TreeView<String> tree, String path, String[] extensionFilter) {
        getListOfFiles(currentPath, extensionFilter);
        tree.setRoot(new TreeItem<>(path));
        tree.getRoot().setExpanded(true);
        for(Serie serie : series) {
            TreeItem<String> itemSerie = findChild(tree.getRoot(),serie.getName());
            if(itemSerie.getParent() == null) { //ajout serie si inexistante
                tree.getRoot().getChildren().add(itemSerie);
            }
            //Ajout nouvelle saison à la serie
            TreeItem<String> itemSaison = new TreeItem<>("Saison " + serie.getSeason());
            itemSerie.getChildren().add(itemSaison);
            //Ajout des épisodes à saison
            for(File episode : serie) {
                itemSaison.getChildren().add(new TreeItem<>(episode.getName()));
            }
        }
        showList();
    }


    /**
     * Retourne l'item si trouvé sinon retourne un item nouvellement généré
     * @param parent
     * @param name
     * @return
     */
    private TreeItem<String> findChild(TreeItem<String> parent, String name) {
        for(TreeItem<String> item : parent.getChildren()) {
            if(item.getValue().equals(name))
                return item;
        }
        return new TreeItem<>(name);
    }

    private boolean isExist(TreeItem<String> parent, String name) {
        for(TreeItem<String> item : parent.getChildren()) {
            if(item.getValue().equals(name))
                return true;
        }
        return false;
    }

    private void getListOfFiles(String path, String[] filter) {
        List<File> fileList = Outils.getFilesInDirectory(path, filter,true);
        series = new SeriesManager();

        for(File f : fileList) {
            String[] nomEtSaison = Outils.extraireNomEtNumSaison(f.getName());
            if (nomEtSaison[0] != null && nomEtSaison[1] != null && !nomEtSaison[0].isEmpty()) {
                Serie serie = series.findSerie(nomEtSaison[0], nomEtSaison[1]);
                if(serie == null) {
                    serie = new Serie(nomEtSaison[0], nomEtSaison[1]);
                    series.add(serie);
                }
                serie.addEpisode(f);
            }
        }
    }

    private void showList() {
        System.out.println(series);
    }

    public void trier() throws IOException {
        if(series != null && currentPath != null) {
            boolean isOk = true;
            StringBuffer error = new StringBuffer();

            for(Serie serie : series) {
                String path = currentPath + "/" + serie.getName() + "/Saison " + serie.getSeason();
                File mkPath = new File(path);
                mkPath.mkdirs();
                for(File episode : serie) {
                    //Move file
                    Path oldPath = Paths.get(episode.getAbsolutePath());
                    Path newPath = Paths.get(path + "/" + episode.getName());
                    Files.move(oldPath,newPath);

                    //Remove NFO
                    List<File> nfo = Outils.getFilesInDirectory(episode.getAbsolutePath(), new String[]{"nfo"},false);
                    for(File rm : nfo) {
                        if(!rm.delete()) {
                            isOk = false;
                            error.append(rm.getPath() + " non supprimé\n");
                        }
                    }

                    //Remove Dir
                    File rmPath = new File(episode.getParent());
                    rmPath.delete();
                }
            }
            if(!isOk) {
                new Alert(Alert.AlertType.ERROR, error.toString());
            }
            new Alert(Alert.AlertType.INFORMATION,"Opération terminé !").show();
        }
    }
}