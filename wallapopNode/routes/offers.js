const {ObjectId} = require("mongodb");
module.exports = function(app, offersRepository, usersRepository) {
    const validator = app.get('validator')
    const moment = app.get('moment')

    // Depending on the @param pay it highlights or unhighlights the offer
    const payHighlight = (req, res) => {
        usersRepository.findUser({email: req.session.user}, {}).then( user => {
            if (user == null) {
                res.redirect("/offers/myOffers" +
                    '?message=Error when highlighting offer.'+
                    "&messageType=alert-danger");
            } else {
                user.wallet -= 20
                req.session.wallet = user.wallet
                usersRepository.updateUser(user, {email: req.session.user}, {}).then(oldUser => {
                    if (oldUser == null) {
                        res.redirect("/offers/myOffers" +
                            '?message=Error when highlighting offer.'+
                            "&messageType=alert-danger");
                    }
                })
            }
        })
    }

    app.get("/offers/myOffers", function (req, res) {
        offersRepository.getOffers({author: req.session.user},{}).then( (offers) => {
            offersRepository.getHighlightedOffers({},{}).then(highlightedOffers => {
                if (highlightedOffers == null){
                    res.redirect("/shop" +
                        '?message=Error occurred when obtaining highlighted offers.'+
                        "&messageType=alert-danger");
                } else {
                    res.render("offers/list.twig", {session:req.session, offers: offers, highlighted: highlightedOffers});
                }
            })
        }).catch( error => {
            res.redirect("/shop" +
                '?message=Error occurred when obtaining your offers.'+
                "&messageType=alert-danger");
        })
    });
    app.get("/offers/delete/:id", function (req, res) {
        let offerId = ObjectId(req.params.id);
        let filter = {_id: ObjectId(req.params.id)};

        offersRepository.findOffer(filter, {}).then(offer => {
           if (offer == null){
               res.redirect("/offers/myOffers" +
                   '?message=Error occurred while trying to get the offer.'+
                   "&messageType=alert-danger");
           } else {
               if (offer.author !== req.session.user){
                   res.redirect("/offers/myOffers" +
                       '?message=The offer you are trying to delete does not belong to you.'+
                       "&messageType=alert-danger");
               }
               if (offer.purchased) {
                   res.redirect("/offers/myOffers" +
                       '?message=The offer you are trying to delete is already sold.'+
                       "&messageType=alert-danger");
               }
               offersRepository.deleteOffer( {_id: offerId} , {}).then( result => {
                   if (result === null || result.deletedCount === 0) {
                       res.redirect("/offers/myOffers" +
                           '?message=Could not eliminate the offer.'+
                           "&messageType=alert-danger");
                   } else {
                       res.redirect("/offers/myOffers");
                   }
               }).catch(error => {
                   res.redirect("/offers/myOffers" +
                       '?message=Error occurred while deleting the offer.'+
                       "&messageType=alert-danger");
               })
           }
        });
    })
    app.post('/offers/add', [
        validator.body('details').notEmpty().withMessage("Details cant be empty"),
            validator.body('title').notEmpty().withMessage("Title cant be empty"),
        validator.body("price")
            .notEmpty().withMessage("Price cant be empty")
            .isFloat({ min: 0 }).withMessage('Price must be a positive number')
        ], function (req, res) {

        // If there are validation errors, show them
        const errors = validator.validationResult(req);
        if (!errors.isEmpty()) {
            res.redirect("/offers/add" +
                "?message=" + errors.array().map(e => {
                    return " " + e.msg
                }) +
                "&messageType=alert-danger ");
            return
        }

        let offer = {
            title: req.body.title,
            details: req.body.details,
            date: new Date().toLocaleDateString('es-ES', {day: '2-digit', month: '2-digit', year: 'numeric'}),
            price: req.body.price,
            author: req.session.user,
            highlighted: false
        }

        if (req.body.highlight === "on"){
            offer.highlighted = true
            offersRepository.insertHighlight(offer).then(inserted => {
                if (inserted == null) {
                    res.redirect("/offers/myOffers" +
                        '?message=Error when highlighting offer.'+
                        "&messageType=alert-danger");
                }
            })
            payHighlight(req, res, true)
        }

        offersRepository.insertOffer(offer).then(offerId => {
            res.redirect("/offers/myOffers" + '?message=Offer added.'+
                "&messageType=alert-info");
        }).catch(error => {
            res.redirect("/offers/myOffers" +
                '?message=Error when adding offer.'+
                "&messageType=alert-danger");
        });
    });
    app.get('/offers/add', function (req, res) {
        res.render("offers/add.twig",{session:req.session});
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
                search: searchQuery,
                session: req.session
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
                                                req.session.wallet = newUser.wallet
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
                res.render("purchase.twig", {offers: offers, session: req.session});
            }).catch(error => {
                res.send("Error occurred when obtaining your purchases: " + error);
            });
        }).catch(error => {
            res.send("Error occurred when listing your purchases " + error);
        });
    });
    app.get('/offers/highlight/:id', function (req, res) {
        offersRepository.findOffer( {_id: ObjectId(req.params.id)}, {}).then(offer => {
            if (offer == null){
                res.redirect("/offers/myOffers" +
                    '?message=Error when obtaining offer.'+
                    "&messageType=alert-danger");
            } else {
                offersRepository.findHighlightedOffer({_id: ObjectId(req.params.id)}, {}).then(highOffer => {
                    if (highOffer == null){
                        payHighlight(req, res)
                        offersRepository.insertHighlight(offer).then( offerId => {
                            if (offerId == null) {
                                res.redirect("/offers/myOffers" +
                                    '?message=Error when highlighting offer.'+
                                    "&messageType=alert-danger");
                            } else {
                                offer.highlighted = true
                                offersRepository.updateOffer(offer, {_id:ObjectId(req.params.id)}, {}).then(updatedOffer => {
                                    if (updatedOffer == null) {
                                        res.redirect("/offers/myOffers" +
                                            '?message=Error when highlighting offer.'+
                                            "&messageType=alert-danger");
                                    } else {
                                        res.redirect("/offers/myOffers" +
                                            '?message=Offer highlighted.'+
                                            "&messageType=alert-info")
                                    }
                                })
                            }
                        })
                    } else {
                        res.redirect("/offers/myOffers" +
                            '?message=Offer already highlighted.'+
                            "&messageType=alert-danger");
                    }

                })

            }
        })
    })
    app.get('/offers/unhighlight/:id', function (req, res) {
        offersRepository.findOffer( {_id: ObjectId(req.params.id)}, {}).then(offer => {
            if (offer == null){
                res.redirect("/offers/myOffers" +
                    '?message=Error when obtaining offer.'+
                    "&messageType=alert-danger");
            } else {
                offersRepository.findHighlightedOffer({_id: ObjectId(req.params.id)}, {}).then(highOffer => {
                    if (highOffer == null){
                        res.redirect("/offers/myOffers" +
                            '?message=Offer is not highlighted.'+
                            "&messageType=alert-danger");
                    } else {
                        offersRepository.deleteHighlightedOffer({_id: ObjectId(req.params.id)}, {}).then( offerId => {
                            if (offerId == null) {
                                res.redirect("/offers/myOffers" +
                                    '?message=Error when unhighlighting offer.'+
                                    "&messageType=alert-danger");
                            } else {
                                offer.highlighted = false
                                offersRepository.updateOffer(offer, {_id:ObjectId(req.params.id)}, {}).then(updatedOffer => {
                                    if (updatedOffer == null) {
                                        res.redirect("/offers/myOffers" +
                                            '?message=Error when highlighting offer.'+
                                            "&messageType=alert-danger");
                                    } else {
                                        res.redirect("/offers/myOffers" +
                                            '?message=Offer highlighted.'+
                                            "&messageType=alert-info")
                                    }
                                })
                            }
                        })
                    }

                })

            }
        })
    })
}