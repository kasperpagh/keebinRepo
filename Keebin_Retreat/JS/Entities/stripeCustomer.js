/**
 * Created by mrlef on 5/11/2017.
 */


// Set your secret key: remember to change this to your live secret key in production
// See your keys here: https://dashboard.stripe.com/account/apikeys
var stripe = require("stripe")("sk_test_owha8O0rJ9JkxvSyHQpFgUjP");
var User = require('./User.js'); // Requires

const util = require('util')


function _createStripeCustomer(userEmail, callback) {
    console.log("CreateStripeCustomer is running.")
    //check if user already has a stripe CustomerId
    var customer = stripe.customers.create({
        email: userEmail,
    }, function (err, customer) {
        // asynchronously called
        if (err) {
            console.log("Something went wrong with creating CustomerId")
            callback(false)
        } else {
            callback(customer)
        }
    })
}

function _deleteStripeCustomer(customerId, callback) {
    stripe.customers.del(
        customerId,
        function(err, confirmation) {
            // asynchronously called
            if(err) {
                console.log("Deletion of stripeCustomer failed: " + err)
                callback(false)
            } else {
                console.log(confirmation)
                callback(true)
            }
        })
}

function _subscribeCustomerToPlan(userStripeId, callback) {
    stripe.customers.retrieve(
        userStripeId,
        function (err, customer) {
            // asynchronously called
            if (err) {
                console.log(err)
                callback(false)
            } else {
                if (customer.subscriptions.data[0].canceled_at == null) {
                    var subscription = stripe.subscriptions.create({
                        customer: userStripeId,
                        plan: "premiumPlanRetreat",
                    }, function (err, subscription) {
                        // asynchronously called
                        if (err) {
                            if (err == "Error: This customer has no attached payment source") {
                                callback("noCard")
                            } else {
                                console.log("error i _subscribeCustomerToPlan: " + err)
                                callback(false)
                            }

                        } else {
                            console.log("der er ikke error")
                            callback(true)
                        }
                    })
                } else {
                    stripe.subscriptions.update(
                        customer.subscriptions.data[0].id,
                        {plan: "premiumPlanRetreat"},
                        function (err, subscription) {
                            // asynchronously called
                            if (err) {
                                console.log(err)
                                callback(false)
                            } else {
                                console.log("StripeCustomers Plan has been updated")
                                callback(true)
                            }
                        }
                    )
                }
            }

        })

}

function _unsubscribeFromPremium(user, callback) {
            stripe.customers.retrieve(
                user.stripeCustomerId,
                function(err, customer) {
                    // asynchronously called
                    if (err){
                        console.log(err)
                        callback(false)
                    } else {
                        if (customer.subscriptions.data[0] != null) {
                            if (customer.subscriptions.data[0].canceled_at == null) {
                            stripe.subscriptions.del(customer.subscriptions.data[0].id,
                                {at_period_end: true},
                                function (err, confirmation) {
                                    // asynchronously called
                                    if (err) {
                                        console.log("Error: failure in cancelling subscription for userId: " + userId +
                                            " customerStripeId: " + user.stripeCustomerId)
                                        callback(false)
                                    } else {
                                        callback(true)
                                    }
                                }
                            )
                        } else {
                                console.log("Customer's Premium has already been canceled.")
                                callback(false)
                            }
                    } else {
                            console.log("Customer does not have a Premium Plan.")
                            callback(false)
                        }

                    }
                }
            )
}

function _addCardToCustomer(customerId, token, callback) {
    stripe.customers.update(customerId, {
        description: "Premium Customer",
        source: token
    }, function(err, customer) {
        // asynchronously called
        if (err){
            callback(false)
        } else {
            callback(true)
        }
    })
}

module.exports = {
    createStripeCustomer: _createStripeCustomer,
    subscribeCustomerToPlan: _subscribeCustomerToPlan,
    deleteStripeCustomer: _deleteStripeCustomer,
    unsubscribeFromPremium: _unsubscribeFromPremium,
    addCardToCustomer: _addCardToCustomer
};


