package ch.heigvd.mac.hungryme.telegram;

import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.mongodb.MongoDBController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HungryMeBot  extends TelegramLongPollingBot {

    private MongoDBController _mongoDBController;

    public HungryMeBot(MongoDBController mongoDBController) {
        this._mongoDBController = mongoDBController;
    }

    public void onUpdateReceived(Update update) {
        // TODO : check if user in database. If not, add user to database

        Recipe recipe = _mongoDBController.getRecipeById("5e1331b8c9166876534a7878");

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            // DONE : tockenize the message
            String tockens[]= update.getMessage().getText().split("\\s+");

            // TODO : parse the message to know what to do


            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setParseMode(ParseMode.HTML);

            String finalMessage = new String("<b>Verificador de Qualis - CAPES</b>\n");
            finalMessage = finalMessage.concat("\nUtilize os seguintes comandos para consulta\n");
            finalMessage = finalMessage.concat("\n<b>/conferencia</b> <i>sigla_conferencia</i> OU <i>nome_conferencia</i>");
            finalMessage = finalMessage.concat("\n<b>/periodico</b> <i>issn_periodico</i> OU <i>nome_periodico</i>");

            /*SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setParseMode("HTML")
                    .setText(test);*/
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