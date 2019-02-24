//
//  cardViewController.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 13/05/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit
import Stripe

class cardViewController: UIViewController{



//    let cardParams = STPCardParams()

    var paymentField: STPPaymentCardTextField? = nil
    var tokens =  [String: String]()
    @IBOutlet weak var createCardButton: UIButton!
    
    @IBAction func createCardButton(_ sender: Any) {
        createToken(cardParams: (self.paymentField?.cardParams)!)
    }


    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        paymentField = STPPaymentCardTextField(frame: CGRect(x: 10, y: 100, width:300, height: 44))
        paymentField?.delegate = self as? STPPaymentCardTextFieldDelegate
        self.view.addSubview(paymentField!)
        
    }
    
    // MARK: STPPaymentCardTextFieldDelegate
    
    func paymentCardTextFieldDidChange(_ textField: STPPaymentCardTextField) {

//        self.createCardButton.isEnabled = textField.isValid
    }
  

    func alert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: goBack)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    
    
    func createToken(cardParams: STPCardParams){
        
            STPAPIClient.shared().createToken(withCard: cardParams) { (token, error) in
                if error != nil {
                    // show the error to the user
                } else if let token = token {
            
                    self.submitTokenToBackend(token: token){completion in
                        if completion {
                            // show a receipt page
                            DispatchQueue.main.async {
                            self.alert(message: "Du har nu tilføjet et kort til din profil. Nu har du mulighed for at blive Premium kunde.", title: "Kort tilføjet")
                                
                            }
                            
                        } else {
                            // show the error to the user
                            DispatchQueue.main.async {
                            self.alert(message: "Der skete desværre en fejl. Prøv igen.", title: "Error")
                            }
                        }
                    }
                    
                }
            }
        
        
    }
    
    
    func goBack(alert: UIAlertAction) {
        _ = navigationController?.popViewController(animated: true)
    }
    
    func submitTokenToBackend(token: STPToken, callback: @escaping (_ completion: Bool)-> ()) {
        
        getTokensFromDB(){ dbTokens in
            self.tokens = dbTokens
        }
        
        let urlPath = "\(baseApiUrl)/users/addCardToUser"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue(tokens["accessToken"]!, forHTTPHeaderField: "accessToken")
        request.addValue(tokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "POST"
        
        let jsonObject: [String: Any] = [
            "token" : token.tokenId
        ]
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
            
            request.httpBody = jsonData
            let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                let httpResponse = response as? HTTPURLResponse;
                if(httpResponse!.statusCode == 200)
                {
                    callback(true)
                }
                else
                {
                    
                    callback(false);
                }
            })
            task.resume()
        } catch {
            callback(false)
        }
        
        
    }
    
    
    
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
