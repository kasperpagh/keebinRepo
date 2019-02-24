//
//  SettingsViewController.swift
//  Keebin_development_1
//
//  Created by sr on 27/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class SettingsViewController: UIViewController {
    
    
    // new password outlets
    
    @IBOutlet weak var email_ChangeMail_Button: UIButton!
    @IBOutlet weak var newPasswordOldPassword: UITextField!
    @IBOutlet weak var newPassword: UITextField!
    @IBOutlet weak var newPasswordRepeated: UITextField!

    @IBOutlet weak var err: UITextView!
    
        // new email outlets
    @IBOutlet weak var errEmail: UITextView!
    @IBOutlet weak var newEmailPassword: UITextField!
    @IBOutlet weak var newEmail: UITextField!
    
    @IBOutlet weak var changePassword: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.navigationBar.tintColor = UIColor.white;
        
        let str: String! = self.restorationIdentifier
        
        if(str == "settings2")
        {
            changePassword.layer.cornerRadius = 10;
            changePassword.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        } else if(str == "settings1")
        {
            email_ChangeMail_Button.layer.cornerRadius = 10;
            email_ChangeMail_Button.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        }
       
        
 
        
        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(SettingsViewController.dismissKeyboard))
        
        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    
    @IBAction func changeEmail(_ sender: Any) { // skal logges ud eller noget, bugger pga email er ændret.
        if(!isValidEmail(testStr: newEmail.text!))
        {
            errEmail.text = "Emailen er ikke valid."
        }
        else
        {
            self.errEmail.text = "yoloz"
            editUser(oldpassword: newEmailPassword.text!,email: newEmail.text!)
            {a in
                if(a)
                {
                    self.errEmail.text = "Din email er nu opdateret!"
                }
                else
                {
                    self.err.text = "der gik noget galt. prøv igen senere."
                }
            }
        }
        
    }
    
    @IBAction func changePassword(_ sender: Any) {
        
        
        
        var errmsg = "";
        //
        
        if(newPassword.text! != newPasswordRepeated.text!){
            errmsg = "Dine kodeord stemmer ikke overens"
        }
        
        if(errmsg.isEmpty)
        {
              print("reached")
            print(newPasswordOldPassword.text!)

            editUser(oldpassword: newPasswordOldPassword.text!,password: newPassword.text!)
            {a in
                if(a)
                {
                                       self.err.text = "Dit password er nu opdateret!"
                }
                else
                {
                    self.err.text = "der gik noget galt. prøv igen senere."
                }
            }
        }
        else
        {
            err.text = errmsg
        }
    }
    
    
    func editUser(oldpassword: String, firstName: String = "", lastName: String = "", email: String = "", sex: String = "", password: String = "", date: String = "", callback: @escaping (_ abe: Bool)-> ()){
        var fn = firstName;
        var ln = lastName;
        var mail = email;
        var sexvar = sex;
        var pass = password;
        var birthday = date;
        
        getTokensFromDB(){ dbTokens in
            
            let accessToken = dbTokens["accessToken"]!
            let refreshToken = dbTokens["refreshToken"]!
            if(firstName.isEmpty){fn = LoginViewController.user.firstName ?? ""}
            if(lastName.isEmpty){ln = LoginViewController.user.lastName ?? ""}
            if(email.isEmpty){mail = LoginViewController.user.email ?? ""}
            if(sex.isEmpty){sexvar = LoginViewController.user.sex ?? ""}
            if(password.isEmpty){pass = LoginViewController.user.password ?? ""}
            if(date.isEmpty){birthday = LoginViewController.user.birthday ?? ""}
            
            
            let urlPath = "\(baseApiUrl)/users/user/\(LoginViewController.user.email!)"
            let url = NSURL(string: urlPath)
            let session = URLSession.shared
            let request = NSMutableURLRequest(url: url! as URL)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue(accessToken, forHTTPHeaderField: "accessToken")
            request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
            request.httpMethod = "PUT"
            
            let jsonObject: [String: Any] = [
                "oldpassword" : oldpassword,
                "firstName": fn,
                "lastName": ln,
                "email": mail,
                "role": LoginViewController.user.roleId!,
                "birthday": birthday,
                "sex": sexvar,
                "password": pass
            ]
            var cb = false;
            print(jsonObject)
            do {
                let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
                // if the call fails, the catch block is executed
                
                request.httpBody = jsonData
                
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
                    
                })
                task.resume()
            } catch {
                callback(cb)
            }
        }
    }
    func isValidEmail(testStr:String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: testStr)
    }
}
