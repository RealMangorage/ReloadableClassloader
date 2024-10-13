module org.mangorage.classloader {
    requires java.desktop;
    requires com.google.gson;

    opens org.mangorage.classloader.api;
    exports org.mangorage.classloader.api;

    opens org.mangorage.classloader.api.event;
    exports org.mangorage.classloader.api.event;

    opens org.mangorage.classloader.event;
    exports org.mangorage.classloader.event;

    exports org.mangorage.classloader.internal to com.google.gson;
    opens org.mangorage.classloader.internal to com.google.gson;

}