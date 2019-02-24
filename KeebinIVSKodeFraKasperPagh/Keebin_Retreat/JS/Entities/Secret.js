/**
 * Created by mrlef on 10/25/2016.
 */

var uuid = require('node-uuid');
var db = require('./../HouseKeeping/DataBaseCreation.js');
var Auth = db.Authentication();
var sequelize = db.connect();

var secretKey = uuid.v4();

// _createSecret(secretKey);

function _createSecret(secretKey) // this creates a secret. ONLY TO BE RUN ONCE
{

    console.log("_createSecret is running.")
    Auth.find({where: {id: 1}}).then(function (data) { //check if a secret already exists
        if (data !== null) {
            console.log("secret already exists")

        } else {
            return sequelize.transaction(function (t) {

                // chain all your queries here. make sure you return them.
                return Auth.create({
                    secret: secretKey

                }, {transaction: t})

            }).then(function (result) {
                console.log("Transaction has been committed - secret has been saved to the DB - " + result.secret);

                // Transaction has been committed
                // result is whatever the result of the promise chain returned to the transaction callback
            }).catch(function (err) {
                console.log(err);
                console.log("Something went wrong. Secret not saved.");
                // Transaction has been rolled back
                // err is whatever rejected the promise chain returned to the transaction callback
            })
        }
    })
}

//Token creation

function _getSecretKey(callback) {
    Auth.find({where: {id: 1}}).then(function (data, err) {
        if (data !== null) {
            console.log("Her er secret " + data.secret);
            secretKey = data.secret;
            callback(secretKey);


        } else {
            console.log(err);
            console.log("could not find any secret");
        }
    })
}


module.exports = {getSecretKey: _getSecretKey};