package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jController implements ch.heigvd.mac.hungryme.interfaces.GraphDatabase, AutoCloseable {
    private final Driver _driver;

    public Neo4jController(String uri, String username, String password) {
        this(uri, username, password, 7687);
    }

    public Neo4jController(String uri, String username, String password, int port) {
        this._driver = GraphDatabase.driver(
                "bolt://" + uri + ":" + port,
                AuthTokens.basic(username, password)
        );
    }

    @Override
    public void close() {
        this._driver.close();
    }

    public void addRecipe(Recipe recipe) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(new TransactionWork<String>() {

                @Override
                public String execute(Transaction transaction) {
                    // label for query and propriety if not queryable
                    StatementResult result = transaction.run(
                            "CREATE (n:Recipe:$id:$preptime:$ {" +
                                    "name: $name" +
                                    "})",
                            parameters(
                                    "id", recipe.getId(),
                                    "name", recipe.getName()
                                    //TODO: finish recipe neo4j
                            )
                    );
                    return result.single().get(0).asString();
                }
            });
        }
    }

    public void addUser(User user) {
        //TODO: add user
    }
}
