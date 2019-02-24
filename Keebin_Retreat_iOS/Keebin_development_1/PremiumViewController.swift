//
//  PremiumViewController.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 27/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class PremiumViewController: UIViewController {

    
    
    
    @IBOutlet weak var label: UILabel!
    @IBOutlet weak var coffeeAvailableLabel: UILabel!
    @IBOutlet weak var coffeeCup: UIImageView!
    @IBOutlet weak var circle: UIImageView!
    @IBOutlet weak var box_small: UIImageView!
    @IBOutlet weak var box_big: UIImageView!
    @IBOutlet weak var premiumInfoButton: UIButton!

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    
    
    var json:[String:Any] = [:]
    var tokens =  [String: String]()
    var brugerID: Int = 0000
    
    
    func noLoyaltyCardsAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func redeemAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func getPremiumSubscription(callback: @escaping (_ premiumLoaded: Bool) -> ()){
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/users/getPremiumSubscription/"
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
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String:Any] {
                        self.json = jsonResponse!
                        
                        callback(true)
                    }
                } else {
                    DispatchQueue.main.async {
                        self.label.isEnabled = true
                        self.coffeeCup.image = #imageLiteral(resourceName: "black_cup")
                        self.circle.image = #imageLiteral(resourceName: "grey_circle")
                        self.box_small.image = #imageLiteral(resourceName: "grey_box")
                        self.box_big.image = #imageLiteral(resourceName: "grey_box")
                        self.premiumInfoButton.isHidden = false
                        
                        self.coffeeAvailableLabel.isHidden = true
         
                        callback(false)
                    }
                    
                }
            }
        })
        task.resume()
    }

    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        coffeeAvailableLabel.text = ""
        label.text = "Kan du lide gratis kaffe?"
        labelAndImage()

    }


    override func viewWillAppear(_ animated: Bool) {
        labelAndImage()
        activityIndicator.hidesWhenStopped = true
    }
    
    
    func labelAndImage(){
        getPremiumSubscription(){premiumLoaded in
            if (premiumLoaded){
                for each in self.json{
                    if (each.key == "isValidForPremiumCoffee"){
                        if(each.value as! Bool == true){
                            DispatchQueue.main.async {
                                self.premiumInfoButton.isHidden = true
                                self.label.text = "BrugerID: \(self.brugerID)"
                                self.label.isEnabled = true
                                self.coffeeCup.image = #imageLiteral(resourceName: "white_cup")
                                self.circle.image = #imageLiteral(resourceName: "red_circle")
                                self.box_small.image = #imageLiteral(resourceName: "red_box_small")
                                self.box_big.image = #imageLiteral(resourceName: "red_box")
                                self.coffeeAvailableLabel.text = "Din Premium kaffe er klar."
   
                                self.activityIndicator.stopAnimating()
                            }
                        } else {
                            DispatchQueue.main.async {
                                self.premiumInfoButton.isHidden = true
                                self.label.text = "BrugerID: \(self.brugerID)"
                                self.coffeeCup.image = #imageLiteral(resourceName: "black_cup")
                                self.circle.image = #imageLiteral(resourceName: "grey_circle")
                                self.box_small.image = #imageLiteral(resourceName: "grey_box")
                                self.box_big.image = #imageLiteral(resourceName: "grey_box")
                                self.coffeeAvailableLabel.text = "På mandag er Premium klar igen!"
                      
                                self.activityIndicator.stopAnimating()
                            }
                        }
                    }
                }
            }
        }
    }
    
    

    
    
    
    //Api kald til redeem loyaltyCard
    func useCard(callback: @escaping (_ abe: Bool)-> ()){
        self.activityIndicator.startAnimating()
        getTokensFromDB(){ dbTokens in
            let accessToken = dbTokens["accessToken"]!
            let refreshToken = dbTokens["refreshToken"]!
            
            let urlPath = "\(baseApiUrl)/users/setPremiumSubscriptionToCoffeeNotReady"
            let url = NSURL(string: urlPath)
            let session = URLSession.shared
            let request = NSMutableURLRequest(url: url as! URL)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue(accessToken, forHTTPHeaderField: "accessToken")
            request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
            request.httpMethod = "PUT"
            
            var cb = false;

            do {
              
                let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                    
                    let httpResponse = response as? HTTPURLResponse;
                    
                    if(httpResponse != nil)
                    {
                        
                        if(httpResponse!.statusCode == 200)
                        {
                            cb = true
                        }
                        else
                        {
                            cb = false;
                        }
                    }
                    else
                    {
                        cb = false;
                        
                    }
                    callback(cb)
                })
                task.resume()
            }
        }
    }
    
}
