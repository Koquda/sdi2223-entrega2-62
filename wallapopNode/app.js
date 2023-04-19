var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');



var app = express();
let crypto = require('crypto');

// Espress-session
let expressSession = require('express-session');
app.use(expressSession({
  secret: 'abcdefg',
  resave: true,
  saveUninitialized: true
}));

app.use(function(req, res, next) {
  res.locals.session = req.session;
  next();
});


// Express-Validator
const {body, validationResult} = require('express-validator');
app.set('validator', {body, validationResult})
const moment = require('moment');
app.set('moment', moment)

// MongoDB
const { MongoClient } = require('mongodb')
const url = "mongodb://localhost:27017/"
app.set('connectionStrings', url)


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

const usersRepository = require("./repositories/usersRepository.js");
usersRepository.init(app, MongoClient);
require("./routes/users.js")(app, usersRepository);

let indexRouter = require('./routes/index.js');
app.use('/', indexRouter);


app.set('clave','abcdefg');
app.set('crypto',crypto);




// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});



// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
