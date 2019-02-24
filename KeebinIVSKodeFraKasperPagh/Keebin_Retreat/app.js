var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var bcrypt = require('bcryptjs');
var Token = require('./JS/Entities/Token.js');
var jwt = require('jsonwebtoken');
var Secret = require('./JS/Entities/Secret.js');
var User = require('./JS/Entities/User.js');
var cookie = require('cookie');
var passport = require('passport');

//Loggers
var expressWinston = require('express-winston');
var winston = require('winston');

// var Token = require('./JS/Token.js');
// var jwt = require('jsonwebtoken');
// var Secret = require('./JS/Secret.js');
// var passport = require('passport');
// var Strategy = require('passport-http-bearer').Strategy;

var login = require('./routes/login');
var users = require('./routes/userApi');
var coffee = require('./routes/coffeeApi');
var order = require('./routes/orderApi');
var houseKeeping = require("./routes/houseKeepingApi");


var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// function redirectSec(req, res, next) {
//
//     if (req.headers['x-forwarded-proto'] == 'http') {
//         console.log("rammer redir til https, her er url: " + 'https://' + req.headers.host + req.path);
//         res.redirect('https://' + req.headers.host + req.path);
//     } else {
//         console.log("Allerede https");
//         return next();
//     }
// }

//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
// app.use(redirectSec());


var logPathEroor = path.join(__dirname, 'logs', 'serverError.log');
console.log(logPathEroor);
var logPath = path.join(__dirname, 'logs', 'serverLog.log');
console.log(logPath);

app.use(function (req, res, next)
{
    if (req.headers['x-forwarded-proto'] == 'http')
    {
        console.log("rammer redir til https, her er url: " + 'https://' + req.headers.host + req.path);
        res.redirect('https://' + req.headers.host + req.path);
    } else
    {
        console.log("Allerede https");
        return next();
    }
});


app.get('/', function (req, res)
{
    res.status('200').send('Service is up');
});

app.post("/appswitchstatus", function (req, res)
{
    console.log("Vi er i MP api!, her er req: " + req.body);
});


app.use(expressWinston.logger({
    transports: [
        new (winston.transports.File)({
            filename: process.env.OPENSHIFT_LOG_DIR + "serverLog.log",
            json: true,
            colorize: true
        })
    ]
}));

app.use('/login', login);

app.all('/api/*', function (req, res, next)
{
    var secretKey;

    // Her henter vi først secretKey
    var getSecret = Secret.getSecretKey(function (data)
    {
        secretKey = data;
        //Hvis vi finder secretKey går vi videre.
        if (getSecret !== null)
        {
            // check header  for Token
            console.log("her er req: " + req)
            console.log("checking if there is a accessToken.")
            var accessToken = req.get('accessToken'); //det er navnet vi skal give accessToken i request fra client.
            console.log("her er accessToken: " + accessToken)
            // decode Token
            if (accessToken !== null)
            {
                console.log("Verifying said accessToken.")
                // verifies Token
                jwt.verify(accessToken, secretKey, function (err, decoded)
                {
                    if (err)
                    {
                        console.log("accessToken blev ikke verified.")
                        var refreshToken = req.get('refreshToken');

                        //hvis vi finder en refreshToken
                        if (refreshToken !== null)
                        {
                            console.log("verifying refreshToken: " + refreshToken);

                            User.getUserByRefreshToken(refreshToken, function (user)
                            {
                                //her skal vi tjekke på refreshToken før vi går videre nedenunder.
                                if (user === false)
                                {
                                    console.log("kunne ikke verify refreshToken")
                                    //det virkede ikke vi sender user til Login.
                                    res.status(401).send(false);
                                } else
                                {

                                    console.log("refreshToken blev verified, laver ny accessToken");
                                    //Hvis vi får lavet en ny accessToken sender vi user til home med en accessToken. Den skal client gemme i sharedPreferences og lave en ny cookie med den i.
                                    //lav ny accessToken
                                    Token.getToken(user, function (data)
                                    {
                                        console.log("hvad er user? " + user)
                                        console.log("Success vi har fået en ny accessToken: " + data)
                                        var newAccessToken = data;
                                        req.headers.accessToken = newAccessToken;
                                        jwt.verify(newAccessToken, secretKey, function (err, decoded)
                                        {
                                            console.log("this is decoded from authenticate in app.ja: " + JSON.stringify(decoded) + " her er info vi skal have " + decoded.data.roleId)
                                            req.decoded = decoded;

                                            next();
                                        })
                                    });

                                }


                            });
                        }

                    } else
                    {
                        // if everything is good, save to request for use in other routes
                        req.headers.accessToken = accessToken;
                        req.decoded = decoded;
                        console.log("accessToken blev verified")
                        next();
                        // res.redirect(307, "/home"); //redirect til appens "home" side - Kan ikke finde ud af hvordan jeg sender decoded med. Skal jeg lave en cookie?
                    }
                });

            } else
            {
                console.log("No Token found will start redirecting...")
                // if there is no Token
                //redirect user to login page.
                res.status(401).send("Send bruger til login side.");
            }
        }
    })
});


app.use('/api/users', users); // User + Role + LoyaltyCard -- Done (testet og alt virker)
app.use('/api/coffee', coffee); // everything to do with Coffee brand, shop, shopuser... -- Done
app.use('/api/order', order); // order + orderitem --- DONE (testet og alt virker. manglede get all users func som er added og testet!)
app.use('/api/housekeeping', houseKeeping);

// skal kommenteres ind igen!
app.use(expressWinston.errorLogger({
    transports: [
        new winston.transports.Console({
            json: true,
            colorize: true
        }),
        new (winston.transports.File)({
            filename: process.env.OPENSHIFT_LOG_DIR + "errorLog.log",
            json: true,
            colorize: true
        })
    ]
}));


// catch 404 and forward to error handler
app.use(function (req, res, next)
{
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development')
{
    app.use(function (err, req, res, next)
    {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function (err, req, res, next)
{
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;
