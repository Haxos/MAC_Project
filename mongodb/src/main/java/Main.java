import parsers.JSONParser;
import parsers.ParserBuilder;

public class Main {
    public static void main(String[] args) {
        JSONParser jsonParser = ParserBuilder.JSONInputBuilder("resources/db-recipes.json");
        try {
            jsonParser.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
