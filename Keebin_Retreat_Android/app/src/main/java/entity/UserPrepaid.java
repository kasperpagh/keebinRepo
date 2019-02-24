package entity;



public class UserPrepaid {
    int id;
    int usesleft;
    int userId;
    int PrePaidCoffeeCardId;

    @Override
    public String toString() {
        return "UserPrepaid{" +
                "id=" + id +
                ", usesleft=" + usesleft +
                ", userId=" + userId +
                ", PrePaidCoffeeCardId=" + PrePaidCoffeeCardId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsesleft() {
        return usesleft;
    }

    public void setUsesleft(int usesleft) {
        this.usesleft = usesleft;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPrePaidCoffeeCardId() {
        return PrePaidCoffeeCardId;
    }

    public void setPrePaidCoffeeCardId(int prePaidCoffeeCardId) {
        PrePaidCoffeeCardId = prePaidCoffeeCardId;
    }

    public UserPrepaid(int usesleft, int userId, int prePaidCoffeeCardId) {
        this.usesleft = usesleft;
        this.userId = userId;
        PrePaidCoffeeCardId = prePaidCoffeeCardId;
    }

    public UserPrepaid(int id, int usesleft, int userId, int prePaidCoffeeCardId) {
        this.id = id;
        this.usesleft = usesleft;
        this.userId = userId;
        PrePaidCoffeeCardId = prePaidCoffeeCardId;
    }




}
