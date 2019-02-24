package entity;

/**
 * Created by kaspe on 2017-02-16.
 */

public class PrePaidCard
{
    private int id;
    private int count;
    private int price;
    private int cents;
    private String name;
    private String coffeeBrandId;

    @Override
    public String toString() {
        return "PrePaidCard{" +
                "id=" + id +
                ", count=" + count +
                ", price=" + price +
                ", cents=" + cents +
                ", name='" + name + '\'' +
                ", coffeeBrandId='" + coffeeBrandId + '\'' +
                '}';
    }



    public PrePaidCard(int count, int price, int cents, String name, String coffeeBrandId) {
        this.count = count;
        this.price = price;
        this.cents = cents;
        this.name = name;
        this.coffeeBrandId = coffeeBrandId;
    }



    public PrePaidCard(int id, int count, int price, int cents, String name, String coffeeBrandId) {
        this.id = id;
        this.count = count;
        this.price = price;
        this.cents = cents;
        this.name = name;
        this.coffeeBrandId = coffeeBrandId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCents() {
        return cents;
    }

    public void setCents(int cents) {
        this.cents = cents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoffeeBrandId() {
        return coffeeBrandId;
    }

    public void setCoffeeBrandId(String coffeeBrandId) {
        this.coffeeBrandId = coffeeBrandId;
    }
}
