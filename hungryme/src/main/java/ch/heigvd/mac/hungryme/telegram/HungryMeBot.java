package ch.heigvd.mac.hungryme.telegram;

import ch.heigvd.mac.hungryme.Env;
import ch.heigvd.mac.hungryme.Utils;
import ch.heigvd.mac.hungryme.interfaces.DocumentDatabase;
import ch.heigvd.mac.hungryme.interfaces.GraphDatabase;
import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.Unit;
import ch.heigvd.mac.hungryme.models.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class HungryMeBot extends TelegramLongPollingBot {

    private final DocumentDatabase _documentDB;
    private final GraphDatabase _graphDB;

    public HungryMeBot(DocumentDatabase documentDatabase, GraphDatabase graphDatabase) {
        this._documentDB = documentDatabase;
        this._graphDB = graphDatabase;
    }

    public void onUpdateReceived(Update update) {
        User chatUser = new User(
                    update.getMessage().getFrom().getId().toString(),
                    update.getMessage().getFrom().getUserName(),
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLastName()
                );

        _graphDB.addUser(chatUser);

        Recipe recipe = _documentDB.getRecipeById("5e1c74cefd1a3f219f977b2a");

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.toString());
            // DONE : tockenize the message
            String tockens[]= update.getMessage().getText().split("\\s+");

            // TODO : parse the message to know what to do


            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setParseMode(ParseMode.HTML);

            message.setText(format(recipe));

            // Create ReplyKeyboardMarkup object
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            // Create the keyboard (list of keyboard rows)
            List<KeyboardRow> keyboard = new ArrayList<>();
            // Create a keyboard row
            KeyboardRow row = new KeyboardRow();
            // Set each button, you can also use KeyboardButton objects if you need something else than text
            row.add("\uD83D\uDC4D like");
            row.add("\uD83D\uDC4Edislike");
            row.add("❤️add to favorite");
            row.add("Show more");
            // Add the first row to the keyboard
            keyboard.add(row);
            // Create another keyboard row
            /*row = new KeyboardRow();
            // Set each button for the second line
            row.add("Row 2 Button 1");
            row.add("Row 2 Button 2");
            row.add("Row 2 Button 3");
            // Add the second row to the keyboard
            keyboard.add(row);*/
            // Set the keyboard to the markup
            keyboardMarkup.setKeyboard(keyboard);
            // Add it to the message
            message.setReplyMarkup(keyboardMarkup);


            /*SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setParseMode("HTML")
                    .setText(test);*/
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                System.out.println("ERROR :\n=======");
                System.out.println();
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return Env.TELEGRAM_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return Env.TELEGRAM_TOKEN;
    }

    private String format(Recipe recipe) {
        // TITLE
        String telegramRecipe = "<b><u>" + recipe.getName() + "</u></b>";
        int servings = recipe.getServings();
        if(servings != 0) {
            telegramRecipe = telegramRecipe.concat(" - <i>(" + servings + " servings)</i>");
        }
        telegramRecipe = telegramRecipe.concat("\n");

        // COOKING TIME STUFF
        telegramRecipe = telegramRecipe.concat("<i>Time :</i>\n");
        int preptime = recipe.getPrepTime();
        int waittime = recipe.getWaitTime();
        int cooktime = recipe.getCookTime();

        if(preptime != 0) telegramRecipe = telegramRecipe.concat(" Prep : " + Utils.formatSeconds(preptime));
        if(waittime != 0) telegramRecipe = telegramRecipe.concat(" Wait : " + Utils.formatSeconds(waittime));
        if(cooktime != 0) telegramRecipe = telegramRecipe.concat(" Cook : " + Utils.formatSeconds(cooktime));

        telegramRecipe = telegramRecipe.concat("\n\n");

        // INGREDIENTS STUFF
        telegramRecipe = telegramRecipe.concat("<i>Ingrdients :</i>\n");
        for (Ingredient ingredient : recipe.getIngredients()) {

            telegramRecipe = telegramRecipe.concat(((Double)ingredient.getQuantity()).toString());
            if(!ingredient.getUnit().equals(Unit.NONE)){
                telegramRecipe = telegramRecipe.concat(" " + ingredient.getUnit().toString());
            }

            telegramRecipe = telegramRecipe.concat("\t" + ingredient.getName() + "\n");
        }
        telegramRecipe = telegramRecipe.concat("\n");

        // PREPARATION STUFF
        telegramRecipe = telegramRecipe.concat("<i>Preparation :</i>\n");
        telegramRecipe = telegramRecipe.concat(recipe.getInstructions());

        return telegramRecipe;
    }
}