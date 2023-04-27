const { ObjectId } = require("mongodb")
const jwt = require("jsonwebtoken");
module.exports = function(app, offersRepository) {
  function getSessionUser(req) {
    let user;
    let token = req.headers['token'] || req.body.token || req.query.token;
    jwt.verify(token, 'secreto', {}, function (err, infoToken) {
        user = infoToken.user;
    });
    return user;
  }

  app.get("/api/offers", function(req, res) {
    const user = getSessionUser(req);
    let filter = {author: { $ne: user }};
    let options = {};
    offersRepository.getOffers(filter, options).then(offers => {
      if (offers == null) {
        res.status(404)
        res.json({ error: "Error occurred when obtaining offers, there are none"})
      } else {
        res.status(200);
        res.send({ offers: offers })
      }
    }).catch(error => {
      res.status(500);
      res.json({ error: "Error occurred when obtaining offers" })
    });
  })
}
