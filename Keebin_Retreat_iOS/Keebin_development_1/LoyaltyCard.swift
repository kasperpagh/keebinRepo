//
//  LoyaltyCard.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 01/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import Foundation


struct loyaltyCard{
    
    var brand: CoffeeBrand?
    var coffeeShop: CoffeeShop? //If some brand wants to issue their loyalty cards to ONLY specific branches, otherwise null.
    var numberOfCoffeesBought: Int?
    var numberofBeans: String?
    var dateIssued: Date?
    var isValid: Bool?
    var readyForFreeCoffee: Bool?
    var id: Int?
    var brandName: Int?
    var nameOfBrand: String?
    
}
