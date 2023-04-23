const {ObjectId, ObjectID} = require("mongodb");
module.exports = function(app, offersRepository, usersRepository) {
    app.get("/offers", function (req, res) {
        let offers = [{
            "title": "Blank space",
            "price": "1.2"
        }, {
            "title": "See you again",
            "price": "1.3"
        }, {
            "title": "Uptown Funk",
            "price": "1.1"
        }];

        let response = {
            seller: 'Tienda de ofertas',
            offers: offers
        };
        res.render("shop.twig", response);
    });

    app.post('/offers/add', function (req, res) {
        let offer = {
            title: req.body.title,
            details: req.body.details,
            date: new Date().toLocaleDateString('es-ES', {day: '2-digit', month: '2-digit', year: 'numeric'}),
            price: req.body.price,
            author: req.session.user
        }
        offersRepository.insertOffer(offer).then(offerId => {
            res.redirect("/offers/myOffers" + '?message=Oferta agregada.'+
                "&messageType=alert-info");
        }).catch(error => {
            res.redirect("/offers/myOffers" +
                '?message=Se ha producido un error al registrar la oferta.'+
                "&messageType=alert-danger");
        });
    });

    app.get('/offers/add', function (req, res) {
        res.render("offers/add.twig");
    });

    app.get('/shop', function (req, res) {
        let filter = {};
        let options = {sort: {title: 1}};
        let searchQuery = "";
        if (req.query.search != null && typeof (req.query.search) != "undefined" && req.query.search != "") {
            filter = { "title": { $regex: new RegExp(req.query.search, "i") } };
            searchQuery = "&search=" + req.query.search;
        }
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offersRepository.getOffersPg(filter, options, page).then(result => {
            let totalOffers = result.total;
            let offersPerPage = 5;
            let lastPage = Math.ceil(totalOffers / offersPerPage);

            let pages = [];
            for (let i = Math.max(page - 2, 1); i <= Math.min(page + 2, lastPage); i++) {
                pages.push(i);
            }

            let response = {
                offers: result.offers,
                pages: pages,
                currentPage: page,
                search: searchQuery
            }

            res.render("shop.twig", response);
        }).catch(error => {
            res.send("Se ha producido un error al listar las ofertas " + error);
        });
    });


    app.get('/offers/buy/:id', function (req, res) {
        let offerId = ObjectId(req.params.id);
        let shop = {
            user: req.session.user,
            offerId: offerId
        };
        let filter = {_id: ObjectId(req.params.id)};
        let options = {};

        offersRepository.findOffer(filter, options).then(offer => {
            let cantBuy = offer.author == req.session.user;
            if (cantBuy) {
                res.redirect("/shop" +
                    "?message=Eres el autor" +
                    "&messageType=alert-danger ");
            } else if ((req.session.wallet - offer.price) < 0){
                res.redirect("/shop" +
                    "?message=No tienes dinero suficiente" +
                    "&messageType=alert-danger ");
            }else {
                let filter = {user: req.session.user};
                offersRepository.getPurchases(filter, options).then(myOffers => {
                    let result = myOffers.find(s => s.offerId.equals(ObjectId(req.params.id)));
                    let cantBuy = result == null;
                    if (!cantBuy) {
                        res.redirect("/shop" +
                            "?message=Cancion ya comprada" +
                            "&messageType=alert-danger ");
                    } else {
                        offersRepository.buyOffer(shop, function (shopId) {
                            if (shopId == null) {
                                res.send("Error al realizar la compra");
                            } else {
                                let newOffer = {
                                    purchased: true
                                };
                                let filter = {_id: shop.offerId};
                                const options = {upsert: false};
                                offersRepository.updateOffer(newOffer, filter, options).then(() => {
                                    usersRepository.findUser({email: req.session.user}, {}).then(user =>{
                                        if(user == null){
                                            res.redirect("/shop" +
                                                "?message=Error al buscar usuario" +
                                                "&messageType=alert-danger ");
                                        }else{
                                            let newUser = {
                                                wallet: user.wallet - offer.price  // resta el precio de la oferta comprada
                                            };
                                            let filter = {email: user.email};
                                            const options = {upsert: false};
                                            usersRepository.updateUser(newUser, filter, options).then(() => {
                                                res.redirect("/shop");
                                            }).catch(error => {
                                                res.send("Se ha producido un error al actualizar el monedero " + error);
                                            });
                                        }
                                    }).catch(error => {
                                        res.send("Se ha producido un error al actualizar la oferta " + error);
                                    });
                                    }).catch(error => {
                                        res.send("Se ha producido un error al buscar usuario " + error);
                                    });

                            }
                        });
                    }
                });
            }
        });
    });

    app.get('/purchases', function (req, res) {
        let filter = {user: req.session.user};
        let options = {projection: {_id: 0, offerId: 1}};
        offersRepository.getPurchases(filter, options).then(purchasedIds => {
            let purchasedOffers = [];
            for (let i = 0; i < purchasedIds.length; i++) {
                purchasedOffers.push(purchasedIds[i].offerId)
            }
            let filter = {"_id": {$in: purchasedOffers}};
            let options = {sort: {title: 1}};
            offersRepository.getOffers(filter, options).then(offers => {
                res.render("purchase.twig", {offers: offers});
            }).catch(error => {
                res.send("Se ha producido un error al listar las ofertas del usuario: " + error);
            });
        }).catch(error => {
            res.send("Se ha producido un error al listar las offertas del usuario " + error);
        });
    });
}