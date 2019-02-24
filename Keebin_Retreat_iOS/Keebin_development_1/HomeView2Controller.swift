//
//  HomeView2Controller.swift
//  Keebin_development_1
//
//  Created by sr on 22/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit
import CoreLocation

class HomeView2Controller: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate, CLLocationManagerDelegate {
    
    struct cellDataHome{
        let text : String!
        let header : String!
        let shopImage : UIImage!
        let mapsImage : UIImage!
        var shop : CoffeeShop!
        var meterstolocation : Int!
    }
    
    var screenHideWidth = UIScreen.main.bounds.width
    var menuShowWidth = UIScreen.main.bounds.width / 2
    var animDuration = 0.7; // animation duration of menu show/hide
    var meters: Int?
    var brandNameForHomeView: String?
    var menuShowing = false;
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
    var coffeeBrandsFromDB = [AnyObject]()

    
    
    @IBOutlet weak var Search: UISearchBar!
    @IBOutlet weak var err: UILabel!
    @IBOutlet weak var menuView: UIView!
    @IBOutlet weak var leadingConstant: NSLayoutConstraint!
    @IBOutlet weak var menu: UIBarButtonItem!
    @IBOutlet weak var myTable: UITableView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    
    @IBAction func swipeLeft(_ sender: Any) {
        print(menuShowing)
        if(menuShowing)
        {
            leadingConstant.constant = screenHideWidth;
            UIView.animate(withDuration: animDuration, animations:
                {self.view.layoutIfNeeded()
            }
            )
            menuShowing = !menuShowing
        }
    }
    
    @IBAction func swipeRight(_ sender: Any) {
        if(!menuShowing)
        {
            leadingConstant.constant = menuShowWidth;
            
            UIView.animate(withDuration: animDuration, animations:
                {self.view.layoutIfNeeded()
            }
            )
            menuShowing = !menuShowing
        }
    }
    
    
    @IBAction func Example(_ sender: Any) {
        print("example menu")
    }
    
    @IBAction func profileSettings(_ sender: Any) {
        // for additional logic when being sent to profile.
    }
    
    @IBAction func subscriptions(_ sender: Any) {
        // no action yet.
    }
    
    @IBAction func logOut(_ sender: Any) {
        logOut()
    }
    
    @IBAction func menuClick(_ sender: Any) {
        
        if(menuShowing)
        {
            
            leadingConstant.constant = screenHideWidth;
            
            UIView.animate(withDuration: animDuration, animations:
                {self.view.layoutIfNeeded()
            }
            )
        }
        
        if(!menuShowing)
        {
            leadingConstant.constant = menuShowWidth;
            
            UIView.animate(withDuration: animDuration, animations:
                {self.view.layoutIfNeeded()
            }
            )
        }
        view.layoutIfNeeded()
        view.setNeedsUpdateConstraints()
        view.updateConstraintsIfNeeded()
        menuShowing = !menuShowing
    }
    
    
//    @IBAction func fabClick(_ sender: Any) {
//        mapsbool = false;
//        homebool = false;
//    }
    
    
    @IBAction func getImages(_ sender: Any) {
        
    }
    
    
    func logOut() {
        
        getTokensFromDB(){ dbTokens in
            
            let accessToken = dbTokens["accessToken"]!
            let refreshToken = dbTokens["refreshToken"]!
            let urlPath = "\(baseApiUrl)/users/user/logout"
            let url = NSURL(string: urlPath)
            let session = URLSession.shared
            let request = NSMutableURLRequest(url: url! as URL)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue(accessToken, forHTTPHeaderField: "accessToken")
            request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
            request.httpMethod = "POST"
            
            do {
                
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
            }
        }
    }
    
    
    func tableView( _ myTable: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(arrayOfCellData.count > limit)
        {
            return limit;
        }
        else
        {
            return arrayOfCellData.count
        }
    }
    
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
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
    
    func tableView( _ myTable: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = Bundle.main.loadNibNamed("TableViewCellHome", owner: self, options: nil)?.first as! TableViewCellHome
        
        cell.button_ImageMaps.setImage(arrayOfCellData[indexPath.row].mapsImage,  for: UIControlState.normal)
        cell.button_ImageMaps.tag = Int(indexPath.row)
        cell.button_ImageMaps.addTarget(self, action: #selector(HomeView2Controller.buttonToMaps(_:)), for: .touchUpInside)
        
        cell.button_ImageShop.setImage(arrayOfCellData[indexPath.row].shopImage, for: UIControlState.normal)
        cell.button_ImageShop.tag = Int(indexPath.row)
        cell.button_ImageShop.addTarget(self, action: #selector(HomeView2Controller.buttonToShop(_:)), for: .touchUpInside)
        
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
        let distanceInMeters = coordinate₀.distance(from: coordinate₁) /// result is in meters
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
            dest.brandName = arrayOfCellData[tag].header;
            dest.shop =  arrayOfCellData[tag].shop
            dest.pictureUrl = arrayOfCellData[tag].shopImage
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
        leadingConstant.constant = screenHideWidth;  // to close the menu
        menuShowing = false;
        homebool = false;
        mapsbool = false;
        tabBarController?.tabBar.isHidden = false
    }
    
    override func viewDidLoad() {
        navigationController?.navigationBar.tintColor = UIColor.white;activityIndicator.hidesWhenStopped = true
        activityIndicator.startAnimating()
        MobilePayManager.sharedInstance().setup(withMerchantId: "APPDK0000000000", merchantUrlScheme: "keebin", country: MobilePayCountry.denmark)
        getCoffeeBrandsFromDB(){ dbCoffeeBrands in
            self.coffeeBrandsFromDB = dbCoffeeBrands
        }

        

        if (LoginViewController.isServerAhead){
            getAndSaveCoffeeBrandLogos(){numberOfBrands in
            }
        }


        
        getTokensFromDB(){ dbTokens in
            
            self.getAllCoffeeShops(accessToken: dbTokens["accessToken"]!, refreshToken: dbTokens["refreshToken"]!){coffeeArray in
                
                self.coffeeShopArrayList = coffeeArray

                self.callBackFunction(){when in
                
                // desired number of seconds
            DispatchQueue.main.asyncAfter(deadline: when) {
                self.activityIndicator.stopAnimating()
                super.viewDidLoad()
                
                self.islocationon()
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
                        self.Search.delegate = self
                        self.err.isHidden = true;
                        

                                getShopImageFromDB(coffeeShopArray: self.coffeeShopArrayList){imagesFromDB in
                                    
                                    for item in coffeeArray
                                    {
                                        for i in self.coffeeBrandsFromDB {

                                            if ((i.value(forKey: "dataBaseId")! as! Int)  == item.brandName){
                                                self.brandNameForHomeView = i.value(forKey: "brandName") as? String
                                            }
                                        }
                                        
                                        if self.locationServiceIsAllowed && self.locationServiceIsOn {
                                            
                                            self.getdistance(lat0: item.latitude!, long0: item.longitude!, lat1: self.lat!, long1: self.long!)
                                            {s in
                                                self.meters = s;
                                                
                                                for each in imagesFromDB{
                                                    if (each.key == self.brandNameForHomeView!){
                                        
                                                        let image: UIImage = each.value
                                                        self.arrayOfCellData.append(cellDataHome( text : "\(item.address!)", header : "\(self.brandNameForHomeView!)", shopImage : image.withRenderingMode(.alwaysOriginal), mapsImage: UIImage(named: "Maps")?.withRenderingMode(.alwaysOriginal), shop: item, meterstolocation : self.meters))
                                                    }
                                                }
                                                
                                            }
                                        }
                                        else
                                        {
                                            self.arrayOfCellData.append(cellDataHome( text : "\(item.address!)", header : "\(item.brandName!)", shopImage : UIImage(named: "riccos_1")?.withRenderingMode(.alwaysOriginal), mapsImage: UIImage(named: "Maps")?.withRenderingMode(.alwaysOriginal), shop: item, meterstolocation : 0))
                                        }
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
        }
        
        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        leadingConstant.constant = screenHideWidth;  // to close the menu
        menuShowing = false;
        homebool = false;
        mapsbool = false;
        tabBarController?.tabBar.isHidden = false
        view.endEditing(true)
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func getAllCoffeeShops(accessToken: String, refreshToken: String, callback: @escaping (_ allUsersArray: Array<CoffeeShop>)-> ()) {
        
        let urlPath = "\(baseApiUrl)/coffee/allshops/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue(accessToken, forHTTPHeaderField: "accessToken")
        request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
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
                } else if (httpResponse.statusCode == 401) {
                    self.dismiss(animated: true, completion: nil)
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
    
    
    func callBackFunction(callback: @escaping (_ when: DispatchTime)-> ()) {
        if (LoginViewController.isServerAhead){
            getAndSaveCoffeeShopImages(coffeeShopsArray: self.coffeeShopArrayList){numberOfShops in
                let when = (DispatchTime.now() + numberOfShops)
                LoginViewController.isServerAhead = false
                callback(when)
                
            }
        } else {
            let when = (DispatchTime.now() + 0)
            callback(when)
        }
    }
    
    
    
}
