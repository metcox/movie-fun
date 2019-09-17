package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Component
public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        java.nio.file.Files.copy(
                blob.inputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        try {
            blob.inputStream.close();
        } catch (IOException e) {
            // murlock
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        File coverFile = new File(name);
        Path coverFilePath = coverFile.toPath();

        Tika tika = new Tika();
        String mimeType = tika.detect(coverFile);

        if (coverFile.exists()) {
            return Optional.of(new Blob(name, new FileInputStream(coverFile),  mimeType));
        }

        return Optional.empty();
    }

    @Override
    public void deleteAll() {
    }
}