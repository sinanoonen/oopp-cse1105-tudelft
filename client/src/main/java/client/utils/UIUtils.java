package client.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * A utility class for UI operations.
 */
public class UIUtils {

    private static final int CONTRAST_THRESHOLD = 75;
    private static final double CONTRAST_MODIFIER = 0.86;
    private static final double BRIGHTNESS_MODIFIER = 23;

    private static final HashMap<Node, String> colorMap = new HashMap<>();

    private static final List<Node> activePages = new LinkedList<>();

    /**
     * Changes the color of a node.
     *
     * @param node the node to change the color of
     * @param color the new color
     */
    public static void changeColor(Node node, String color, String attribute) {
        String currentStyle = node.getStyle();
        String newColor = attribute + ": " + color + ";";

        if (currentStyle.contains(attribute)) {
            int startIndex = currentStyle.indexOf(attribute);
            int endIndex = currentStyle.indexOf(";", startIndex);
            currentStyle = currentStyle.substring(0, startIndex) + currentStyle.substring(endIndex + 1);
        }

        node.setStyle(currentStyle + newColor);
    }

    private static void increaseNodeContrast(Node node) {
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
            changeColor(node, newColor, "-fx-text-fill");
        } else if (node instanceof Text) {
            String newColor = String.format("#%02x%02x%02x", 255, 255, 255);
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

    private static void increaseColorContrast(int[] rgb) {
        int average = (rgb[0] + rgb[1] + rgb[2]) / 3;
        // if average is above threshold, add contrastModifier to all rgb values
        if (average > CONTRAST_THRESHOLD) {
            for (int i = 0; i < 3; i++) {
                rgb[i] = Math.min(255, (int) ((rgb[i] * (1.0 + CONTRAST_MODIFIER)) + BRIGHTNESS_MODIFIER));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                rgb[i] = Math.max(0, (int) ((rgb[i] * (1.0 - CONTRAST_MODIFIER)) + BRIGHTNESS_MODIFIER));
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
    public static void activateHighContrastMode(Node root) {

        if (activePages.contains(root)) {
            return;
        }

        increaseNodeContrast(root);
        activePages.add(root);

        if (root instanceof Parent && !(root instanceof ChoiceBox) && !(root instanceof Hyperlink)) {
            ((Parent) root).getChildrenUnmodifiable().forEach(UIUtils::activateHighContrastMode);
        }

    }

    /**
     * Deactivates high contrast mode for a given root node and recursively for all its children.
     *
     * @param root the root node
     */
    public static void deactivateHighContrastMode(Node root) {

        if (!activePages.contains(root)) {
            return;
        }

        if (root instanceof Parent) {
            ((Parent) root).getChildrenUnmodifiable().forEach(UIUtils::deactivateHighContrastMode);
        }

        if (colorMap.containsKey(root)) {
            root.setStyle(colorMap.get(root));
            activePages.remove(root);
        }

    }

    private static int[] hexToRGB(String hex) {
        hex = stringToHex(hex);

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new int[] {r, g, b};
    }

    private static String stringToHex(String color) {
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
}
