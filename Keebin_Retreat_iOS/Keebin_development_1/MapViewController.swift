//
//  MapViewController.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 10/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit
import MapKit

class MapViewController: UIViewController, CLLocationManagerDelegate, MKMapViewDelegate {
    
    // outlets
    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var map: MKMapView!
    
    // variables
    let locManager  = CLLocationManager()
    var shop = CoffeeShop()
    var tokens =  [String: String]()
    var home = HomeView2Controller()
    var coffeeBrandsFromDB = [AnyObject]()
    var coffeeShops = [CoffeeShop]()
    
    
    struct shopLoc {
        var brandName: String?
        var address: String?
        var coordinate: CLLocationCoordinate2D?
    }
    
    
    func infoAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    
    @IBAction func refresh(_ sender: UIButton) {
        if CLLocationManager.locationServicesEnabled() {
            let refreshLatitude = self.locManager.location?.coordinate.latitude
            let refreshLongitude = self.locManager.location?.coordinate.longitude
            if (refreshLatitude != nil || refreshLongitude != nil){
                let center = CLLocationCoordinate2D(latitude: refreshLatitude!, longitude: refreshLongitude!)
                let region = MKCoordinateRegionMakeWithDistance(center, 1000, 1000)
                
                self.map.setRegion(region, animated: true)
            } else {
                infoAlert(message: "Keebin kunne ikke finde din lokation.")
            }
        } else {
            infoAlert(message: "Location services skal være slået til for at Keebin kan finde din lokation.")
        }
    }
    
    
    override func viewDidLoad() {
        map.delegate = self
        super.viewDidLoad()
        getCoffeeBrandsFromDB(){ dbCoffeeBrands in
            self.coffeeBrandsFromDB = dbCoffeeBrands
            self.getAllCoffeeShops(){CoffeeShopArray in
                self.coffeeShops = CoffeeShopArray
                self.coffeeShopLocs()
            }
        }
        
        map.showsUserLocation = true
        locManager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    
    
    override func viewWillAppear(_ animated: Bool) {
        
        locManager.requestAlwaysAuthorization()
        locManager.requestWhenInUseAuthorization()
        
        if (shop.latitude != nil && shop.longitude != nil){
            
            tabBarController?.tabBar.isHidden = true
            let initialLocation = CLLocationCoordinate2D(latitude: shop.latitude!, longitude: shop.longitude!)
            let viewRegion = MKCoordinateRegionMakeWithDistance(initialLocation, 1000, 1000)
            map.setRegion(viewRegion, animated: true)
        } else {
            tabBarController?.tabBar.isHidden = false
            let myLocation = self.locManager.location?.coordinate
            if ((myLocation) != nil){
                let viewRegion = MKCoordinateRegionMakeWithDistance(myLocation!, 1000, 1000)
                map.setRegion(viewRegion, animated: true)
            } else {
                let initialLocation = CLLocationCoordinate2D(latitude: 55.649508, longitude: 10.601288)
                let viewRegion = MKCoordinateRegionMakeWithDistance(initialLocation, 300000, 300000)
                map.setRegion(viewRegion, animated: true)
            }
            locManager.requestWhenInUseAuthorization()
            if CLLocationManager.locationServicesEnabled() {
                locManager.startUpdatingLocation()
            }
        }
    }
    
    
    func coffeeShopLocs(){
        for each in coffeeShops{
            var brandNameForShopLoc: String = ""
            for i in coffeeBrandsFromDB {
                if (each.brandName == i.value(forKey: "dataBaseId")! as? Int){
                    brandNameForShopLoc = i.value(forKey: "brandName") as! String
                }
            }
            placeShopMarker(lat: each.latitude!, long: each.longitude!, brandName: brandNameForShopLoc, address: each.address!)
        }
    }
    
    
    func placeShopMarker(lat: Double, long: Double, brandName: String, address: String){
        let shop = shopLocForMap(brandName: brandName,
                                 address: address,
                                 coordinate: CLLocationCoordinate2D(latitude: lat, longitude: long))
        
        DispatchQueue.main.async {
            self.map.addAnnotation(shop)
        }
    }
    
    
    
    func getAllCoffeeShops(callback: @escaping (_ allUsersArray: Array<CoffeeShop>)-> ()) {
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/coffee/allshops/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue(tokens["accessToken"]!, forHTTPHeaderField: "accessToken")
        request.addValue(tokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        var CoffeeShopArray = Array<(CoffeeShop)>()
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            var json:[[String:Any]] = []
            
            if let httpResponse = response as? HTTPURLResponse {
                
                if (httpResponse.statusCode == 200){
                    let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                    updateAccessTokenOnly(newAccessToken: aToken!)
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [[String:Any]] {
                        json = jsonResponse!
                    }
                }
            }
            
            var mu = CoffeeShop()
            if json.count > 0 {
                for blog in json {
                    mu = CoffeeShop()
                    if let address = blog["address"]! as? String {
                        mu.address = (address)
                    }
                    if let brandName = blog["brandName"]! as? Int {
                        mu.brandName = (brandName)
                    }
                    if let coffeeCode = blog["coffeeCode"]! as? String {
                        mu.coffeeCode = (coffeeCode)
                    }
                    if let email = blog["email"]! as? String {
                        mu.email = (email)
                    }
                    if let id = blog["id"]! as? Int {
                        mu.id = (id)
                    }
                    if let latitude = blog["latitude"]! as? Double {
                        mu.latitude = (latitude)
                    }
                    if let longitude = blog["longitude"]! as? Double {
                        mu.longitude = (longitude)
                    }
                    if let phone = blog["phone"]! as? String {
                        mu.phone = (phone)
                    }
                    CoffeeShopArray.append(mu)
                }
            }
            callback(CoffeeShopArray)
        })
        task.resume()
    }
}
