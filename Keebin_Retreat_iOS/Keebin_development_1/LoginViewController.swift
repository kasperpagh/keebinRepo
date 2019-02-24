 //
 //  LoginViewController.swift
 //  Keebin_development_1
 //
 //  Created by sr on 07/02/2017.
 //  Copyright © 2017 Keebin. All rights reserved.
 //
 
 import UIKit
 import CoreLocation

 
 class LoginViewController: UIViewController {
    
    
    // outlets
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var loading: UIActivityIndicatorView!
    @IBOutlet weak var error: UITextView!
    @IBOutlet weak var loginbtn: UIButton!
    @IBOutlet weak var register: UIButton!
    
    // variables
    var accessToken: String = ""
    var refreshToken: String = ""
    var getLoc = CLLocationManager()
    var testing = false;
    static var user = User()
    static var isServerAhead: Bool = false

    
    // viewWillAppear is called whenever you visit the view.
    override func viewWillAppear(_ animated: Bool) {
        username.layer.cornerRadius = 10;
        password.layer.cornerRadius = 10;
        loginbtn.layer.cornerRadius = 10;
        loginbtn.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        register.layer.cornerRadius = 10;
        register.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        // the code below is used to handle multiple clicks on the button so it does not make alot of api calls. and make a visual indicator that shows the device is loading.
        loginbtn.isEnabled = true;
        error.text = ""
        loading.stopAnimating();
    }
    
    override func viewDidLoad() {
        
        
        // auto set password and username from global variables (from globalvariable file) so we dont have to type in login every time (only for development)
        username.text = loginUsername;
        password.text = loginPassword;
        
        // ViewDidLoad is a function that gets called when the view (UI) loads on the screen. It's basically saying the device is ready to do stuff.
        super.viewDidLoad()
        getLoc.requestAlwaysAuthorization()
        navigationController?.navigationBar.tintColor = UIColor.white;
        // calls global function named getTokensFromDB that gets the user's tokens from the database.
        getTokensFromDB(){dbTokens in // callback response named dbTokens
            if (dbTokens["refreshToken"] != nil){
                getDBVersionFromPhoneDB(){phoneDbVersion in
                    
                    getDbVersionFromServer(){serverDbVersion in
//                        print("vi når ind i getDBVersionFromServer. Server: \(serverDbVersion) og phone: \(phoneDbVersion)")
                        if(serverDbVersion > phoneDbVersion){
                            dropCoffeeBrandEntity()
                            deletePicturesFromDD()
                            saveDbVersion(versionFromServer: serverDbVersion)
                            getAllCoffeeBrands(accessToken: self.accessToken, refreshToken: self.refreshToken)
                            LoginViewController.isServerAhead = true
                        }
                    }
                }
                // call method setUserObj
                self.setUserObj()
                    { res in
                        
                        if(res)
                        {
                            print("refreshToken var ikke nil")
                            // if you get a valid refreshToken, send the user to home.
                            DispatchQueue.main.async { // put the task inside the dispatchqueue in main and do them async. (queued)
                                
                                // get the storyboard with name "Main"
                                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                                
                                // get the viewController of the view we want to show.
                                let vc = storyboard.instantiateViewController(withIdentifier: "TabBarC")
                                
                                // change page to home.
                                self.present(vc, animated: true, completion: nil)
                            }
                        }
                        else
                        {
                            self.error.text = "Kunne ikke logge dig automatisk ind, prøv igen."
                        }
                }
            }
        }
        
        
        
        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        tap.cancelsTouchesInView = false
        // adds the function.
        view.addGestureRecognizer(tap)
    }
    
    
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // register is called when the "register" button is clicked.
    @IBAction func Register(_ sender: Any) {
        self.register.isEnabled = false;
        // hides the navigationbar and performs a seque with the indentifier showRegister and tells it the sender is me. (This context).
        navigationController?.setNavigationBarHidden(false, animated: true)
        performSegue(withIdentifier: "ShowRegister", sender: self)
    }
    
    
    @IBAction func Login(_ sender: Any) {
        // if testing is a variable that i was using to skip login at some point, but everything needs tokens now.
        if(testing)
        {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "TabBarC")
            self.present(vc, animated: true, completion: nil)
        }
        else
        {
            // checks if email is valid AND password is not empty.
            if((self.isValidEmail(testStr: self.username.text!)) && !self.password.text!.isEmpty)
            {
                loginbtn.isEnabled = false;
                self.loading.startAnimating();
                
                self.login(email: self.username.text!, password: self.password.text!, callback: {a in
                    
                    DispatchQueue.main.async {
                        if(a)
                        {
                            // calls function getUser with accessToken and refreshToken which is set earlier in the process.
                            self.getUser(accessToken: self.accessToken, refreshToken: self.refreshToken, email: self.username.text!)
                            {check in
                                if(check)
                                {
                                    self.loading.stopAnimating();
                                    let storyboard = UIStoryboard(name: "Main", bundle: nil)
                                    let vc = storyboard.instantiateViewController(withIdentifier: "TabBarC")
                                    self.present(vc, animated: true, completion: nil)
                                }
                            }
                        }
                        else
                        {
                            self.loginbtn.isEnabled = true;
                        }
                    }
                })
            }
            else
            {
                var both: Bool = false;
                if(!(self.isValidEmail(testStr: self.username.text!)))
                {
                    both = true;
                    
                    self.error.text = "Du skal indtaste en gyldig email."
                }
                if(self.password.text!.isEmpty)
                {
                    // checks if there both errors fail.
                    if(both == true)
                    {
                        let text = self.error.text!;
                        let text2 = " \(text)\n Du skal indtaste et gyldigt password."
                        self.error.text = text2
                        both = false;
                    }
                    else
                    {
                        self.error.text = "Du skal indtaste et gyldigt password."
                    }
                }
            }
        }
    }
    
    // @escaping basically means you dont have to define callback when using this function
    /*
     etc:
     with escaping:
     
     setUserObj()
     {
     }
     
     without:
     
     setUserObj(callback: {
     
     })
     
     */
    
    // this is called if the user is allready logged in (if they have a accessToken and refreshToken allready so they can be automatically logged in.
    func setUserObj(callback: @escaping (_ abe: Bool)-> ()) {
        
        getTokensFromDB()
            {Tokens in
                
                // sets the accessToken and refreshToken.
    let aToken = Tokens["accessToken"]!
    let rToken = Tokens["refreshToken"]!
        
                
   
        let urlPath = "\(baseApiUrl)/users/gwt"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue(aToken, forHTTPHeaderField: "accessToken")
        request.addValue(rToken, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        
                // makes a variable task, which handles a http get reqeuest.
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            var json: [String:Any] = [:]
            
            var res = false;
            //Ny kode, med update aToken i sig
            if let httpResponse = response as? HTTPURLResponse {
                //print("response code is: \(httpResponse.statusCode)")
                if (httpResponse.statusCode == 200){
                    res = true;
                    let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                    updateAccessTokenOnly(newAccessToken: aToken!)
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String:Any] {
                        json = jsonResponse!
                    }
                } else if (httpResponse.statusCode == 401) {
                    
                    res = false;
                }
            }
            
            DispatchQueue.main.async {
                
                // iterates over json and creates a user object.
                if json.count > 0 {
                    for blog in json {
                        if(blog.key == "email")
                        {
                            LoginViewController.user.email =  blog.value as? String
                        }
                        
                        if(blog.key == "id")
                        {
                            LoginViewController.user.id = blog.value as? Int
                        }
                        
                        if(blog.key == "roleId")
                        {
                            LoginViewController.user.roleId = blog.value as? Int
                        }
                        
                        if(blog.key == "lastName")
                        {
                            LoginViewController.user.lastName = blog.value as? String
                        }
                        
                        if(blog.key == "firstName")
                        {
                            LoginViewController.user.firstName = blog.value as? String
                        }
                        
                        if(blog.key == "birthday")
                        {
                            LoginViewController.user.birthday = blog.value as? String
                        }
                        if(blog.key == "password")
                        {
                            LoginViewController.user.password = blog.value as? String
                        }
                        if(blog.key == "sex")
                        {
                            LoginViewController.user.sex = blog.value as? String
                        }
                    }
                }
                else
                {
                    self.error.isHidden = false;
                    self.error.text = "Vores server er muligvis nede, Prøv igen senere.";
                }
                callback(res)
            }
        })
                // tells the task to run.
        task.resume()
                    }
    }
    
    

    func login(email: String, password: String, callback: @escaping (_ abe: Bool)-> ()) {
        var a = [String : String]()
        
        let urlPath = "\(baseLoginUrl)/login"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        let jsonObject: [String: Any] = [
            "email": email,
            "password": password,
            
            ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)             // do something with data
            
            request.httpBody = jsonData
            let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                
                let httpResponse = response as? HTTPURLResponse;
                if(httpResponse!.statusCode == 200)
                {
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
                        //                        print("intet virker vi er i else")
                    }
                    updateTokens(newAccessToken: self.accessToken, newRefreshToken: self.refreshToken)
                    getDBVersionFromPhoneDB(){phoneDbVersion in
                        
                        getDbVersionFromServer(){serverDbVersion in
//                            print("vi når ind i getDBVersionFromServer. Server: \(serverDbVersion) og phone: \(phoneDbVersion)")
                            if(serverDbVersion > phoneDbVersion){
                                dropCoffeeBrandEntity()
                                deletePicturesFromDD()
                                saveDbVersion(versionFromServer: serverDbVersion)
                                getAllCoffeeBrands(accessToken: self.accessToken, refreshToken: self.refreshToken)
                                LoginViewController.isServerAhead = true
                            }
                        }
                    }
                    
                    callback(true)
                }
                else
                {
                    DispatchQueue.main.async {
                        self.loading.stopAnimating();
                        self.error.text = "Forkert brugernavn eller password."
                    }
                    callback(false);
                }
            })
            task.resume()
        } catch {
            
            DispatchQueue.main.async {
                self.loading.stopAnimating();
                self.error.text = "kan ikke oprette forbindelse til serveren.... prøv igen senere."
            }
            callback(false);
        }
    }
    
    
    
    // this is called if the user is not automatically logged in and have to log in manually.
    func getUser(accessToken: String, refreshToken: String, email : String, callback: @escaping (_ boolean: Bool)-> ()) {
        
        let urlPath = "\(baseApiUrl)/users/user/\(email)"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url as! URL)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue(accessToken, forHTTPHeaderField: "accessToken")
        request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            var json: [String:Any] = [:]
            
            //Ny kode, med update aToken i sig
            if let httpResponse = response as? HTTPURLResponse {
                //print("response code is: \(httpResponse.statusCode)")
                if (httpResponse.statusCode == 200){
                    let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                    updateAccessTokenOnly(newAccessToken: aToken!)
                    if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String:Any] {
                        json = jsonResponse!
                    }
                } else if (httpResponse.statusCode == 401) {
                    // if status is 401 return to this site because you are not logged in.
                    self.dismiss(animated: true, completion: nil)
                }
            }
            
            DispatchQueue.main.async {
                if json.count > 0 {
                    for blog in json {
                        if(blog.key == "email")
                        {
                            LoginViewController.user.email =  blog.value as? String
                        }
                        if(blog.key == "id")
                        {
                            LoginViewController.user.id = blog.value as? Int
                        }
                        if(blog.key == "roleId")
                        {
                            LoginViewController.user.roleId = blog.value as? Int
                        }
                        if(blog.key == "lastName")
                        {
                            LoginViewController.user.lastName = blog.value as? String
                        }
                        if(blog.key == "firstName")
                        {
                            LoginViewController.user.firstName = blog.value as? String
                        }
                        if(blog.key == "birthday")
                        {
                            LoginViewController.user.birthday = blog.value as? String
                        }
                        if(blog.key == "password")
                        {
                            LoginViewController.user.password = blog.value as? String
                        }
                        if(blog.key == "sex")
                        {
                            LoginViewController.user.sex = blog.value as? String
                        }
                    }
                }
                else
                {
                    self.error.isHidden = false;
                    self.error.text = "Vores server er muligvis nede, Prøv igen senere.";
                }
                callback(true)
            }
        })
        task.resume()
    }
    
    // function to check if a email is valid with a regex. basically you tell the function does this string match, the regex? if so, return a true. otherwise return a false.
    func isValidEmail(testStr:String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: testStr)
    }
    
 }
 
