//
//  TestsWithTokens.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 03/05/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import XCTest

@testable import Keebin_development_1

class TestsWithTokens: XCTestCase {
    var aToken: String?
    var reToken: String?
    var brandsArray: [AnyObject]?
    
    
    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        
        super.setUp()
        getTokensFromDB(){tokens in
            if(!tokens.isEmpty){
                self.aToken = tokens["accessToken"]!
                self.reToken = tokens["refreshToken"]!
            }
        }
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
        self.aToken = nil
        self.reToken = nil
        self.brandsArray = nil
    }
    
    
    
    func testAStatusCode200() {

        let urlPath = "\(baseApiUrl)/coffee/allbrands/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue(self.aToken!, forHTTPHeaderField: "accessToken")
        request.addValue(self.reToken!, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        // 1
        let promise = expectation(description: "Status code: 200")
        
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            if let error = error {
                XCTFail("Error: \(error.localizedDescription)")
            } else if let statusCode = (response as? HTTPURLResponse)?.statusCode {
                if (statusCode == 200){
                    promise.fulfill()
                } else {
                    XCTFail("Status code: \(statusCode)")
                }
            }
        })
        task.resume()
        // 3
        waitForExpectations(timeout: 5, handler: nil)
    }
    
    func testBUseWrongAToken() {
        
        let urlPath = "\(baseApiUrl)/coffee/allbrands/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue("hejMedDig", forHTTPHeaderField: "accessToken")
        request.addValue(self.reToken!, forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        // 1
        let promise = expectation(description: "Status code: 200")
        
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            if let error = error {
                XCTFail("Error: \(error.localizedDescription)")
            } else if let statusCode = (response as? HTTPURLResponse)?.statusCode {
                if (statusCode == 200){
                    promise.fulfill()
                } else {
                    XCTFail("Status code: \(statusCode)")
                }
            }
        })
        task.resume()
        // 3
        waitForExpectations(timeout: 5, handler: nil)
    }
    
    func testCUseWrongTokens() {
        
        let urlPath = "\(baseApiUrl)/coffee/allbrands/"
        let url = NSURL(string: urlPath)
        let session = URLSession.shared
        let request = NSMutableURLRequest(url: url! as URL)
        request.addValue("yo", forHTTPHeaderField: "accessToken")
        request.addValue("yoyo", forHTTPHeaderField: "refreshToken")
        request.httpMethod = "GET"
        
        // 1
        let promise = expectation(description: "Status code: 401")
        
        
        let task = session.dataTask(with: request as URLRequest, completionHandler: {data, response, error -> Void in
            if let error = error {
                XCTFail("Error: \(error.localizedDescription)")
            } else if let statusCode = (response as? HTTPURLResponse)?.statusCode {
                if (statusCode == 401){
                    promise.fulfill()
                } else {
                    XCTFail("Status code: \(statusCode)")
                }
            }
        })
        task.resume()
        // 3
        waitForExpectations(timeout: 5, handler: nil)
    }
    
    
    func testDGetBrands() {
        
        getAllCoffeeBrands(accessToken: self.aToken!, refreshToken: self.reToken!)
        
        
        let expectations = expectation(description: "Get Brands from DB Succeeds")
        
        getCoffeeBrandsFromDB(){brands in
            if(!brands.isEmpty){
                self.brandsArray = brands
                for each in self.brandsArray!{
                    let a = each.value(forKey: "brandName")
                    print("brandName: \(a as! String)")
                }
                expectations.fulfill()
            } else {
                XCTFail()
            }
        }
        waitForExpectations(timeout: 5, handler: nil)
        
    }
    

    
}
