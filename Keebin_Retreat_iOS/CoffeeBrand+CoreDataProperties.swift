//
//  CoffeeBrand+CoreDataProperties.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 08/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import Foundation
import CoreData


extension CoffeeBrand {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<CoffeeBrand> {
        return NSFetchRequest<CoffeeBrand>(entityName: "CoffeeBrand");
    }

    @NSManaged public var brandName: String?
    @NSManaged public var dataBaseId: Int64
    @NSManaged public var id: Int64
    @NSManaged public var numberOfCoffeesNeeded: Int32

}
