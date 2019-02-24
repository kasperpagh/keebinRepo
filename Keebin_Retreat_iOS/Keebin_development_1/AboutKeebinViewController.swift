//
//  AboutKeebinViewController.swift
//  Keebin_development_1
//
//  Created by sr on 31/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class AboutKeebinViewController: UIViewController {

    @IBOutlet weak var aboutKeebinText: UITextView!
    override func viewDidLoad() {
        super.viewDidLoad()
        

        
        aboutKeebinText.layer.cornerRadius = 10;
        
        var aboutKeebinstring = "Keebin digitaliserer kaffekort, så man aldrig igen behøver besværet fra kort af pap og papir. \n \n Kunderne får en nem adgang til alle deres kaffekort og vil aldrig komme til at glemme dem derhjemme. \n \n  Kaffebarerne får en måde at måle deres kunder på, samt spare penge på trykkerier og gør deres loyalitetskort, endnu mere attraktive."
        
        aboutKeebinText.text! = aboutKeebinstring
        
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
