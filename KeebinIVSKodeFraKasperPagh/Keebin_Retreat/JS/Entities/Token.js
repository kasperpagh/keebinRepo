/**
 * Created by mrlef on 10/25/2016.
 */

var db = require('./../HouseKeeping/DataBaseCreation.js');
var jwt = require('jsonwebtoken');
var auth = db.Authentication();
var User = db.User();
var uuid = require('node-uuid');
var sequelize = db.connect();


var accessToken = null;
var refreshToken = null;

function _getToken(userData, callback) {
    auth.find({where: {id: 1}}).then(function (data, err) {
        if (data !== null) {
            console.log(data.secret);
            secretKey = data.secret;
            createClaim(userData, secretKey);
            callback(accessToken);


        } else {
            console.log(err);
            console.log("could not find any secret");
        }
    })
}


function createClaim(userData, secretKey) {

    var claims =
    {
        sub: userData.id,
        iss: "www.js.keebin.dk",
        email: userData.email,
        roleId: userData.roleId


    }
    createAccessToken(claims, secretKey)
}


function createAccessToken(claims, secretKey) {


    console.log("running create AccessToken");
    //create accessToken
    accessToken = jwt.sign({
        data: claims
    }, secretKey, {expiresIn: 900}); //dette er 1 måned i sekunder. Skal self være mindre.


    console.log("accessToken has been created: " + accessToken); //this is what our accessToken looks like.

}

//create refreshToken - this happens every time a user logs in.
function _createRefreshToken(userId, callback) {

    //vi putter userId ind forrest sådan så reFreshToken altid vil være unik.
    var refreshToken = userId + uuid.v4();
    console.log("here is a new refreshToken: " + refreshToken)
    console.log("user Id is: " + userId)


    var userRefreshTokenUpdated = false;

    User.find({where: {id: userId}}).then(function (data, err) {
            if (data !== null) {
                console.log("user found - ready to edit refreshToken");

                return sequelize.transaction(function (t) {

                    // chain all your queries here. make sure you return them.
                    return data.updateAttributes({
                        // firstName: firstName,
                        // lastName: lastName,
                        // email: email,
                        // roleId: role,
                        // birthday: birthday,
                        // sex: sex,
                        // password: password
                        refreshToken: refreshToken

                    }, {transaction: t})

                }).then(function (result) {
                    console.log("Transaction has been committed - user with email: " + result.id + ", has the refreshToken updated and saved to the DB");
                    callback(result);

                    // Transaction has been committed
                    // result is whatever the result of the promise chain returned to the transaction callback
                }).catch(function (err) {
                    console.log(err);
                    console.log("userFind in Token har fejl")
                    callback(userRefreshTokenUpdated);
                    // Transaction has been rolled back
                    // err is whatever rejected the promise chain returned to the transaction callback
                });
            } else {

                console.log(err);
                console.log("could not find: " + userId);
                callback(userRefreshTokenUpdated);
            }


        }
    )

    ;

}


module.exports = {getToken: _getToken, createRefreshToken: _createRefreshToken};