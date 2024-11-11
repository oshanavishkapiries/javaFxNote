module com.editor.editor {
    requires javafx.fxml;
    requires javafx.web;


    opens com.editor.editor to javafx.fxml;
    exports com.editor.editor;
}