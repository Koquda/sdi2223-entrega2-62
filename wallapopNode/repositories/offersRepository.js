module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this. mongoClient= mongoClient;
        this.app = app;
    },
    deleteOffer: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const result = await offersCollection.deleteOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteHighlightedOffer: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'highlightedOffers';
            const offersCollection = database.collection(collectionName);
            const result = await offersCollection.deleteOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    buyOffer: function (shop, callbackFunction) {
        this.mongoClient.connect(this.app.get('connectionStrings'), function (err, dbClient) {
            if (err) {
                callbackFunction(null)
            } else {
                const database = dbClient.db("wallapopDB");
                const collectionName = 'purchases';
                const purchasesCollection = database.collection(collectionName);
                purchasesCollection.insertOne(shop)
                    .then(result => callbackFunction(result.insertedId))
                    .then(() => dbClient.close())
                    .catch(err => callbackFunction({error: err.message}));
            }
        });
    },
    updateOffer: async function(newSong, filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'offers';
            const songsCollection = database.collection(collectionName);
            const result = await songsCollection.updateOne(filter, {$set: newSong}, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    getPurchases: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'purchases';
            const purchasesCollection = database.collection(collectionName);
            const purchases = await purchasesCollection.find(filter, options).toArray();
            return purchases;
        } catch (error) {
            throw (error);
        }
    },
    getOffersPg: async function (filter, options, page) {
        try {
            const limit = 5;
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const offersCollectionCount = await offersCollection.count();
            const cursor = offersCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const offers = await cursor.toArray();
            const result = {offers: offers, total: offersCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    },
    getHighlightedOffers: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'highlightedOffers';
            const offersCollection = database.collection(collectionName);
            const offers = await offersCollection.find(filter, options).toArray();
            return offers;
        } catch (error) {
            throw (error);
        }
    },
    getOffers: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const offers = await offersCollection.find(filter, options).toArray();
            return offers;
        } catch (error) {
            throw (error);
        }
    },
    findOffer: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const offer = await offersCollection.findOne(filter, options);
            return offer;
        } catch (error) {
            throw (error);
        }
    },
    findHighlightedOffer: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'highlightedOffers';
            const offersCollection = database.collection(collectionName);
            const offer = await offersCollection.findOne(filter, options);
            return offer;
        } catch (error) {
            throw (error);
        }
    },
    insertHighlight: async function (offer){
      try {
          const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
          const database = client.db("wallapopDB");
          const collectionName = 'highlightedOffers';
          const offersCollection = database.collection(collectionName);
          const result = await offersCollection.insertOne(offer);
          return result.insertedId;
      } catch (error) {
          throw (error);
      }
    },
    insertOffer: async function (offer) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("wallapopDB");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const result = await offersCollection.insertOne(offer);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    }
};