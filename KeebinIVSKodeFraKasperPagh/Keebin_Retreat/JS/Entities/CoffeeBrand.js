/**
 * Created by Dino on 10/19/2016.
 */
var db = require('./../HouseKeeping/DataBaseCreation.js');
var conn = db.connect();
var CoffeeBrand = db.CoffeeBrand();

function _createCoffeeBrand(CoffeeBrandName, NumbersOfCoffeesNeeded, callback)
{
    var runIfCoffeeBrandFoundFalse = function (doesCoffeeBrandExist)
    {
        if (doesCoffeeBrandExist == false)
        {
            return conn.transaction(function (t)
            {
                return CoffeeBrand.create({

                    brandName: CoffeeBrandName, numberOfCoffeeNeeded: NumbersOfCoffeesNeeded
                }, {transaction: t})
            }).then(function (result)
            {
                console.log("Transaction has been committed - CoffeBrand " + CoffeeBrandName + " has been saved to the DB.");
                callback(true);

            }).catch(function (err)
            {
                console.log("error while trying to create CoffeBrand with the name " + CoffeeBrandName + " and gave error - " + err);
                console.log(err);
                callback(false);
            });
        } else
        {
            console.log("couldn't create CoffeBrand with the name " + CoffeeBrandName);
            callback(false);
        }
        ;
    }


    var setCoffeeBrandFound = function (callback)
    {


        console.log("setCoffeeBrand is running. ")
        CoffeeBrand.find({where: {brandName: CoffeeBrandName}}).then(function (data)
        { // we have run the callback inside the .then
            var CoffeeBrandFound;

            if (data !== null)
            {
                console.log("CoffeBrand found " + data.brandName)
                CoffeeBrandFound = true;
            } else
            {

                CoffeeBrandFound = false;
            }

            callback(CoffeeBrandFound);

        })


    };

    setCoffeeBrandFound(runIfCoffeeBrandFoundFalse);


}

function _deleteCoffeeBrand(CoffeeBrandID, callback)
{

    CoffeeBrand.find({where: {Id: CoffeeBrandID}}).then(function (FoundCoffeeBrand)
    {
        if (FoundCoffeeBrand !== null)
        {
            FoundCoffeeBrand.destroy().then(function (data)
            {

                if (data !== null)
                {
                    console.log("Successfully deleted CoffeeBrand with ID - " + CoffeeBrandID)
                    callback(true);

                }
                else
                {
                    console.log("Failed to delete CoffeeBrand with ID - " + CoffeeBrandID)
                    callback(false);
                }
            })
        }
        else
        {
            console.log("No CoffeeBrand exists with the ID - " + CoffeeBrandID)
            callback(false);
        }
    })

}


function _getCoffeeBrand(CoffeeBrandID, callback)
{
    CoffeeBrand.find({where: {Id: CoffeeBrandID}}).then(function (data)
    { // we have run the callback inside the .then
        console.log("running loyaltycards");
        if (data !== null)
        {
            console.log("CoffeeBrand found -  " + data.brandName)
            callback(data);

        } else
        {
            console.log("Failed to find CoffeeBrand with id - " + CoffeeBrandID);
            callback(false);
        }


    })

}


function _getAllCoffeeBrand(callback)
{
    var allCoffeeCoffeeBrands = [];

    var log = function (inst)
    {

        allCoffeeCoffeeBrands.push(inst.get());
    }

    console.log("CoffeBrands is running.");
    CoffeeBrand.findAll().then(function (data, err)
    {
        if (data !== null)
        {
            console.log("her er data: " + data)
            console.log("CoffeBrands found.");
            data.forEach(log);
            callback(allCoffeeCoffeeBrands);

        } else
        {
            console.log(err);
            allCoffeeCoffeeBrands = false;
            console.log("could not find any CoffeBrands");
            callback(false);

        }


    })


};  // this one "gets" all CoffeeShops.


function _putCoffeeBrand(CoffeeBrandID, CoffeeBrandName, numberOfCoffeeNeeded, callback)
{
    CoffeeBrand.find({where: {id: CoffeeBrandID}}).then(function (data, err)
    {
        if (err)
        {

            console.log("something went wrong while editting " + CoffeeBrandName + " and gave error - " + err);
            callback(false);

        }
        if (data)
        {
            console.log("Trying to update " + CoffeeBrandName + "...")

            data.updateAttributes({

                brandName: CoffeeBrandName, numberOfCoffeeNeeded: numberOfCoffeeNeeded

            }).then(function (result)
            {
                console.log(CoffeeBrandName + " has been updated!");
                callback(result);
            })
        }
    });
}
// put delete get create
module.exports = {
    getAllCoffeeBrands: _getAllCoffeeBrand,
    createCoffeeBrand: _createCoffeeBrand,
    deleteCoffeeBrand: _deleteCoffeeBrand,
    getCoffeeBrand: _getCoffeeBrand,
    putCoffeeBrand: _putCoffeeBrand
};