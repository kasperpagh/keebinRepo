/**
 * Created by Dino on 10/20/2016.
 */
//COFFEESHOPUSER STARTS HERE

var db = require('./../HouseKeeping/DataBaseCreation.js');
var sequelize = db.connect();
var CoffeeShop = db.CoffeeShop();
var CoffeeShopUsers = db.CoffeeShopUsers();
var facade = require("../Facade/DataBaseFacade")
var user = db.User();
var fuser = require('./User.js');
var shop = require('./CoffeeShop.js');

function _createCoffeeShopUser(userEmail, coffeeShopEmail, callback) {
    var status = false;
    var theCoffeShopUser;
    var theCoffeeShop;
    fuser.getUser(userEmail, function (data) {
            if (data !== false) {
                theCoffeShopUser = data;
                CoffeeShop.find({where: {Email: coffeeShopEmail}}).then(function (data, err) {
                        if (data) {
                            theCoffeeShop = data;
                            return sequelize.transaction(function (t) {

                                    // chain all your queries here. make sure you return them.
                                    return CoffeeShopUsers.create({

                                            userId: theCoffeShopUser.id,
                                            coffeeShopId: theCoffeeShop.id

                                        }, {transaction: t}
                                    )

                                }
                            ).then(function (result) {
                                    console.log("Transaction has been committed - coffeeShopUser has been saved to the DB");
                                    status = true;
                                    callback(status);

                                    // Transaction has been committed
                                    // result is whatever the result of the promise chain returned to the transaction callback
                                }
                            ).catch(function (err) {
                                    console.log(err);
                                    callback(status);
                                    // Transaction has been rolled back
                                    // err is whatever rejected the promise chain returned to the transaction callback
                                }
                            )
                        }
                        else {
                            callback(status);
                        }
                    }
                );
            }
            else {
                callback(status);
            }
        }
    );
};


function _getAllCoffeeShopUserByCoffeeShop(coffeeShopId, callback) {

    var usersFound = false;
    CoffeeShopUsers.findAll({
            where: {
                coffeeShopId: coffeeShopId
            }
        }
    ).then(function (data, err) {
            // console.logId(data.get());
            if (data) {
                console.log("here! " + data);
                var returnUsersIds = [];
                var returnUsers = [];
                var logId = function (inst) {
                    console.log("her fra log ID - " + inst.get().userId);
                    returnUsersIds.push(inst.get().userId);
                };
                var logPers = function (inst) {
                    delete inst.get().password;
                    returnUsers.push(inst.get());
                };

                data.forEach(logId);

                console.log("her er rusrids: " + returnUsersIds);

                user.findAll({
                    where: {
                        id: returnUsersIds
                    }
                }).then(function (data, err) {
                    if (data) {
                        data.forEach(logPers);
                        console.log("her er find all usrs data: " + data);
                        callback(returnUsers);
                    }
                    if (err) {
                        console.log("her er err: " + err)
                        callback(false);
                    }
                });

            }
            if (err) {
                console.log("fejl i find all!");
                callback(usersFound);
            }
        }
    );
};

module.exports = {
    getAllCoffeeShopUserByCoffeeShop: _getAllCoffeeShopUserByCoffeeShop,
    createCoffeeShopUser: _createCoffeeShopUser
}; // Export Module