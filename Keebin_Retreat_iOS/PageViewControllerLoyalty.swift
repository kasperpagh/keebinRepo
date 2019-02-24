//
//  PageViewControllerLoyalty.swift
//  Keebin_development_1
//
//  Created by sr on 03/03/2017.
//  Copyright © 2017 Keebin. All rights reserved.
//

import UIKit

class PageViewControllerLoyalty: UIPageViewController, UIPageViewControllerDelegate, UIPageViewControllerDataSource {
    
    lazy var VCArr: [UIViewController] = {
        return [self.VCInstance(name: "SecondVCKlippekort"), self.VCInstance(name: "ThirdVCPremium")]
    }()
    
     func VCInstance(name: String) -> UIViewController
    {
        return UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: name)
    }
    
    let pageControl = UIPageControl();
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.dataSource = self;
        self.delegate = self;
        if let firstVC = VCArr.first {
            setViewControllers([firstVC], direction: .forward, animated: true, completion: nil)
        }
        
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

    }
    
     func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController?
    {
        
       guard let viewControllerIndex = VCArr.index(of: viewController) else
       {
        return nil;
        }
        
        let previousIndex = viewControllerIndex - 1;
        
        guard previousIndex >= 0 else
        {
            return nil
        }
        
        guard VCArr.count > previousIndex else {
            return nil;
        }
        return VCArr[previousIndex]
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController?
    {
        print("i am run")
        guard let viewControllerIndex = VCArr.index(of: viewController) else
        {
            return nil;
        }
        let nextIndex = viewControllerIndex + 1;
        
        guard nextIndex >= 0 else
        {
            return VCArr.first
        }
        guard VCArr.count > nextIndex  else {
            return nil;
        }
        return VCArr[nextIndex]
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    



}
