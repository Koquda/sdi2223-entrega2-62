const express = require('express');
const logsAdminRouter = express.Router();
logsAdminRouter.use(function (req, res, next) {


    if (req.session.role == "admin") {
        next();
    } else {

        res.redirect("/offers/myOffers");
    }


});
module.exports = logsAdminRouter;