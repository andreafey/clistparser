package parser;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CraigslistParser {

    public static List<String> fetchListings(String baseUrl, String url) {
        List<String> listings = Collections.synchronizedList(new ArrayList<String>());
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), 
                Executors.defaultThreadFactory());

//        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(new CraigslistFetcher(baseUrl, url, listings, executor));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        // await termination of main thread before shutdown
        int count = 100;
        while (executor.getActiveCount() > 0 && count > 0) {
            try {
                Thread.sleep(10);
                count--;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("count: " + count);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return listings;
    }
}
