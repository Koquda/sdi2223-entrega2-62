const express = require('express');
const userSessionRouter = express.Router();
userSessionRouter.use(function(req, res, next) {
    console.log("routerUsuarioSession");
    let user;
    let token = req.headers['token'] || req.body.token || req.query.token;
    jwt.verify(token, 'secreto', {}, function (err, infoToken) {
        user = infoToken.user;
    });
    if (user == null) {
        res.redirect("/users/login")
    } else {
        next()
    }
});
module.exports = userSessionRouter;

