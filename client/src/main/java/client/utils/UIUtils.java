package client.utils;

import client.enums.Language;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.springframework.stereotype.Service;

/**
 * A utility class for UI operations.
 */
@Service
public class UIUtils {

    private final int contrastThreshold = 75;
    private final double contrastModifier = 0.86;
    private final double brightnessModifier = 23;
    private final double tooltipFontSize = 12;

    private final HashMap<Node, String> colorMap = new HashMap<>();

    private final List<Node> activePages = new LinkedList<>();

    private Map<String, String> languageMap = new HashMap<>();

    public UIUtils() {}

    /**
     * Changes the color of a node.
     *
     * @param node the node to change the color of
     * @param color the new color
     */
    public void changeColor(Node node, String color, String attribute) {
        String currentStyle = node.getStyle();
        String newColor = attribute + ": " + color + ";";

        if (currentStyle.contains(attribute)) {
            int startIndex = currentStyle.indexOf(attribute);
            int endIndex = currentStyle.indexOf(";", startIndex);
            currentStyle = currentStyle.substring(0, startIndex) + currentStyle.substring(endIndex + 1);
        }

        node.setStyle(currentStyle + newColor);
    }

    private void increaseNodeContrast(Node node) {
        String currentStyle = node.getStyle();
        if (!colorMap.containsKey(node)) {
            colorMap.put(node, currentStyle);
        }


        String backgroundColor = "";
        if (currentStyle.contains("-fx-background-color")) {
            int startIndex = currentStyle.indexOf("-fx-background-color");
            int endIndex = currentStyle.indexOf(";", startIndex);
            backgroundColor = currentStyle.substring(startIndex, endIndex);
            backgroundColor = backgroundColor.replace("-fx-background-color: ", "");
        }

        String fillColor = "";
        if (currentStyle.contains("-fx-fill")) {
            int startIndex = currentStyle.indexOf("-fx-fill");
            int endIndex = currentStyle.indexOf(";", startIndex);
            fillColor = currentStyle.substring(startIndex, endIndex);
            fillColor = fillColor.replace("-fx-fill: ", "");
        }

        String borderColor = "";
        if (currentStyle.contains("-fx-border-color")) {
            int startIndex = currentStyle.indexOf("-fx-border-color");
            int endIndex = currentStyle.indexOf(";", startIndex);
            borderColor = currentStyle.substring(startIndex, endIndex);
            borderColor = borderColor.replace("-fx-border-color: ", "");
        }

        if (!backgroundColor.isEmpty() && !backgroundColor.equals("transparent")) {
            int[] rgb = hexToRGB(backgroundColor);
            increaseColorContrast(rgb);
            String newColor = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            changeColor(node, newColor, "-fx-background-color");
        }

        if (node instanceof Hyperlink) {
            String newColor = String.format("#%02x%02x%02x", 255, 255, 255);

            String oldColor = ((Hyperlink) node).getTextFill().toString();
            oldColor = "#" + oldColor.substring(2, 8);

            if (!colorMap.get(node).contains("-fx-text-fill")) {
                colorMap.put(node, colorMap.get(node) + "-fx-text-fill: " + oldColor + ";");
            }

            changeColor(node, newColor, "-fx-text-fill");
        } else if (node instanceof Text) {
            String newColor = String.format("#%02x%02x%02x", 255, 255, 255);

            String oldColor = ((Text) node).getFill().toString();
            oldColor = "#" + oldColor.substring(2, 8);

            if (!colorMap.get(node).contains("-fx-fill")) {
                colorMap.put(node, colorMap.get(node) + "-fx-fill: " + oldColor + ";");
            }

            changeColor(node, newColor, "-fx-fill");
        } else if (!fillColor.isEmpty() && !fillColor.equals("transparent")) {
            int[] rgb = hexToRGB(fillColor);
            increaseColorContrast(rgb);
            String newColor = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            changeColor(node, newColor, "-fx-fill");
        }

        if (!borderColor.isEmpty() && !borderColor.equals("transparent")) {
            int[] rgb = hexToRGB(borderColor);
            increaseColorContrast(rgb);
            String newColor = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            changeColor(node, newColor, "-fx-border-color");
        }
    }

    private void increaseColorContrast(int[] rgb) {
        int average = (rgb[0] + rgb[1] + rgb[2]) / 3;
        // if average is above threshold, add contrastModifier to all rgb values
        if (average > contrastThreshold) {
            for (int i = 0; i < 3; i++) {
                rgb[i] = Math.min(255, (int) ((rgb[i] * (1.0 + contrastModifier)) + brightnessModifier));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                rgb[i] = Math.max(0, (int) ((rgb[i] * (1.0 - contrastModifier)) + brightnessModifier));
            }
        }

        for (int i = 0; i < 3; i++) {
            rgb[i] = Math.min(255, rgb[i]);
            rgb[i] = Math.max(0, rgb[i]);
        }
    }

    /**
     * Activates high contrast mode for a given root node and recursively for all its children.
     *
     * @param root the root node
     */
    public void activateHighContrastMode(Node root) {

        if (activePages.contains(root)) {
            return;
        }

        increaseNodeContrast(root);
        activePages.add(root);

        if (root instanceof Parent && !(root instanceof ChoiceBox) && !(root instanceof Hyperlink)) {
            ((Parent) root).getChildrenUnmodifiable().forEach(this::activateHighContrastMode);
        }

    }

    /**
     * Deactivates high contrast mode for a given root node and recursively for all its children.
     *
     * @param root the root node
     */
    public void deactivateHighContrastMode(Node root) {

        if (!activePages.contains(root)) {
            return;
        }

        if (root instanceof Parent) {
            ((Parent) root).getChildrenUnmodifiable().forEach(this::deactivateHighContrastMode);
        }

        if (colorMap.containsKey(root)) {
            root.setStyle(colorMap.get(root));
            activePages.remove(root);
        }

    }

    private int[] hexToRGB(String hex) {
        hex = stringToHex(hex);

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new int[] {r, g, b};
    }

    private String stringToHex(String color) {
        return switch (color) {
            case "black" -> "#000000";
            case "white" -> "#FFFFFF";
            case "red" -> "#FF0000";
            case "green" -> "#00FF00";
            case "blue" -> "#0000FF";
            case "yellow" -> "#FFFF00";
            case "cyan" -> "#00FFFF";
            case "magenta" -> "#FF00FF";
            case "gray" -> "#808080";
            default -> color;
        };
    }

    /**
     * This warning is shown when the event was deleted.
     *
     * @param eventTitle the title of the event
     */
    public void showEventDeletedWarning(String eventTitle) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Event Deleted");
        alert.setHeaderText(null);
        alert.setContentText("The event '" + eventTitle + "' has been deleted.");
        alert.showAndWait();
    }

    /**
     * Adds a tooltip to a node.
     *
     * @param node node to add tooltip to
     * @param text text to display in tooltip
     */
    public void addTooltip(Node node, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setFont(new Font("SansSerif", tooltipFontSize));
        Tooltip.install(node, tooltip);
    }

    /**
     * Loads the language json key-value pairs into the languageMap.
     * This can be accessed through the getLanguageMap() method.
     *
     * @param language language to load into map
     */
    @SuppressWarnings("unchecked")
    public void loadLanguageMap(Language language) {
        String path = "src/main/resources/client/languages/" + language.toString().toLowerCase() + ".json";
        ObjectMapper mapper = new ObjectMapper();
        try {
            languageMap = mapper.readValue(new File(path), Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getLanguageMap() {
        return languageMap;
    }
}
