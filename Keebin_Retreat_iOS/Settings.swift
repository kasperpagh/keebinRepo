//
//  Settings.swift
//  Keebin_development_1
//
//  Created by sr on 27/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit
import Stripe

class Settings: UIViewController {

    
    
    
    @IBOutlet weak var email: UIButton!

    @IBOutlet weak var textarea: UITextView!
    @IBOutlet weak var aboutme: UIButton!
    @IBOutlet weak var password: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    
    func alert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    
    
    
    override func viewWillAppear(_ animated: Bool) {
        email.layer.cornerRadius = 10;
        email.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
  
 
        
        aboutme.layer.cornerRadius = 10;
        aboutme.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        
        
        password.layer.cornerRadius = 10;
        password.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)

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
