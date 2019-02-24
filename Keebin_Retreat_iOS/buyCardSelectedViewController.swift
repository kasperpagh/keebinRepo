//
//  buyCardSelectedViewController.swift
//  Keebin_development_1
//
//  Created by sr on 15/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class buyCardSelectedViewController: UIViewController {
    
    @IBOutlet weak var coffeeCodeLabel: UITextField!
    
    var cardToBuy: cellDataShowKlippeKortVariations!
     var tokens =  [String: String]()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print()
        print()
        print()
            print(cardToBuy)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func buyCoffee(_ sender: Any) {
        
        buyCard()
            {cb in
                print(cb)
                DispatchQueue.main.async {
                    if(cb)
                    {
                        self.cardBoughtAlert(message: "Du har nu købt en nyt klippekort Hurra!", title: "Nyt klippekort");
                    }
                    else
                    {
                        
                        self.cardNotBought(message: "Købet gik ikke igennem, prøv igen.", title: "Købsfejl")
                    }
                }
        }
        
    }
    
    
    func cardNotBought(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func cardBoughtAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: sendBackToPreviousSegue)
        alertController.addAction(OKAction) //
        self.present(alertController, animated: true, completion: nil)
    }
    


    
    func sendBackToPreviousSegue(alert: UIAlertAction){
        DispatchQueue.main.async {
            //            self.activityIndicator.stopAnimating()
            _ = self.navigationController?.popViewController(animated: true)
        }
    }
    
    
    func buyCard(callback: @escaping (_ abe: Bool)-> ()){
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/coffee/klippekort/new"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue(tokens["accessToken"]!, forHTTPHeaderField: "accessToken")
        request.addValue(tokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        
        let jsonObject: [String: Any] = [
            "storeCardId": cardToBuy.id!,
            "coffeeCode": coffeeCodeLabel.text!,
            "userId": LoginViewController.user.id!
        ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
            // if the call fails, the catch block is executed
            
            request.httpBody = jsonData
            
            let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                
                if let httpResponse = response as? HTTPURLResponse {
                    print("response code is: \(httpResponse.statusCode)")
                    if (httpResponse.statusCode == 200){
                        let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                        updateAccessTokenOnly(newAccessToken: aToken!)
                      
                            
                            callback(true)
                        }
                    else
                    {
                        callback(false)
                    }
                    } else {
                        callback(false)
                    }
                
           
                
            })
            task.resume()
        } catch {
            callback(false)
        }

    }



}
