package parser;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class UrlParser {
    private static final int POOLSIZE = 10;
    // appending to all three Queues must be synchronized
    private Queue<String> listings;
    private Queue<String> responses;
    private Queue<String> urls;
    
    public UrlParser() {
        listings = new SynchronousQueue<String>();
        responses = new SynchronousQueue<String>();
        urls = new SynchronousQueue<String>();
    }

    synchronized void appendUrl(String url) {
        urls.add(url);
    }
    synchronized void appendListing(String listing) {
        listings.add(listing);
    }
    synchronized void appendResponse(String response) {
        responses.add(response);
    }
    public void process(String url) {
        appendUrl(url);
        ExecutorService executor = Executors.newFixedThreadPool(POOLSIZE);
        while (urls.size() > 0 || responses.size() > 0) {
            while (urls.size() > 0) {
                String u = urls.remove();
                Runnable r = new CraigslistFetcher(u, this);
                executor.execute(r);
            }
            while (responses.size() > 0) {
                String resp = responses.remove();
                Runnable r = new ResponseProcessor(resp, this);
                executor.execute(r);
            }
        }
        // TODO main problem here is that we don't want this while loop to execute before 
        // urls are retrieved and responses threads spun off
     // wait for threads to finish
        executor.shutdown();
        try {
                executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
    
    public static String urlToText(String url) throws IOException {
        Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8"); 
        String contents = scanner.useDelimiter("\\A").next();
        scanner.close();
        return contents;
    }

}
