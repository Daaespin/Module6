package cen3024;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class WordCGUI extends Application {
    private TextArea resultTextArea;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Word Frequency Counter");

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setPrefRowCount(40);
        Button countButton = new Button("Count");
        countButton.setOnAction(e -> countButtonClicked());

        BorderPane borderPane = new BorderPane();
        HBox buttonBox = new HBox(10, countButton);
        buttonBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(buttonBox);

        ScrollPane scrollPane = new ScrollPane(resultTextArea);
        scrollPane.setFitToWidth(true); 
        scrollPane.setFitToHeight(true);
        borderPane.setCenter(scrollPane);
        borderPane.setPadding(new Insets(10));
        
        Scene scene = new Scene(borderPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void countButtonClicked() {
        String url = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";

        new Thread(() -> {
            try {
                Map<String, Integer> wordFreq = getWordFreq(url);

                Platform.runLater(() -> displayWordFreq(wordFreq));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String removeHtmlTags(String html) {
        String strippedText = html.replaceAll("\\<.*?\\>", "");
        strippedText = strippedText.replaceAll("&\\w+;", "").replaceAll("[\"“”‘’]", "");
        strippedText = strippedText.replaceAll("\\s+", " ").trim();
        return strippedText;
    }
    
    private static Map<String, Integer> getWordFreq(String url) throws IOException {
    	
        Map<String, Integer> wordFreq = new HashMap<>();
        StringBuilder htmlBuilder = new StringBuilder();
        URLConnection connection = new URL(url).openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            boolean insidePoem = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<div class=\"chapter\">")) {
                    insidePoem = true;
                } else if (line.contains("</div><!--end chapter-->")) {
                    insidePoem = false;
                }
                if (insidePoem) {
                    htmlBuilder.append(line);
                    htmlBuilder.append("\n");
                }
            }
        }

        String poemHtml = htmlBuilder.toString();
        String strippedText = removeHtmlTags(poemHtml);
        String[] words = strippedText.split("[\\s\\p{Punct}]+");
        for (String word : words) {
            word = word.toLowerCase();
            if (!word.isEmpty()) {
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        return wordFreq;
    }

    private void displayWordFreq(Map<String, Integer> wordFreq) {
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((a, b) -> b.getValue()-a.getValue());
        pq.addAll(wordFreq.entrySet());

        StringBuilder resultBuilder = new StringBuilder();
        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            resultBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        resultTextArea.setText(resultBuilder.toString());
    }
}
