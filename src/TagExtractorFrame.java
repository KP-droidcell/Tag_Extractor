import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class TagExtractorFrame extends JFrame
{
    private JFileChooser fileChooser;
    private JTextArea textArea;
    private Map<String, Integer> wordFrequency = new HashMap<>();
    private Set<String> stopWords;

    public TagExtractorFrame()
    {
        setTitle("Tag Extactor");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        textArea = new JTextArea();
        fileChooser = new JFileChooser();
        wordFrequency = new HashMap<>();
        stopWords = new TreeSet<>();
        loadStopWords();
        JButton openButton = new JButton("Open File");
        JButton saveButton = new JButton("Save Tags");
        openButton.addActionListener(e -> openFile());
        saveButton.addActionListener(e -> saveTags());
        setVisible(true);

    }
    private void loadStopWords()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null)
            {
                stopWords.add(line.toLowerCase());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            extractTags(file);
        }
    }

    private void extractTags(File file) {
        wordFrequency.clear();
        textArea.setText("");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayTags();
    }

    private void displayTags() {
        List<Map.Entry<String, Integer>> sortedEntries = wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            sb.append(entry.getKey()).append(" (").append(entry.getValue()).append(")\n");
        }
        textArea.setText(sb.toString());
    }

    private void saveTags() {
        List<Map.Entry<String, Integer>> sortedEntries = wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tags.txt"))) {
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
