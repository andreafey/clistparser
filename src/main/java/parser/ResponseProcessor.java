package parser;

public class ResponseProcessor implements Runnable {
    
    private final String response;
    private final UrlParser parser;
    
    public ResponseProcessor(String response, UrlParser parser) {
        this.response = response;
        this.parser = parser;
    }

    @Override
    public void run() {
        if (containsCluster(response)) {
            // TODO parse urls and add to urls
        }
        else {
            // TODO parse id and add to listings
        }
        // TODO Auto-generated method stub
        
    }
    private static boolean containsCluster(String response) {
        // TODO
        return false;
    }

}
