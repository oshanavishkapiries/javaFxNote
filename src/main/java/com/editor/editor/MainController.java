package com.editor.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainController {

    @FXML
    private WebView webView;

    private WebEngine webEngine;
    private String currentFilePath = null;

    private static final String SECRET_KEY = "1234567890123456";

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();
        String editorFilePath = getClass().getResource("/view/editor/index.html").toExternalForm();
        webEngine.load(editorFilePath);
    }

    public String getEditorContent() {
        return (String) webEngine.executeScript("getEditorContent();");
    }

    public void setEditorContent(String content) {
        webEngine.executeScript("setEditorContent('" + content.replace("'", "\\'") + "');");
    }

    @FXML
    protected void onSaveButtonClick() {
        try {
            String content = getEditorContent();
            if (currentFilePath == null) {
                String directoryPath = "saveNotes";
                Files.createDirectories(Paths.get(directoryPath));
                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                currentFilePath = directoryPath + "/note_" + timestamp + ".enc";
            }

            byte[] encryptedContent = encrypt(content, SECRET_KEY);
            try (FileOutputStream fos = new FileOutputStream(currentFilePath)) {
                fos.write(encryptedContent);
            }

            showNotification("Note saved at: ", new File(currentFilePath).getAbsolutePath());
            System.out.println(new File(currentFilePath).getAbsolutePath());
            setEditorContent("");
            currentFilePath = null;
        } catch (Exception e) {
            showNotification("Failed to save the note.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLoadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Note");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Encrypted Files", "*.enc"));
        fileChooser.setInitialDirectory(new File("saveNotes"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                byte[] encryptedContent = Files.readAllBytes(selectedFile.toPath());
                String decryptedContent = decrypt(encryptedContent, SECRET_KEY);
                setEditorContent(decryptedContent);
                currentFilePath = selectedFile.getAbsolutePath();
                showNotification("Note loaded successfully", "from:\n" + selectedFile.getAbsolutePath());
            } catch (Exception e) {
                showNotification("Failed to load note: ", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private byte[] encrypt(String content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(content.getBytes());
    }

    private String decrypt(byte[] encryptedContent, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedContent);
        return new String(decryptedBytes);
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
