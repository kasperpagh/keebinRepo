package entity;

import java.util.Date;

/**
 * Created by kaspe on 2016-10-26.
 */


public class LoyaltyCard
{
    private CoffeeBrand brand;
    private CoffeeShop coffeeShop; //If some brand wants to issue their loyalty cards to ONLY specific branches, otherwise null.
    private int numberOfCoffeesBought;
    private String numberofBeans;
    private String createdAt;
    private boolean isValid;
    private boolean readyForFreeCoffee;
    private int numberOfFreeCoffeeAvailable;
    private int id;
    private int brandName;
    private String nameOfBrand;

    public LoyaltyCard(int id,CoffeeBrand brand, CoffeeShop coffeeShop, String createdAt, boolean isValid, int numberOfFreeCoffeeAvailable, int numberOfCoffeesBought)
    {
        this.id = id;
        this.brand = brand;
        this.coffeeShop = coffeeShop;
        this.createdAt = createdAt;
        this.isValid = isValid;
        this.numberOfFreeCoffeeAvailable = numberOfFreeCoffeeAvailable;
        this.numberOfCoffeesBought = numberOfCoffeesBought;
        this.nameOfBrand =brand.getBrandName();
        this.brandName = brand.getId();

    }

    public LoyaltyCard(int brandName, String numberofBeans){
        this.brandName = brandName;
        this.numberofBeans = numberofBeans;
    }

    public LoyaltyCard(String nameOfBrand, String numberofBeans){
        this.nameOfBrand = nameOfBrand;
        this.numberofBeans = numberofBeans;
    }

    public LoyaltyCard(int numberOfCoffeesBought, String numberofBeans, String createdAt, boolean isValid, boolean readyForFreeCoffee, String nameOfBrand)
    {
        this.numberOfCoffeesBought = numberOfCoffeesBought;
        this.numberofBeans = numberofBeans;
        this.createdAt = createdAt;
        this.isValid = isValid;
        this.readyForFreeCoffee = readyForFreeCoffee;
        this.nameOfBrand = nameOfBrand;
    }

    public String getNameOfBrand() {
        return nameOfBrand;
    }

    public int getMaxCoffee() { return brand.getNumberOfCoffeeNeeded();}

    public void setNameOfBrand(String nameOfBrand) {
        this.nameOfBrand = nameOfBrand;
    }

    public int getBrandName() {
        return brandName;
    }

    public void setBrandName(int brandName) {
        this.brandName = brandName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumberofBeans() {
        return numberofBeans;
    }

    public void setNumberofBeans(String numberofBeans) {
        this.numberofBeans = numberofBeans;
    }
    public void setnumberOfFreeCoffeeAvailable(int numberOfFreeCoffeeAvailable){ this.numberOfFreeCoffeeAvailable =numberOfFreeCoffeeAvailable;}

    public int getNumberOfFreeCoffeeAvailable(){return this.numberOfFreeCoffeeAvailable;}
    public CoffeeShop getCoffeeShop()
    {
        return coffeeShop;
    }

    public void setCoffeeShop(CoffeeShop coffeeShop)
    {
        this.coffeeShop = coffeeShop;
    }

    public int getNumberOfCoffeesBought()
    {
        return numberOfCoffeesBought;
    }

    public void setNumberOfCoffeesBought(int numberOfCoffeesBought)
    {
        this.numberOfCoffeesBought = numberOfCoffeesBought;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public boolean isValid()
    {
        return isValid;
    }

    public void setValid(boolean valid)
    {
        isValid = valid;
    }

    public boolean isReadyForFreeCoffee()
    {
        return readyForFreeCoffee;
    }

    public void setReadyForFreeCoffee(boolean readyForFreeCoffee)
    {
        this.readyForFreeCoffee = readyForFreeCoffee;
    }

    @Override
    public String toString()
    {
        return "LoyaltyCard{" +
                "brand=" + brand +
                ", coffeeShop=" + coffeeShop +
                ", numberOfCoffeesBought=" + numberOfCoffeesBought +
                ", numberofBeans='" + numberofBeans + '\'' +
                ", createdAt=" + createdAt +
                ", isValid=" + isValid +
                ", readyForFreeCoffee=" + readyForFreeCoffee +
                ", id=" + id +
                ", brandName=" + brandName +
                ", nameOfBrand='" + nameOfBrand + '\'' +
                '}';
    }
}
