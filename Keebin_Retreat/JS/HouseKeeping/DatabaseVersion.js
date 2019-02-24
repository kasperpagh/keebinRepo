/**
 * Created by mrlef on 4/24/2017.
 */

var db = require('./DataBaseCreation.js');
var conn = db.connect();
var dbVersion = db.databaseVersion()

function _getDBVersion(callback) {
    dbVersion.find({where: {id: 1}}).then(function (data, err) {
        if (data !== null) {
            console.log("Her er DBVersion " + data.version);
            var version = data.version;
            callback(version);
        } else {
            console.log(err);
            console.log("could not find any DB Version");
            callback("false")
        }
    })
}

module.exports = {
    getDBVersion: _getDBVersion
};