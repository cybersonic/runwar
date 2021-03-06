package runwar;


import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import runwar.logging.RunwarLogger;

public class ErrorHandler implements HttpHandler {

    private final HttpHandler next;

    public ErrorHandler(final HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.addDefaultResponseListener(new DefaultResponseListener() {
            @Override
            public boolean handleDefaultResponse(final HttpServerExchange exchange) {
                if (!exchange.isResponseChannelAvailable()) {
                    RunwarLogger.LOG.error("The response channel was closed prematurely.  Request path: " + exchange.getRequestPath() + " status-code: " + exchange.getStatusCode() );
                    return false;
                }
                String message = "Location: " + exchange.getRequestPath() + " generated no content, maybe verify any errorPage locations? (status code: "
                        + exchange.getStatusCode() + ")";
                RunwarLogger.LOG.error(message);
/*  
                // does not seem to actually return the content, no idea why
                if (exchange.getStatusCode() != 200) {
                    final String errorPage = "<html><head><title>Error</title></head><body>"+ message + "</body></html>";
                    exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + errorPage.length());
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    Sender sender = exchange.getResponseSender();
                    sender.send(errorPage);
                    return true;
                }
*/
                return false;
            }
        });
        try {
            next.handleRequest(exchange);
        } catch (Exception e) {
            RunwarLogger.LOG.error("ErrorHandler handleRequest triggered", e);
            if (exchange.isResponseChannelAvailable()) {
            }
        }
    }
}