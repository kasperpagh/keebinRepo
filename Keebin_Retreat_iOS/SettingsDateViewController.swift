//
//  SettingsDateViewController.swift
//  Keebin_development_1
//
//  Created by sr on 28/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class SettingsDateViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {
    
    // outlets
    @IBOutlet weak var sex: UIPickerView!
    @IBOutlet weak var date: UIDatePicker!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var err: UITextView!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var firstName: UITextField!
    
    @IBOutlet weak var opdate: UIButton!
    //variables
    var sexstring = "";
    var sexarray = ["male", "female"]
    var mdate = "";
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        sex.layer.cornerRadius = 10;
        sex.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        
        date.layer.cornerRadius = 10;
        date.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        
        opdate.layer.cornerRadius = 10;
        opdate.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        
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
    
    @IBAction func update(_ sender: Any) {
        print(mdate)
        print(sexstring)
        print(firstName.text!)
        print(lastName.text!)
        
        let api = SettingsViewController()
        
        api.editUser(oldpassword: password.text!,firstName: firstName.text!, lastName: lastName.text!, sex: sexstring, date: mdate)
        {a in
            if(a)
            {
                self.err.text = "Opdateret!"
            }
        }
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
    
}
