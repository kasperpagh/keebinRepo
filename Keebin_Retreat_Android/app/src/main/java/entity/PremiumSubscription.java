package entity;

/**
 * Created by pelle on 3/29/2017.
 */

public class PremiumSubscription {
    int id;
    boolean isValidForPremiumCoffee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsValidForPremiumCoffee() {
        return isValidForPremiumCoffee;
    }

    public void setIsValidForPremiumCoffee(boolean isValidForPremiumCoffee) {
        this.isValidForPremiumCoffee = isValidForPremiumCoffee;
    }



    public PremiumSubscription(int id, boolean isValidForPremiumCoffee) {
        this.id = id;
        this.isValidForPremiumCoffee = isValidForPremiumCoffee;
    }
}
