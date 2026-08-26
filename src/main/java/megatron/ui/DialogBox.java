package megatron.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one chat message with a simple avatar and message bubble.
 */
public final class DialogBox extends HBox {
    private static final String USER_AVATAR = "You";
    private static final String MEGATRON_AVATAR = "Meg";

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

        Label avatar = new Label(isUserMessage ? USER_AVATAR : MEGATRON_AVATAR);
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
}
