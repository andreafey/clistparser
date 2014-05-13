package parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CraigslistParserTest {
    private final String BASE_URL = "file://" + System.getProperty("user.dir") + "/src/test/resources/data1";

    @Test
    public void testFetchListings() {
        List<String> list = CraigslistParser.fetchListings(BASE_URL, "/0000.json");
        for (String item : list) {
            System.out.println(item);
        }
    }

}
