module havis.capture.api {
    requires jackson.databind;
    requires java.logging;

    requires transitive havis.transport.api;
    requires transitive havis.util.cycle;
    requires transitive javax.annotation.api;
    requires transitive javax.ws.rs.api;
    requires transitive jaxb.api;

    exports havis.capture;
    exports havis.capture.cycle;
    exports havis.capture.poll;
    exports havis.capture.provider;
    exports havis.capture.rest;

    opens havis.capture.cycle to jackson.databind, gson;
}