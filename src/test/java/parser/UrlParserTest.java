package parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class UrlParserTest {

    @Test
    public void testUrlToText() throws IOException {
        String contents = UrlParser.urlToText("http://www.google.com");
        System.out.println(contents);
//        fail("Not yet implemented");
    }
    @Test
    public void testFileUrlToText() throws IOException {

        final String dir = System.getProperty("user.dir");
//        System.out.println("current dir = " + dir);
        String baseUrl = "file://" + dir;
        String contents = UrlParser.urlToText(baseUrl + "/src/test/resources/jsonsamples/sample.json");
        System.out.println(contents);
    }

}
