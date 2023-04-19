module.exports = function (app, usersRepository) {
  const validator = app.get('validator')
  const moment = app.get('moment')

  const passwordMatches = (value, { req }) => {
    if (value !== req.body.confirmPassword) {
      throw new Error('Passwords do not match');
    }
    return true;
  };

  // Checks if it is a date
  const isDateValid = (value) => {
    const date = moment(value, 'YYYY-MM-DD', true);
    if ((!date.isValid())) {
      throw new Error('Invalid date');
    }
    if (date.isAfter(new Date())) {
      throw new Error('Birth Date cannot be after today');
    }
    return true;
  };

  // formats the date to DD-MM-YYYY
  const formatDate = (value) => {
    // format the date
    // Convert the date string to a Date object
    const date = new Date(value)
    // Extract the day, month, and year from the Date object
    const day = date.getDate();
    const month = date.getMonth() + 1; // add 1 because getMonth() returns a zero-based index
    const year = date.getFullYear();
    // Create and return the formatted date string in the format "dd-mm-yyyy"
    return `${day}-${month}-${year}`
  }

  // Lists users TODO
  app.get('/users', function (req, res) {
    res.send('lista de usuarios');
  })
  // Get for signup
  app.get('/users/signup', function (req, res) {
    res.render("signup.twig");
  })
  // Post for signup
  app.post('/users/signup', [
    validator.body('password').notEmpty().withMessage('Password is required'),
    validator.body('confirmPassword').notEmpty().withMessage('Confirm password is required'),
    validator.body('password').custom(passwordMatches),
      validator.body('birthdate').custom(isDateValid)
  ], function (req, res) {

    // If there are validation errors, show them
    const errors = validator.validationResult(req);
    if (!errors.isEmpty()) {
      res.redirect("/users/signup" +
          "?message=" + errors.array().map(e => {
            return " " + e.msg
          }) +
          "&messageType=alert-danger ");
      return
    }


    // encrypting the password
    let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
        .update(req.body.password).digest('hex');

    // user to add
    let user = {
      email: req.body.email,
      name: req.body.name,
      surnames: req.body.surnames,
      birthdate: formatDate(req.body.birthdate),
      password: securePassword,
      wallet: 100,
      role: "user"
    }

    // filter to check if the user already exists
    const filter = {
      email: user.email
    }

    // Checks if the user is not already created
    usersRepository.findUser(filter, {}).then( dbUser => {
          if (dbUser == null) {
            usersRepository.insertUser(user).then(userId => {
              res.redirect("/offers/listMyOffers") // TODO: arreglar esto
            }).catch(error => {
              res.redirect("/users/signup" +
                  "?message=Error while signup"+
                  "&messageType=alert-danger ");
            })
          } else {
            res.redirect("/users/signup" +
                "?message=User already exists"+
                "&messageType=alert-danger ");
          }
    }).catch(e => {
      res.redirect("/users/signup" +
          "?message=Error while signup"+
          "&messageType=alert-danger ");
    })
  });
  // Get for login
  app.get('/users/login', function (req, res) {
    res.render("login.twig")
  })
  // Post for login
  app.post('/users/login', function (req, res) {
    let securePassword = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex')
    let filter = {
      email: req.body.email,
      password: securePassword
    }
    usersRepository.findUser(filter, {}).then(user => {
      if (user == null) {
        req.session.user = null;
        res.redirect("/users/login" +
            "?message=Email o password incorrecto"+
            "&messageType=alert-danger ");
      } else {
        req.session.user = user.email;
        if (user.role === "admin"){
          res.redirect("/users")
        } else {
          res.redirect("/users/listMyOffers")
        }
      }
    }).catch(error => {
      req.session.user = null;
      res.redirect("/users/login" +
          "?message=Se ha producido un error al buscar el usuario"+
          "&messageType=alert-danger ");
    })
  })

}
