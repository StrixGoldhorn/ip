package megatron.ui;

import java.io.InputStream;
import java.util.Objects;

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

    /**
     * Creates a dialog box for the given message.
     *
     * @param message The message text.
     * @param isUserMessage Whether the message was entered by the user.
     */
    public DialogBox(String message, boolean isUserMessage) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-text");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(450);

        ImageView avatar = new ImageView(loadAvatar(isUserMessage
                ? USER_AVATAR_PATH : MEGATRON_AVATAR_PATH));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.getStyleClass().add(isUserMessage ? "user-avatar" : "megatron-avatar");

        setSpacing(10);
        setAlignment(isUserMessage ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getStyleClass().add(isUserMessage ? "user-dialog" : "megatron-dialog");

        if (isUserMessage) {
            getChildren().addAll(messageLabel, avatar);
        } else {
            getChildren().addAll(avatar, messageLabel);
        }
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
