//
//  CoffeeShopImage+CoreDataProperties.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 27/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import Foundation
import CoreData


extension CoffeeShopImage {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<CoffeeShopImage> {
        return NSFetchRequest<CoffeeShopImage>(entityName: "CoffeeShopImage");
    }

    @NSManaged public var brandName: String?
    @NSManaged public var image: NSData?

}
