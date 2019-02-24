//
//  HomeSelectedShopViewController.swift
//  Keebin_development_1
//
//  Created by sr on 08/02/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class HomeSelectedShopViewController: UIViewController {
    
    @IBOutlet weak var picture: UIImageView!
    @IBOutlet weak var textView: UITextView!
    
    var shops = Array<(CoffeeShop)>()
    var shop = CoffeeShop()
    var brandName = "";
    var pictureUrl:UIImage?;
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        tabBarController?.tabBar.isHidden = true
        self.navigationController?.navigationBar.tintColor = UIColor.white;
        
        picture.image = pictureUrl
        textView.layer.cornerRadius = 10;
        textView.text = "  \(brandName) \n \n  \(shop.address!) \n  \(shop.email!) \n  \(shop.phone!)"
        
        
//        picture.setImage(pictureUrl, for: UIControlState.normal)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
