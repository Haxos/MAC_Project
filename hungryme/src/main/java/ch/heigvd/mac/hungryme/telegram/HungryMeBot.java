package ch.heigvd.mac.hungryme.telegram;

import ch.heigvd.mac.hungryme.Env;
import ch.heigvd.mac.hungryme.Utils;
import ch.heigvd.mac.hungryme.interfaces.DocumentDatabase;
import ch.heigvd.mac.hungryme.interfaces.GraphDatabase;
import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.Unit;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HungryMeBot extends TelegramLongPollingBot {

    private final DocumentDatabase _documentDB;
    private final GraphDatabase _graphDB;

    public HungryMeBot(DocumentDatabase documentDatabase, GraphDatabase graphDatabase) {
        this._documentDB = documentDatabase;
        this._graphDB = graphDatabase;
    }

    public void onUpdateReceived(Update update) {
        // TODO : check if user in database. If not, add user to database

        Recipe recipe = _documentDB.getRecipeById("5e16095fb302b7111d4c35cb");

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            // DONE : tockenize the message
            String tockens[]= update.getMessage().getText().split("\\s+");

            // TODO : parse the message to know what to do


            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setParseMode(ParseMode.HTML);
            message.setText(recipe.toString());


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