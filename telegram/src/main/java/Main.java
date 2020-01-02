
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new HungryMeBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
