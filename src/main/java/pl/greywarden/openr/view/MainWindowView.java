package pl.greywarden.openr.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
public class MainWindowView implements FxmlView<MainWindowViewModel>, Initializable {
    @FXML
    private TextField selectedFile;

    @InjectViewModel
    private MainWindowViewModel mainWindowViewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFile.textProperty().bindBidirectional(mainWindowViewModel.selectedFileProperty());
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
}
