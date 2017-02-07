package pl.greywarden.openr.gui.directoryview.context_menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import pl.greywarden.openr.commons.IconManager;
import pl.greywarden.openr.gui.dialogs.ConfirmDeleteDialog;
import pl.greywarden.openr.gui.directoryview.DirectoryView;

import static pl.greywarden.openr.commons.I18nManager.getString;

public class MultipleSelectionContextMenu extends ContextMenu {

    private final DirectoryView directoryView;

    public MultipleSelectionContextMenu(DirectoryView directoryView) {
        super();
        this.directoryView = directoryView;
        buildOptions();
    }

    private void buildOptions() {
        MenuItem deletePermanently = createDeletePermanentlyMenuItem();
        MenuItem moveToTrash = createMoveToTrashMenuItem();

        super.getItems().setAll(deletePermanently, moveToTrash);
    }

    private MenuItem createMoveToTrashMenuItem() {
        MenuItem moveToTrash = new MenuItem(getString("move-to-trash"));
        moveToTrash.setGraphic(IconManager.getIcon("trash"));
        moveToTrash.setOnAction(event -> moveSelectedItemsToTrash());
        return moveToTrash;
    }

    private void moveSelectedItemsToTrash() {
        directoryView.getSelectionModel().getSelectedItems()
                .forEach(entryWrapper -> entryWrapper.getEntry().moveToTrash());
        directoryView.reload();
    }

    private MenuItem createDeletePermanentlyMenuItem() {
        MenuItem deletePermanently = new MenuItem(getString("delete-permanently-menu-item"));
        deletePermanently.setGraphic(IconManager.getIcon("delete-permanent-small"));
        deletePermanently.setOnAction(event -> new ConfirmDeleteDialog(directoryView));
        return deletePermanently;
    }
}
