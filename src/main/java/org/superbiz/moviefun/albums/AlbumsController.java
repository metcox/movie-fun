package org.superbiz.moviefun.albums;

import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToBlob(albumId, uploadedFile);

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Blob blob = getExistingCoverBlob(albumId);
        byte[] imageBytes = IOUtils.toByteArray(blob.inputStream);
        HttpHeaders headers = createImageHttpHeaders(blob.contentType, imageBytes);

        return new HttpEntity<>(imageBytes, headers);
    }

    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(uploadedFile.getBytes());
        }
    }

    private void saveUploadToBlob(long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        blobStore.put(new Blob(getCoverName(albumId), uploadedFile.getInputStream(), uploadedFile.getContentType()));
    }

    private HttpHeaders createImageHttpHeaders(String contentType, byte[] imageBytes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private String getCoverName(@PathVariable long albumId) {
        return format("covers/%d", albumId);
    }

    private Blob getExistingCoverBlob(@PathVariable long albumId) throws URISyntaxException {
        String name = getCoverName(albumId);
        Path coverFilePath;

        Blob blob = null;
        try {
            blob = this.blobStore.get(name).orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(null != blob) {
            return blob;
        }
        try {
            return new Blob(name, getSystemResource("default-cover.jpg").openStream(), MediaType.IMAGE_JPEG_VALUE);
        } catch (IOException e) {
            return null;
        }
    }
}
