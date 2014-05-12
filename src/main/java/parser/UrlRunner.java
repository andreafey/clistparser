package parser;

import java.io.IOException;

public class UrlRunner implements Runnable {
    private final String url;
    private final UrlParser parser;
    
    public UrlRunner(String url, UrlParser parser) {
        this.parser = parser;
        this.url = url;
    }

    
    @Override
    public void run() {
        String response = null;
        try {
            response = UrlParser.urlToText(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (response != null) {
            parser.appendResponse(response);
        }
        
    }

}
