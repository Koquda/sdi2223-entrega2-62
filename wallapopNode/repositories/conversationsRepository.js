module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    findConversation: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const conversation = await conversationsCollection.findOne(filter, options);
            return conversation;
        } catch (error) {
            throw (error);
        }
    },
    insertConversation: async function (conversation) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const result = await conversationsCollection.insertOne(conversation);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    updateConversation: async function (newConversation, filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const result = await conversationsCollection.updateOne(filter, {$set: newConversation}, options);
            return result;
        } catch (error) {
            throw (error);
        }
    }
    };