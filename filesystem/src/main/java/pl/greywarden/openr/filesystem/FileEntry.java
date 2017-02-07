package pl.greywarden.openr.filesystem;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

@Log4j
public class FileEntry extends AbstractEntry {

    public FileEntry(String path) {
        super(path);
    }

    @Override
    public void move(AbstractEntry target) {
        try{
            if (target.getEntryProperties().isDirectory()) {
                FileUtils.moveFileToDirectory(this.filesystemEntry, target.filesystemEntry, false);
            } else {
                FileUtils.moveFile(getFilesystemEntry(), target.getFilesystemEntry());
            }
        } catch (IOException exception) {
            log.error("Rename file exception", exception);
        }
    }

}
