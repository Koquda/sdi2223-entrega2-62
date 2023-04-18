module.exports = function (app, usersRepository) {
  const validator = app.get('validator')

  app.get('/users', function (req, res) {
    res.send('lista de usuarios');
  })
  app.get('/users/signup', function (req, res) {
    res.render("signup.twig");
  })

  const passwordMatches = (value, { req }) => {
    if (value !== req.body.confirmPassword) {
      throw new Error('Passwords do not match');
    }
    return true;
  };


  app.post('/users/signup', [
    validator.body('password').notEmpty().withMessage('Password is required'),
    validator.body('confirmPassword').notEmpty().withMessage('Confirm password is required'),
    validator.body('password').custom(passwordMatches),
  ], function (req, res) {

    // If there are validation errors, show them
    const errors = validator.validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }


    // encrypting the password
    let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
        .update(req.body.password).digest('hex');

    // user to add
    let user = {
      email: req.body.email,
      name: req.body.name,
      surnames: req.body.surnames,
      birthdate: req.body.birthdate,
      password: securePassword,
      wallet: 100
    }

    const filter = {
      email: user.email
    }

    // Checks if the user is not already created
    usersRepository.findUser(filter, {}).then( dbUser => {
          if (dbUser == null) {

            usersRepository.insertUser(user).then(userId => {
              res.send('User registered ' + userId);
            }).catch(error => {
              res.send("Error asdasdasdasd signup");
            })
          } else {
            res.status(400)
            res.send('User already exists')
          }
    }).catch(e => {
      res.status(400)
      res.send('Error while signup')
    })
  });
}
