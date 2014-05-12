package parser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExampleParser {

    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static void processJSONResponse(String jsonString) {
        // Note that Craigslist JSON responses, as far as I can tell, take the following form:
        // [
        //     [
        //         {
        //             "PostingID": "4465780829",
        //             ...
        //         }, 
        //         {
        //             "GeoCluster": "113006608270", 
        //             "url": "/jsonsearch/sub/brk?geocluster=113006608270&key=2KQ1PZ5TVwqyEn8mlFbx7g",
        //             ...
        //         }, 
        //         {
        //             "PostingID": "4461343780", 
        //             ...
        //         }, 
        //     ], 
        //     {
        //         "NonGeocoded": 0, 
        //         ...
        //     }
        // ]
        // At the outermost level is a JSON array containing 2 entries.
        // The first of these entries is a JSON array containing the listings we want.
        // The second of these entries is a JSON object containing, among other things, the property "NonGeocoded".
        // We don't care about this second entry.
        // All we care about is the the list of listings --- i.e. the first entry in the outermost list.
        // Within the listings array, each entry is a JSON object which is either an individual listing,
        // or a geocluster.  We can tell which ones are geoclusters because geoclusters have a property
        // called "GeoCluster"; individual listing entries do not have this property.
        // (Note that both geoclusters and individual listing entries have a property called "PostingID",
        // but we only care about the PostingID of the individual listings; we ignore the PostingID property
        // of the geocluster entries.

        JSONArray jsonArray = new JSONArray(jsonString); // this is the outermost list
        JSONArray listings = jsonArray.getJSONArray(0);  // pull out the listings array

        // loop over the listings array, processing each one
        for (int i = 0; i < listings.length(); ++i) {
            JSONObject listing = listings.getJSONObject(i);
            if (listing.has("GeoCluster")) {
                // this one is a cluster
                String url = "http://newyork.craigslist.org" + listing.getString("url");
                //
                // TODO: initiate a request for `url` here
                //
            } else {
                // this one is an individual listing
                String postingID = listing.getString("PostingID");
                //
                // TODO: add this listing's PostingID to our master list of postings
                //
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String jsonString = readFile("sample.json");
        processJSONResponse(jsonString);
    }

}
