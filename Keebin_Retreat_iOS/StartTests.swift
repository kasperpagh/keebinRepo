//
//  TokensTest.swift
//  Keebin_development_1
//
//  Created by Steffen Lefort on 02/05/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import XCTest

@testable import Keebin_development_1

class TokensTest: XCTestCase {
    var login: LoginViewController?
    var aToken: String?
    var reToken: String?

    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        login = LoginViewController()
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
        login = nil
        aToken = nil
        reToken = nil

    }
    
    func testALoginSetup() {

        let expectations = expectation(description: "Login Succeeds")
        
        login?.login(email: "tester@email.dk", password: "123"){loggedIn in
            
            if(loggedIn){
                expectations.fulfill()
            } else {
                XCTFail()
            }
        }
        waitForExpectations(timeout: 5, handler: nil)
    }
    
    
    
    func testBTokens() {
        let expectations = expectation(description: "Get tokens from DB Succeeds")
        
        getTokensFromDB(){tokens in
            if(!tokens.isEmpty){
                self.aToken = tokens["accessToken"]!
                self.reToken = tokens["refreshToken"]!
                expectations.fulfill()
            } else {
                XCTFail()
            }
        }
        waitForExpectations(timeout: 5, handler: nil)
        
    }
    
    
       

}


