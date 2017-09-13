package gg.revival.factions.core.db;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

public class DBManager {

    @Getter @Setter static MongoCollection<Document> deathbans;
    @Getter @Setter static MongoCollection<Document> lives;
    @Getter @Setter static MongoCollection<Document> stats;
    @Getter @Setter static MongoCollection<Document> protection;

}
