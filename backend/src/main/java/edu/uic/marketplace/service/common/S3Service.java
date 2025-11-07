package edu.uic.marketplace.service.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String upload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Empty file");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image/* allowed");
        }

        String safeName = sanitize(file.getOriginalFilename());
        String key = "listings/" + UUID.randomUUID() + "-" + safeName;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, key, inputStream, metadata));

            return amazonS3.getUrl(bucket, key).toString(); // return S3 public URL
        } catch (IOException e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    public void deleteByUrl(String url) {
        String key = extractKey(url);
        if (key != null) {
            amazonS3.deleteObject(bucket, key);
        }
    }

    public void deleteFiles(List<String> urls) {

        if (urls == null || urls.isEmpty()) return;

        List<String> keys = urls.stream()
                .filter(Objects::nonNull)
                .map(this::extractKey)
                .filter(k -> k != null && !k.isBlank())
                .collect(Collectors.toList());

        if (keys.isEmpty()) return;

        DeleteObjectsRequest req = new DeleteObjectsRequest(bucket)
                .withKeys(keys.toArray(new String[0]));

        amazonS3.deleteObjects(req);
    }

    private String sanitize(String originalName) {

        String name = (originalName == null ? "file" : originalName);
        name = name.replaceAll("[\\p{Cntrl}\\\\/:*?\"<>|]+", "_");
        if (name.length() > 80) {
            name = name.substring(name.length() - 80);
        }

        return name;
    }

    private String extractKey(String url) {

        int index = url.indexOf(".amazonaws.com/");
        if (index == -1)
            return null;

        return url.substring(index + ".amazonaws.com/".length());
    }
}
