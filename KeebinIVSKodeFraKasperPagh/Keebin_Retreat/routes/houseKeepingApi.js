/**
 * Created by kaspe on 2017-02-09.
 */
var express = require('express');
var router = express.Router();
var facade = require("../JS/Facade/DataBaseFacade");
var bcrypt = require('bcryptjs');


router.get("/picture/:name", function (req, res, next) {
    var options = {
        root: __dirname + '/pics/brandImages/',
        headers: {
            "accessToken": req.headers.accessToken
        }
    }
    console.log("i api");
    res.sendFile(req.params.name + ".png", options, function (err) {
        console.log("i funk");
        if (err) {
            console.log("i err");
            console.log(err);
        } else {
            console.log("i not err");
            console.log('Sent: picture');

        }
    });
});


router.get("/image/:shopEmail", function (req, res, next) {
    var options =
        {
            root: __dirname + '/pics/shopImages/',
            headers: {
                "accessToken": req.headers.accessToken
            }
        }
    console.log("i api");
    res.sendFile(req.params.shopEmail + ".png", options, function (err) {
        console.log("i funk");
        if (err) {
            console.log("i err");
            console.log(err);
        } else {
            console.log("i not err");
            console.log('Sent: picture');
        }
    });
});

router.get("/info", function (req, res, next) {
    res.send("abekat").end();
});


module.exports = router;
