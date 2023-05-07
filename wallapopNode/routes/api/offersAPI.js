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
    app.post("/api/offers/:id/messages", function (req, res) {
        let user = getSessionUser(req);
        let message = req.body.message;
        let offerID = req.params.id;
        if (user == null) {
            return res.status(401).json({error: "Error occurred while user authentication process"});
        }

        if (!message || message == "") {
            return res.status(400).json({error: "The message cannot be empty"});
        }

        let filter = {_id: ObjectId(offerID)}
        offersRepository.findOfferOwner(filter).then(offer => {
            if (offer == null){
                return res.status(404).json({error:"Offer not found"});
            } else {
                let offerOwner = offer.author;
                const offerTitle =  offer.title

                filter = {ownerID: offerOwner, offerID: offerID}
                conversationsRepository.findAllConversationFilter(filter).then(conversations => {
                    if (conversations.length == 0) {
                        if (user === offerOwner) {
                            return res.status(403).json({error: "You cant start a conversation on your own offer"});
                        } else {
                            let newMessage = {
                                userID: user,
                                ownerID: offerOwner,
                                offerID: offerID,
                                message: message,
                                date: new Date().toLocaleString(),
                                read: false,
                                sentBy: user,
                                offerTitle: offerTitle
                            }

                            conversationsRepository.insertConversation(newMessage)

                            return res.status(201).json({message: "Message created successfully"});
                        }
                    } else {
                        let newMessage = {
                            userID: conversations[0].userID,
                            ownerID: offerOwner,
                            offerID: offerID,
                            message: message,
                            date: new Date().toLocaleString(),
                            read: false,
                            sentBy: user,
                            offerTitle: offerTitle
                        }

                        conversationsRepository.insertConversation(newMessage)

                        return res.status(201).json({message: "Message created successfully"});
                    }

                })
            }

        });
    })
    app.get("/api/offers/:id/conversation", function (req, res) {
        let user = getSessionUser(req);
        let offerID = req.params.id;
        if (user == null) {
            return res.status(403).json({error: "Error occurred while user authentication process"});
        }
        let filter = {}

        offersRepository.findOfferOwner({_id:ObjectId(offerID)}).then(offer => {
            if (offer == null){
                return res.status(404).json({error:"Offer not found"});
            }
            if (offer.author == user){
                filter={
                    offerID: offerID,
                    ownerID:user
                }
                conversationsRepository.findAllConversationFilter(filter).then(messages => {
                    res.status(200);
                    res.send({messages: messages})
                }).catch(error => {
                    res.status(500);
                    res.json({error: "Se ha producido un error al recuperar los mensajes."})
                });
            }else{
                filter = {
                    offerID: offerID,
                    userID:user
                }
                conversationsRepository.findAllConversationFilter(filter).then(messages => {
                    res.status(200);
                    res.send({messages: messages})
                }).catch(error => {
                    res.status(500);
                    res.json({error: "Se ha producido un error al recuperar los mensajes."})
                });
            }
        }).catch(error =>{
            return res.status(404).json({error:"Offer not found"});
        })


    })
    app.get("/api/offers/conversations", function (req, res) {
        let user = getSessionUser(req);
        if (user == null) {
            return res.status(401).json("Error occurred while user authentication process");
        }

        let filter = {$or: [{userID: user}, {ownerID: user}]}
        conversationsRepository.findAllConversationGroupOfferId(filter).then(messages => {
            if(messages == null){
                return res.status(404).json({error:"Conversations not found"});
            }

            res.status(200);
            res.send({conversations: messages})
        }).catch(error => {
            res.status(500);
            res.json({error: "Se ha producido un error al recuperar las conversaciones."})
        });
    })
    app.delete("/api/offers/conversation/delete/:id", function (req, res) {
        let user = getSessionUser(req);
        let conversationID = req.params.id;
        if (user == null) {
            return res.status(401).json("Error occurred while user authentication process");
        }
        let filter = {_id: ObjectId(conversationID)}

        conversationsRepository.findConversation(filter).then(conversation => {
            if(conversation == null){
                return res.status(404).json({error:"Conversation not found"});
            }

            if (user != conversation.userID && user != conversation.ownerID) {
                return res.status(403).json({error: "You cant remove a conversation that you are not part of"});
            } else {
                conversationsRepository.deleteConversations({
                    userID: conversation.userID,
                    ownerID: conversation.ownerID,
                    offerID: conversation.offerID
                }).then(() => {

                    return res.status(200).json({message: "Conversation removed"})

                })

            }
        })
    })
    app.post("/api/offers/:id/markRead", function(req, res){
        let user = getSessionUser(req);
        let conversationID = req.params.id;

        let filter={_id:ObjectId(conversationID)}
        conversationsRepository.findConversation(filter,{}).then(conversation => {
            if(conversation == null){
                return res.status(404).json({error:"Conversation not found"});
            }

            if (user != conversation.userID && user != conversation.ownerID) {
                return res.status(403).json({error: "You cant mark as read a conversation that you are not part of"});
            }

            conversation.read = true;
            conversationsRepository.updateConversation(conversation,filter,{}).then(()=>{
                return res.status(200).json({message: "Message read"})
            } )
        })

    })



    // EndPoint que devuelve las ofertas del usuario, usada solo para testing
    app.get("/api/myOffers", function (req, res) {
        const user = getSessionUser(req);
        let filter = {author: user};
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
}


