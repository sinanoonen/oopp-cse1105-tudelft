package client.utils;

import javafx.scene.Node;
import javafx.scene.Parent;

public class UIUtils {

    private static int CONTRAST_THRESHOLD = 75;
    private static double CONTRAST_MODIFIER = 0.86;
    private static double BRIGHTNESS_MODIFIER = 23;

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


        String backgroundColor = "";
        if(currentStyle.contains("-fx-background-color")) {
            int startIndex = currentStyle.indexOf("-fx-background-color");
            int endIndex = currentStyle.indexOf(";", startIndex);
            backgroundColor = currentStyle.substring(startIndex, endIndex);
            backgroundColor = backgroundColor.replace("-fx-background-color: ", "");
        }

        String fillColor = "";
        if(currentStyle.contains("-fx-fill")) {
            int startIndex = currentStyle.indexOf("-fx-fill");
            int endIndex = currentStyle.indexOf(";", startIndex);
            fillColor = currentStyle.substring(startIndex, endIndex);
            fillColor = fillColor.replace("-fx-fill: ", "");
        }



        String borderColor = "";
        if(currentStyle.contains("-fx-border-color")) {
            int startIndex = currentStyle.indexOf("-fx-border-color");
            int endIndex = currentStyle.indexOf(";", startIndex);
            borderColor = currentStyle.substring(startIndex, endIndex);
            borderColor = borderColor.replace("-fx-border-color: ", "");
        }

        if(!backgroundColor.isEmpty() && !backgroundColor.equals("transparent")) {
            int[] rgb = hexToRGB(backgroundColor);
            increaseColorContrast(rgb);
            String newColor = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            changeColor(node, newColor, "-fx-background-color");
        }

        if(!fillColor.isEmpty() && !fillColor.equals("transparent")) {
            int[] rgb = hexToRGB(fillColor);
            increaseColorContrast(rgb);
            String newColor = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            changeColor(node, newColor, "-fx-fill");
        }

        if(!borderColor.isEmpty() && !borderColor.equals("transparent")) {
            int[] rgb = hexToRGB(borderColor);
            increaseColorContrast(rgb);
            String newColor = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
            changeColor(node, newColor, "-fx-border-color");
        }
    }

    private static void increaseColorContrast(int[] rgb) {
        int average = (rgb[0] + rgb[1] + rgb[2]) / 3;
        // if average is above threshold, add contrastModifier to all rgb values
        if(average > CONTRAST_THRESHOLD) {
            for(int i = 0; i < 3; i++) {
                rgb[i] = Math.min(255, (int) ((rgb[i] * (1.0 + CONTRAST_MODIFIER)) + BRIGHTNESS_MODIFIER));
            }
        } else {
            for(int i = 0; i < 3; i++) {
                rgb[i] = Math.max(0, (int) ((rgb[i] * (1.0 - CONTRAST_MODIFIER)) + BRIGHTNESS_MODIFIER));
            }
        }

        for(int i = 0; i < 3; i++) {
            rgb[i] = Math.min(255, rgb[i]);
            rgb[i] = Math.max(0, rgb[i]);
        }
    }

    public static void activateHighContrastMode(Parent root) {
        increaseNodeContrast(root);

        //get root children
        root.getChildrenUnmodifiable().forEach(node -> {
            if(node instanceof Parent) {
                activateHighContrastMode((Parent) node);
            } else {
                increaseNodeContrast(node);
            }
        });
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
        switch (color) {
            case "black":
                return "#000000";
            case "white":
                return "#FFFFFF";
            case "red":
                return "#FF0000";
            case "green":
                return "#00FF00";
            case "blue":
                return "#0000FF";
            case "yellow":
                return "#FFFF00";
            case "cyan":
                return "#00FFFF";
            case "magenta":
                return "#FF00FF";
            case "gray":
                return "#808080";
            default:
                return color;
        }
    }
}
