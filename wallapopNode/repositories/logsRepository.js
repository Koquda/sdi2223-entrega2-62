module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    insertLog: async function (log) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.insertOne(log);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    findAllLogs: async function() {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const logs = await logsCollection.find().toArray();
            return logs;
        } catch (error) {
            throw (error);
        }
    },
    deleteAllLogs: async function() {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.deleteMany({});
            return result.deletedCount;
        } catch (error) {
            throw (error);
        }
    },
     findAllByType: async function(type) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            let logs;

            if (type) {
                logs = await logsCollection.find({ type }).sort({ date: -1 }).toArray();
            } else {
                logs = await logsCollection.find().sort({ date: -1 }).toArray();
            }

            return logs;
        } catch (error) {
            throw (error);
        }
    }

};
