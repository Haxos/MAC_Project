package ch.heigvd.mac.hungryme;

import ch.heigvd.mac.hungryme.mongodb.MongoDBController;
import ch.heigvd.mac.hungryme.neo4j.Neo4jController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ch.heigvd.mac.hungryme.telegram.HungryMeBot;

public class Main {

    public static void main(String[] args) {
        MongoDBController mongoDBController = new MongoDBController(
                Env.MONGODB_URI,
                Env.MONGODB_PORT,
                Env.MONGODB_CREDENTIAL_USERNAME,
                Env.MONGODB_CREDENTIAL_PASSWORD,
                Env.MONGODB_DATABASE_NAME,
                Env.MONGODB_DATABASE_AUTH,
                Env.MONGODB_COLLECTION_NAME
        );

        Neo4jController neo4jController = new Neo4jController(
                Env.MONGODB_URI,
                Env.NEO4J_CREDENTIAL_USERNAME,
                Env.NEO4J_CREDENTIAL_PASSWORD
        );

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new HungryMeBot(mongoDBController, neo4jController));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
