package entity;

import java.util.List;

/**
 * Created by kaspe on 2016-10-26.
 */

public class User
{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String birthday;
    private String sex;
    private int roleId;
    private String password;
    private LoginData loginData;
    private String refreshToken;

    @Override
    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                ", roleId=" + roleId +
                ", password='" + password + '\'' +
                ", loginData=" + loginData +
                ", refreshToken='" + refreshToken + '\'' +
                ", isSubscriber=" + isSubscriber +
                ", subscriberFreeCoffeeLeft=" + subscriberFreeCoffeeLeft +
                ", loyaltyCards=" + loyaltyCards +
                '}';
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    private boolean isSubscriber;
    private int subscriberFreeCoffeeLeft;

    public User(int id, String firstName, String lastName, String email, String birthday, String sex, int roleId, boolean isSubscriber, String password)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.sex = sex;
        this.roleId = roleId;
        this.isSubscriber = isSubscriber;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String birthday, String sex, int roleId, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.sex = sex;
        this.roleId = roleId;
        this.password = password;
    }

    public LoginData getLoginData()
    {
        return loginData;
    }

    public void setLoginData(LoginData loginData)
    {
        this.loginData = loginData;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    private List<LoyaltyCard> loyaltyCards;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public int getRoleId()
    {
        return roleId;
    }

    public void setRoleId(int roleId)
    {
        this.roleId = roleId;
    }

    public boolean isSubscriber()
    {
        return isSubscriber;
    }

    public void setSubscriber(boolean subscriber)
    {
        isSubscriber = subscriber;
    }

    public int getSubscriberFreeCoffeeLeft()
    {
        return subscriberFreeCoffeeLeft;
    }

    public void setSubscriberFreeCoffeeLeft(int subscriberFreeCoffeeLeft)
    {
        this.subscriberFreeCoffeeLeft = subscriberFreeCoffeeLeft;
    }

    public List<LoyaltyCard> getLoyaltyCards()
    {
        return loyaltyCards;
    }

    public void setLoyaltyCards(List<LoyaltyCard> loyaltyCards)
    {
        this.loyaltyCards = loyaltyCards;
    }
}
