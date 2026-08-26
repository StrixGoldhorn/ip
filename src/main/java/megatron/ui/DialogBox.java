package megatron.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message with a simple avatar and message bubble.
 */
public final class DialogBox extends HBox {
    private static final String USER_AVATAR_PATH = "/images/user-avatar.png";
    private static final String MEGATRON_AVATAR_PATH = "/images/megatron-avatar.png";
    private static final double AVATAR_SIZE = 128;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView avatar;

    /**
     * Creates a dialog box for the given message.
     *
     * @param message The message text.
     * @param isUserMessage Whether the message was entered by the user.
     */
    public DialogBox(String message, boolean isUserMessage) {
        loadLayout();

        messageLabel.setText(message);
        avatar.setImage(loadAvatar(isUserMessage ? USER_AVATAR_PATH : MEGATRON_AVATAR_PATH));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add(isUserMessage ? "user-avatar" : "megatron-avatar");

        setSpacing(10);
        setAlignment(isUserMessage ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getStyleClass().add(isUserMessage ? "user-dialog" : "megatron-dialog");

        if (isUserMessage) {
            flip();
        }
    }

    /**
     * Loads the reusable dialog box layout from FXML.
     */
    private void loadLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }
    }

    /**
     * Reverses the child order for a user message.
     */
    private void flip() {
        var children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }

    /**
     * Loads an avatar image from the application resources.
     *
     * @param resourcePath The avatar resource path.
     * @return The loaded avatar image.
     */
    private static Image loadAvatar(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(DialogBox.class.getResourceAsStream(resourcePath));
        return new Image(imageStream);
    }
}
