/**
 * Created by Dino on 10/19/2016.
 */


var db = require('./../HouseKeeping/DataBaseCreation.js');
var conn = db.connect();
var Role = db.Role();

function _createRole(RoleName, callback) {


    var runIfRoleFoundFalse = function (doesRoleExist) {

        if (doesRoleExist == false) {
            return conn.transaction(function (t) {


                return Role.create({
                    roleName: RoleName


                }, {transaction: t})

            }).then(function (result) {
                console.log("Transaction has been committed - Role " + RoleName + " - has been saved to the DB.");
                callback(true);
            }).catch(function (err) {
                console.log(err);
                callback(false);
            });
        } else {
            console.log("couldn't create Role " + RoleName);
            callback(false);
        }
        ;
    }


    var setRoleFound = function (callback) {
        //setROleFound is the first function to run.


        Role.find({where: {RoleName: RoleName}}).then(function (data) { // we have run the callback inside the .then
            var RoleFound;
            if (data !== null) {
                console.log("role found " + data.roleName)
                RoleFound = true;
            } else {
                RoleFound = false;
            }
            //we give RoleFound to callback
            callback(RoleFound);

        })


    };

    setRoleFound(runIfRoleFoundFalse);


}

function _deleteRole(ID, callback) {
    var returnstatement = false;
    Role.find({where: {id: ID}}).then(function (FoundRole) {
        if (FoundRole !== null) {
            FoundRole.destroy().then(function (data) {

                if (data !== null) {
                    console.log("Successfully deleted Role with ID - " + ID)
                    callback(true);
                    // successfully deleted the project
                }
                else {
                    console.log("Failed to delete Role with ID - " + ID)
                    callback(false);
                }
            })
        }
        else {
            console.log("No Role exists with the ID - " + ID)
            callback(false);
        }
    })

}


function _findRole(ID, callback) {
    Role.find({where: {id: ID}}).then(function (data) {

        if (data !== null) {
            console.log("Role found -  " + data.roleName)
            callback(data);

        } else {
            console.log("Failed to find role with the ID - " + ID);
            callback(false);
        }


    })

}

function _getAllRoles(callback) {
    var allRoles = [];

    var log = function (inst) {

        allRoles.push(inst.get());
    }

    console.log("Roles is running.");
    Role.findAll().then(function (data, err) {
        if (data !== null) {
            console.log("her er data: " + data)
            console.log("Roles found.");
            data.forEach(log);
            callback(allRoles);

        } else {
            console.log(err);
            allRoles = false;
            console.log("could not find any Roles");
            callback(false);

        }


    })


};  // this one "gets" all CoffeeShops.

function _putRole(RoleID, RoleName, callback) {
    Role.find({where: {Id: RoleID}}).then(function (data, err) {
        if (err) {

            console.log("error in updating Role " + RoleName + " returned error - " + err);
            callback(false);

        }
        if (data) {
            console.log("Trying to update Role - " + RoleName)
            data.updateAttributes({
                roleName: RoleName
            }).then(function (result) {
                console.log("Role - " + RoleName + " has been successfully updated!");
                callback(result);
            })
        }
    });
}

module.exports = {
    getAllRoles: _getAllRoles,
    createRole: _createRole,
    deleteRole: _deleteRole,
    getRole: _findRole,
    putRole: _putRole
};