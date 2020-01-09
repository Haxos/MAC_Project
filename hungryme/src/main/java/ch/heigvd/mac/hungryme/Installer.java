package ch.heigvd.mac.hungryme;

import ch.heigvd.mac.hungryme.mongodb.MongoDBController;

public class Installer {

    public static void main(String[] args) {
        MongoDBController mongoDBController = new MongoDBController(
                Env.MONGODB_URI,
                Env.MONGODB_PORT,
                Env.MONGODB_CREDENTIAL_USERNAME,
                Env.MONGODB_CREDENTIAL_PASSWORD,
                Env.MONGODB_DATABASE_NAME,
                Env.MONGODB_DATABASE_AUTH,
                Env.MONGODB_COLLECTION_NAME
        );
        mongoDBController.addData(Env.MONGODB_DATA_PATH);

        //TODO: install neo4j data
    }
}
