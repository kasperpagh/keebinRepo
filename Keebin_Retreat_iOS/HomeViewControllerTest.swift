//
//  HomeViewControllerTest.swift
//  Keebin_development_1
//
//  Created by sr on 22/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit
import CoreLocation

class HomeViewControllerTest: UIViewController, UISearchBarDelegate, CLLocationManagerDelegate {
    
    
    @IBOutlet weak var Search: UISearchBar!
    @IBOutlet weak var err: UILabel!

    
    
    
    func logOut(email: String) {
        var a = [String : String]()
        
        
        
        getTokensFromDB(){ dbTokens in
            
            var accessToken = dbTokens["accessToken"]!
            var refreshToken = dbTokens["refreshToken"]!
            
            // Now escape anything else that isn't URL-friendly
            
            let urlPath = "http://keebintest-pagh.rhcloud.com/api/users/user/logout"
            let url = NSURL(string: urlPath)
            let session = URLSession.shared
            let request = NSMutableURLRequest(url: url as! URL)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue(accessToken, forHTTPHeaderField: "accessToken")
            request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
            request.httpMethod = "POST"
            
            
            
            let jsonObject: [String: Any] = ["email": email]
            
            
            
            
            do {
                let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
                // if the call fails, the catch block is executed
                
                request.httpBody = jsonData
                
                let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                    
                    let httpResponse = response as? HTTPURLResponse;
                    
                    
                    
                    if(httpResponse!.statusCode == 200)
                    {
                        DispatchQueue.main.async {
                            
                            
                            dropTokensTable()
                                {response in
                                    
                                    self.dismiss(animated: true, completion: nil)
                                    
                            }
                            
                            
                        }
                        
                        
                    }
                    else
                    {
                        
                        self.infoAlert(message: "Du kunne ikke logges ud på det givne tidspunkt.")
                        
                    }
                    
                    
                })
                task.resume()
                // The task is just an object with all these properties set
                // In order to actually make the web request, we need to "resume"
            } catch {
                
                
                
                self.infoAlert(message: "Du kunne ikke logges ud.")
                
            }
        }
        
        
    }
    
    
    // Global Variables
    var arrayOfCellData = [cellDataHome]()
    var limit: Int = 5;
    var limitadd: Int = 5;
    let locationManager  = CLLocationManager()
    var tag: Int = 0;
    var long: Double?
    var lat: Double?
    var times: Int = 0;
    var run: Bool = true;
    var coffeeList = Array<(CoffeeShop)>()
    final var finalcoffeeList = Array<(cellDataHome)>()
    var coffeeShopArrayList = Array<(CoffeeShop)>()
    var mapsbool: Bool = false;
    var homebool:Bool = false;
    
    
    struct cellDataHome{
        let text : String!
        let header : String!
        let shopImage : UIImage!
        let mapsImage : UIImage!
        var shop : CoffeeShop!
        var meterstolocation : Int!
    }
    
    override func tableView( _ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(arrayOfCellData.count > limit)
        {
            return limit;
        }
        else
        {
            return arrayOfCellData.count
        }
    }
    
    override func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        let  height = scrollView.frame.size.height
        let contentYoffset = scrollView.contentOffset.y
        let distanceFromBottom = scrollView.contentSize.height - contentYoffset
        if distanceFromBottom < height {
            
            self.err.isHidden = false;
            self.err.text = "Loading...";
            updateViewLimit()
        }
    }
    
    func updateViewLimit(){
        if(limit + limitadd <= coffeeShopArrayList.count)
        {
            limit = limit + limitadd;
            DispatchQueue.main.async {
                self.myTable.reloadData()
                self.err.isHidden = true;
                self.err.text = "";
            }
        }
        else
        {
            if(limit != 10000)
            {
                limit = 10000
                DispatchQueue.main.async {
                    self.myTable.reloadData()
                }
            }
            self.err.isHidden = true;
            self.err.text = "";
        }
    }
    
    override func tableView( _ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = Bundle.main.loadNibNamed("TableViewCellHome", owner: self, options: nil)?.first as! TableViewCellHome
        
        cell.button_ImageMaps.setImage(arrayOfCellData[indexPath.row].mapsImage,  for: UIControlState.normal)
        cell.button_ImageMaps.tag = Int(indexPath.row)
        cell.button_ImageMaps.addTarget(self, action: #selector(HomeViewController.buttonToMaps(_:)), for: .touchUpInside)
        
        cell.button_ImageShop.setImage(arrayOfCellData[indexPath.row].shopImage, for: UIControlState.normal)
        cell.button_ImageShop.tag = Int(indexPath.row)
        cell.button_ImageShop.addTarget(self, action: #selector(HomeViewController.buttonToShop(_:)), for: .touchUpInside)
        
        cell.label_header.text = arrayOfCellData[indexPath.row].header
        cell.label_text.text = arrayOfCellData[indexPath.row].text
        
        if self.locationServiceIsAllowed && self.locationServiceIsOn {
            cell.label_tolocation.text = "\(arrayOfCellData[indexPath.row].meterstolocation!)m"
        }
        else
        {
            cell.label_tolocation.text = ""
        }
        
        return cell
    }
    
    func getdistance(lat0: Double, long0: Double, lat1: Double, long1: Double, callback: @escaping ((Int) -> ())) {
        
        let coordinate₀ = CLLocation(latitude: lat0, longitude: long0)
        let coordinate₁ = CLLocation(latitude: lat1, longitude: long1)
        let distanceInMeters = coordinate₀.distance(from: coordinate₁) // result is in meters
        callback(Int(distanceInMeters))
    }
    
    func buttonToMaps(_ sender: AnyObject?) {
        homebool = false;
        tag = sender!.tag
        mapsbool = true;
        performSegue(withIdentifier: "showMap", sender: self)
    }
    
    func buttonToShop(_ sender: AnyObject?) {
        mapsbool = false;
        tag = sender!.tag
        homebool = true;
        performSegue(withIdentifier: "ShowShop", sender: self)
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if(homebool)
        {
            let dest = segue.destination as! HomeSelectedShopViewController
            dest.shop =  arrayOfCellData[tag].shop
        } else if (mapsbool)
        {
            
            let dest = segue.destination as! MapViewController
            dest.shop =  arrayOfCellData[tag].shop
            
            
        }
        
    }
    
    func sortnearest(){
        if self.locationServiceIsAllowed && self.locationServiceIsOn {
            arrayOfCellData.sort { $0.meterstolocation < $1.meterstolocation }
            DispatchQueue.main.async {
                self.myTable.reloadData()
            }
        }
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        let keywords = Search.text!
        if(!keywords.isEmpty)
        {
            arrayOfCellData = [];
            DispatchQueue.main.async{
                for a in self.finalcoffeeList
                {
                    if(a.header!.lowercased().contains(keywords.lowercased()) || a.text!.lowercased().contains(keywords.lowercased()))
                    {
                        self.arrayOfCellData.append(a)
                    }
                }
                
                self.myTable.reloadData()
                
                if(self.arrayOfCellData.count == 0)
                {
                    self.err.isHidden = false;
                    self.err.text = "Din søgning gav ingen resultater.";
                    self.myTable.isScrollEnabled = false;
                }
                else
                {
                    self.err.text = "";
                    self.err.isHidden = true;
                    self.myTable.isScrollEnabled = true;
                }
            }
        }
        else
        {
            DispatchQueue.main.async{
                self.arrayOfCellData = self.finalcoffeeList
                self.err.isHidden = true;
                self.myTable.isScrollEnabled = true;
                self.myTable.reloadData()
            }
        }
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        let keywords = Search.text!
        arrayOfCellData = [];
        DispatchQueue.main.async{
            for a in self.finalcoffeeList
            {
                if(a.header!.lowercased().contains(keywords.lowercased()) || a.text!.lowercased().contains(keywords.lowercased()))
                {
                    self.arrayOfCellData.append(a)
                }
            }
            self.myTable.reloadData()
            self.Search.endEditing(true)
            if(self.arrayOfCellData.count == 0)
            {
                self.err.isHidden = false;
                self.err.text = "Din søgning gav ingen resultater.";
                self.myTable.isScrollEnabled = false;
            }
            else
            {
                self.err.text = "";
                self.err.isHidden = true;
                self.myTable.isScrollEnabled = true;
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let locValue:CLLocationCoordinate2D = manager.location!.coordinate
        
        
        long = locValue.longitude
        lat = locValue.latitude
        
        if(times == 5)
        {
            sortnearest()
        }
        else
        {
            times = times + 1;
        }
        
        if(self.arrayOfCellData.count > 0)
        {
            for i in 0...self.arrayOfCellData.count-1
            {
                getdistance(lat0: self.arrayOfCellData[i].shop.latitude!, long0: self.arrayOfCellData[i].shop.longitude!, lat1: locValue.latitude, long1: locValue.longitude)
                {distance in
                    self.arrayOfCellData[i].meterstolocation = distance
                }
            }
            DispatchQueue.main.async {
                self.myTable.reloadData()
            }
        }
    }
    
    var locationServiceIsOn: Bool = true;
    var locationServiceIsAllowed: Bool = true;
    
    func islocationon(callback: @escaping ()-> ())
    {
        if CLLocationManager.locationServicesEnabled() {
            switch(CLLocationManager.authorizationStatus()) {
            case .notDetermined, .restricted, .denied:
                locationServiceIsOn = false;
                locationServiceIsAllowed = false;
            case .authorizedAlways, .authorizedWhenInUse:
                locationServiceIsAllowed = true;
                locationServiceIsOn = true;
            }
        } else {
            locationServiceIsOn = false;
            locationServiceIsAllowed = false;
        }
        
        callback()
        
    }
    
    
    
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if CLLocationManager.locationServicesEnabled() {
            switch(CLLocationManager.authorizationStatus()) {
            case .notDetermined, .restricted, .denied:
                locationServiceIsOn = false;
                locationServiceIsAllowed = false;
            case .authorizedAlways, .authorizedWhenInUse:
                locationServiceIsAllowed = true;
                locationServiceIsOn = true;
            }
        } else {
            locationServiceIsOn = false;
            locationServiceIsAllowed = false;
        }
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        tabBarController?.tabBar.isHidden = false
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        islocationon()
            {
                self.err.isHidden = false;
                self.err.text = "Loading...";
                self.locationManager.requestAlwaysAuthorization()
                
                
                self.lat = 2.2;
                self.long = 2.2;
                
                
                
                
                self.locationManager.delegate = self
                self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
                self.locationManager.pausesLocationUpdatesAutomatically = false
                self.locationManager.startUpdatingLocation()
                
                
                
                //        Timer.scheduledTimer(timeInterval: 1,
                //                             target: self,
                //                             selector: #selector(self.islocationon),
                //                             userInfo: nil,
                //                             repeats: true)
                
                
                self.Search.delegate = self
                
                self.err.isHidden = true;
                
                getTokensFromDB(){ dbTokens in
                    
                    self.getAllCoffeeShops(accessToken: dbTokens["accessToken"]!, refreshToken: dbTokens["refreshToken"]!){coffeeArray in
                        
                        self.coffeeShopArrayList = coffeeArray
                        
                        for item in coffeeArray
                        {
                            var meters: Int?
                            
                            
                            
                            if self.locationServiceIsAllowed && self.locationServiceIsOn {
                                
                                self.getdistance(lat0: item.latitude!, long0: item.longitude!, lat1: self.lat!, long1: self.long!)
                                {s in
                                    
                                    meters = s;
                                    self.arrayOfCellData.append(cellDataHome( text : "\(item.address!)", header : "\(item.brandName!)", shopImage : UIImage(named: "riccos_1")?.withRenderingMode(.alwaysOriginal), mapsImage: UIImage(named: "Maps")?.withRenderingMode(.alwaysOriginal), shop: item, meterstolocation : meters))
                                    
                                }
                            }
                            else
                            {
                                
                                self.arrayOfCellData.append(cellDataHome( text : "\(item.address!)", header : "\(item.brandName!)", shopImage : UIImage(named: "riccos_1")?.withRenderingMode(.alwaysOriginal), mapsImage: UIImage(named: "Maps")?.withRenderingMode(.alwaysOriginal), shop: item, meterstolocation : 0))
                                
                            }
                            
                        }
                        DispatchQueue.main.async{
                            self.finalcoffeeList = self.arrayOfCellData
                            self.myTable.reloadData()
                            self.err.isHidden = true;
                            self.err.text = "";
                            self.sortnearest()
                            
                        }
                        
                    }
                    
                    
                    
                }
        }
    }
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func getAllCoffeeShops(accessToken: String, refreshToken: String, callback: @escaping (_ allUsersArray: Array<CoffeeShop>)-> ()) {
        
        
        let urlPath = "http://keebintest-pagh.rhcloud.com/api/coffee/allshops/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue(accessToken, forHTTPHeaderField: "accessToken")
        request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        var CoffeeShopArray = Array<(CoffeeShop)>()
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            var json:[[String:Any]] = []
            
            //Ny kode, med update aToken i sig
            if let httpResponse = response as? HTTPURLResponse {
                //print("response code is: \(httpResponse.statusCode)")
                if (httpResponse.statusCode == 200){
                    let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                    updateAccessTokenOnly(newAccessToken: aToken!)
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [[String:Any]] {
                        json = jsonResponse!
                    }
                } else if (httpResponse.statusCode == 401) {
                    self.dismiss(animated: true, completion: nil)
                }
            }
            
            
            //      Sebastians kode
            //            if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [[String:Any]] {
            //                json = jsonResponse!
            //            }
            
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
            else
            {
                self.err.isHidden = false;
                self.err.text = "Vores server er muligvis nede, Prøv igen senere.";
                self.myTable.isScrollEnabled = false;
            }
            
            callback(CoffeeShopArray)
        })
        
        task.resume()
    }
    
    func infoAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
}


