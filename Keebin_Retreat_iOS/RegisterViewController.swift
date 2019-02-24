//
//  RegisterViewController.swift
//  Keebin_development_1
//
//  Created by sr on 10/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class RegisterViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {
    
    
    //Outlets
    @IBOutlet weak var loading: UIActivityIndicatorView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var firstname: UITextField!
    @IBOutlet weak var lastname: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var password_repeated: UITextField!
    @IBOutlet weak var sex: UIPickerView!
    @IBOutlet weak var date: UIDatePicker!
    @IBOutlet weak var err: UITextView!
    @IBOutlet weak var register: UIButton!
    
    // Variables
    var sexstring = "";
    var sexarray = ["male", "female"]
    var errormsg = "";
    var mdate = "";
    var registermoved = false;
    var accessToken: String = ""
    var refreshToken: String = ""
    
 
    
    override func viewDidLoad() {
        
        date.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        date.layer.cornerRadius = 10;
        date.layer.layoutIfNeeded()
        sex.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        sex.layer.cornerRadius = 10;
        register.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        register.layer.cornerRadius = 10;
        
        super.viewDidLoad()
        sex.delegate = self;
        sex.dataSource = self;
        date.addTarget(self, action: #selector(dateChanged(_:)), for: .valueChanged)
        
        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return sexarray.count;
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return sexarray[row];
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        sexstring = sexarray[row];
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func dateChanged(_ sender: UIDatePicker) {
        let componenets = Calendar.current.dateComponents([.year, .month, .day], from: sender.date)
        if let day = componenets.day, let month = componenets.month, let year = componenets.year {
            mdate = "\(year)-\(month)-\(day)"
        }
        
        
    }
    
    
    @IBAction func Regiser(_ sender: Any) {
        
        err.text = "";
        errormsg = ""; // empties the errormessages to get new ones. so it looks nicely.
        
        
        if(password.text! == password_repeated.text!)
        {
            let checkValidPassword = isValidPassword(testStr: password.text!)
            
            if(checkValidPassword)
            {
                
                
                if(registermoved == true)
                {
                    registermoved = false;
                    register.frame = register.frame.offsetBy( dx: 0, dy: -30 ); // offset by an amount
                }
            }
            else
            {
                
                if(errormsg == "")
                {
                    errormsg = "Dit password skal minimum indeholde 1 bogstav, 1 nummer og 1 special tegn."
                }
                else
                {
                    errormsg = "\(errormsg)\nDit password skal minimum indeholde 1 bogstav, 1 nummer og 1 special tegn."
                }
                if(registermoved == false)
                {
                    registermoved = true;
                    register.frame = register.frame.offsetBy( dx: 0, dy: 30 ); // offset by an amount
                }
            }
        }
        else
        {
            
            if(errormsg == "")
            {
                errormsg = "Dine passwords stemmer ikke overens "
            }
            else
            {
                errormsg = "\(errormsg)\nDine passwords stemmer ikke overens."
            }
            
            if(registermoved == false)
            {
                registermoved = true;
                register.frame = register.frame.offsetBy( dx: 0, dy: 30 ); // offset by an amount
            }
        }
        
        let validemail = isValidEmail(testStr: email.text!)
        
        
        if(validemail)
        {
            if(registermoved == true)
            {
                registermoved = false;
                register.frame = register.frame.offsetBy( dx: 0, dy: -30 ); // offset by an amount
            }
            
        }
        else
        {
            
            if(errormsg == "")
            {
                errormsg = "Email er ikke formateret ordentligt. "
            }
            else
            {
                errormsg = "\(errormsg)\nEmail er ikke formateret ordentligt."
            }
            
            
            if(registermoved == false)
            {
                registermoved = true;
                register.frame = register.frame.offsetBy( dx: 0, dy: 30 ); // offset by an amount
            }
        }
        
        if(errormsg == "")
        {
            register.isEnabled = false;
            self.loading.startAnimating();
            createUser(firstName: firstname.text!, lastName: lastname.text!, email: email.text!, role: 2, sex: sexstring, password: password.text!, date: mdate)
            {response in
                
                if(response)
                {
                    self.login(email: self.email.text!, password: self.password.text!, callback: {a in
                        
                        DispatchQueue.main.async {
                            
                            if(a)
                            {
                                self.register.isEnabled = true;
                                self.loading.stopAnimating();
                                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                                let vc = storyboard.instantiateViewController(withIdentifier: "TabBarC")
                                self.present(vc, animated: true, completion: nil)
                            }
                            else
                            {
                                self.register.isEnabled = true;
                                self.loading.stopAnimating();
                                self.dismiss(animated: true, completion: nil) // burde gå tilbage hvis du ikke bliver logget ind automatisk!
                            }
                        }
                    })
                }
                else
                {
                    self.register.isEnabled = true;
                    self.loading.stopAnimating();
                    self.err.text = "Der skete en fejl ved oprettelse af brugeren, prøv igen.";
                }
                
            }
        }
        err.text! = errormsg
    }
    
    
    
    func createUser(firstName: String, lastName: String, email: String,
                    role: Int, sex: String, password: String, date: String, callback: @escaping (_ abe: Bool)-> ()){
        
        let urlPath = "\(baseLoginUrl)/login/user/new"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        
        let jsonObject: [String: Any] = [
            "firstName": firstName,
            "lastName": lastName,
            "email": email,
            "roleId": role,
            "birthday": date,
            "sex": sex,
            "password": password
        ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
            // if the call fails, the catch block is executed
            
            request.httpBody = jsonData
            
            let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                
                callback(true)
                
            })
            task.resume()
        } catch {
            callback(false)
        }
        
    }
    
    
    func isValidPassword(testStr:String) -> Bool
    {
        let validPassword = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{6,}$"
        
        let testPassword = NSPredicate(format:"SELF MATCHES %@", validPassword)
        return testPassword.evaluate(with: testStr)
    }
    
    func isValidEmail(testStr:String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: testStr)
    }
    
    
    func login(email: String, password: String, callback: @escaping (_ abe: Bool)-> ()) {
        var a = [String : String]()
        
        let urlPath = "\(baseLoginUrl)/login"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        let jsonObject: [String: Any] = [
            "email": email,
            "password": password,
            ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
            // if the call fails, the catch block is executed
            
            request.httpBody = jsonData
            
            let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                
                let httpResponse = response as? HTTPURLResponse;
                if(httpResponse!.statusCode == 200)
                {
                    //Her laver vi data om til en dictionary
                    let s = (try! JSONSerialization.jsonObject(with: data!, options: .mutableContainers)) as! NSDictionary
                    
                    if (s["accessToken"] as? String) != nil {
                        a = s as! [String : String]
                        
                        for (c,x) in a
                        {
                            if(c == "accessToken")
                            {
                                self.accessToken = x
                            }
                            if(c == "refreshToken")
                            {
                                self.refreshToken = x
                            }
                            
                        }
                        
                    } else {
                        
                    }
                    
                    updateTokens(newAccessToken: self.accessToken, newRefreshToken: self.refreshToken)
                    getAllCoffeeBrands(accessToken: self.accessToken, refreshToken: self.refreshToken)
                    
                    callback(true)
                    
                    
                }
                else
                {
                    
                    callback(false);
                }
                
                
            })
            task.resume()
            
        } catch {
            
            callback(false);
        }
        
    }
    
}
