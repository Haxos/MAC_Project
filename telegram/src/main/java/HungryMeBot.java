import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

public class HungryMeBot  extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        // TODO : check if user in database. If not, add user to database


        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            // DONE : tockenize the message
            String tockens[]= update.getMessage().getText().split("\\s+");

            // TODO : parse the message to know what to do

            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText()+" length : "+tockens.length);
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "HungryMe_bot";
    }

    public String getBotToken() {
        return "930161348:AAE6K8Uy61B9AWgOlY7EB22po4KhCoKCQiI";
    }

}