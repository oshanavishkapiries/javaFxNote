package com.editor.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class NoteTakingApp extends Application {
    private WebEngine webEngine;

    @Override
    public void start(Stage primaryStage) {

        WebView webView = new WebView();
        webEngine = webView.getEngine();


        String editorFilePath = getClass().getResource("/view/editor/index.html").toExternalForm();
        webEngine.load(editorFilePath);

        BorderPane root = new BorderPane();
        root.setCenter(webView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("JavaFX Note Taking App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String getEditorContent() {
        return (String) webEngine.executeScript("getEditorContent();");
    }


    public void setEditorContent(String content) {
        webEngine.executeScript("setEditorContent('" + content.replace("'", "\\'") + "');");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

