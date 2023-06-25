package cen3024;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordCGUITest {

    @Test
    public void testGetWordFreq() throws IOException {
        String url = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";
        Map<String, Integer> wordFreq = WordCGUI.getWordFreq(url);
        assertTrue(wordFreq.containsKey("word"));
        assertEquals(2, wordFreq.get("word"));
    }

    @Test
    public void testRemoveHtmlTags() {
        String html = "<div class=\"chapter\">This is <b>some</b> text.</div><!--end chapter-->";
        String strippedText = WordCGUI.removeHtmlTags(html);
        assertEquals("This is some text.", strippedText);
    }
}
