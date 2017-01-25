package pl.greywarden.openr.gui.dialogs;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import pl.greywarden.openr.filesystem.AbstractEntry;
import pl.greywarden.openr.gui.directoryview.DirectoryView;
import pl.greywarden.openr.i18n.I18nManager;

import java.io.File;

public class RenameDialog extends Dialog <ButtonType> {

    public RenameDialog(DirectoryView directoryView) {
        I18nManager i18n = I18nManager.getInstance();
        i18n.setBundle("rename-dialog");
        GridPane layout = new GridPane();
        layout.setHgap(5);

        ButtonType ok = new ButtonType(i18n.getString("ok"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(i18n.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        Label newNameLabel = new Label(i18n.getString("new-name") + ":");
        TextField newName = new TextField();

        AbstractEntry selectedItem = directoryView.getSelectionModel().getSelectedItem().getEntry();

        layout.addRow(0, newNameLabel, newName);
        super.setTitle(i18n.getString("rename"));
        super.getDialogPane().getButtonTypes().setAll(ok, cancel);
        super.getDialogPane().setContent(layout);
        Node buttonOk = super.getDialogPane().lookupButton(ok);
        newName.textProperty().addListener((observable, oldValue, newValue) -> {
            String extension = selectedItem.getEntryProperties().getExtension();
            if (!extension.isEmpty()) {
                extension = "." + extension;
            }
            File targetFile = new File(selectedItem.getEntryProperties().getParentFile(),
                    newValue + extension);
            buttonOk.setDisable(
                    newValue.trim().isEmpty() ||
                            targetFile.exists());
        });
        buttonOk.setDisable(true);
        super.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                File sourceDir = selectedItem.getFilesystemEntry();
                File target = new File(selectedItem.getEntryProperties().getParentFile(), newName.getText());
                sourceDir.renameTo(target);
                directoryView.reload();
            }
        });
    }

}
