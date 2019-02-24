//
//  CoffeeShop.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 01/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import Foundation

struct CoffeeShop{
    
    var id: Int?
    var email: String?
    var address: String?
    var phone: String? //Needs to be a String since someone is gonna be dumb enough to give the number as: +45 whatever
    var coffeeCode: String?
    var longitude: Double?
    var latitude: Double?
    var brandName: Int?
}
