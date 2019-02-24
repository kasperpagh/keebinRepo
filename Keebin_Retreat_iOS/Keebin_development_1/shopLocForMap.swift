//
//  shopLocForMap.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 16/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import MapKit

class shopLocForMap: NSObject, MKAnnotation {
    let title: String?
    let locationName: String
    var coordinate: CLLocationCoordinate2D
    
    init(brandName: String, address: String, coordinate: CLLocationCoordinate2D) {
        self.title = brandName
        self.locationName = address
        self.coordinate = coordinate
        
        super.init()
    }
    var subtitle: String? {
        return locationName
    }
}
