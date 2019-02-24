/**
 * Created by Dino on 10/27/2016.
 */
var express = require('express');
var router = express.Router();
var facade = require("../JS/Facade/DataBaseFacade");


// Done


//Deletes a order by OrderId -- WORKS
router.delete("/order/:id", function (req, res) {
    if (req.decoded.data.roleId === 1) {
        facade.deleteOrder(req.params.id, function (status) {
            if (status !== false) {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});
                res.status(200).send();
            }
            else {
                res.status(500).send();
            }
        });
    }
    else {
        res.status(401).send();
    }
});

//New Order -- WORKS..
router.post("/order/new", function (req, res, next) {
        console.log("id here! - " + req.body.userId)
        facade.createOrder(req.body.userId, req.body.coffeeShopId, req.body.platform, function (status) {
                if (status === true) {
                    res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

                    res.status(200).send();
                }
                else {
                    res.status(500).send();
                }
            }
        );
    }
);

//Get order by OrderId -- WORKS
router.get("/order/:orderId", function (req, res, next) {
        facade.getOrder(req.params.orderId, function (data) {
            if (data !== false) {
                res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

                res.end(JSON.stringify(data));
            }
            else {
                res.status(500).send();
            }
        });
    }
);


//creates a new link between the given customers email and the coffeshops email (can do it with full user
// and coffeShop objects too, but this info will be available in client, and will save network traffic
/*
 Example JSON:
 {
 "userEmail" : "lars1@gmail.com",
 "coffeeShopEmail" : "a@a.dk"
 }
 */

// WORKS
router.get("/allorders/from/:email", function (req, res, next) {
    var email = req.params.email;
    console.log("here is email - " + req.params.email);
    facade.getAllOrdersByUser(email, function (status) {
        if (status !== false) {
            res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

            res.end(JSON.stringify(status));
        }
        else {
            res.status(500).send();
        }
    })
});

// WORKS -- but needs to allow duplicates on ID's!!! So we can check more itemorders on the same order.
router.post("/orderitem/new", function (req, res, next) {
        console.log("id here! - " + req.body.orderId)
        facade.createOrderItem(req.body.orderId, req.body.coffeeKindId, req.body.quantity, function (status) {
                if (status === true) {
                    res.writeHead(200, {"Content-Type": "application/json", "accessToken": req.headers.accessToken});

                    res.status(200).send();
                }
                else {
                    res.status(500).send();
                }
            }
        );
    }
);



module.exports = router;
