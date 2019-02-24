var express = require('express');
var router = express.Router();
var facade = require("../JS/Facade/DataBaseFacade");
var bcrypt = require('bcryptjs');
var Token = require('../JS/Entities/Token.js');
var jwt = require('jsonwebtoken');
var Secret = require('../JS/Entities/Secret.js');
var User = require('../JS/Entities/User.js');
var cookie = require('cookie');
var passport = require('passport');


//Deletes a user by email -- WORKS
router.delete("/user/:email", function (req, res)
{
    if (req.decoded.data.roleId === 1)  //1 = admin
    {
        console.log("param: " + req.params.email)
        facade.deleteUser(req.params.email, function (status)
        {

            if (status !== false)
            {
                res.writeHead(200, {"accessToken": req.headers.accessToken});
                res.status(200).send();
            }
            else
            {
                res.status(500).send();
            }
        });
    } else
    {
        res.status(401).send();
    }
});

router.get("/gwt", function (req, res, next)
    {
        facade.getUser(req.decoded.data.email, function (data)
        {
            if (data !== false)
            {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

                res.end(JSON.stringify(data));
            }
            else
            {
                res.status(500).send();
            }
        });
    }
);


//New User  -- WORKS
router.post("/user/new", function (req, res, next)
    {


        if (req.decoded.data.roleId === 1) //1 = admin
        {
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

                    console.log("this is the decoded: " + req.decoded);
                    console.log("this is the headers.accessToken: " + req.headers.accessToken);
                    if (status === true)
                    {


                        res.writeHead(200, {"accessToken": req.headers.accessToken});
                        res.status(200).send();
                    }
                    else
                    {
                        res.status(500).send();
                    }
                }
            );
        } else
        {
            res.status(401).send();
        }
    }
);


// WORKS
router.post("/card/new", function (req, res, next)
    {
        facade.createLoyaltyCard(req.body.brandId, req.body.userId, req.body.numberOfCoffeesBought, function (status)
            {


                if (status === true)
                {

                    res.writeHead(200, {"accessToken": req.headers.accessToken});
                    res.status(200).send();
                }
                else
                {
                    res.status(500).send();
                }
            }
        );
    }
);
// WORKS
router.post("/card/coffeeBought", function (req, res, next)
    {
        facade.coffeeBought(req.body.userId, req.body.coffeeCode, req.body.numberOfCoffeesBought, function (status)
            {
                if (status === true)
                {
                    res.writeHead(200, {"accessToken": req.headers.accessToken});
                    res.status(200).send();
                }
                else
                {
                    res.status(500).send();
                }
            }
        );
    }
);


//New Role -- WORKS
router.post("/role/new", function (req, res, next)
    {
        if (req.decoded.data.roleId === 1) //1 = admin
        {

            facade.createRole(req.body.roleName, function (status)
                {

                    if (status === true)
                    {

                        res.writeHead(200, {"accessToken": req.headers.accessToken});
                        res.status(200).send();
                    }
                    else
                    {
                        res.status(500).send();
                    }

                }
            );
        } else
        {
            res.status(401).send();
        }
    }
);

//Get user by email -- WORKS
router.get("/user/:email", function (req, res, next)
    {

        //for at få fat på indholdet i req skal man sige req.get("");
        facade.getUser(req.params.email, function (data)
        {

            if (data !== false)
            {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

                console.log("Sender getUser her er data: " + JSON.stringify(data))
                res.end(JSON.stringify(data));
            }
            else
            {
                res.status(500).send();
            }
        });
    }
);

// WORKS
router.get("/card/:LoyaltyCardId", function (req, res)
    {
        facade.getLoyaltyCard(req.params.LoyaltyCardId, function (data)
        {

            if (data !== false)
            {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

                res.end(JSON.stringify(data));
            }
            else
            {
                res.status(500).send();
            }
        });
    }
);

//Edit a user expects the full input -- WORKS (Returns the edited user)
router.put("/user/:email", function (req, res, next)
    {


        if (req.body.password != null && req.body.password != undefined && req.body.password != "")
        {
            let salt = bcrypt.genSaltSync(10);
            let pw = bcrypt.hashSync(req.body.password, salt);
        }
        else
        {
            pw = req.body.password
        }


        var userToSave =
            {
                "firstName": req.body.firstName,
                "lastName": req.body.lastName,
                "email": req.body.email,
                "role": req.body.role,
                "birthday": new Date(req.body.birthday),
                "sex": req.body.sex,
                "password": pw
            }


        facade.putUser(req.body.oldpassword, req.params.email, userToSave.firstName, userToSave.lastName, userToSave.email, userToSave.role, userToSave.birthday, userToSave.sex, userToSave.password, function (status)
            {
                console.log("her er status: " + status)
                if (status !== false)
                {
                    delete userToSave.password;
                    res.writeHead(200, {"accessToken": req.headers.accessToken});
                    res.write(JSON.stringify(userToSave));
                    res.status(200).send();
                }
                if (status === false)
                {
                    res.status(500).send();
                }
            }
        );

    }
);

// WORKS
router.put("/role/:roleId", function (req, res, next)
    {
        if (req.decoded.data.roleId === 1) //1 = admin
        {
            facade.putRole(req.params.roleId, req.body.roleName, function (status)
                {
                    if (status !== false)
                    {
                        res.writeHead(200, {"accessToken": req.headers.accessToken});
                        res.write(JSON.stringify(status));
                        res.status(200).send();
                    }
                    if (status === false)
                    {
                        res.status(500).send();
                    }
                }
            );
        } else
        {
            res.status(401).send();
        }

    }
);

// WORKS
router.put("/card/:LoyaltyCard", function (req, res, next)
    {
        var LoyaltyCardID = req.params.LoyaltyCard;
        facade.putLoyaltyCard(LoyaltyCardID, req.body.brandName, req.body.userId, req.body.numberOfCoffeesBought, function (status)
            {
                if (status !== false)
                {
                    res.writeHead(200, {"accessToken": req.headers.accessToken});
                    res.write(JSON.stringify(status));
                    res.status(200).send();
                }
                if (status === false)
                {
                    res.status(500).send();
                }
            }
        );
    }
);

router.put("/cardRedeem/:LoyaltyCard", function (req, res, next)
    {
        var LoyaltyCardID = req.params.LoyaltyCard;

        facade.putLoyaltyCardRedeem(LoyaltyCardID, req.body.userId, req.body.numberOfCoffeeRedeems, function (status)
            {
                if (status !== false)
                {
                    res.writeHead(200, {"accessToken": req.headers.accessToken});
                    res.write(JSON.stringify(status));
                    res.status(200).send();
                }
                if (status === false)
                {
                    res.status(500).send();
                }
            }
        );

    }
);


// get all roles WORKS
router.get("/allroles/", function (req, res, next)
{
    if (req.decoded.data.roleId === 1)  //1 = admin  (her skriver du altså før at hvis decoded IKKE er admin kører den getallroles. hvilket er forkert?
    {

        facade.getAllRoles(function (status)
        {
            if (status !== false)
            {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});
                res.end(JSON.stringify(status));
            }
            else
            {
                res.status(500).send();
            }
        })
    } else
    {
        res.status(401).send();
    }
});


// WORKS
router.get("/allcards/", function (req, res)
{
    console.log("decodedsub" + req.decoded.data.sub)
    facade.getAllloyaltyCards(req.decoded.data.sub, function (status)
    {
        if (status !== false)
        {
            res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});
            res.end(JSON.stringify(status));
        }
        else
        {
            res.status(500).send();
        }
    })
});

// WORKS
router.get("/allusers/", function (req, res)
{
    if (req.decoded.data.roleId === 1) //1 = admin
    {
        facade.getAllUsers(function (status)
        {
            if (status !== false)
            {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});
                res.end(JSON.stringify(status));
            }
            else
            {
                res.status(500).send();
            }
        })
    } else
    {
        res.status(401).send();
    }
});


router.post("/user/logout", function (req, res)
{
    User.logoutUser(req.decoded.data.email, function (data)
    {
        if (data)
        {
            res.status(200).send("du er nu logget ud");
        } else
        {
            console.log(false)
        }
    })
});


//Steffen userLogin, userAuth og userLogout slut


router.post("/createPremiumSubscription", function (req, res) {
    //If user does not have Stripe Customer ID it will be created first.
    // facade.createStripeCustomer(req.decoded.data.email, function (data) {
    //     if (data) {
    User.getUserById(req.decoded.data.sub, function (user) {
        if (user){
            facade.subscribeStripeCustomerToPremium(user.stripeCustomerId, function (data) {
                if (data) {
                    if (data == "noCard") {
                        //statusCode for no payment source.
                        res.status(757).send()
                    } else if (data) {
                        facade.createNewPremiumSubscription(req.decoded.data.sub, function (status) {
                            if (status !== false) {
                                res.writeHead(200, {"accessToken": req.headers.accessToken});
                                res.status(200).send();
                            }
                            else {
                                console.log("ERROR in DB: Customer with id: " + req.decoded.data.sub +
                                    " subscribed to Premium Stripe Plan but could not be set to Premium in DB.")
                                res.status(500).send();
                            }
                        })
                    } else {
                        res.status(500).send()
                    }
                } else {
                    res.status(500).send()
                }
            })
        } else {
            console.log("could not find user with id: " + req.decoded.data.sub)
            res.status(500).send();

        }
    })

        // } else {
        //     res.status(500).send();
        // }
    // })
});

router.delete("/deletePremiumSubscription", function (req, res)
{
    User.getUserById(req.decoded.data.sub, function (user) {
        if (user){
            facade.unsubscribeStripeCustomerFromPremium(user, function (data) {
                if(data){
                    facade.deletePremiumSubscription(req.decoded.data.sub, function (status)
                        {
                            if (status !== false)
                            {
                                res.writeHead(200, {"accessToken": req.headers.accessToken});
                                res.status(200).send();
                            }
                            else
                            {
                                res.status(500).send();
                            }
                        }
                    )
                } else {
                    res.status(500).send();
                }
            })
        }else {
            res.status(500).send();
        }
    })
})

router.get("/getPremiumSubscription", function (req, res)
{
    facade.getPremiumSubscription(req.decoded.data.sub, function (status)
        {
            if (status !== false)
            {
                res.writeHead(200, {"accessToken": req.headers.accessToken});
                res.write(JSON.stringify(status));
                res.status(200).send();
            }
            else
            {
                res.status(500).send();
            }
        }
    );
});

router.get("/getAllPremiumSubscriptions", function (req, res)
{
    if (req.decoded.data.roleId === 1) //1 = admin
    {
        facade.getAllPremiumSubscriptions(function (status)
            {
                if (status !== false)
                {
                    res.writeHead(200, {"accessToken": req.headers.accessToken});
                    res.write(JSON.stringify(status));
                    res.status(200).send();
                }
                else
                {
                    res.status(500).send();
                }
            }
        )
    } else
    {
        res.status(401).send();
    }
});

router.put("/setPremiumSubscriptionToCoffeeNotReady", function (req, res)
{
    facade.putPremiumSubscriptionSetToCoffeeNotReady(req.decoded.data.sub, function (status)
        {
            if (status !== false)
            {
                res.writeHead(200, {"accessToken": req.headers.accessToken});
                res.status(200).send();
            }
            else
            {
                res.status(500).send();
            }
        }
    );
});

router.put("/setPremiumSubscriptionToCoffeeReady", function (req, res)
{
    facade.putPremiumSubscriptionSetToCoffeeReady(req.decoded.data.sub, function (status)
        {
            if (status !== false)
            {
                res.writeHead(200, {"accessToken": req.headers.accessToken});
                res.status(200).send();
            }
            else
            {
                res.status(500).send();
            }
        }
    );
});

router.get("/getDBVersion", function (req, res)
{
    facade.getDatabaseVersion(function (status)
        {
            if (status !== false)
            {
                res.writeHead(200, {"accessToken": req.headers.accessToken});
                res.write(JSON.stringify(status));
                res.status(200).send();
            }
            else
            {
                res.status(500).send();
            }
        }
    )
});

router.post("/addCardToUser", function (req, res)
{
    facade.addACardToCustomer(req.decoded.data.email, req.body.token, function (data) {
        if (data){
            res.writeHead(200, {"accessToken": req.headers.accessToken});
            res.status(200).send();
        }
        else
        {
            res.status(500).send();
        }
    })


});




module.exports = router;
