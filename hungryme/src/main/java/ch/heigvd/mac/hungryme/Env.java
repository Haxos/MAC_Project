package ch.heigvd.mac.hungryme;

public class Env {
    final static String MONGODB_URI = "localhost";
    final static int    MONGODB_PORT = 27017;
    final static String MONGODB_CREDENTIAL_USERNAME = "admin";
    final static String MONGODB_CREDENTIAL_PASSWORD = "pass";
    final static String MONGODB_DATABASE_NAME = "hungry-me";
    final static String MONGODB_DATABASE_AUTH = "admin";
    final static String MONGODB_COLLECTION_NAME = "recipes";
    final static String MONGODB_DATA_PATH = "resources/db-recipes.json";

    final static String NEO4J_URI = "localhost";
    final static String NEO4J_CREDENTIAL_USERNAME = "neo4j";
    final static String NEO4J_CREDENTIAL_PASSWORD = "pass";

    public final static String TELEGRAM_BOT_NAME = "HungryMe_bot";
    public final static String TELEGRAM_TOKEN = "930161348:AAE6K8Uy61B9AWgOlY7EB22po4KhCoKCQiI";
}
