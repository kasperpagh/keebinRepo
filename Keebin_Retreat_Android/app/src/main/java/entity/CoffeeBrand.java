package entity;

import java.util.List;

/**
 * Created by kaspe on 2016-10-26.
 */

public class CoffeeBrand
{
    private int id;

    private int dataBaseId;

    private String brandName;
    private int numberOfCoffeeNeeded;
    private String coffeeBrandCode;
    private List<CoffeeShop> branches;
    private int totalCoffeeStampsFromAllBranches;


    //For mange input? Brand har ikke coffeeBrandCode, det er kun coffeeshop?
    public CoffeeBrand(int databaseId, String brandName, int numberOfCoffeeNeeded, String coffeeBrandCode, int totalCoffeeStampsFromAllBranches)
    {
        this.id = databaseId;
        this.brandName = brandName;
        this.numberOfCoffeeNeeded = numberOfCoffeeNeeded;
        this.coffeeBrandCode = coffeeBrandCode;
        this.totalCoffeeStampsFromAllBranches = totalCoffeeStampsFromAllBranches;
    }


    public CoffeeBrand(int id, String brandName, int numberOfCoffeeNeeded) {
        this.id = id;
        this.brandName = brandName;
        this.numberOfCoffeeNeeded = numberOfCoffeeNeeded;

    }



    public CoffeeBrand(int id, String brandName, int numberOfCoffeeNeeded, int dataBaseId) {
        this.id = id;
        this.brandName = brandName;
        this.numberOfCoffeeNeeded = numberOfCoffeeNeeded;
        this.dataBaseId = dataBaseId;
    }

    public CoffeeBrand(){
        //Empty constructor
    }




    public int getDataBaseId() {
        return dataBaseId;
    }

    public void setDataBaseId(int dataBaseId) {
        this.dataBaseId = dataBaseId;
    }
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getBrandName()
    {
        return brandName;
    }

    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }

    public int getNumberOfCoffeeNeeded()
    {
        return numberOfCoffeeNeeded;
    }

    public void setNumberOfCoffeeNeeded(int numberOfCoffeeNeeded)
    {
        this.numberOfCoffeeNeeded = numberOfCoffeeNeeded;
    }

    public String getCoffeeBrandCode()
    {
        return coffeeBrandCode;
    }

    public void setCoffeeBrandCode(String coffeeBrandCode)
    {
        this.coffeeBrandCode = coffeeBrandCode;
    }

    public List<CoffeeShop> getBranches()
    {
        return branches;
    }

    public void setBranches(List<CoffeeShop> branches)
    {
        this.branches = branches;
    }

    public int getTotalCoffeeStampsFromAllBranches()
    {
        return totalCoffeeStampsFromAllBranches;
    }

    public void setTotalCoffeeStampsFromAllBranches(int totalCoffeeStampsFromAllBranches)
    {
        this.totalCoffeeStampsFromAllBranches = totalCoffeeStampsFromAllBranches;
    }
}
