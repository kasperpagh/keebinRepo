import UIKit



struct cellDataKlippeKort{
    let brandpic : UIImage!
    let usesleft: Int!
    let clipFromCard: Int!
}


class KlippeKortViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    // outlets
    
    @IBOutlet weak var btn_BuyCard: UIButton!
    @IBOutlet weak var loyaltyCardsTable: UITableView!
    
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    // variables
    var arrayOfCellData = [cellDataKlippeKort]()
    var tokens =  [String: String]()
    var useKlippeKort: Bool = false;
    var json:[[String:Any]] = []
    var json2:[[String:Any]] = []
    var jsonToSend:[[String:Any]] = []
    var coffeeBrandsFromDB = [AnyObject]()
    

    
    
    
    
    
    
    
    
    func noLoyaltyCardsAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: changeTab)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func infoAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func changeTab(alertView: UIAlertAction!){
        DispatchQueue.main.async {
            self.tabBarController?.selectedIndex = 0
        }
    }
    
    
    func getCardsFromServer(callback: @escaping (_ loyaltyCardsloaded: Bool) -> ()){
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/coffee/mineklippekort/\(LoginViewController.user.id!)"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue(tokens["accessToken"]!, forHTTPHeaderField: "accessToken")
        request.addValue(tokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            
            if let httpResponse = response as? HTTPURLResponse {
                print("response code is: \(httpResponse.statusCode)")
                if (httpResponse.statusCode == 200){
                    let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                    updateAccessTokenOnly(newAccessToken: aToken!)
                    
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [[String:Any]] {
                        self.json = jsonResponse!
                        
                        print(jsonResponse!)
                        
                        callback(true)
                    }
                } else {
                    callback(false)
                }
            }
        })
        task.resume()
    }
    
    
    override func viewDidLoad() {
        btn_BuyCard.layer.cornerRadius = 10;
        btn_BuyCard.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        

        
        getCoffeeBrandsFromDB(){ dbCoffeeBrands in
            self.coffeeBrandsFromDB = dbCoffeeBrands
        }
    }
    
    func getCardVariations(callback: @escaping (_ loyaltyCardsloaded: Bool) -> ()){
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/coffee/alleklippekortsvariationer/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue(tokens["accessToken"]!, forHTTPHeaderField: "accessToken")
        request.addValue(tokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            
            if let httpResponse = response as? HTTPURLResponse {
                print("response code is: \(httpResponse.statusCode)")
                if (httpResponse.statusCode == 200){
                    let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                    updateAccessTokenOnly(newAccessToken: aToken!)
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [[String:Any]] {
                        self.json2 = jsonResponse!
                        
                        callback(true)
                    }
                } else {
                    callback(false)
                }
            }
        })
        task.resume()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        
                getCardVariations()
                    {a in
        
        
                        print("ID")
                        print(LoginViewController.user.id!)
                        self.arrayOfCellData.removeAll()
                        self.activityIndicator.hidesWhenStopped = true
                        self.activityIndicator.startAnimating()
                        self.getCardsFromServer(){loyaltyCardsloaded in
                        getBrandImageFromDB(){brandImagesFromDB in
        
                            if (loyaltyCardsloaded){
        
                                for each in self.json {
        
                                    for each2 in self.json2 {
        
                                        if(each["PrePaidCoffeeCardId"]! as! Int == each2["id"]! as! Int)
                                        {
                                            for each3 in self.coffeeBrandsFromDB
                                            {
        
        
                                                if (each2["coffeeBrandId"]! as! Int == each3.value(forKey: "dataBaseId") as! Int)
                                                {
                                                          print(each)
                                                    let brandName = each3.value(forKey: "brandName") as! String
                                                    print("her er brandName fra klippekort: \(brandName)")
                                                    var brandLogo: UIImage!
                                                    for each4 in brandImagesFromDB{
                                                        if (brandName == each4.key){
                                                            print("her er key fra klippekort: \(each4.key)")
                                                            brandLogo = each4.value
                                                        }
                                                    }

                                                    let count: Int = each2["count"] as! Int
                                                    let uses: Int = each["usesleft"] as! Int
        
                                                    self.jsonToSend.append(each)
        
        
        
        
                                                    self.arrayOfCellData.append(cellDataKlippeKort(brandpic : brandLogo, usesleft : uses, clipFromCard : count))
                                                                                                                    DispatchQueue.main.async {
                                                                            self.activityIndicator.stopAnimating()
                                                                            self.loyaltyCardsTable.reloadData()
                                                                        }
                                                }
                                            }
        
                                        }
        
                                    }
        
                                    
                                }
                            }
                                
                            else {
                                self.noLoyaltyCardsAlert(message: "Der opstod en fejl ved forbindelse til serveren. Bekræft denne enhed har adgang til internettet.")
                                self.activityIndicator.stopAnimating()
                            }
                        }
                }
                }
            }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayOfCellData.count
    }
    
    var tag: Int = 0;
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.tag = indexPath.row
        if(arrayOfCellData[tag].usesleft > 0)
        {
            print(arrayOfCellData[tag].usesleft)
            DispatchQueue.main.async {
                self.useKlippeKort = true;
                
                self.performSegue(withIdentifier: "showBrugKlippeKort", sender: self)
            }
        }
        else {
            infoAlert(message: "du har ikke nogle klip tilbage på kortet.", title: "Klippekort")
        }
    }
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        // showBrugKlippeKort
        if(useKlippeKort)
        {
            useKlippeKort = false;
            let dest = segue.destination as! KlippekortSelectedViewController
            dest.cellData = arrayOfCellData[tag]
            dest.json = jsonToSend[tag];
            
        }
        
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = Bundle.main.loadNibNamed("KlippeKortTableViewCell", owner: self, options: nil)?.first as! KlippeKortTableViewCell
        var uses: Int = arrayOfCellData[indexPath.row].usesleft!
        cell.brandPic.image = arrayOfCellData[indexPath.row].brandpic
        
        let clipUseablePic = #imageLiteral(resourceName: "kaffekop_YES")
        let clipUsedPic = #imageLiteral(resourceName: "kaffekop_NO")
        let max = arrayOfCellData[indexPath.row].clipFromCard!
        
        if(uses >= 1)
        {
            cell.paidCoffeeImage1.image = clipUseablePic
        } else {
            cell.paidCoffeeImage1.image = clipUsedPic
        }
        
        
        
        if(uses >= 2)
        {
            cell.paidCoffeeImage2.image = clipUseablePic
        } else {
            cell.paidCoffeeImage2.image = clipUsedPic
        }
        
        
        if(uses >= 3)
        {
            cell.paidCoffeeImage3.image = clipUseablePic
        } else {
            cell.paidCoffeeImage3.image = clipUsedPic
        }
        
        
        if(uses >= 4)
        {
            cell.paidCoffeeImage4.image = clipUseablePic
        } else {
            cell.paidCoffeeImage4.image = clipUsedPic
        }
        
        
        if(uses >= 5)
        {
            cell.paidCoffeeImage5.image = clipUseablePic
        } else {
            cell.paidCoffeeImage5.image = clipUsedPic
        }
        
        
        if(uses >= 6)
        {
            cell.paidCoffeeImage6.image = clipUseablePic
        } else {
            cell.paidCoffeeImage6.image = clipUsedPic
        }
        
        
        if(uses >= 7)
        {
            cell.paidCoffeeImage7.image = clipUseablePic
        } else {
            cell.paidCoffeeImage7.image = clipUsedPic
        }
        
        
        if(uses >= 8)
        {
            cell.paidCoffeeImage8.image = clipUseablePic
        } else {
            cell.paidCoffeeImage8.image = clipUsedPic
        }
        
        
        if(uses >= 9)
        {
            cell.paidCoffeeImage9.image = clipUseablePic
        } else {
            cell.paidCoffeeImage9.image = clipUsedPic
        }
        
        if(uses >= 10)
        {
            cell.paidCoffeeImage10.image = clipUseablePic
        } else {
            cell.paidCoffeeImage10.image = clipUsedPic
        }
        
        
        if(uses >= 11)
        {
            uses = uses-10;
            
            cell.numberOfClipsLeft.text = "+\(uses)"
            
        }
        else
        {
            cell.numberOfClipsLeft.text = ""
        }
        
        
        return cell
        
    }
    
    
    func printtimes(times: Int)
    {
        for a in 0...times
        {
            print("")
        }
        
    }
    
}
