//
//  saveCoffeeBrandsToDB.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 01/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import Foundation
import CoreData
import UIKit


func getContext () -> NSManagedObjectContext {
    let appDelegate = UIApplication.shared.delegate as! AppDelegate
    return appDelegate.persistentContainer.viewContext
}



func getCoffeeBrandsFromDB(callback: @escaping (_ dbCoffeeBrands: Array<AnyObject>)-> ()) {
    
    var coffeeBrandArray = Array<AnyObject>()
    
    let fetchRequest: NSFetchRequest<NSManagedObject> = NSFetchRequest(entityName: "CoffeeBrand")
    
    do {
        let searchResults = try getContext().fetch(fetchRequest)
        
        for brands in searchResults {
            //            print("her er fra db: \(brands.value(forKey: "brandName"))")
            coffeeBrandArray.append(brands)
        }
        callback(coffeeBrandArray)
    } catch {
        print("Error with request: \(error)")
    }
}



func getAllCoffeeBrands(accessToken: String, refreshToken: String) {
    
    let urlPath = "\(baseApiUrl)/coffee/allbrands/"
    let url = NSURL(string: urlPath)
    let session = URLSession.shared
    let request = NSMutableURLRequest(url: url! as URL)
    request.addValue(accessToken, forHTTPHeaderField: "accessToken")
    request.addValue(refreshToken, forHTTPHeaderField: "refreshToken")
    request.httpMethod = "GET"
    
    
    let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
        print("Task completed \(String(describing: data))")
        if let httpResponse = response as? HTTPURLResponse {
            print("response code is: \(httpResponse.statusCode)")
        }
        
        var json:[[String:Any]] = []
        if let data = data, let jsonResponse = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [[String:Any]] {
            json = jsonResponse!
        }
        
        
        
        var coffeeBrandArrayFromGetCoffeeBrandsFromDB = [AnyObject]()
        
        if json.count > 0 {
            getCoffeeBrandsFromDB(){ dbCoffeeBrands in
                coffeeBrandArrayFromGetCoffeeBrandsFromDB = dbCoffeeBrands
                
                //Hvis klient databasen er tom så gemmer den alt.
                if (coffeeBrandArrayFromGetCoffeeBrandsFromDB.count == 0){
                    for blog in json {
                        saveCoffeeBrand(brandName: (blog["brandName"]! as? String)!, dataBaseId: (blog["id"]! as? Int64)!, numberOfCoffeesNeeded: (blog["numberOfCoffeeNeeded"]! as? Int)!)
                    }
                } else {
                    for blog in json {
                        let b = coffeeBrandArrayFromGetCoffeeBrandsFromDB.contains { $0.value(forKey: "dataBaseId") as! Int != blog["id"]! as! Int
                        }
                        
                        if (b){
                            print("\(blog["id"]!) findes allerede i coffeeBrandArrayFromGetCoffeeBrandsFromDB")
                        } else {
                            print("\(blog["id"]!) fandtes ikke og nu gemmes den")
                            saveCoffeeBrand(brandName: (blog["brandName"]! as? String)!, dataBaseId: (blog["id"]! as? Int64)!, numberOfCoffeesNeeded: (blog["numberOfCoffeeNeeded"]! as? Int)!)
                        }
                    }
                }
            }
        }
    })
    task.resume()
}


func getTokensFromDB (callback: @escaping (_ dbTokens: Dictionary<String, String>)-> ()) {
    
    var tokensFromDB = [String : String]()
    
    let fetchRequest: NSFetchRequest<NSFetchRequestResult> = NSFetchRequest(entityName: "Tokens")
    
    do {
        let searchResults = try getContext().fetch(fetchRequest)
        
        for token in searchResults as! [NSManagedObject] {
            if (token.value(forKey: "accessToken") != nil && token.value(forKey: "refreshToken") != nil ){
                tokensFromDB = ["accessToken": token.value(forKey: "accessToken")! as! String, "refreshToken": token.value(forKey: "refreshToken")! as! String]
            }
        }
        callback(tokensFromDB)
    } catch {
        print("Error with request: \(error)")
    }
}


//Drop coffeeBrandTable
func dropCoffeeBrandEntity(){
    
    let managedContext = getContext()
    let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "CoffeeBrand")
    let batchDeleteRequest = NSBatchDeleteRequest(fetchRequest: fetchRequest)
    
    do {
        try managedContext.execute(batchDeleteRequest)
        print("CoffeeBrand has been dropped")
    } catch {
        
    }
}


//Drop Tokens table.
func dropTokensTable(callback: @escaping (_ tableDropped: Bool)-> ()){
    let managedContext = getContext()
    let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "Tokens")
    let batchDeleteRequest = NSBatchDeleteRequest(fetchRequest: fetchRequest)
    
    do {
        try managedContext.execute(batchDeleteRequest)
        print("Tokens has been dropped")
        callback(true)
    } catch {
        
    }
}



func saveCoffeeBrand(brandName: String, dataBaseId: Int64, numberOfCoffeesNeeded: Int) {
    
    let managedContext = getContext()
    let entity =
        NSEntityDescription.entity(forEntityName: "CoffeeBrand",
                                   in: managedContext)!
    let CoffeeBrand = NSManagedObject(entity: entity,
                                      insertInto: managedContext)
    
    
    CoffeeBrand.setValue(brandName, forKey: "brandName")
    CoffeeBrand.setValue(dataBaseId, forKey: "dataBaseId")
    CoffeeBrand.setValue(numberOfCoffeesNeeded, forKey: "numberOfCoffeesNeeded")
    
    do {
        try managedContext.save()
        print("saved!")
    } catch let error as NSError {
        print("Could not save. \(error), \(error.userInfo)")
    }
}






func saveTokens(accessToken: String, refreshToken: String) {
    
    let managedContext = getContext()
    let entity =
        NSEntityDescription.entity(forEntityName: "Tokens",
                                   in: managedContext)!
    let Tokens = NSManagedObject(entity: entity,
                                 insertInto: managedContext)
    
    
    Tokens.setValue(accessToken, forKey: "accessToken")
    Tokens.setValue(refreshToken, forKey: "refreshToken")
    
    
    do {
        try managedContext.save()
        print("saved Tokens!")
    } catch let error as NSError {
        print("Could not save. \(error), \(error.userInfo)")
    }
}




func updateTokens(newAccessToken: String, newRefreshToken: String) {
    getTokensFromDB(){ dbTokens in
        if (!dbTokens.isEmpty){
            let b = dbTokens.contains { (Any) -> Bool in
                ((dbTokens["accessToken"]!) != newAccessToken || (dbTokens["refreshToken"]!) != newRefreshToken)
            }
            if (b){
                dropTokensTable(){tableDropped in
                    saveTokens(accessToken: newAccessToken, refreshToken: newRefreshToken)
                }
            } else {
            }
        } else {
            saveTokens(accessToken: newAccessToken, refreshToken: newRefreshToken)
        }
    }
}


func updateAccessTokenOnly(newAccessToken: String) {
    getTokensFromDB(){ dbTokens in
        if (!dbTokens.isEmpty){
            let b = dbTokens.contains { (Any) -> Bool in
                ((dbTokens["accessToken"]!) != newAccessToken)
            }
            if (b){
                print("updateAccessTokenOnly - table will be dropped and saved anew")
                dropTokensTable(){tableDropped in
                    saveTokens(accessToken: newAccessToken, refreshToken: dbTokens["refreshToken"]!)
                }
            } else {
                print("tokens var ens og vil ikke blive gemt.")
            }
        }
    }
}

//tjek på om databasen skal opdateres

func getDBVersionFromPhoneDB (callback: @escaping (_ phoneDbVersion: Int16)-> ()) {
    var versionFromPhone = [String: Int16]()
    let fetchRequest: NSFetchRequest<NSFetchRequestResult> = NSFetchRequest(entityName: "DatabaseVersion")
    
    do {
        let searchResults = try getContext().fetch(fetchRequest)

        if (searchResults.isEmpty){
            callback(0)
        } else {
            for version in searchResults as! [NSManagedObject] {
                
                versionFromPhone = ["phonedbversion": version.value(forKey: "phonedbversion")! as! Int16]
            }
            callback(versionFromPhone["phonedbversion"]!)
        }
            
    } catch {
        print("Error with request: \(error)")
    }
}

func getDbVersionFromServer(callback: @escaping (_ serverDbVersion: Int16)-> ()) {
    let urlPath = "\(baseApiUrl)/users/getDBVersion"
    let url = NSURL(string: urlPath)
    let session = URLSession.shared
    let request = NSMutableURLRequest(url: url! as URL)
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    getTokensFromDB(){dbTokens in
    request.addValue(dbTokens["accessToken"]!, forHTTPHeaderField: "accessToken")
    request.addValue(dbTokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
    request.httpMethod = "GET"
            
            let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
                if let httpResponse = response as? HTTPURLResponse {
                    if (httpResponse.statusCode == 200){
                        let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                        updateAccessTokenOnly(newAccessToken: aToken!)
                        
                        if let data = data, let stringResponse = String(data: data, encoding: .utf8) {
                            callback(Int16(stringResponse)!)
                        }
                        
                    }
                }
            })
            task.resume()

    }
}


func saveDbVersion(versionFromServer: Int16) {
    let managedContext = getContext()
    let entity =
        NSEntityDescription.entity(forEntityName: "DatabaseVersion",
                                   in: managedContext)!
    let version = NSManagedObject(entity: entity,
                                 insertInto: managedContext)
    
    
    version.setValue(versionFromServer, forKey: "phonedbversion")
    
    do {
        try managedContext.save()
        print("saved dbVersion!")
    } catch let error as NSError {
        print("Could not save. \(error), \(error.userInfo)")
    }
}

// slut på DB tjek



func login(email: String, password: String, callback: @escaping (_ abe: Dictionary<String, Any>)-> ()) {
    print("login is running")
    
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
        let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)
        
        request.httpBody = jsonData
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            print("Task completed \(data)")
            if let httpResponse = response as? HTTPURLResponse {
                print("response code is: \(httpResponse.statusCode)")
            }
            
            let s = (try! JSONSerialization.jsonObject(with: data!, options: .mutableContainers)) as! NSDictionary
            callback(s as! Dictionary<String, Any>)
        })
        task.resume()
    } catch {
        print("task i userPut fejlede")
    }
}


func getAndSaveCoffeeShopImages(coffeeShopsArray: Array<CoffeeShop> , callback: @escaping (_ success: Double)-> ()){
    let myGroup = DispatchGroup()
    
        let numberOfShops = Double(coffeeShopsArray.count)
        getTokensFromDB(){dbTokens in
                
                for each in coffeeShopsArray{
                    myGroup.enter()
                    
                    let imageShopEmail = each.email!
                    let coffeeShopIdFromBackEnd = "\(each.brandName!)"
                    let urlPath = "\(baseApiUrl)/housekeeping/image/\(imageShopEmail.lowercased())"
                    let url = NSURL(string: urlPath)
                    let session = URLSession.shared
                    let request = NSMutableURLRequest(url: url as! URL)
                    request.addValue(dbTokens["accessToken"]!, forHTTPHeaderField: "accessToken")
                    request.addValue(dbTokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
                    request.httpMethod = "GET"
                    
                    let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in

                        if let httpResponse = response as? HTTPURLResponse {
                            if (httpResponse.statusCode == 200){
                                let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                                updateAccessTokenOnly(newAccessToken: aToken!)
                                
                                let image = data?.base64EncodedString(options: Data.Base64EncodingOptions(rawValue: 0))
                                let imageToSave = Data(base64Encoded: image!, options: .ignoreUnknownCharacters)
                                saveImageToDB(brandName: coffeeShopIdFromBackEnd, image: imageToSave!)
                            }
                        }
                    })
                    task.resume()
                    myGroup.leave()
                }
                myGroup.notify(queue: .main) {
                    print("Finished all requests.")
                    callback(numberOfShops)
                }
        }
}


func getAndSaveCoffeeBrandLogos(callback: @escaping (_ success: Double)-> ()){
    let myGroup = DispatchGroup()
    getCoffeeBrandsFromDB(){dbCoffeeBrands in
        
        let numberOfBrands = Double(dbCoffeeBrands.count)
        getTokensFromDB(){dbTokens in
                
                for each in dbCoffeeBrands{
                    myGroup.enter()
                    
                    let imageBrandName = each.value(forKey: "brandName")! as! String
                    let urlPath = "\(baseApiUrl)/housekeeping/picture/\(imageBrandName.lowercased())"
                    let url = NSURL(string: urlPath)
                    let session = URLSession.shared
                    let request = NSMutableURLRequest(url: url as! URL)
                    request.addValue(dbTokens["accessToken"]!, forHTTPHeaderField: "accessToken")
                    request.addValue(dbTokens["refreshToken"]!, forHTTPHeaderField: "refreshToken")
                    request.httpMethod = "GET"
                    
                    let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
      
                        
                        if let httpResponse = response as? HTTPURLResponse {
                            if (httpResponse.statusCode == 200){
                                let aToken = httpResponse.allHeaderFields["accessToken"] as? String
                                updateAccessTokenOnly(newAccessToken: aToken!)
                                
                                let image = data?.base64EncodedString(options: Data.Base64EncodingOptions(rawValue: 0))
                                let imageToSave = Data(base64Encoded: image!, options: .ignoreUnknownCharacters)
                                saveImageToDB(brandName: imageBrandName, image: imageToSave!)
                                
                            }
                        }
                    })
                    task.resume()
                    myGroup.leave()
                }
                myGroup.notify(queue: .main) {
                    print("Finished all requests.")
                    callback(numberOfBrands)
                }
            
        }
    }
}

//Save images to documentsDirectory
func saveImageToDB(brandName: String, image: Data) {
    let documentsDirectoryURL = try! FileManager().url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
    let fileURL = documentsDirectoryURL.appendingPathComponent("\(brandName).png")
    if !FileManager.default.fileExists(atPath: fileURL.path) {
        do {
            let decodedImage = UIImage(data: image)
            try UIImagePNGRepresentation(decodedImage!)?.write(to: fileURL)
        } catch {
            print(error)
        }
    } else {
        print("Image Not Added")
    }
}

//Deletes images from documentsDirectory
func deletePicturesFromDD(){
    let fileManager = FileManager.default
    do {
        let url = try fileManager.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false)
        if let enumerator = fileManager.enumerator(at: url, includingPropertiesForKeys: nil) {
            while let fileURL = enumerator.nextObject() as? URL {
                print("deleting file at url: \(fileURL)")
                try fileManager.removeItem(at: fileURL)
            }
        }
    }  catch  {
        print(error)
    }
}




func getBrandImageFromDB(callback: @escaping (_ brandImagesFromDB: Dictionary<String, UIImage>)-> ()) {
    
    var coffeeShopImages = Dictionary<String, UIImage>()
    var coffeeBrandsFromDB = [AnyObject]()
    
    getCoffeeBrandsFromDB(){ dbCoffeeBrands in
        coffeeBrandsFromDB = dbCoffeeBrands
    }
    
    let nsDocumentDirectory = FileManager.SearchPathDirectory.documentDirectory
    let nsUserDomainMask    = FileManager.SearchPathDomainMask.userDomainMask
    let paths               = NSSearchPathForDirectoriesInDomains(nsDocumentDirectory, nsUserDomainMask, true)
    if let dirPath          = paths.first
    {
        for each in coffeeBrandsFromDB{
            let brand = each.value(forKey: "brandName")! as! String
            let imageURL = URL(fileURLWithPath: dirPath).appendingPathComponent("\(brand).png")
            let image    = UIImage(contentsOfFile: imageURL.path)
            coffeeShopImages["\(brand)"] = image
        }
        callback(coffeeShopImages)
    }
}


func getShopImageFromDB(coffeeShopArray: Array<CoffeeShop>, callback: @escaping (_ imagesFromDB: Dictionary<String, UIImage>)-> ()) {
    
    var coffeeShopImages = Dictionary<String, UIImage>()
    var coffeeBrandsFromDB = [AnyObject]()
    var brandName: String?
    
    getCoffeeBrandsFromDB(){ dbCoffeeBrands in
        coffeeBrandsFromDB = dbCoffeeBrands
    }
    
    let nsDocumentDirectory = FileManager.SearchPathDirectory.documentDirectory
    let nsUserDomainMask    = FileManager.SearchPathDomainMask.userDomainMask
    let paths               = NSSearchPathForDirectoriesInDomains(nsDocumentDirectory, nsUserDomainMask, true)
    if let dirPath          = paths.first
    {
        for each in coffeeShopArray{
            let idFromBackEnd = each.brandName!
            for brand in coffeeBrandsFromDB{
                
                if (idFromBackEnd == brand.value(forKey: "dataBaseId")! as! Int){
                brandName = brand.value(forKey: "brandName")! as? String
                }
            }
            let imageURL = URL(fileURLWithPath: dirPath).appendingPathComponent("\(idFromBackEnd).png")
            let image    = UIImage(contentsOfFile: imageURL.path)
            coffeeShopImages["\(brandName!)"] = image
        }
        callback(coffeeShopImages)
    }
}


