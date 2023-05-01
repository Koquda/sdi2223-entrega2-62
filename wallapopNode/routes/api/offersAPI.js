const {ObjectId} = require("mongodb")
const jwt = require("jsonwebtoken");
module.exports = function (app, offersRepository, conversationsRepository) {

    function getSessionUser(req) {
        let user;
        let token = req.headers['token'] || req.body.token || req.query.token;
        jwt.verify(token, 'secreto', {}, function (err, infoToken) {
            user = infoToken.user;
        });
        return user;
    }

    app.get("/api/offers", function (req, res) {
        const user = getSessionUser(req);
        let filter = {author: {$ne: user}};
        let options = {};
        offersRepository.getOffers(filter, options).then(offers => {
            if (offers == null) {
                res.status(404)
                res.json({error: "Error occurred when obtaining offers, there are none"})
            } else {
                res.status(200);
                res.send({offers: offers})
            }
        }).catch(error => {
            res.status(500);
            res.json({error: "Error occurred when obtaining offers"})
        });
    })

    app.post("/api/offers/:id/mensajes", function (req, res) {
        let user = getSessionUser(req);
        let message = req.body.message;
        let offerID = req.params.id;
        if (user == null) {
            return res.status(401).json({error:"Error occurred while user authentication process"});
        }

        if (!message || message == "") {
            return res.status(400).json({error:"The message cannot be empty"});
        }

        let filter = {_id: ObjectId(offerID)}
        offersRepository.findOfferOwner(filter).then(offer => {
            let offerOwner = offer.author;

            filter = {ownerID:offerOwner, offerID:offerID}
            conversationsRepository.findAllConversationFilter(filter).then(conversations => {
                    if(conversations.length == 0){
                        if (user === offerOwner) {
                            return  res.status(403).json({error:"You cant start a conversation on your own offer"});
                        }else{
                            let newMessage = {
                                userID: user,
                                ownerID: offerOwner,
                                offerID: offerID,
                                message: message,
                                date: new Date().toLocaleString(),
                                read: false
                            }

                            conversationsRepository.insertConversation(newMessage)

                            return res.status(201).json({ message: "Message created successfully" });
                        }
                    }else{
                        let newMessage = {
                            userID: user,
                            ownerID: offerOwner,
                            offerID: offerID,
                            message: message,
                            date: new Date().toLocaleString(),
                            read: false
                        }

                        conversationsRepository.insertConversation(newMessage)

                        return res.status(201).json({ message: "Message created successfully" });
                    }

            })





        });
    })

    app.get("/api/offers/:id/conversation", function (req, res) {
        let user = getSessionUser(req);
        let offerID = req.params.id;
        if (user == null) {
            return res.status(401).json("Error occurred while user authentication process");
        }
        let filter = {
            offerID: offerID
        }
        conversationsRepository.findAllConversationFilter(filter).then(messages => {
            res.status(200);
            res.send({messages: messages})
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error al recuperar los mensajes." })
        });
    })

    app.get("/api/offers/conversations", function (req, res) {
        let user = getSessionUser(req);
        if (user == null) {
            return res.status(401).json("Error occurred while user authentication process");
        }

        let filter = { userID: user }
        conversationsRepository.findAllConversationGroupOfferId(filter).then(messages => {
            res.status(200);
            res.send({conversations: messages})
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error al recuperar las conversaciones." })
        });
    })
}


