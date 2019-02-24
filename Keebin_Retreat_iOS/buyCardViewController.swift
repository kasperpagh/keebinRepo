//
//  buyCardViewController.swift
//  Keebin_development_1
//
//  Created by sr on 14/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

struct cellDataBuyKlippeKort{
    let brandpic : UIImage!
    let brandid : Int!
}

class buyCardViewController:  UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var klippeKortTable: UITableView!

    var useKlippeKort: Bool = false;
    
    override func viewDidLoad() {
        super.viewDidLoad()
        getCoffeeBrandsFromDB(){cbfdb in
            
        
        getBrandImageFromDB(){brandImagesFromDB in
        
 

        self.getCardVariations()
            {cb in
                print(self.json2)
                for a in self.json2
                {
                    var brandName: String!
                    var brandLogo: UIImage!
                    for each in cbfdb{
                        if (each.value(forKey: "dataBaseId") as! Int == a["brandName"]! as! Int){
                            brandName = each.value(forKey: "brandName") as! String
                        }
                    }
                    
                    for each2 in brandImagesFromDB{
                        if(brandName == each2.key){
                            brandLogo = each2.value
                        }
                    }
                    
//                    let brandName: String = "brand\(a["brandName"]! as! Int)"
                    let klip1 = cellDataBuyKlippeKort(brandpic: brandLogo, brandid : a["id"]! as! Int);
                    self.arrayOfCellData.append(klip1)

                }
                DispatchQueue.main.async {
                    self.klippeKortTable.reloadData();
                }
        }
            }
            }

    }
    
    var arrayOfCellData = [cellDataBuyKlippeKort]()
    var json:[[String:Any]] = []
    var json2:[[String:Any]] = []
        var tokens =  [String: String]()
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayOfCellData.count
    }
    
    var tag: Int = 0;
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        DispatchQueue.main.async {
            self.useKlippeKort = true;
            self.tag = indexPath.row
            print(self.tag);
            self.performSegue(withIdentifier: "showPurchaseableCards", sender: self)
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = Bundle.main.loadNibNamed("buyCardTableViewCell", owner: self, options: nil)?.first as! buyCardTableViewCell
        
        cell.logoImage.image = arrayOfCellData[indexPath.row].brandpic
        
        
        return cell
        
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        useKlippeKort = false;
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        // showBrugKlippeKort
        if(useKlippeKort)
        {
            let dest = segue.destination as! showCardVariationsForShopViewController
            dest.tdata = arrayOfCellData[tag]
        }

    }
    
    
    func getCardVariations(callback: @escaping (_ loyaltyCardsloaded: Bool) -> ()){
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
    
}
