package ch.heigvd.mac.hungryme;

import ch.heigvd.mac.hungryme.mongodb.MongoDBController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ch.heigvd.mac.hungryme.telegram.HungryMeBot;

public class Main {

    public static void main(String[] args) {
        MongoDBController mongoDBController = new MongoDBController(
                Env.MONGODB_HOST_SITE,
                Env.MONGODB_HOST_PORT,
                Env.MONGODB_CREDENTIAL_USERNAME,
                Env.MONGODB_CREDENTIAL_PASSWORD,
                Env.MONGODB_DATABASE_NAME,
                Env.MONGODB_DATABASE_AUTH,
                Env.MONGODB_COLLECTION_NAME
        );

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new HungryMeBot(mongoDBController));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
