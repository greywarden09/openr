package pl.greywarden.openr.gui.directoryview;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import pl.greywarden.openr.filesystem.AbstractEntry;
import pl.greywarden.openr.filesystem.DirectoryEntry;
import pl.greywarden.openr.filesystem.EntryWrapper;
import pl.greywarden.openr.i18n.I18nManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Log4j
public class DirectoryView extends TableView {

    private DirectoryEntry rootEntry;

    private final I18nManager i18n = I18nManager.getInstance();

    @Getter
    private String rootPath;

    public DirectoryView(String rootPath) {
        super.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        build(rootPath);
    }

    public void changePath(String rootPath) {
        this.rootPath = rootPath;
        rootEntry = new DirectoryEntry(rootPath);
        loadData();
    }

    private void build(String rootPath) {
        changePath(rootPath);
        i18n.setBundle("directory-view");
        createColumns();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        DirectoryViewDataBuilder builder = new DirectoryViewDataBuilder(rootEntry);
        makeFirstRowAlwaysFirst();
        List<EntryWrapper> data = builder.getData();
        super.setItems(FXCollections.observableList(data));
        super.refresh();
    }

    @SuppressWarnings("unchecked")
    private void makeFirstRowAlwaysFirst() {
        super.sortPolicyProperty().set((Callback<TableView<EntryWrapper>, Boolean>) param -> {
            FXCollections.sort(getItems(), getWrapperComparator(param));
            return true;
        });
    }

    private Comparator<EntryWrapper> getWrapperComparator(TableView<EntryWrapper> param) {
        return (r1, r2) -> {
                    if (Objects.equals(r1.getName(), "..")) {
                        return -1;
                    } else if (Objects.equals(r2.getName(), "..")) {
                        return 1;
                    } else if (param.getComparator() == null) {
                        return 0;
                    } else {
                        return param.getComparator().compare(r1, r2);
                    }
                };
    }

    @SuppressWarnings("unchecked")
    private void createColumns() {
        final TableColumn
                name = new TableColumn(i18n.getString("name")),
                extension = new TableColumn(i18n.getString("extension")),
                size = new TableColumn(i18n.getString("size")),
                modificationDate = new TableColumn(i18n.getString("modification-date")),
                privileges = new TableColumn(i18n.getString("privileges"));

        name.setCellValueFactory(new PropertyValueFactory<>("entry"));
        name.setCellFactory(param -> new DirectoryViewEntryNameCell());
        name.setComparator(nameComparator());
        name.minWidthProperty().bind(super.widthProperty().multiply(0.3));

        extension.setCellValueFactory(new PropertyValueFactory<>("extension"));
        extension.setCellFactory(param -> new DirectoryViewEntryStringPropertyCell());
        extension.setComparator(stringComparator());

        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        size.setCellFactory(param -> new DirectoryViewEntrySizePropertyCell());
        size.setComparator(longComparator());

        modificationDate.setCellValueFactory(new PropertyValueFactory<>("modificationDate"));
        modificationDate.setCellFactory(param -> new DirectoryViewEntryDatePropertyCell());
        modificationDate.minWidthProperty().bind(super.widthProperty().multiply((1 - 0.3) * 0.08));
        modificationDate.setComparator(dateComparator());

        privileges.setCellValueFactory(new PropertyValueFactory<>("privileges"));
        privileges.setCellFactory(param -> new DirectoryViewEntryStringPropertyCell());
        privileges.setComparator(stringComparator());

        super.getColumns().addAll(name, extension, size, modificationDate, privileges);
        setRowFactory();
    }

    private Comparator<AbstractEntry> nameComparator() {
        return (o1, o2) -> {
            String n1 = o1.getEntryProperties().getBaseName();
            String n2 = o2.getEntryProperties().getBaseName();
            if ("..".equals(n1) || "..".equals(n2)) {
                return -1;
            }
            return n1.compareToIgnoreCase(n2);
        };
    }

    private Comparator<String> stringComparator(){
        return (o1, o2) -> {
            if ("..".equals(o1) || "..".equals(o2)) {
                return -1;
            }
            return o1.compareToIgnoreCase(o2);
        };
    }

    private Comparator<Date> dateComparator() {
        return (o1, o2) -> {
            if (o1.equals(new Date(Long.MIN_VALUE)) || o2.equals(new Date(Long.MIN_VALUE))) {
                return -1;
            }
            return o1.compareTo(o2);
        };
    }

    private Comparator<Long> longComparator() {
        return (o1, o2) -> {
            if (o1.equals(Long.MIN_VALUE) || o2.equals(Long.MIN_VALUE)) {
                return -1;
            }
            return o1.compareTo(o2);
        };
    }

    @SuppressWarnings("unchecked")
    private void setRowFactory() {
        super.setRowFactory(tv -> createTableRow());
    }

    private Object createTableRow() {
        TableRow<EntryWrapper> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                EntryWrapper rowData = row.getItem();
                if (rowData.getEntry().getEntryProperties().isDirectory()) {
                    TableViewSelectionModel model = super.getSelectionModel();
                    model.setSelectionMode(null);
                    changePath(rowData.getEntry().getEntryProperties().getAbsolutePath());
                } else {
                    if (Desktop.isDesktopSupported()) {
                        File target = new File(rowData.getEntry().getEntryProperties().getAbsolutePath());
                        new Thread(() -> {
                            try {
                                Desktop.getDesktop().browse(target.toURI());
                            } catch (IOException exception) {
                                log.error("Unable to open selected file", exception);
                            }
                        }).start();
                    }
                }
            }
        });
        return row;
    }

    public void reload() {
        changePath(rootPath);
    }

}