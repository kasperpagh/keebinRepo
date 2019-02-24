var Sequelize = require('sequelize'); // Requires

var sequelize = new Sequelize('keebin', 'adminMA4wq6y', 'lqNM96Hax8Ru', {
    host: process.env.OPENSHIFT_MYSQL_DB_HOST,
    port: process.env.OPENSHIFT_MYSQL_DB_PORT,
    dialect: 'mysql'
}); // Establishing conconnection to the MySQL database schema called keebin


// var sequelize = new Sequelize('keebin', 'keebin', '1234', {
//    host: "localhost",
//    port: 3306,
//    dialect: 'mysql'
// }); // Establishing connection to the MySQL database schema called keebin

sequelize.authenticate().then(function (err) {
    if (err) {
        console.log('There is connection in ERROR');
    } else {
        console.log('Connection has been established successfully');
    }
});  // Authenticate Database connection

var role = sequelize.define('roles', {
    roleName: {
        type: Sequelize.STRING,
        Validate: {notNull: true, unique: true}
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

});   // role table setup

var user = sequelize.define('user', {

    firstName: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        field: 'first_name', // Will result in an attribute that is firstName when user facing but first_name in the database
        Validate: {max: 40}
    },
    lastName: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        Validate: {max: 40}
    },
    email: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        Validate: {notNull: true, isEmail: true, unique: true}
    },
    birthday: {
        type: Sequelize.DATE, // here we decide parameters for this field in the table
        Validate: {isDate: true, notNull: false}
    },
    sex: {
        type: Sequelize.STRING // here we decide parameters for this field in the table
    },
    password: {
        type: Sequelize.STRING,
        Validate: {notNull: true}
    },
    refreshToken: {
        type: Sequelize.STRING,
        Validate: {notNull: false}
    },
    stripeCustomerId: {
        type: Sequelize.STRING,
        Validate: {notNull: false}
    }


}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: true // fjerner timestamps med false denne option skal stå på tabellen


}); // user table setup

var coffeeBrand = sequelize.define('coffeeBrand', {
    brandName: {
        type: Sequelize.STRING,
        Validate: {notNull: true}
    },
    numberOfCoffeeNeeded: {
        type: Sequelize.INTEGER,
        Validate: {notNull: true}
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

}); // coffeeBrand table setup

var loyaltyCards = sequelize.define('loyaltyCards', {
    numberOfCoffeesBought: {
        type: Sequelize.INTEGER,
        Validate: {notNull: true},
    },
    isValid: {
        type: Sequelize.BOOLEAN,
        Validate: {notNull: true}
    },
    numberOfFreeCoffeeAvailable: {
        type: Sequelize.INTEGER,
        Validate: {notNull: true}
    },
    timesUsed: {
        type: Sequelize.INTEGER,
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: true // fjerner timestamps med false denne option skal stå på tabellen

}); // loyaltyCards table setup


var premiumSubscription = sequelize.define('premiumSubscription', {
    isValidForPremiumCoffee: {
        type: Sequelize.BOOLEAN,
        Validate: {notNull: true},
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
}); // premiumSubscription table setup



var coffeeKind = sequelize.define('coffeeKind', {
    price: {
        type: Sequelize.DOUBLE,
        Validate: {notNull: true},
    },
    coffeeKindName: {
        type: Sequelize.STRING,
        Validate: {notNull: true},
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

}); // coffeeKind table setup

var order = sequelize.define('order', {
    platform: {
        type: Sequelize.STRING,
        Validate: {notNull: true},
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: true // fjerner timestamps med false denne option skal stå på tabellen

}); // order table setup

var coffeeShop = sequelize.define('coffeeShop', {
    email: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        Validate: {notNull: true, isEmail: true, unique: true}
    },
    address: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        Validate: {notNull: true}
    },
    phone: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        Validate: {notNull: true}
    },
    coffeeCode: {
        type: Sequelize.STRING, // here we decide parameters for this field in the table
        Validate: {notNull: true}
    },
    longitude: {
        type: Sequelize.DOUBLE // here we decide parameters for this field in the table

    },
    latitude: {
        type: Sequelize.DOUBLE // here we decide parameters for this field in the table

    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

}); // coffeeShop table setup

var coffeeShopUsers = sequelize.define('coffeeShopUsers', {
    //denne tabel tager 2 foreignkeys fra andre tabeller. Den skal ikke indholde "noget af sig selv".

}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

}); // coffeeShopUsers table setup

var orderItem = sequelize.define('orderItem', {
    quantity: {
        type: Sequelize.INTEGER, // here we decide parameters for this field in the table
        Validate: {notNull: true}
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

}); // orderItem table setup


var authentication = sequelize.define('authentication', {
    secret: {
        type: Sequelize.STRING,
        Validate: {notNull: true}
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
    timestamps: false // fjerner timestamps med false denne option skal stå på tabellen

}); // coffeeBrand table setup

var PrePaidCard = sequelize.define('coffeeBrandPrePaidCard', {
    usesleft: {
        type: Sequelize.INTEGER, // here we decide parameters for this field in the table
        Validate: {notNull: true, isEmail: true, unique: true}
    }
});

var PrePaidCoffeeCard = sequelize.define('PrePaidCoffeeCard', {
    price: {
        type: Sequelize.INTEGER,
        Validate: {notNull: true}
    },
    cents: {
      type: Sequelize.INTEGER(2),
        Validate: {notNull: true}
    },
    name: {
        type: Sequelize.STRING,
        Validate: {notNull: true}
    },
    count: {
        type: Sequelize.INTEGER,
        Validate: {notNull: true}
    }

});

var databaseVersion = sequelize.define('databaseVersion', {
    version: {
        type: Sequelize.INTEGER,
        Validate: {notNull: true},
    }
}, {
    freezeTableName: true, // Model tableName will be the same as the model name
}); // premiumSubscription table setup




role.hasMany(user, {foreignKey: {allowNull: false}, onDelete: 'CASCADE'});
user.belongsTo(role, {foreignKey: {allowNull: false}, onDelete: 'CASCADE'});


user.hasMany(loyaltyCards);
loyaltyCards.belongsTo(user);
coffeeBrand.hasMany(loyaltyCards, {foreignKey: 'brandName'});
loyaltyCards.belongsTo(coffeeBrand, {foreignKey: 'brandName'});

premiumSubscription.belongsTo(user);

coffeeShop.hasMany(coffeeKind);
coffeeKind.belongsTo(coffeeShop);

coffeeShopUsers.belongsTo(user);
coffeeShop.hasMany(coffeeShopUsers);
coffeeShopUsers.belongsTo(coffeeShop);

user.hasMany(order);
order.belongsTo(user);
coffeeShop.hasMany(order);
order.belongsTo(coffeeShop);
user.hasMany(order);


coffeeBrand.hasMany(coffeeShop, {foreignKey: "brandName"});
coffeeShop.belongsTo(coffeeBrand, {foreignKey: "brandName"});


order.belongsToMany(coffeeKind, {through: 'orderItem', foreignKey: 'Order_ID'});
coffeeKind.belongsToMany(order, {through: 'orderItem', foreignKey: 'CoffeeKind_ID'}); // Working with  // associations


PrePaidCoffeeCard.belongsTo(coffeeBrand)
PrePaidCard.belongsTo(user);
PrePaidCoffeeCard.hasMany(PrePaidCard);
PrePaidCard.belongsTo(PrePaidCoffeeCard);

coffeeBrand.sync();


role.sync();
premiumSubscription.sync();
user.sync(); // executes the command from above and inserts a new table into the database

loyaltyCards.sync();
coffeeKind.sync();
order.sync();
coffeeShop.sync();
coffeeShopUsers.sync();
orderItem.sync();
authentication.sync();
PrePaidCoffeeCard.sync();
PrePaidCard.sync();
databaseVersion.sync();
// Creating Tables

var drop = 2; // sæt den her til 1 og så dropper du tables.

function _Role() {
    return role;
}

function _User() {
    return user;
}

function _CoffeeBrand() {
    return coffeeBrand;
}

function _LoyaltyCards() {
    return loyaltyCards;
}

function _CoffeeKind() {
    return coffeeKind;
}

function _Order() {
    return order;
}

function _CoffeeShop() {
    return coffeeShop;
}

function _CoffeeShopUsers() {
    return coffeeShopUsers;
}

function _OrderItem() {
    return orderItem;
}

function _connect() {
    return sequelize;
}

function _authentication() {
    return authentication;
}

function _PrePaidCard()
{
    return PrePaidCard
}

function _PrePaidCoffeeCard()
{
    return PrePaidCoffeeCard
}

function _databaseVersion(){
    return databaseVersion
}


if(drop == 1)
{
    sequelize
        .sync() // create the database table for our model(s)
        .then(function(){
            // do some work
        })
        .then(function(){
            return sequelize.drop() // drop all tables in the db
        });
}

function _premiumSubscription(){
    return premiumSubscription
}


// Export Functions // Export Functions

module.exports = {
    Role: _Role, User: _User, CoffeeBrand: _CoffeeBrand,
    LoyaltyCards: _LoyaltyCards, CoffeeKind: _CoffeeKind, Order: _Order, CoffeeShop: _CoffeeShop,
    CoffeeShopUsers: _CoffeeShopUsers, OrderItem: _OrderItem, connect: _connect, Authentication: _authentication,
    PrePaidCoffeeCard: _PrePaidCoffeeCard, PrePaidCard: _PrePaidCard,
    premiumSubscription: _premiumSubscription, databaseVersion: _databaseVersion
}; // Export Module
