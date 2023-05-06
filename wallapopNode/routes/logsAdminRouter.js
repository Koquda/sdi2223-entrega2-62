const express = require('express');
const logsAdminRouter = express.Router();
logsAdminRouter.use(function (req, res, next) {

    if(req.session.user){
        if (req.session.role == "admin") {
            next();
        }else{
            res.redirect("/offers/myOffers?message=You need role admin to enter that URL"  +
                "&messageType=alert-danger");
        }

    }else{
        res.redirect("/users/login");
    }

});
module.exports = logsAdminRouter;