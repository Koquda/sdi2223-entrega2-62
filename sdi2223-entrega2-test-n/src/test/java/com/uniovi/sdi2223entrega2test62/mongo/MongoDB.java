package com.uniovi.sdi2223entrega2test62.mongo;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
    private MongoClient mongoClient;
    private MongoDatabase mongodb;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void setMongodb(MongoDatabase mongodb) {
        this.mongodb = mongodb;
    }

    public MongoDatabase getMongodb() {
        return mongodb;
    }

    public void resetMongo() {
        try {
            setMongoClient(new MongoClient(new MongoClientURI(
                    "mongodb://localhost:27017/")));
                    setMongodb(getMongoClient().getDatabase("wallapopDB"));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        deleteData();
        insertUsuarios();
        insertarOfertasYConversaciones();
    }

    private void deleteData() {
        getMongodb().getCollection("users").drop();
        getMongodb().getCollection("offers").drop();
        getMongodb().getCollection("highlightedOffers").drop();
        getMongodb().getCollection("conversations").drop();

    }

    private void insertUsuarios() {
        MongoCollection<Document> usuarios = getMongodb().getCollection("users");
        Document admin = new Document()
                .append("email", "admin@email.com")
                .append("name", "admin")
                .append("surnames", "surnameAdmin1 surnameAdmin2")
                .append("birthdate", "1-1-2002")
                .append("password", "ebd5359e500475700c6cc3dd4af89cfd0569aa31724a1bf10ed1e3019dcfdb11")
                .append("wallet", 100)
                .append("role", "admin");
        usuarios.insertOne(admin);

        Document user01 = new Document()
                .append("email", "user01@email.com")
                .append("name", "user01")
                .append("surnames", "surnamesUser01")
                .append("birthdate", "12-5-1998")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user01);

        Document user02 = new Document()
                .append("email", "user02@email.com")
                .append("name", "user02")
                .append("surnames", "surnamesuser02")
                .append("birthdate", "21-5-1978")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user02);

        Document user03 = new Document()
                .append("email", "user03@email.com")
                .append("name", "user03")
                .append("surnames", "surnamesUser03")
                .append("birthdate", "5-5-1990")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user03);

        Document user04 = new Document()
                .append("email", "user04@email.com")
                .append("name", "user04")
                .append("surnames", "surnamesUser04")
                .append("birthdate", "18-2-1985")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user04);

        Document user05 = new Document()
                .append("email", "user05@email.com")
                .append("name", "user05")
                .append("surnames", "surnamesUser05")
                .append("birthdate", "3-9-1992")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user05);

        Document user06 = new Document()
                .append("email", "user06@email.com")
                .append("name", "user06")
                .append("surnames", "surnamesUser06")
                .append("birthdate", "22-7-1987")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user06);

        Document user07 = new Document()
                .append("email", "user07@email.com")
                .append("name", "user07")
                .append("surnames", "surnamesUser07")
                .append("birthdate", "14-4-1995")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user07);

        Document user08 = new Document()
                .append("email", "user08@email.com")
                .append("name", "user08")
                .append("surnames", "surnamesUser08")
                .append("birthdate", "9-11-1980")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user08);

        Document user09 = new Document()
                .append("email", "user09@email.com")
                .append("name", "user09")
                .append("surnames", "surnamesUser09")
                .append("birthdate", "25-6-1983")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user09);

        Document user10 = new Document()
                .append("email", "user10@email.com")
                .append("name", "user10")
                .append("surnames", "surnamesUser10")
                .append("birthdate", "19-2-1991")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user10);

        Document user11 = new Document()
                .append("email", "user11@email.com")
                .append("name", "user11")
                .append("surnames", "surnamesUser11")
                .append("birthdate", "3-9-1977")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user11);

        Document user12 = new Document()
                .append("email", "user12@email.com")
                .append("name", "user12")
                .append("surnames", "surnamesUser12")
                .append("birthdate", "22-12-1985")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user12);

        Document user13 = new Document()
                .append("email", "user13@email.com")
                .append("name", "user13")
                .append("surnames", "surnamesUser13")
                .append("birthdate", "7-5-1998")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user13);

        Document user14 = new Document()
                .append("email", "user14@email.com")
                .append("name", "user14")
                .append("surnames", "surnamesUser14")
                .append("birthdate", "12-10-1989")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user14);

        Document user15 = new Document()
                .append("email", "user15@email.com")
                .append("name", "user15")
                .append("surnames", "surnamesUser15")
                .append("birthdate", "16-7-1993")
                .append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a")
                .append("wallet", 100)
                .append("role", "user");
        usuarios.insertOne(user15);
    }

    private void insertarOfertasYConversaciones() {
        MongoCollection<Document> ofertas = getMongodb().getCollection("offers");
        Random rand = new Random();

        for (int userNum = 1; userNum <= 15; userNum++) {
            String userEmail = (userNum < 10)?"user0" + userNum + "@email.com":"user" + userNum + "@email.com";

            for (int offerNum = 1; offerNum <= 10; offerNum++) {
                String offerTitle =   "offer" + offerNum + "User" + userNum;
                String offerDetails = "offer" + offerNum + "User" + userNum + "Details";
                String offerDate = String.format("%02d/05/2023", offerNum);
                int offerPrice = rand.nextInt(20) + 1; // precio aleatorio entre 1 y 20
                boolean isHighlighted = false; // resaltar cada veinteavo elemento

                Document offer = new Document()
                        .append("title", offerTitle)
                        .append("details", offerDetails)
                        .append("date", offerDate)
                        .append("price", offerPrice)
                        .append("author", userEmail)
                        .append("highlighted", isHighlighted);

                ofertas.insertOne(offer);
            }
        }

    }

}