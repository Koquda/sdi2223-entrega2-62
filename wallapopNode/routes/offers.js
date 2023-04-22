const {ObjectId} = require("mongodb");
module.exports = function(app, offersRepository) {
    const validator = app.get('validator')
    const moment = app.get('moment')

    app.get("/offers/myOffers", function (req, res) {
        offersRepository.getOffers({author: req.session.user},{}).then( (offers) => {
            res.render("offers/list.twig", {session:req.session, offers: offers});
        }).catch( error => {
            res.redirect("/publications" +
                '?message=Se ha producido un error al obtener sus ofertas.'+
                "&messageType=alert-danger");
        })
    });

    app.post('/offers/add', [
        validator.body('details').notEmpty().withMessage("Details cant be empty"),
            validator.body('title').notEmpty().withMessage("Title cant be empty"),
        validator.body("price")
            .notEmpty().withMessage("Price cant be empty")
            .isFloat({ min: 0 }).withMessage('Price must be a positive number')
        ]
        ,function (req, res) {

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
            author: req.session.user
        }
        offersRepository.insertOffer(offer).then(offerId => {
            res.redirect("/publications" + '?message=Oferta agregada.'+
                "&messageType=alert-info");
        }).catch(error => {
            res.redirect("/publications" +
                '?message=Se ha producido un error al registrar la oferta.'+
                "&messageType=alert-danger");
        });
    });

    app.get('/offers/add', function (req, res) {
        res.render("offers/add.twig",{session:req.session});
    });

    app.get('/shop', function (req, res) {
        let filter = {};
        let options = {sort: {title: 1}};
        if (req.query.search != null && typeof (req.query.search) != "undefined" && req.query.search != "") {
            filter = {"title": {$regex: ".*" + req.query.search + ".*"}};
        }
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            // Puede no venir el param
            page = 1;
        }
        offersRepository.getOffersPg(filter, options, page).then(result => {
            let lastPage = result.total / 4;
            if (result.total % 4 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            let response = {
                offers: result.offers,
                pages: pages,
                currentPage: page
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
            } else {
                let filter = {user: req.session.user};
                offersRepository.getPurchases(filter, options).then(myOffers => {
                    let result = myOffers.find(s => s.offerId.equals(ObjectId(req.params.id)));
                    let cantBuy = result == null;
                    if (!cantBuy) {
                        res.redirect("/shop" +
                            "?message=Cancion comprada" +
                            "&messageType=alert-danger ");
                    } else {
                        offersRepository.buyOffer(shop, function (shopId) {
                            if (shopId == null) {
                                res.send("Error al realizar la compra");
                            } else {
                                let newOffer = {
                                    title: offer.title,
                                    details: offer.details,
                                    date: offer.date,
                                    price: offer.price,
                                    author: req.session.user,
                                    purchased: true
                                };
                                let filter = {_id: offerId};
                                const options = {upsert: false};
                                offersRepository.updateOffer(newOffer, filter, options).then(() => {
                                    res.redirect("/publications");
                                }).catch(error => {
                                    res.send("Se ha producido un error al modificar la canción " + error);
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
    app.get('/publications', function (req, res) {
        let filter = {author: req.session.user};
        let options = {sort: {title: 1}};
        offersRepository.getOffers(filter, options).then(offers => {
            res.render("publications.twig", {offers: offers,session:req.session});
        }).catch(error => {
            res.send("Se ha producido un error al listar las publicaciones del usuario:" + error);
        });
    });
}