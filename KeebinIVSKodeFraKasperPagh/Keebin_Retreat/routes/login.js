/**
 * Created by mrlef on 11/8/2016.
 */
var express = require('express');
var router = express.Router();
var facade = require("../JS/Facade/DataBaseFacade");
var bcrypt = require('bcryptjs');
var Token = require('../JS/Entities/Token.js');
var Secret = require('../JS/Entities/Secret.js');
var jwt = require('jsonwebtoken');


router.post("/", function (req, res)
    {

console.log("hej fra update")
        //her skal vi tjekke om der er en accessToken, eller en refreshToken og sammenligner den med vores secretKey.


        console.log("her er email " + req.body.email)
        facade.getUser(req.body.email, function (data)
        {
            if (data !== false)
            {
                console.log("req pass: " + req.body.password + "data pass: " + data.password);

                if (bcrypt.compareSync(req.body.password, data.password))
                {
                    console.log("vi er logget ind")
                    //steffen laver the shit
                    var refreshToken = null;
                    Token.createRefreshToken(data.id, function (newRefreshTokenCreated)
                    {
                        console.log("vi kører refreshTOken")
                        refreshToken = newRefreshTokenCreated.refreshToken;

                        Token.getToken(data, function (accessToken)
                        {
                            console.log("vi kører accessToken")
                            console.log("Found accessToken - " + accessToken);
                            console.log("Found refreshToken - " + refreshToken);
                            var tokens = {"accessToken": accessToken, "refreshToken": refreshToken}
                            res.status(200).send(JSON.stringify(tokens));


                        });
                    });
                } else
                {
                    console.log("vi bliver bare smidt herned")
                    res.status(747).send(); //747 returns that the username or password is incorrect.
                }


            }
            else
            {
                res.status(747).send(); //747 returns that the username or password is incorrect.
            }
        })


    }
);


router.post("/user/new", function (req, res, next)
    {
        console.log("vi er i new user, her er data: " + req.body.firstName);

        var salt = bcrypt.genSaltSync(12);
        var pw = bcrypt.hashSync(req.body.password, salt);
        var userToSave =
            {
                "firstName": req.body.firstName,
                "lastName": req.body.lastName,
                "email": req.body.email,
                "role": req.body.roleId,
                "birthday": new Date(req.body.birthday),
                "sex": req.body.sex,
                "password": pw
            }
        facade.createUser(userToSave.firstName, userToSave.lastName, userToSave.email, userToSave.role, userToSave.birthday, userToSave.sex, userToSave.password, function (status)
            {
                console.log("Når facade, her er status: " + status + " og pw: "+ userToSave.password);
                if (status === true)
                {
                    var tokens = {};
                    facade.getUser(userToSave.email, function (data)
                    {
                        Token.createRefreshToken(data.id, function (newRefreshTokenCreated)
                        {
                            console.log("vi kører refreshTOken")
                            refreshToken = newRefreshTokenCreated.refreshToken;

                            Token.getToken(data, function (accessToken)
                            {
                                console.log("vi kører accessToken")
                                console.log("Found accessToken - " + accessToken);
                                console.log("Found refreshToken - " + refreshToken);
                                tokens = {"accessToken": accessToken, "refreshToken": refreshToken}
                                res.writeHead(200, {"accessToken": tokens.accessToken, "refreshToken": tokens.refreshToken});
                                res.status(200).send();

                            });
                        });
                    })

                }
                else
                {
                    res.status(500).send();
                }
            }
        );
    }
);


module.exports = router;