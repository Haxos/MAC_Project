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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class HungryMeBot extends TelegramLongPollingBot {

    final String like = "\uD83D\uDC4D";
    final String dislike = "\uD83D\uDC4E";
    final String addFav = "❤️";
    final String remFav = "\uD83D\uDC94";

    private final DocumentDatabase _documentDB;
    private final GraphDatabase _graphDB;

    public HungryMeBot(DocumentDatabase documentDatabase, GraphDatabase graphDatabase) {
        this._documentDB = documentDatabase;
        this._graphDB = graphDatabase;
    }

    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            // Create user and add if not exists in _graphDB
            User chatUser = new User(
                    update.getMessage().getFrom().getId().toString(),
                    update.getMessage().getFrom().getUserName(),
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLastName()
            );
            _graphDB.addUser(chatUser);

            // tockenize the message
            String tockens[]= update.getMessage().getText().toLowerCase().split("\\s+");

            // parse the message and get the replies
            ArrayList<SendMessage> messages = parseResult(tockens, update.getMessage().getChatId());

            // send all the messages
            for(SendMessage message : messages){
                try {
                    execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        }else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data[]= update.getCallbackQuery().getData().split("\\s+");

            String userId = update.getCallbackQuery().getFrom().getId().toString();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if(call_data[0].equals(like) ||
                    call_data[0].equals(dislike) ||
                    call_data[0].equals(addFav) ||
                    call_data[0].equals(remFav)){

                if(call_data[0].equals(like)){
                    if(_graphDB.isRecipeDisliked(call_data[1], userId)){
                        _graphDB.unDislikeRecipe(call_data[1], userId);
                    }else {
                        _graphDB.likeRecipe(call_data[1], userId);
                    }
                }else if(call_data[0].equals(dislike)){
                    if(_graphDB.isRecipeLiked(call_data[1], userId)){
                        _graphDB.unLikeRecipe(call_data[1], userId);
                    }else{
                        _graphDB.dislikeRecipe(call_data[1], userId);
                    }
                }else if(call_data[0].equals(addFav)){
                    _graphDB.favoriteRecipe(call_data[1], userId);
                }else if(call_data[0].equals(remFav)) {
                    _graphDB.unFavoriteRecipe(call_data[1], userId);
                }
                // replace the original message
                replaceMessage(call_data[1], userId, chat_id, message_id);

            }else if (call_data[0].length() == "5e1c74cefd1a3f219f977b2a".length() && call_data.length == 1) {

                sendRecipeToUser(call_data[0], userId, chat_id);

            }else if (call_data[0].length() == "5e1c74cefd1a3f219f977b2a".length() && call_data.length > 1) {
                SendMessage new_message = new SendMessage()
                        .setChatId(chat_id)
                        .setText("Here are some more recipes !")
                        .setParseMode(ParseMode.HTML);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                for(int i = 0; i< call_data.length; i++){
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(getRecipeName(call_data[i])).setCallbackData(call_data[i]));
                    rowsInline.add(rowInline);
                }

                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                new_message.setReplyMarkup(markupInline);

                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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

        int preptime = recipe.getPrepTime();
        int waittime = recipe.getWaitTime();
        int cooktime = recipe.getCookTime();

        if( (preptime + waittime + cooktime) != 0 ) {
            telegramRecipe = telegramRecipe.concat("<i>Time :</i>\n");
            if (preptime != 0) telegramRecipe = telegramRecipe.concat(" Prep : " + Utils.formatSeconds(preptime));
            if (waittime != 0) telegramRecipe = telegramRecipe.concat(" Wait : " + Utils.formatSeconds(waittime));
            if (cooktime != 0) telegramRecipe = telegramRecipe.concat(" Cook : " + Utils.formatSeconds(cooktime));
            telegramRecipe = telegramRecipe.concat("\n");
        }

        telegramRecipe = telegramRecipe.concat("\n");

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

    private String getRecipeName(String recipeId){
       return _documentDB.getRecipeById(recipeId).getName();
    }

    //private List<String> parseResult(String[] tokens){
    private ArrayList<SendMessage> parseResult(String[] tokens, Long chatId){
        // keywords
        final String unique = "a";
        final String forTags = "for";
        final String forIngredients = "with";

        ArrayList<String> tags = new ArrayList<String>();
        ArrayList<String> ingredients = new ArrayList<String>();

        LinkedList<ArrayList<String>> recipesId = new LinkedList<ArrayList<String>>();
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        ArrayList<SendMessage> messages = new ArrayList<SendMessage>();

        if(tokens[0].equals(unique)){  //unique recipe
            System.out.println("==> unique recipe");
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
        recipesId = _graphDB.getRecipes(ingredients, tags);

        if(recipesId.isEmpty()){
            SendMessage message = new SendMessage();
            message.setChatId(chatId); // long chatId
            message.setParseMode(ParseMode.HTML);
            message.setText("Sorry... Nothing was found...\nPlease try again !");
            messages.add(message);
        }else{
            SendMessage message = new SendMessage();
            message.setChatId(chatId); // long chatId
            message.setText("Here are some recipes !");
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            for(int i = 0; i<5 && i<recipesId.size(); i++){
                ArrayList<String> recipe = recipesId.pollFirst();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText(recipe.get(1)).setCallbackData(recipe.get(0)));
                rowsInline.add(rowInline);
            }

            if(recipesId.size() > 0) {
                String remainingRecipeId = "";

                for(int i = 0; i< recipesId.size() && remainingRecipeId.length() <= 40; i++){
                    remainingRecipeId = remainingRecipeId.concat(recipesId.get(i).get(0)+" ");
                }
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                if(recipesId.size() == 1) {
                    rowInline.add(new InlineKeyboardButton().setText(recipesId.get(0).get(1)).setCallbackData(remainingRecipeId));
                }else{
                    rowInline.add(new InlineKeyboardButton().setText("➕ Show more ! ➕").setCallbackData(remainingRecipeId));
                }
                rowsInline.add(rowInline);
            }

            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);
            messages.add(message);
        }
        return messages;
    }

    private String addFooterInfo(String recipe, String recipeId, String userId){

        if(_graphDB.isRecipeLiked(recipeId, userId) ||
                _graphDB.isRecipeDisliked(recipeId, userId) ||
                _graphDB.isRecipeFavorite(recipeId, userId)){
            recipe = recipe.concat("\n");
            if(_graphDB.isRecipeLiked(recipeId, userId))
                recipe = recipe.concat(like);
            if(_graphDB.isRecipeDisliked(recipeId, userId))
                recipe = recipe.concat(dislike);
            if(_graphDB.isRecipeFavorite(recipeId, userId))
                recipe = recipe.concat(addFav);
        }

        return recipe;
    }

    private void replaceMessage(String recipeId, String userId, Long chatId, Long messageId){
        // replace the original message
        String answer = format(_documentDB.getRecipeById(recipeId));
        answer = addFooterInfo(answer, recipeId, userId);

        EditMessageText new_message = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText(answer)
                .setParseMode(ParseMode.HTML);

        new_message.setReplyMarkup(getMessageButtons(recipeId, userId));

        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendRecipeToUser(String recipeId, String userId, long chatId){
        String answer = format(_documentDB.getRecipeById(recipeId));
        answer = addFooterInfo(answer, recipeId ,userId);

        SendMessage new_message = new SendMessage()
                .setChatId(chatId)
                .setText(answer)
                .setParseMode(ParseMode.HTML);

        new_message.setReplyMarkup(getMessageButtons(recipeId, userId));

        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getMessageButtons(String recipeId, String userId){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // add all recipe buttons

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        if(!_graphDB.isRecipeLiked(recipeId, userId))
            rowInline.add(new InlineKeyboardButton().setText(like).setCallbackData(like+" "+ recipeId));

        if(!_graphDB.isRecipeDisliked(recipeId, userId))
            rowInline.add(new InlineKeyboardButton().setText(dislike).setCallbackData(dislike+" "+ recipeId));

        if(!_graphDB.isRecipeFavorite(recipeId, userId)) {
            rowInline.add(new InlineKeyboardButton().setText(addFav).setCallbackData(addFav + " " + recipeId));
        }else{
            rowInline.add(new InlineKeyboardButton().setText(remFav).setCallbackData(remFav + " " + recipeId));
        }
        rowsInline.add(rowInline);

        // Add it to the message
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}