
import UIKit

class KlippekortSelectedViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {

    var cellData: cellDataKlippeKort!
    @IBOutlet weak var err: UILabel!
    var json:[String:Any] = [:]
    
    var clip = "1";
    var clipArray: [String] = []
    
    @IBOutlet weak var slider: UISlider!
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
         self.navigationController?.navigationBar.tintColor = UIColor.white;
        
        slider.layer.cornerRadius = 10;
        slider.backgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)

        
        let uses = cellData.usesleft!
        if(uses > 0)
        {
            err.text = "";
            
            
            for a in 1...uses
            {
                clipArray.append("\(a)")
            }
            
            slider.addTarget(self, action: #selector(KlippekortSelectedViewController.sliderDidEndSliding(_:)), for: .touchUpInside)
        }
        else
        {
            
            noClipLeftAlert(message: "Du har ikke flere klip tilbage på dette kort.")
        }
        
        
        
    }
    
    func noClipLeftAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(OKAction)
        self.present(alertController, animated: true, completion: nil)
    }
    
    func usedClipAlert(message: String, title: String = "") {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let OKAction = UIAlertAction(title: "OK", style: .default, handler: sendBackToPreviousSegue)
        alertController.addAction(OKAction) //
        self.present(alertController, animated: true, completion: nil)
    }
    
    
    func sliderDidEndSliding(_ sender: Any) {
        if(slider.value == 30.0) {
            //            print("her er json")
            //            print(self.json)
            //            print("her er edn")
            self.usecard()
                {b in
                    print(b)
                    
                    if(b)
                    {
                        self.usedClipAlert(message: "du har nu brugt \(self.clip) klip.", title: "Klippekort")
                        
                        self.err.text = "Du har nu brugt \(self.clip) klip på dit kort."
                        
                    }
                    else
                    {
                        print("cb2")
                        self.err.text = "Brugen af klippekortet gik ikke igennem. Prøv igen senere."
                    }
            }
        }
        else
        {
            UIView.animate(withDuration: 0.3, animations:
                {   self.slider.value = 0;
                    self.view.layoutIfNeeded()
            }
            )
        }
    }
    
    
    
    
    
    func sendBackToPreviousSegue(alert: UIAlertAction){
        DispatchQueue.main.async {
            //            self.activityIndicator.stopAnimating()
            _ = self.navigationController?.popViewController(animated: true)
        }
    }
    
    var tokens = [[]]
    
    func usecard(callback: @escaping (_ abe: Bool)-> ()){
        
        
        getTokensFromDB(){ dbTokens in
            
            let accessToken = dbTokens["accessToken"]!
            let refreshToken = dbTokens["refreshToken"]!
            
            
            
            let urlPath = "\(baseApiUrl)/coffee/klippekort/\(self.json["id"]!)"
            let url = NSURL(string: urlPath)
            let session = URLSession.shared
            let request = NSMutableURLRequest(url: url as! URL)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue(accessToken, forHTTPHeaderField: "accessToken")
            request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
            request.httpMethod = "PUT"
            
            let jsonObject: [String: Any] = [
                "purchasedAmount": self.clip,
                "userId" : LoginViewController.user.id!
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
                    callback(cb)
                })
                print("u a rn first")
                task.resume()
                
                
            } catch {
                
                callback(cb)
            }
        }
    }
    
    
    
    
    
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return clipArray.count;
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return clipArray[row];
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        clip = clipArray[row];
    }
    
    override func accessibilityElementDidLoseFocus() {
        
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
}
