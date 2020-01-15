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
import java.util.Arrays;
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
            parseResult(tockens);

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setParseMode(ParseMode.HTML);

            //message.setText(format(recipe));
            message.setText("OK...");

            // Create ReplyKeyboardMarkup object
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            // Create the keyboard (list of keyboard rows)
            List<KeyboardRow> keyboard = new ArrayList<>();
            // Create a keyboard row
            KeyboardRow row = new KeyboardRow();
            // Set each button, you can also use KeyboardButton objects if you need something else than text
            row.add("\uD83D\uDC4D like");
            row.add("\uD83D\uDC4E dislike");
            row.add("❤️ add to favorite");
            row.add("Show more");
            // Add the first row to the keyboard
            keyboard.add(row);
            keyboardMarkup.setKeyboard(keyboard);
            // Add it to the message
            message.setReplyMarkup(keyboardMarkup);

            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
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

        telegramRecipe = telegramRecipe.concat("<i>{id:"+ recipe.getId() +"}</i>\n\n");

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

    //private List<String> parseResult(String[] tokens){
    private void parseResult(String[] tokens){
        // keywords
        final String unique = "a";
        final String forTags = "for";
        final String forIngredients = "with";

        final String like = "\uD83D\uDC4D";
        final String dislike = "\uD83D\uDC4E";
        final String addFav = "❤️";
        final String remFav = "\uD83D\uDC94";

        ArrayList<String> tags = new ArrayList<String>();
        ArrayList<String> ingredients = new ArrayList<String>();

        ArrayList<String> recipesId = new ArrayList<String>();
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        if(tokens[0].equals(unique)){  //unique recipe
            System.out.println("==> unique recipe");
        }else if(tokens[0].equals(like)){ // Like
            System.out.println("==> like");
        }else if(tokens[0].equals(dislike)){ // dislike
            System.out.println("==> dislike");
        }else if(tokens[0].equals(addFav)){   // add to favorite
            System.out.println("==> addd to favorite");
        }else if(tokens[0].equals(remFav)){// remove from favorite
            System.out.println("==> remove from favorite");
        }

        //a {time : (very) fast/short} recipe(s) with {ingredients} for {tags}

        // get ingredients
        for(int i = Arrays.asList(tokens).indexOf(forIngredients); i>=0 && i<tokens.length && !tokens[i].equals(forTags); i++){
            if(!tokens[i].equals(forIngredients))
                ingredients.add(tokens[i]);
        }

        // get tags
        for(int i = Arrays.asList(tokens).indexOf(forTags); i>=0 && i<tokens.length; i++){
            if(!tokens[i].equals(forTags))
                tags.add(tokens[i]);
        }
        recipesId = (ArrayList<String>) _graphDB.getRecipes(ingredients, tags);
        System.out.println("ingredients : "+ ingredients.toString());
        System.out.println("tags : "+ tags.toString());
    }
}