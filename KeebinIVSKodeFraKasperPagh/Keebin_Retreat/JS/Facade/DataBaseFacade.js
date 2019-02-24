/**
 * Created by dino on 07-10-2016.
 */


var db = require('./../HouseKeeping/DataBaseCreation.js');
var Sequelize = require('sequelize'); // Requires
var Role = require('./../Entities/Role.js'); // Requires
var User = require('./../Entities/User.js'); // Requires
var OrderItem = require('./../Entities/OrderItem.js'); // Requires
var LoyaltyCards = require('./../Entities/LoyaltyCard.js'); // Requires
var CoffeeBrand = require('./../Entities/CoffeeBrand.js'); // Requires
var Order = require('./../Entities/Order.js'); // Requires
var CoffeeShop = require('./../Entities/CoffeeShop.js'); // Requires
var CoffeeShopUsers = require('./../Entities/CoffeeShopUser.js'); // Requires
var validate = require('./../validation/Validator');
var klippekort = require('./../Entities/PrePaidCoffeeCard.js')
var premium = require('./../Entities/Premium')
var dbVersion = require('./../HouseKeeping/DatabaseVersion.js')
var stripeCustomer = require('./../Entities/stripeCustomer')

var sequelize = db.connect(); // Establishing connection to the MySQL database schema called keebin

sequelize.authenticate().then(function (err)
    {
        if (err)
        {
            console.log('There is connection in ERROR');
        } else
        {
            console.log('Connection has been established successfully');
        }
    }
); // Authenticating connection to the MySQL database connection above


function _createCoffeeBrand(CoffeeBrandName, NumbersOfCoffeesNeeded, callback)
{
    validate.valBrand(CoffeeBrandName, NumbersOfCoffeesNeeded, function (data)
    {
        if (data)
        {
            CoffeeBrand.createCoffeeBrand(CoffeeBrandName, NumbersOfCoffeesNeeded, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })


}

function _deleteCoffeeBrand(CoffeeBrandID, callback)
{
    validate.valID(CoffeeBrandID, function (data)
    {
        if (data)
        {
            CoffeeBrand.deleteCoffeeBrand(CoffeeBrandID, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })

}


function _getCoffeeBrand(CoffeeBrandID, callback)
{
    validate.valID(CoffeeBrandID, function (data)
    {
        if (data)
        {
            CoffeeBrand.getCoffeeBrand(CoffeeBrandID, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}

function _getAllStoreCards(callback)
{
    klippekort.getallstorecards(function (data)
    {
        return callback(data)
    })
};

function _getAllCoffeeBrand(callback)
{

    CoffeeBrand.getAllCoffeeBrands(function (data)
    {
        return callback(data)
    })


};  // this one "gets" all CoffeeShops.

function _putCoffeeBrand(CoffeeBrandID, CoffeeBrandName, numberOfCoffeeNeeded, callback)
{
    validate.valBrand(CoffeeBrandID, CoffeeBrandName, CoffeeBrandName, function (data)
    {
        if (data)
        {
            CoffeeBrand.putCoffeeBrand(CoffeeBrandID, CoffeeBrandName, numberOfCoffeeNeeded, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })

}

function _deleteLoyaltyCard(ID, callback)
{
    validate.valID(ID, function (data)
    {
        if (data)
        {
            LoyaltyCards.deleteLoyaltyCard(ID, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}

function _createLoyaltyCard(brandId, userID, numberOfCoffeesBought, callback)
{
    validate.valID(userID, function (data)
    {
        if (data)
        {
            validate.valBrand(brandId, numberOfCoffeesBought, function (data)
            {
                if (data)
                {
                    LoyaltyCards.createLoyaltyCard(brandId, userID, numberOfCoffeesBought, function (data2)
                    {
                        callback(data2)
                    })
                } else callback(false)
            })
        } else callback(false)
    })

}


function _getLoyaltyCard(ID, callback)
{
    validate.valID(ID, function (data)
    {
        if (data)
        {
            LoyaltyCards.getLoyaltyCard(ID, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}


function _getAllloyaltyCards(userId, callback)
{

    LoyaltyCards.getAllloyaltyCards(userId, function (data2)
    {
        callback(data2)
    })

};  // this one "gets" all CoffeeShops.


function _putLoyaltyCard(LoyaltyCardID, brandName, userID, numberOfCoffeesBought, callback)
{
    validate.valID(LoyaltyCardID, function (data)
    {
        if (data)
        {
            validate.valID(userID, function (data)
            {
                if (data)
                {
                    LoyaltyCards.putLoyaltyCard(LoyaltyCardID, brandName, userID, numberOfCoffeesBought, function (data2)
                    {
                        callback(data2)
                    })
                } else callback(false)
            })
        } else callback(false)
    })
}

function _putLoyaltyCardRedeem(LoyaltyCardID, userID, numberOfCoffeeRedeems, callback)
{
    validate.valID(LoyaltyCardID, function (data)
    {
        if (data)
        {
            validate.valID(userID, function (data)
            {
                if (data)
                {
                    LoyaltyCards.putLoyaltyCardRedeem(LoyaltyCardID, userID, numberOfCoffeeRedeems, function (data2)
                    {
                        callback(data2)
                    })
                } else callback(false)
            })
        } else callback(false)
    })
}

function _createRole(RoleN, callback)
{
    validate.valRole(RoleN, function (data)
    {
        if (data)
        {
            Role.createRole(RoleN, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
};  //create role if roleN does not exist already.


function _deleteRole(RoleId, callback)
{
    validate.valID(RoleId, function (data)
    {
        if (data)
        {
            Role.deleteRole(RoleId, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
};  //create role if roleN does not exist already.

function _putRole(RoleId, NewRoleName, callback)
{
    validate.valID(RoleId, function (data)
    {
        if (data)
        {
            validate.valRole(NewRoleName, function (data)
            {
                if (data)
                {
                    Role.putRole(RoleId, NewRoleName, function (data2)
                    {
                        callback(data2)
                    })
                } else callback(false)
            })
        } else callback(false)
    })
};  //create role if roleN does not exist already.

function _getRole(RoleId, callback)
{
    validate.valID(RoleId, function (data)
    {
        if (data)
        {
            Role.getRole(RoleId, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
};  //create role if roleN does not exist already.

function _getAllRoles(callback)
{

    Role.getAllRoles(function (data2)
    {
        callback(data2)
    })

};  //create role if roleN does not exist already.

function _createUser(FirstName, LastName, Email, Role, Birthday, Sex, password, callback) // this creates a user
{
    validate.valUser(Email, password, Role, function (data)
    {
        console.log("her er val data: " + data);
        if (data === true)
        {
            User.createUser(FirstName, LastName, Email, Role, Birthday, Sex, password, function (data2)
            {
                if (data2){
                    stripeCustomer.createStripeCustomer(data2.email, function (customer) {
                        if (customer){
                            User.putGiveUserStripeCustomerID(customer.email, customer.id, function (data3) {
                                if (data3){
                                    callback(true)
                                }
                            })
                        } else {
                            console.log("something went wrong with createStripeCustomer, but user has been created.")
                            callback(true)
                        }
                    })

                } else {
                    callback(false)
                }
            })
        } else
        {
            callback(false)
        }
    })
}


function _putUser(oldpassword, userEmail, firstName, lastName, email, role, birthday, sex, password, callback)
{
    console.log("kommer vi ind her? :D")
    console.log("kommer vi ind i validate?")

    User.putUser(oldpassword, userEmail, firstName, lastName, email, role, birthday, sex, password, function (data2)
    {
        callback(data2)

    })


}; // this edits user based on email.


function _deleteUser(userEmail, callback)
{
    validate.valEmail(userEmail, function (data)
    {
        if (data)
        {
            User.deleteUser(userEmail, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}; //this one deletes user based on email.


function _getUser(userEmail, callback)
{

    validate.valEmail(userEmail, function (data)
    {
        if (data)
        {
            User.getUser(userEmail, function (data2)
            {
                console.log("data: " + data2);
                callback(data2)
            })

        } else callback(false)
    })
}; // this one "gets" a user based on email.


function _getAllUsers(callback)
{

    User.getAllUsers(function (data2)
    {
        callback(data2)
    })

};  // this one "gets" all CoffeeShops.

function _getUserById(userId, callback)
{
    validate.valID(userId, function (data)
    {
        if (data)
        {
            User.getUserById(userId, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}; //get one user from the DB by ID.

function _createCoffeeShop(email, brandId, address, phone, coffeeCode, longitude, latitude, callback) // this creates a new CoffeeShop
{

    validate.valCoffeeshop(email, brandId, address, phone, coffeeCode, longitude, latitude, function (data)
    {
        if (data)
        {
            CoffeeShop.createCoffeeShop(email, brandId, address, phone, coffeeCode, longitude, latitude, function (data2)
            {

                callback(data2)
            })
        } else callback(false)
    })

}

function _deleteCoffeeShop(coffeeShopEmail, callback)
{
    validate.valEmail(coffeeShopEmail, function (data)
    {
        if (data)
        {
            CoffeeShop.deleteCoffeeShop(coffeeShopEmail, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}; //this one deletes order based on id.


function _getCoffeeShop(coffeeShopEmail, callback)
{
    validate.valEmail(coffeeShopEmail, function (data)
    {
        if (data)
        {
            CoffeeShop.getCoffeeShop(coffeeShopEmail, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
};  // this one "gets" a CoffeeSHop based on CoffeeShop Email.


function _getAllCoffeeShops(callback)
{

    CoffeeShop.getAllCoffeeShops(function (data2)
    {
        callback(data2)
    })

};  // this one "gets" all CoffeeShops.


function _putCoffeeShop(coffeeShopEmail, email, brandId, address, phone, coffeeCode, longitude, latitude, callback)
{
    validate.valCoffeeshop(coffeeShopEmail, brandId, address, phone, coffeeCode, longitude, latitude, function (data)
    {
        if (data)
        {
            CoffeeShop.putCoffeeShop(coffeeShopEmail, email, brandId, address, phone,
                coffeeCode, longitude, latitude, function (data2)
                {
                    callback(data2)
                })
        } else callback(false)
    })


}; // this edits CoffeeShop based on email.


function _createOrder(currentUser, coffeeShopId, platform, callback) // This creates a new order - belonging to a user through the userId and a coffeeShop through CoffeeShopId
{
    validate.valOrder(currentUser, coffeeShopId, platform, function (data)
    {
        if (data)
        {
            Order.createOrder(currentUser, coffeeShopId, platform, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}

function _deleteOrder(orderId, callback)
{
    validate.valID(orderId, function (data)
    {
        if (data)
        {
            Order.deleteOrder(orderId, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
};  //this one deletes order based on id.

function _getOrder(orderId, callback)
{
    validate.valID(orderId, function (data)
    {
        if (data)
        {
            Order.getOrder(orderId, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })
}; // this one "gets" an order based on orderId.

function _getAllOrdersByUser(userEmail, callback)
{

    Order.getAllOrdersByUser(userEmail, function (data2)
    {
        callback(data2)
    })


};  // this one "gets" all CoffeeShops.

function _createOrderItem(orderId, coffeeKindId, quantity, callback) // This creates a new order - belonging to a user through the userId and a coffeeShop through CoffeeShopId
{
    validate.valOrderItem(orderId, coffeeKindId, quantity, function (data)
    {
        if (data)
        {
            OrderItem.createOrderItem(orderId, coffeeKindId, quantity, function (data2)
            {
                callback(data2)
            })
        } else callback(false)
    })

}


//COFFEESHOPUSER STARTS HERE
function _createCoffeeShopUser(userEmail, coffeeShopEmail, callback)
{
    validate.valEmail(userEmail, function (data)
    {
        if (data)
        {
            CoffeeShopUsers.createCoffeeShopUser(userEmail, coffeeShopEmail, function (data2)
            {
                callback(data2)
            })
        } else
        {
            callback(false)
        }
    })
    // CoffeeShopUsers.createCoffeeShopUser(userEmail, coffeeShopEmail, callback)
    // }else return false
};


function _getAllCoffeeShopUserByCoffeeShop(coffeeShopId, callback)
{
    CoffeeShopUsers.getAllCoffeeShopUserByCoffeeShop(coffeeShopId, function (data2)
    {
        callback(data2)
    })

};

function _coffeeBought(userID, coffeeCode, numberOfCoffeesBought, callback)
{
    //Springer steppet med CoffeeCode over. den skal finde Brandname for mig
    validate.valID(userID, function (d)
    {
        if (d)
        {
            validate.valNumber(numberOfCoffeesBought, function (data)
            {
                if (data)
                {
                    CoffeeShop.getCoffeeShopByCoffeeCode(coffeeCode, function (coffeeData)
                    {
                        if (coffeeData)
                        {
                            CoffeeBrand.getCoffeeBrand(coffeeData.brandName, function (brandData)
                            {
                                LoyaltyCards.getLoyaltyCardByUserAndBrand(userID, brandData.id, function (data)
                                {
                                    if (!data)
                                    {
                                        LoyaltyCards.createLoyaltyCard(coffeeData.brandName, userID, numberOfCoffeesBought, brandData.numberOfCoffeeNeeded, function (createData)
                                        {
                                            _createOrder(userID, coffeeData.id, 'android', function (orderData)
                                            {
                                                _createOrderItem(orderData.id, null, numberOfCoffeesBought, function ()
                                                {
                                                    return callback(createData);
                                                })
                                            })
                                        })
                                    } else
                                    {
                                        LoyaltyCards.addToNumberOfCoffeesBought(data.id, numberOfCoffeesBought, brandData.numberOfCoffeeNeeded, function (addData)
                                        {
                                            _createOrder(userID, coffeeData.id, 'android', function (orderData)
                                            {
                                                _createOrderItem(orderData.id, null, numberOfCoffeesBought, function ()
                                                {
                                                    return callback(addData);
                                                })
                                            })
                                        })
                                    }
                                })
                            })
                        } else return callback(data)
                    })
                } else return callback(data)
            })
        } else return callback(d)
    })
}


function _buycard(CoffeeCode, CardID, userID, callback)
{

    klippekort.buycard(CoffeeCode, CardID, userID, function (data)
    {
        if (data !== null)
        {
            callback(data)
        }
        else
        {
            callback("der gik noget galt..")
        }
    })
}

function _getmycards(userID, callback)
{
    validate.valForNullsAndEmpty(function (data)
    {
        if (data == true)
        {
            klippekort.getmycards(userID, function (data)
            {
                callback(data)
            })
        }
        else
        {
            callback("den indtastede data skal være udfyldt!")
        }
    }, userID)

}

function _getstorecards(coffeebrandID, callback)
{
    validate.valForNullsAndEmpty(function (data)
    {
        if (data == true)
        {
            klippekort.getstorecards(coffeebrandID, function (data)
            {
                callback(data)
            })
        }
        else
        {
            callback("den indtastede data skal være udfyldt!")
        }
    }, coffeebrandID)
}


function _usecard(prepaidcardID, purchasedamount, userID, callback)
{

    klippekort.usecard(prepaidcardID, purchasedamount, userID, function (data)
    {
        callback(data)
    })

}

function _newstorecard(price, name, count, brandID, cents, callback)
{
    validate.valForNullsAndEmpty(function (data)
    {
        if (data == true)
        {

            klippekort.newstorecard(price, name, count, cents, brandID, function (data)
            {
                callback(data)
            })
        }
        else
        {
            callback("den indtastede data skal være udfyldt!")
        }
    }, price, name, count, cents, brandID)
}
function _updatestorecard(storecardid, newprice, newcents, newname, newcount, callback)
{
    validate.valForNullsAndEmpty(function (data)
    {
        if (data == true)
        {
            klippekort.updatestorecard(storecardid, newprice, newcents, newname, newcount, function (data)
            {
                callback(data)
            })
        }
        else
        {
            callback("den indtastede data skal være udfyldt!")
        }
    }, storecardid, newprice, newname, newcents, newcount)
}


function _deletestorecard(storecardID, callback)
{
    validate.valForNullsAndEmpty(function (data)
    {
        if (data == true)
        {
            klippekort.deletecard(storecardID, function (data)
            {
                callback(data)
            })
        }
        else
        {
            callback("den indtastede data skal være udfyldt!")
        }
    }, storecardID)
}


// _createCoffeeShopUser('test1@test1.dk','test@test.dk',function(data){
//     if(data){
//         console.log(data)
//         console.log('gået godt')
//     }else console.log('gået skidt')
// })

//COFFEESHOPUSER ENDS HERE


function _createNewPremiumSubscription(userId, callback)
{
    validate.valPremiumSubscription(userId, function (data)
    {
        if (data)
        {
            premium.createPremiumSubscription(userId, function (data)
            {
                console.log("it works: "  + data)
                callback(data)
            })
        } else
        {
            callback(false)
        }
    })
}

function _deletePremiumSubscription(userId, callback)
{
    validate.valPremiumSubscription(userId, function (data)
    {
        if (data)
        {
            premium.deletePremiumSubscription(userId, function (data)
            {
               callback(data)
            })
        } else
        {
            callback(false)
        }
    })
}

function _putPremiumSubscriptionSetToCoffeeReady(userId, callback)
{
    validate.valPremiumSubscription(userId, function (data)
    {
        if (data)
        {
            premium.putPremiumSubscriptionSetToCoffeeReady(userId, function (data)
            {
                callback(data)
            })
        } else
        {
            callback(false)
        }
    })
}

function _putPremiumSubscriptionSetToCoffeeNotReady(userId, callback)
{
    validate.valPremiumSubscription(userId, function (data)
    {
        if (data)
        {
            premium.putPremiumSubscriptionSetToCoffeeNotReady(userId, function (data)
            {
                callback(data)
            })
        } else
        {
            callback(false)
        }
    })
}

function _getPremiumSubscription(userId, callback)
{
    validate.valPremiumSubscription(userId, function (data)
    {
        if (data)
        {
            premium.getPremiumSubscription(userId, function (data)
            {
                callback(data)
            })
        } else
        {
            callback(false)
        }
    })
}

function _getAllPremiumSubscriptions(callback)
{
    premium.getAllPremiumSubscriptions(function (data)
    {
        callback(data)
    })
}


function _getDatabaseVersion(callback)
{
    dbVersion.getDBVersion(function (data)
    {
        callback(data)
    })
}

function _createStripeCustomer(userEmail, callback)
{
    //create Stripe customer
    stripeCustomer.createStripeCustomer(userEmail, function (createdCustomer) {
        if(createdCustomer) {
            //add Stripe Customer ID to User.stripeCustomerId
            callback(createdCustomer)
        } else {
            console.log("fejl i createStripeCustomer")
            callback(false)
        }
    })
}

function _subscribeStripeCustomerToPremium(customerId, callback) {
    stripeCustomer.subscribeCustomerToPlan(customerId, function (data) {
        if(false){
            console.log("Could not subscribe customer to Premium Plan")
            callback(data)
        } else {
            callback(data)

        }
    })
}

function _unsubscribeStripeCustomerFromPremium(user, callback) {
    stripeCustomer.unsubscribeFromPremium(user, function (data) {
        if(data){
            callback(data)

        } else {
            callback(data)

        }
    })
}

function _addACardToCustomer(userEmail, token, callback) {
    User.getUser(userEmail, function (user) {
        if (user){
            stripeCustomer.addCardToCustomer(user.stripeCustomerId, token, function (data2) {
                if(data2){
                    callback(true)
                } else {
                    callback(false)
                }
            })
        } else {
            callback(false)
        }
    })
}


module.exports = {
    createUser: _createUser,
    createRole: _createRole,
    putUser: _putUser,
    deleteUser: _deleteUser,
    getUser: _getUser,
    createCoffeeShop: _createCoffeeShop,
    deleteCoffeeShop: _deleteCoffeeShop,
    createOrder: _createOrder,
    createOrderItem: _createOrderItem,
    deleteOrder: _deleteOrder,
    getOrder: _getOrder,
    getCoffeeShop: _getCoffeeShop,
    putCoffeeShop: _putCoffeeShop,
    getAllCoffeeShops: _getAllCoffeeShops,
    getAllOrdersByUser: _getAllOrdersByUser,
    getAllUsers: _getAllUsers,
    createCoffeeShopUser: _createCoffeeShopUser,
    getAllcoffeeShopUser: _getAllCoffeeShopUserByCoffeeShop,
    getUserById: _getUserById,
    deleteLoyaltyCard: _deleteLoyaltyCard,
    createLoyaltyCard: _createLoyaltyCard,
    getLoyaltyCard: _getLoyaltyCard,
    getAllloyaltyCards: _getAllloyaltyCards,
    putLoyaltyCard: _putLoyaltyCard,
    createCoffeeBrand: _createCoffeeBrand,
    putCoffeeBrand: _putCoffeeBrand,
    getAllCoffeeBrand: _getAllCoffeeBrand,
    getCoffeeBrand: _getCoffeeBrand,
    deleteCoffeeBrand: _deleteCoffeeBrand,
    deleteRole: _deleteRole,
    putRole: _putRole,
    getRole: _getRole,
    getAllRoles: _getAllRoles,
    coffeeBought: _coffeeBought,
    buycard: _buycard,
    getmycards: _getmycards,
    getstorecards: _getstorecards,
    getAllStoreCards: _getAllStoreCards,
    usecard: _usecard,
    newstorecard: _newstorecard,
    updatestorecard: _updatestorecard,
    deletestorecard: _deletestorecard,
    createNewPremiumSubscription: _createNewPremiumSubscription,
    deletePremiumSubscription: _deletePremiumSubscription,
    putPremiumSubscriptionSetToCoffeeReady: _putPremiumSubscriptionSetToCoffeeReady,
    putPremiumSubscriptionSetToCoffeeNotReady: _putPremiumSubscriptionSetToCoffeeNotReady,
    getPremiumSubscription: _getPremiumSubscription,
    getAllPremiumSubscriptions: _getAllPremiumSubscriptions,
    putLoyaltyCardRedeem: _putLoyaltyCardRedeem,
    getDatabaseVersion: _getDatabaseVersion,
    createStripeCustomer: _createStripeCustomer,
    subscribeStripeCustomerToPremium: _subscribeStripeCustomerToPremium,
    unsubscribeStripeCustomerFromPremium: _unsubscribeStripeCustomerFromPremium,
    addACardToCustomer: _addACardToCustomer
}; // Export Module
