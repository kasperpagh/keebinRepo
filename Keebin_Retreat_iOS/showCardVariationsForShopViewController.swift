//
//  showCardVariationsForShopViewController.swift
//  Keebin_development_1
//
//  Created by sr on 14/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

struct cellDataShowKlippeKortVariations{
    let count : Int!
    let price : Int!
    let name: String!
    let id: Int!
}

class showCardVariationsForShopViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
  var tdata: cellDataBuyKlippeKort!
    
    @IBOutlet weak var cardVariationsTable: UITableView!
    var arrayOfCellData = [cellDataShowKlippeKortVariations]()
    var json:[[String:Any]] = []
    var json2:[[String:Any]] = []
    var tokens =  [String: String]()
    var cardToBuy: Bool = false;
    
    override func viewDidLoad() {
        super.viewDidLoad()

        getCardVariations()
            {a in
                
                if(a)
                {
                    
                    for data in self.json2
                    {
                        
                   
                        let klip1 = cellDataShowKlippeKortVariations(count: data["count"]! as! Int, price: data["price"]! as! Int, name: data["name"]! as! String, id : data["id"]! as! Int);
                        self.arrayOfCellData.append(klip1)
                        
                    }
                    
                    DispatchQueue.main.async {
                        self.cardVariationsTable.reloadData()
                    }
                    
                }
                
        }
        
        // Do any additional setup after loading the view.
    }

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
            self.cardToBuy = true;
            self.tag = indexPath.row
            print(self.tag);
            self.performSegue(withIdentifier: "showCardToBuy", sender: self)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        cardToBuy = false;
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(cardToBuy)
        {
            let dest = segue.destination as! buyCardSelectedViewController
            dest.cardToBuy = arrayOfCellData[tag]
        }
    
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = Bundle.main.loadNibNamed("showBrandVariationsTableViewCell", owner: self, options: nil)?.first as! showBrandVariationsTableViewCell

        let ind = indexPath.row;
        
        cell.name.text = "Navn:  \(arrayOfCellData[ind].name!)"
        cell.count.text = "Antal klip: \(arrayOfCellData[ind].count!) "
        cell.price.text = "Pris: \(arrayOfCellData[ind].price!) kr."
        
        
//        if(ind % 2 == 0)
//        {
//                  cell.backGround.backgroundColor = #colorLiteral(red: 0.3647058904, green: 0.06666667014, blue: 0.9686274529, alpha: 1);
//        }
//        else
//        {
//            cell.backGround.backgroundColor = #colorLiteral(red: 0.2588235438, green: 0.7568627596, blue: 0.9686274529, alpha: 1);
//        }
  

        
        return cell
        
    }


    func getCardVariations(callback: @escaping (_ loyaltyCardsloaded: Bool) -> ()){
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/coffee/klippekortvariation/\(tdata.brandid!)"
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

}
