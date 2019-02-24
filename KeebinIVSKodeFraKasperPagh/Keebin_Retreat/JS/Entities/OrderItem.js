/**
 * Created by mrlef on 10/19/2016.
 */
var orderId;
var coffeeKindId;
var quantity;

var db = require('./../HouseKeeping/DataBaseCreation.js');
var sequelize = db.connect();
var Order = db.Order();
var OrderItem = db.OrderItem();

function _newOrderItem(orderId, coffeeKindId, quantity) {
    this.orderId = orderId;
    this.coffeeKindId = coffeeKindId;
    this.quantity = quantity;

}


// Export Functions

function _createOrderItem(orderId, coffeeKindId, quantity, callback) // This creates a new order - belonging to a user through the userId and a coffeeShop through CoffeeShopId
{
    var orderItemCreated = false;

    console.log("newCoffeeShop is running.")
    Order.find({where: {id: orderId}}).then(function (data) { //we check if the order exists based on it's id.
        if (data == null) {
            console.log("Order not found");
            callback(orderItemCreated);
        } else {
            return sequelize.transaction(function (t) {
                console.log("Order with ID " + data.id + " found. Will insert orderItem.")
                // chain all your queries here. make sure you return them.
                return OrderItem.create({
                    Order_ID: orderId,
                    CoffeeKind_ID: coffeeKindId,
                    quantity: quantity

                }, {transaction: t})

            }).then(function (result) {
                console.log("Transaction has been committed - OrderItem has been added to the DB - to order with ID: " + data.id);
                orderItemCreated = true;
                callback(orderItemCreated);

                // Transaction has been committed
                // result is whatever the result of the promise chain returned to the transaction callback
            }).catch(function (err) {
                console.log(err);
                callback(orderItemCreated);
                // Transaction has been rolled back
                // err is whatever rejected the promise chain returned to the transaction callback
            })
        }
    })
}

module.exports = {CreateNewOrderItemObject: _newOrderItem, createOrderItem: _createOrderItem}; // Export Module