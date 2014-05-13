package parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;

import org.json.JSONArray;
import org.json.JSONObject;

public class CraigslistFetcher implements Runnable {
    private final String baseUrl;
    private final URL url;
    private final List<String> listingIds;
    private final Executor executor;
    
    public CraigslistFetcher(String baseUrl, String url, List<String> listings, Executor executor) 
            throws MalformedURLException {
        this.baseUrl = baseUrl;
        this.url = new URL(baseUrl + url);
        this.listingIds = listings;
        this.executor = executor;
    }
    private void processUrl(URL url) {
        try {
            Scanner scanner = new Scanner(url.openStream(), "UTF-8");
            String contents = scanner.useDelimiter("\\A").next();
            scanner.close();
            processResponse(contents);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    private void processResponse(String jsonString) {
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
                String url = listing.getString("url");
                try {
                    executor.execute(new CraigslistFetcher(baseUrl, url, listingIds, executor));
//                    processUrl(new URL(url));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // this one is an individual listing
                String postingID = listing.getString("PostingID");
                this.listingIds.add(postingID);
            }
        }
    }

    @Override
    public void run() {
        processUrl(url);
    }

}
