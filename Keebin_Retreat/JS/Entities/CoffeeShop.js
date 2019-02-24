/**
 * Created by mrlef on 10/18/2016.
 */
var db = require('./../HouseKeeping/DataBaseCreation.js');
var sequelize = db.connect();
var CoffeeShop = db.CoffeeShop();

var email;
var address;
var phone;
var brandName;
var coffeeCode
var longitude;
var latitude;

function _newCoffeeShop(email, brandName, address, phone, coffeeCode, longitude, latitude) {
    this.email = email;
    this.brandName = brandName;
    this.address = address;
    this.phone = phone;
    this.coffeeCode = coffeeCode;
    this.longitude = longitude;
    this.latitude = latitude;

}


function _createCoffeeShop(email, brandId, address, phone, coffeeCode, longitude, latitude, callback) // this creates a new CoffeeShop

{
    var coffeeShopCreated = false;

    console.log("newCoffeeShop is running.")
    CoffeeShop.find({where: {Email: email}}).then(function (data) { //we check if the CoffeeShop exists based on it's unique email.
        if (data !== null) {
            console.log("CoffeeShop found - email exists already - " + data.email)
            callback(coffeeShopCreated);
        } else {
            return sequelize.transaction(function (t) {

                // chain all your queries here. make sure you return them.
                return CoffeeShop.create({
                    email: email,
                    brandName: brandId,
                    address: address,
                    phone: phone,
                    coffeeCode: coffeeCode,
                    longitude: longitude,
                    latitude: latitude


                }, {transaction: t})

            }).then(function (result) {
                console.log("Transaction has been committed - CoffeeShop has been saved to the DB");
                coffeeShopCreated = true;
                callback(coffeeShopCreated);

                // Transaction has been committed
                // result is whatever the result of the promise chain returned to the transaction callback
            }).catch(function (err) {
                console.log(err);
                callback(coffeeShopCreated);
                // Transaction has been rolled back
                // err is whatever rejected the promise chain returned to the transaction callback
            })
        }
    })
}

function _deleteCoffeeShop(coffeeShopEmail, callback) {
    var coffeeShopDeleted = false;

    console.log("_deleteOrder is running. Finding: " + coffeeShopEmail);
    CoffeeShop.find({where: {Email: coffeeShopEmail}}).then(function (data, err) {
        if (data !== null) {
            console.log("CoffeeShop found - ready to DELETE");
            return sequelize.transaction(function (t) {

                // chain all your queries here. make sure you return them.
                return data.destroy({},
                    {transaction: t})

            }).then(function () {
                console.log("Transaction has been committed - CoffeeShop with email: " + data.email + ", has been DELETED");
                coffeeShopDeleted = true;
                callback(coffeeShopDeleted);


                // Transaction has been committed
                // result is whatever the result of the promise chain returned to the transaction callback
            }).catch(function (err) {
                console.log(err);
                callback(coffeeShopDeleted);

                // Transaction has been rolled back
                // err is whatever rejected the promise chain returned to the transaction callback
            });


        } else {
            console.log(err);
            console.log("could not find: " + coffeeShopEmail);
            callback(orderDeleted);
        }


    })


}; //this one deletes order based on id.


function _getCoffeeShop(coffeeShopEmail, callback) {
    var coffeeShopFound = false;

    console.log("_getCoffeeShop is running. Finding coffeeShop with email: " + coffeeShopEmail);
    CoffeeShop.find({where: {email: coffeeShopEmail}}).then(function (data, err) {
        if (data !== null) {
            console.log("CoffeeShop with email: " + data.email + " found.");
            callback(data);

        } else {
            console.log(err);
            console.log("could not find: " + coffeeShopEmail);
            callback(coffeeShopFound);

        }


    })


};  // this one "gets" a CoffeeSHop based on CoffeeShop Email.

function _getCoffeeShopByCoffeeCode(coffeeCode, callback) {
    var coffeeShopFound = false;
    console.log('vi er inde i get coffeeshop by coffeecode')
    console.log("_getCoffeeShop is running. Finding coffeeShop with coffeeCode: " + coffeeCode);
    CoffeeShop.find({where: {coffeeCode: coffeeCode}}).then(function (data, err) {
        if (data !== null) {
            console.log("CoffeeShop with coffeeCode: " + data.coffeeCode + " found.");
            callback(data);

        } else {
            console.log(err);
            console.log("could not find: " + coffeeShopEmail);
            callback(coffeeShopFound);

        }


    })


};  // this one "gets" a CoffeeSHop based on CoffeeShop coffeeCode.
function _getAllCoffeeShops(callback) {
    var allCoffeeShopsFound = [];

    var log = function (inst) {

        allCoffeeShopsFound.push(inst.get());
    }

    console.log("getAllCoffeeShops is running.");
    CoffeeShop.findAll().then(function (data, err) {
        if (data !== null) {
            console.log("her er data: " + data)
            console.log("CoffeeShops found.");
            data.forEach(log);
            callback(allCoffeeShopsFound);

        } else {
            console.log(err);
            allCoffeeShopsFound = false;
            console.log("could not find any CoffeeShops");
            callback(false);

        }


    })


};  // this one "gets" all CoffeeShops.

function _putCoffeeShop(coffeeShopEmail, email, brandId, address, phone, coffeeCode, longitude, latitude, callback) {
    var coffeeShopUpdated = false;

    console.log("_putCoffeeShop is running. Finding: " + coffeeShopEmail);
    CoffeeShop.find({where: {email: coffeeShopEmail}}).then(function (data, err) {
        if (data !== null) {
            console.log("CoffeeShop found - ready to edit");


            return sequelize.transaction(function (t) {

                // chain all your queries here. make sure you return them.
                return data.updateAttributes({
                    email: email,
                    brandName: brandId,
                    address: address,
                    phone: phone,
                    coffeeCode: coffeeCode,
                    longitude: longitude,
                    latitude: latitude

                }, {transaction: t})

            }).then(function (result) {
                console.log("Transaction has been committed - CoffeeShop with email: " + result.email + ", has been updated and saved to the DB");
                callback(result);

                // Transaction has been committed
                // result is whatever the result of the promise chain returned to the transaction callback
            }).catch(function (err) {
                console.log(err);
                callback(coffeeShopUpdated);
                // Transaction has been rolled back
                // err is whatever rejected the promise chain returned to the transaction callback
            });
        } else {
            console.log(err);
            console.log("could not find: " + email);
            callback(coffeeShopUpdated);
        }


    })


}; // this edits CoffeeShop based on email.

// Export Functions

module.exports = {
    createCoffeeShopObject: _newCoffeeShop,
    getAllCoffeeShops: _getAllCoffeeShops,
    putCoffeeShop: _putCoffeeShop,
    getCoffeeShop: _getCoffeeShop,
    deleteCoffeeShop: _deleteCoffeeShop,
    createCoffeeShop: _createCoffeeShop,
    getCoffeeShopByCoffeeCode: _getCoffeeShopByCoffeeCode
}; // Export Module