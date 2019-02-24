/**
 * Created by dino on 07-02-2017.
 */

var db = require('./../HouseKeeping/DataBaseCreation.js');
var conn = db.connect();
var sequelize = db.connect();
var user = db.User();
var prePaidCard = db.PrePaidCard();
var prePaidCoffeeCard = db.PrePaidCoffeeCard();
var shop = db.CoffeeShop();
//


// _createPrePaidCard(1,2, function (data) {
//
// })

// AZKY == 1
// KYSB == 2
// buycard("KYSB",5,2, function(data)
// {
//     console.log(data)
// });

// usecard(1, 35, 2, function (data) {
//     console.log(data)
// })

// _newstorecard(399, "blå", 5, 1, function(data)
// {
// console.log(data)
// })

// updatestorecard(4, 3200, "blå", 10, function(data)
// {
//  console.log(data)
// })


// _deletecard(1, function(data)
// {
//
//
// })

// _getmycards(2, function(data)
// {
//     console.log(data)
// })

// _getstorecards(1, function(data){
//     console.log(data)
// })

_getallstorecards( function(data){
    console.log(data)
})

function _getmycards(userID, callback)
{
var allCards = [];

var log = function (inst) {

    allCards.push(inst.get());
}
    prePaidCard.findAll({where: {userId: userID}}).then(function (data, err) {
    if (data !== null) {
        data.forEach(log);
        callback(allCards);
    } else {
        console.log("get all cards fejl ---- ")
        console.log(err);
        allCards = false;
        callback(false);
    }
})
};


function _getallstorecards(callback)
{
    var allCards = [];

    var log = function (inst) {

        allCards.push(inst.get());
    }
    prePaidCoffeeCard.findAll().then(function (data, err) {
        if (data !== null) {
            data.forEach(log);
            callback(allCards);
        } else {
            console.log("get all cards fejl ---- ")
            console.log(err);
            allCards = false;
            callback(false);
        }
    })
}

function _getstorecards(coffeeBrandID, callback)
{
    var allCards = [];

    var log = function (inst) {

        allCards.push(inst.get());
    }
    prePaidCoffeeCard.findAll({where: {coffeeBrandId: coffeeBrandID}}).then(function (data, err) {
        if (data !== null) {
            data.forEach(log);
            callback(allCards);
        } else {
            console.log("get all cards fejl ---- ")
            console.log(err);
            allCards = false;
            callback(false);
        }
    })
};

// denne skal vel aldrig rigtig kaldes? (skal også droppe alle relationer som bruger denne)
function _deletecard(ID, callback) {
    var returnstatement = false;
    prePaidCoffeeCard.find({where: {id: ID}}).then(function (data) {
        if (data !== null) {
            data.destroy().then(function (data) {

                if (data !== null) {
                    console.log("Successfully deleted card with ID - " + ID)
                    callback(true);
                    // successfully deleted the project
                }
                else {
                    console.log("Failed to delete card with ID - " + ID)
                    callback(false);
                }
            })
        }
        else {
            console.log("No card exists with the ID - " + ID)
            callback(false);
        }
    })

}

function _updatestorecard(id, newprice, newcents, newname, newcount, callback)
{
    prePaidCoffeeCard.find({where: {id: id}}).then(function (data) {


        if(data !== null) {
            return conn.transaction(function (t) {
                return data.updateAttributes({
                    price: newprice,
                    cents: newcents,
                    name: newname,
                    count: newcount
                }, {transaction: t})

            }).then(function (result) {
                callback("klippekortet er blevet opdateret.")
            }).catch(function (err) {
                callback("Kunne ikke opdatere klippekortet")
            })
        }
        else
        {
            callback("dette kort eksisterer ikke.")
        }
    })

}

function _newstorecard(price, name, count, brandID, cents, callback)
{
    prePaidCoffeeCard.find({where: {price: price, name: name, coffeeBrandId: brandID, count: count}}).then(function (data) {

        if(data === null) {
            return conn.transaction(function (t) {
                return prePaidCoffeeCard.create({
                    price: price,
                    cents: cents,
                    name: name,
                    count: count,
                    coffeeBrandId: brandID
                }, {transaction: t})

            }).then(function (result) {
                console.log("Oprettet ny klippekorts variation.")
                callback(true)
            }).catch(function (err) {
                console.log("Kunne ikke oprette ny klippekorts variation")
                callback(false)
            })
        }
        else
        {
            console.log("dette kort eksistere allerede")
            console.log(false)
        }
    })

}

function _usecard(PrePaidCardID, purchasedamount, userID, callback) {

    prePaidCard.find({where: {id: PrePaidCardID, userId: userID}}).then(function (data) {

        if (data !== null) {
            var antalklip = data.usesleft;

            if (data.usesleft >= purchasedamount) {
                antalklip = antalklip - purchasedamount;
                return conn.transaction(function (t) {
                    return data.updateAttributes({
                        usesleft: antalklip
                    }, {transaction: t})

                }).then(function (result) {
                    callback("Du har nu " + antalklip + " klip tilbage")
                }).catch(function (err) {
                    callback("der skete en fejl ved købet, prøv igen")
                })
            }
            else {
                callback("du har ikke klip nok på kortet.")
                // logic for user to see.

            }
        }
    })
}


function _buycard(coffeeCode, cardID, userID, callback) {


    var uses = 0
    var usesoncard = 0
    var shopID = 0
    this.userID = userID;

    var brandName = 0;
    var CoffeeID = 0;


    shop.find({where: {coffeeCode: coffeeCode}}).then(function (shop) {


        if (shop !== null) {
            shopID = shop.brandName


              prePaidCard.find({where: {PrePaidCoffeeCardId: cardID, userId: userID}}).then(function (data) { // we have run the callback inside the .then
       // console.log(data.dataValues)
       //            console.log(shop.brandName)
       //            console.log(data.PrePaidCoffeeCardId)
                if (data !== null) {

                        uses = data.usesleft
                        prePaidCoffeeCard.find({where: {id: cardID}}).then(function (data1) { // we have run the callback inside the .then


                            if (data1 !== null) {
                                // logic if found
                                usesoncard = data1.count

                                if(data1.coffeeBrandId == shop.brandName) {
                                    brandName = shop.brandName;
                                    CoffeeID = data1.coffeeBrandId

                                return sequelize.transaction(function (t) {

                                    // chain all your queries here. make sure you return them.
                                    return data.updateAttributes({
                                        usesleft: uses + usesoncard

                                    }, {transaction: t})

                                }).then(function (result) {
                                    console.log("Tilføjet " + usesoncard + "til brugeren med et nyt antal klip: " + (parseInt(uses) + parseInt(usesoncard)))
                                    callback(true)
                                }).catch(function (err) {
                                    callback(false)
                                    console.log("noget gik galt... prøv igen")
                                    console.log("her er fejl fra PrepaidCoffeeCard.js update.. --- " + err)

                                    // Transaction has been rolled back
                                    // err is whatever rejected the promise chain returned to the transaction callback
                                });

                            }
                            else
                            {
                                console.log("denne butik har ingen tilsvarende kort.")
                            }


                            }
                            else {
                                console.log("der er ikke noget klippekort ")
                                callback(false)

                            }
                        })

                }
                else {


                     prePaidCoffeeCard.find({where: {id: cardID}}).then(function (data1) { // we have run the callback inside the .then

                         if(data1 !== null)
                         {
                         if (data1.coffeeBrandId == shop.brandName) {
                             user.find({where: {id: userID}}).then(function (data) { // we have run the callback inside the .then

                                 if (data !== null) {

                                     prePaidCoffeeCard.find({where: {id: cardID}}).then(function (data1) { // we have run the callback inside the .then

                                         if (data1 !== null) {
                                             return conn.transaction(function (t) {
                                                 return prePaidCard.create({
                                                     PrePaidCoffeeCardId: cardID,
                                                     userId: userID,
                                                     usesleft: data1.count
                                                 }, {transaction: t})

                                             }).then(function (result) {
                                                 callback(true)
                                                 console.log("Oprettet nyt kort til brugeren.")
                                             }).catch(function (err) {
                                                 callback(false)
                                                 console.log("der gik noget galt i oprettelsen af kortet.")
                                             })
                                         }
                                         else {
                                             callback(false)
                                             console.log("no such card..")
                                             // logic if no store has such a card.
                                         }
                                     })
                                 }
                                 else {
                                     callback(false)
                                     console.log("no user with the given ID")
                                     // logic for user to see.

                                 }
                             })
                         }
                         else {
                             callback(false)
                             console.log("der skette en fejl, enten har butikken ikke dette klippekort ellers er kaffekoden forkert.")
                         }
                     }
                    else
                         {
                             callback(false)
                             console.log("intet klippekort med det id..")
                         }
                })
                    // }
                    // else
                    // {
                    //     console.log("kaffekoden og kaffekortsIDen er forkert.")
                    // }
                }

            })
        }
        else {
            console.log("der skette en fejl, enten har butikken ikke dette klippekort ellers er kaffekoden forkert.")
            callback(false)

        }
    })
}


module.exports = {getallstorecards : _getallstorecards, buycard: _buycard, usecard: _usecard, newstorecard : _newstorecard, updatestorecard : _updatestorecard, getstorecards : _getstorecards, getmycards : _getmycards, deletecard : _deletecard };

