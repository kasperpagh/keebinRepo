var db = require('./../HouseKeeping/DataBaseCreation.js');
var conn = db.connect();

var numberOfCoffeesBought;
var userID = "";

var readyForFreeCoffee;
var loyaltyCards = db.LoyaltyCards();


function _deleteLoyaltyCard(ID, callback)
{
    var returnstatement = false;
    loyaltyCards.find({where: {Id: ID}}).then(function (loyaltyCard)
    {
        if (loyaltyCard !== null)
        {
            loyaltyCard.destroy().then(function (data)
            {

                if (data !== null)
                {
                    console.log("Successfully deleted LoyaltyCard with ID - " + ID)
                    callback(true);
                    // successfully deleted the project
                }
                else
                {
                    console.log("Failed to delete LoyaltyCard with ID - " + ID)
                    callback(false);
                }
            })
        }
        else
        {
            console.log("No LoyaltyCard exists with the ID - " + ID)
            callback(false);
        }
    })

}

function _createLoyaltyCard(brandId, userID, numberOfCoffeesBought, numberOfCoffeeNeeded, newLoyalcallback)
{
    this.brandName = brandId;
    this.numberOfCoffeesBought = numberOfCoffeesBought; //loyaltyCards bliver lavet når man første gang trykker "tilføj kop" til en branch.
    this.userID = userID;
    this.readyForFreeCoffee = false;
    var timesUsed = 1


    var returnstatement;
    console.log('vi er nu i create')
    console.log(brandId, userID, numberOfCoffeesBought)
    var runIfLoyaltyCardFoundFalse = function (duplicatecheck, callback)
    {
        // runIfRoleFoundFalse is the second function (the callback) - we feed RoleFound as a parameter and name is doesRoleExist
        if (duplicatecheck == false)
        {
            var freeCoffee = 0
            while (numberOfCoffeesBought >= numberOfCoffeeNeeded)
            {
                freeCoffee += 1
                numberOfCoffeesBought = numberOfCoffeesBought - numberOfCoffeeNeeded
            }
            return conn.transaction(function (t)
            {
                return loyaltyCards.create({
                    numberOfCoffeesBought: numberOfCoffeesBought,

                    userId: userID,
                    brandName: brandId,
                    isValid: true,
                    numberOfFreeCoffeeAvailable: freeCoffee,
                    timesUsed: timesUsed


                }, {transaction: t})

            }).then(function (result)
            {

                console.log("Transaction has been committed - a new LoyaltyCard has been saved to the DB.");
                returnstatement = true;
                newLoyalcallback(returnstatement);
            }).catch(function (err)
            {
                console.log(err);
                returnstatement = false;
                newLoyalcallback(returnstatement);
            });
        } else
        {
            console.log("couldn't create new Loyalty Card ");
            returnstatement = false;
            console.log(newLoyalcallback)
            newLoyalcallback(returnstatement);

        }
        ;
    }


    var checkforduplicates = function (callback)
    {


        loyaltyCards.find({where: {brandName: brandId, userId: userID}}).then(function (data)
        { // we have run the callback inside the .then

            var Found;
            if (data !== null)
            {
                console.log("User found -  " + data.userId)
                Found = true;
            } else
            {
                Found = false;
            }
            callback(Found);

        })

    }


    checkforduplicates(runIfLoyaltyCardFoundFalse);


}


function _getLoyaltyCard(ID, callback)
{
    loyaltyCards.find({where: {Id: ID}}).then(function (data)
    { // we have run the callback inside the .then
        if (data !== null)
        {
            console.log("LoyaltyCard found -  " + data.Id)
            callback(data);

        } else
        {
            console.log("Failed to find the LoyaltyCard with ID - " + ID);
            callback(false);
        }


    })

}
function _getLoyaltyCardByUserAndBrand(userID, brandName, callback)
{


    loyaltyCards.find({where: {userId: userID, brandName: brandName}}).then(function (data)
    { // we have run the callback inside the .then

        console.log('loyaltyCard: ' + data)
        if (data !== null)
        {
            console.log('vi er i done')
            console.log("LoyaltyCard found -  " + data.Id)
            callback(data);

        } else
        {
            // console.log("Failed to find the LoyaltyCard with ID - " + ID);
            callback(false);
        }


    }, function (data)
    {
        callback(false);
    })
}


function _getAllloyaltyCards(userId, callback)
{
    var allloyaltyCards = [];

    var log = function (inst)
    {

        allloyaltyCards.push(inst.get());
    }
    console.log("fuckdisshit - " + userId);
    console.log("CoffeBrands is running.");
    loyaltyCards.findAll({where: {userId: userId}}).then(function (data, err)
    {
        if (data !== null)
        {
            console.log("her er data: " + data)
            console.log("CoffeBrands found.");
            data.forEach(log);
            callback(allloyaltyCards);

        } else
        {
            console.log(err);
            allloyaltyCards = false;
            console.log("could not find any CoffeBrands");
            callback(false);

        }


    })


};  // this one "gets" all CoffeeShops.

function _addToNumberOfCoffeesBought(LoyaltyCardID, numberOfCoffeesBought, numberOfCoffeesNeededForFreeCoffee, callback)
{
    loyaltyCards.find({where: {Id: LoyaltyCardID}}).then(function (data, err)
    {
        var numberOfCoffeesBoughtSoFar
        var freeCoffee = data.numberOfFreeCoffeeAvailable
        if (data === null)
        {
            console.log("something went wrong with editing " + LoyaltyCardID + " and gave an error - " + err);
            callback(false);
        }
        else
        {
            console.log("Trying to update... " + LoyaltyCardID)
            console.log('vi er i _addToNumberOfCoffeesBought og data er:' + LoyaltyCardID, numberOfCoffeesBought)

            numberOfCoffeesBoughtSoFar = "" + (parseInt(data.numberOfCoffeesBought) + parseInt(numberOfCoffeesBought));
            while (numberOfCoffeesBoughtSoFar >= numberOfCoffeesNeededForFreeCoffee)
            {
                freeCoffee += 1
                numberOfCoffeesBoughtSoFar = numberOfCoffeesBoughtSoFar - numberOfCoffeesNeededForFreeCoffee
            }
            data.updateAttributes({
                numberOfCoffeesBought: numberOfCoffeesBoughtSoFar,
                timesUsed: ++data.timesUsed,
                numberOfFreeCoffeeAvailable: freeCoffee
            }).then(function (result)
            {
                console.log("LoyaltyCard " + LoyaltyCardID + " has been updated!");
                callback(true);
            })
        }
    });
}


function _putLoyaltyCard(LoyaltyCardID, brandName, userID, numberOfCoffeesBought, callback)
{
    loyaltyCards.find({where: {Id: LoyaltyCardID}}).then(function (data, err)
    {
        if (data === null)
        {

            console.log("something went wrong with editting " + LoyaltyCardID + " and gave an error - " + err);
            callback(false);

        }
        else
        {
            console.log("Trying to update... " + LoyaltyCardID)
            // data.updateatt = update given attributes in the object
            // attribute : attributevalue to edit to.
            data.updateAttributes({
                numberOfCoffeesBought: numberOfCoffeesBought,
                userId: userID, brandName: brandName
            }).then(function (result)
            {
                console.log("LoyaltyCard " + LoyaltyCardID + " has been updated!");
                callback(result);
            })
        }
    });
}

function _putLoyaltyCardRedeem(LoyaltyCardID, userID, numberOfCoffeeRedeems, callback)
{
    loyaltyCards.find({where: {Id: LoyaltyCardID}}).then(function (data, err)
    {
        if (data === null)
        {
            console.log("something went wrong with editting " + LoyaltyCardID + " and gave an error - " + err);
            callback(false);
        }
        else
        {
            console.log("Trying to update... " + LoyaltyCardID)
            var numberOfRedeemsAvailable = data.numberOfFreeCoffeeAvailable
            if ((numberOfRedeemsAvailable - numberOfCoffeeRedeems) >= 0)
            {
                numberOfRedeemsAvailable = numberOfRedeemsAvailable - numberOfCoffeeRedeems
                data.updateAttributes({
                    numberOfFreeCoffeeAvailable: numberOfRedeemsAvailable
                }).then(function (result)
                {
                    console.log("LoyaltyCard " + LoyaltyCardID + " has been updated with numberOfRedeems!");
                    callback(result);
                })
            } else
            {
                console.log("bruger med userId: " + userID + " har ikke nok coffeeAvailable som han/hun forsøger at redeem")
                callback(false)
            }
        }
    });
}

/*
 function _a (ID, callback) {
 loyaltyCards.find({where: {Id: ID}}).then(function (data, err) { // we have run the callback inside the .then
 if (err) throw new Error(err);
 callback(data);
 });
 }; */


// Export Functions

module.exports = {
    getAllloyaltyCards: _getAllloyaltyCards,
    createLoyaltyCard: _createLoyaltyCard,
    deleteLoyaltyCard: _deleteLoyaltyCard,
    getLoyaltyCard: _getLoyaltyCard,
    putLoyaltyCard: _putLoyaltyCard,
    getLoyaltyCardByUserAndBrand: _getLoyaltyCardByUserAndBrand,
    addToNumberOfCoffeesBought: _addToNumberOfCoffeesBought,
    putLoyaltyCardRedeem: _putLoyaltyCardRedeem
}; // Export Module/**


// below is a useage of loyality cards in etc Controller.
/*

 lc.newLoyaltyCard('1', '3', '3', function(models) {
 if(models == false)
 {
 console.log("if false.... ! " + models);
 }
 else {
 console.log("if true...  - " + models);
 }
 });


 lc.findLoyaltyCard('8', function(models) {
 if(!models)
 {
 console.log("User has not been found");
 }
 else {
 console.log("User has bought - " + models.numberOfCoffeesBought + " coffees with loyal card id - " + models.id);
 }
 });

 lc.deleteLoyaltyCard('7', function(models) {

 console.log("User has been deleted = " + models);

 });

 lc.editLoyaltyCard(8, 1, 3, 7, function(models) {


 console.log("User has been updated = " + models);

 });

 */


