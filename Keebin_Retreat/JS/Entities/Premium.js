/**
 * Created by mrlef on 2/13/2017.
 */


var db = require('./../HouseKeeping/DataBaseCreation.js');
var conn = db.connect();
var schedule = require('node-schedule');

var premium = db.premiumSubscription();


function _deletePremiumSubscription(userId, callback) {
    premium.find({where: {userId: userId}}).then(function (premiumSubscription) {
        if (premiumSubscription !== null) {
            premiumSubscription.destroy().then(function (data) {

                if (data !== null) {
                    callback(true);
                    // successfully deleted the project
                }
                else {
                    callback(false);
                }
            })
        }
        else {
            callback(false);
        }
    })

}

function _createPremiumSubscription(userId, newPremiumCallback) {
    var runIfPremiumSubscriptionFoundIsFalse = function (duplicatecheck) {
        if (duplicatecheck == false) {
            return conn.transaction(function (t) {
                return premium.create({
                    isValidForPremiumCoffee: true,
                    userId: userId
                }, {transaction: t})

            }).then(function (result) {
                newPremiumCallback(result);
            }).catch(function (err) {
                console.log(err);
                newPremiumCallback(false);
            });
        } else {
            newPremiumCallback(false);
        }
        ;
    }
    var checkforduplicates = function (callback) {
        premium.find({where: {userId: userId}}).then(function (data) {
            var Found;
            if (data !== null) {
                console.log("User already has premium -  " + data.userId)
                Found = true;
            } else {
                Found = false;
            }
            callback(Found);
        })
    }
    checkforduplicates(runIfPremiumSubscriptionFoundIsFalse);
}


function _getPremiumSubscription(userId, callback) {
    premium.find({where: {userId: userId}}).then(function (data) {
        if (data !== null) {
            callback(data);
        } else {
            console.log("Failed to find the LoyaltyCard with ID - " + userId);
            callback(false);
        }
    })
}



function _getAllPremiumSubscriptions(callback) {
    var allPremiumSubscriptions = [];
    var log = function (inst) {
        allPremiumSubscriptions.push(inst.get());
    }
    premium.findAll().then(function (data, err) {
        if (data !== null) {
            data.forEach(log);
            callback(allPremiumSubscriptions);
        } else {
            console.log(err);
            callback(false);
        }
    })
};  // this one "gets" all PremiumSubscriptions.



function _putPremiumSubscriptionSetToCoffeeReady(userId, callback) {
    premium.find({where: {userId: userId}}).then(function (data, err) {
        if (data === null) {
            console.log("something went wrong with _putPremiumSubscription: " + err);
            callback(false);
        }
        else if (data.isValidForPremiumCoffee === false){
            console.log("Trying to update... " + data.id)
            // data.updateatt = update given attributes in the object
            // attribute : attributevalue to edit to.
            data.updateAttributes({
                isValidForPremiumCoffee: true,
            }).then(function (result) {
                console.log("PremiumSubscription " + data.id + " has been updated!");
                callback(result);
            })
        } else {
            console.log("Data.isValidForPremiumCoffee is already true.")
        }
    });
}

function _putPremiumSubscriptionSetToCoffeeNotReady(userId, callback) {
    premium.find({where: {userId: userId}}).then(function (data, err) {
        if (data === null) {
            console.log("something went wrong with _putPremiumSubscription: " + err);
            callback(false);
        }
        else {
            data.updateAttributes({
                isValidForPremiumCoffee: false,
            }).then(function (result) {
                callback(result);
            })
        }
    });
}



var j = schedule.scheduleJob({hour: 23, minute: 59, dayOfWeek: 0}, function(){
    _getAllPremiumSubscriptions(function (allPSubs) {
        for (i = 0; i < allPSubs.length; i++){
            _putPremiumSubscriptionSetToCoffeeReady(allPSubs[i].userId, function (data) {
                console.log(data)
            })
        }
    })
});



// Export Functions

module.exports = {
    getAllPremiumSubscriptions: _getAllPremiumSubscriptions,
    createPremiumSubscription: _createPremiumSubscription,
    deletePremiumSubscription: _deletePremiumSubscription,
    getPremiumSubscription: _getPremiumSubscription,
    putPremiumSubscriptionSetToCoffeeReady: _putPremiumSubscriptionSetToCoffeeReady,
    putPremiumSubscriptionSetToCoffeeNotReady: _putPremiumSubscriptionSetToCoffeeNotReady
}; // Export Module/**


