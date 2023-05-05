module.exports = function (app, usersRepository, logsRepository, offersRepository) {
    const validator = app.get('validator')
    const moment = app.get('moment')

    const passwordMatches = (value, {req}) => {
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

    // Lists users
    app.get('/users', function (req, res) {
        // Find all users in the database
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        usersRepository.findUsersPg({}, {}, page).then(result => {
            let totalUsers = result.total;
            let usersPerPage = 5;
            let lastPage = Math.ceil(totalUsers / usersPerPage);

            let pages = [];
            for (let i = Math.max(page - 2, 1); i <= Math.min(page + 2, lastPage); i++) {
                pages.push(i);
            }
            let response = {
                users: result.users,
                pages: pages,
                currentPage: page,
                session: req.session
            }
            // Render the users list template with the users data
            res.render("users.twig", response);
        }).catch(error => {
            // If there's an error, redirect to the error page
            res.redirect("/error");
        });
    })
    // Get for signup
    app.get('/users/signup', function (req, res) {
        res.render("signup.twig", {session: req.session});
    })
    // Post for signup
    app.post('/users/signup', [
        validator.body('email').notEmpty().withMessage('Email is required'),
        validator.body('name').notEmpty().withMessage('Name is required'),
        validator.body('surnames').notEmpty().withMessage('Surnames are required'),
        validator.body('birthdate').custom(isDateValid),
        validator.body('password').notEmpty().withMessage('Password is required'),
        validator.body('confirmPassword').notEmpty().withMessage('Confirm password is required'),
        validator.body('password').custom(passwordMatches)

    ], function (req, res) {
        let date = new Date();
        let dateStr = date.toLocaleDateString();
        let timeStr = date.toLocaleTimeString();
        let logDate = `${dateStr} ${timeStr}`;

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
        usersRepository.findUser(filter, {}).then(dbUser => {
            if (dbUser == null) {
                usersRepository.insertUser(user).then(userId => {
                    req.session.user = user.email;
                    req.session.wallet = user.wallet;
                    req.session.save();

                    let mapping = req.url;
                    let method = req.method;
                    let params = JSON.stringify(req.body);


                    let type = "ALTA";
                    let description = `${mapping} ${method} ${params}`;

                    let log = {
                        type: type,
                        date: logDate,
                        description: description
                    }

                    logsRepository.insertLog(log);

                    res.redirect("/offers/myOffers")
                }).catch(error => {
                    res.redirect("/users/signup" +
                        "?message=Error while signup" +
                        "&messageType=alert-danger ");
                })
            } else {
                res.redirect("/users/signup" +
                    "?message=User already exists" +
                    "&messageType=alert-danger ");
            }
        }).catch(e => {
            res.redirect("/users/signup" +
                "?message=Error while signup" +
                "&messageType=alert-danger ");
        })
    });
    // Get for login
    app.get('/users/login', function (req, res) {
        res.render("login.twig", {session: req.session})
    })
    // Post for login
    app.post('/users/login', function (req, res) {
        let date = new Date();
        let dateStr = date.toLocaleDateString();
        let timeStr = date.toLocaleTimeString();
        let logDate = `${dateStr} ${timeStr}`;

        let securePassword = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex')
        let filter = {
            email: req.body.email,
            password: securePassword
        }
        usersRepository.findUser(filter, {}).then(user => {
            if (user == null) {
                req.session.user = null;

                let type = "LOGIN-ERR";
                let description = `${req.body.email}`;

                let log = {
                    type: type,
                    date: logDate,
                    description: description
                }

                logsRepository.insertLog(log);

                res.redirect("/users/login" +
                    "?message=Email o password incorrecto" +
                    "&messageType=alert-danger ");
            } else {
                req.session.user = user.email;
                req.session.wallet = user.wallet;
                req.session.role = user.role;

                req.session.save();

                let type = "LOGIN-EX";
                let description = `${user.email}`;

                let log = {
                    type: type,
                    date: logDate,
                    description: description
                }

                logsRepository.insertLog(log);

                if (user.role === "admin") {
                    res.redirect("/users")
                } else {
                    res.redirect("/offers/myOffers");
                }
            }
        }).catch(error => {
            req.session.user = null;
            res.redirect("/users/login" +
                "?message=Se ha producido un error al buscar el usuario" +
                "&messageType=alert-danger ");
        })
    })
    app.get('/users/logout', function (req, res) {
        let date = new Date();
        let dateStr = date.toLocaleDateString();
        let timeStr = date.toLocaleTimeString();
        let logDate = `${dateStr} ${timeStr}`;

        let type = "LOGOUT";
        let description = req.session.user;

        let log = {
            type: type,
            date: logDate,
            description: description
        }

        logsRepository.insertLog(log);

        req.session.wallet=null;
        req.session = null;
        res.clearCookie('connect.sid');

        res.redirect("/users/login");
    })
    app.post('/users/deleteSelected', function (req, res) {
        let selectedUsers = req.body.selectedUsers;

        if (typeof selectedUsers === "string") {
            let filter = {email: selectedUsers};
            usersRepository.findUser(filter, {}).then(user => {
                if (user != null && user.role == "user") {

                    usersRepository.deleteUser(filter).then(cant => {
                        offersRepository.deleteOffer({author: user.email}, {}).then(cant => {
                            res.redirect("/users");
                        })

                    });

                }
            })

        } else {
            for (let i = 0; i < selectedUsers.length; i++) {
                let filter = {email: selectedUsers[i]};
                usersRepository.findUser(filter, {}).then(user => {
                    if (user != null && user.role == "user") {

                        usersRepository.deleteUser(filter).then(cant => {
                            offersRepository.deleteOffer({author: user.email}, {}).then(cant => {
                                if(i == selectedUsers.length - 1){
                                    res.redirect("/users");
                                }

                            })
                        });
                    }
                })
            }

        }
    })
}
