package parser;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CraigslistParser {

    private static final int POOLSIZE = 8;

    public static List<String> fetchListings(String baseUrl, String url) {
        List<String> listings = Collections.synchronizedList(new ArrayList<String>());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.execute(new CraigslistFetcher(baseUrl, url, listings, executor));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
// TODO can i await termination before shutdown?
        // TODO make sure this isn't prematurely preventing new threads from being added
//        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return listings;
    }
}
