package pl.greywarden.openr.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.greywarden.openr.component.DirectoryTableView;
import pl.greywarden.openr.component.DirectoryViewPathComponent;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
public class MainWindowView implements FxmlView<MainWindowViewModel>, Initializable {
    @FXML
    public DirectoryTableView dvLeft;
    @FXML
    public DirectoryTableView dvRight;

    @FXML
    private VBox mainContainer;
    @FXML
    private DirectoryViewPathComponent leftPath;
    @FXML
    private DirectoryViewPathComponent rightPath;
    @FXML
    private TextField selectedFile;

    @InjectViewModel
    private MainWindowViewModel mainWindowViewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureMainContainerSize();

        leftPath.textProperty().bindBidirectional(mainWindowViewModel.leftPathProperty());
        rightPath.textProperty().bindBidirectional(mainWindowViewModel.rightPathProperty());

        dvLeft.pathProperty().bindBidirectional(leftPath.textProperty());
        dvRight.pathProperty().bindBidirectional(rightPath.textProperty());

        selectedFile.textProperty().bindBidirectional(mainWindowViewModel.selectedFileProperty());

        Platform.runLater(() -> changeDirectory(dvLeft));
        Platform.runLater(() -> changeDirectory(dvRight));
    }

    private void changeDirectory(DirectoryTableView tableView) {
        var files = mainWindowViewModel.getFiles(tableView.pathProperty());
        tableView.setData(files);
    }

    private void configureMainContainerSize() {
        mainContainer.setPrefWidth(mainWindowViewModel.getWidth());
        mainContainer.setPrefHeight(mainWindowViewModel.getHeight());

        mainContainer.widthProperty().addListener(observable -> {
            if (!getWindow().isMaximized()) {
                mainWindowViewModel.mainWindowWidthProperty().setValue(((ReadOnlyDoubleProperty)observable).get());
            }
        });
        mainContainer.heightProperty().addListener(observable -> {
            if (!getWindow().isMaximized()) {
                mainWindowViewModel.mainWindowHeightProperty().setValue(((ReadOnlyDoubleProperty)observable).get());
            }
        });
    }

    @FXML
    private void exitApplication() {
        var confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        confirmationDialog.setTitle("Confirm exit");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to exit?");
        confirmationDialog
                .showAndWait()
                .filter(ButtonType.YES::equals)
                .ifPresent(b -> Platform.runLater(Platform::exit));
    }

    @FXML
    private void showAboutDialog() {
        var appVersion = mainWindowViewModel.getApplicationVersion();
        var content = new Label(String.format("OpenR file manager, version: %s%n%nAuthor: Marcin Las%nEmail: las.marcin.94@gmail.com", appVersion));
        var aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About");
        aboutDialog.setHeaderText("OpenR");
        aboutDialog.getDialogPane().setContent(content);
        
        aboutDialog.show();
    }

    private Stage getWindow() {
        return (Stage) mainContainer.getScene().getWindow();
    }

}
