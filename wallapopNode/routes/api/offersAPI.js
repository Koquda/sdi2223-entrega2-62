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
            return res.status(401).json("Error occurred while user authentication process");
        }

        if (!message || message == "") {
            return res.status(400).json("The message cannot be empty");
        }

        let filter = {_id: ObjectId(offerID)}
        offersRepository.findOfferOwner(filter).then(offer => {
            let offerOwner = offer.author;

            if (user === offerOwner) {
               return  res.status(403).json("You cant start a conversation on your own offer");
            }

            let newMessage = {
                userID: user,
                ownerID: offerOwner,
                offerID: offerID,
                message: message,
                date: new Date().toLocaleString(),
                read: false
            }

            conversationsRepository.insertConversation(newMessage)

            res.status(201).json({ message: "Message created successfully" });

        });




    })


}


