const { ObjectId } = require("mongodb")
module.exports = function(app, usersRepository) {
  app.post('/api/users/login', function(req, res) {
    try {
      let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
        .update(req.body.password).digest('hex')
      let filter = {
        email: req.body.email,
        password: securePassword
      }
      let options = {}
      usersRepository.findUser(filter, options).then(user => {
        if (user == null){
          res.status(401) // Unauthorised
          res.json({
            message: "user unauthorised",
            authenticated: false
          })
        } else {
          let token = app.get('jwt').sign(
              {user:user.email, time: Date.now() / 1000},
              "secreto")
          res.status(200)
          res.json({
            message: "user authorised",
            authenticated: true,
            token: token
          })
        }
      }).catch(error => {
        res.status(401)
        res.json({
          message: "Error occurred while verifying credentials",
          authenticated: false,
        })
      })
    } catch (e) {
      res.status(500)
      res.json({
        message: "Error occurred while verifying credentials",
        authenticated: false
      })
    }
  })
}

