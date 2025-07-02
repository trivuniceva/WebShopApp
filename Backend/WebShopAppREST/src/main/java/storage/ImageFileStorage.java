package storage;

import model.Image;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImageFileStorage {
    private final String filePath = "src/main/resources/files/images.json";

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private List<Image> images;

    public ImageFileStorage() {
        loadImages();
    }

    private void loadImages() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                images = new ArrayList<>();
                return;
            }
            images = mapper.readValue(file, new TypeReference<List<Image>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            images = new ArrayList<>();
        }
    }

    public Image findById(String id) {
        return images.stream()
                .filter(img -> img.getId().equals(id) && !img.isLogicallyDeleted())
                .findFirst()
                .orElse(null);
    }

    public List<Image> getAllImages() {
        return images;
    }

    public List<Image> getImagesByIds(List<String> ids) {
        List<Image> result = new ArrayList<>();
        for (String id : ids) {
            images.stream()
                    .filter(img -> img.getId().equals(id) && !img.isLogicallyDeleted())
                    .findFirst()
                    .ifPresent(result::add);
        }
        return result;
    }

    // NOVO: Metoda za dodavanje nove slike
    public Image addImage(Image newImage) {
        if (newImage.getId() == null || newImage.getId().isEmpty()) {
            newImage.setId(UUID.randomUUID().toString());
        }
        images.add(newImage);
        saveImages();
        return newImage;
    }

    public void updateImage(Image updatedImage) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getId().equals(updatedImage.getId())) {
                images.set(i, updatedImage);
                saveImages();
                return;
            }
        }
    }

    public void saveImages() {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, images);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(String id) {
        Image imageToDelete = findById(id);
        if (imageToDelete != null) {
            imageToDelete.setLogicallyDeleted(true);
            saveImages();
        }
    }
}