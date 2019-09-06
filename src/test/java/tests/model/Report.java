package tests.model;

public class Report {

    private Integer count;
    private Integer averageOrderAmount;
    private Integer turnoverWithoutVAT;
    private Integer turnoverVAT;
    private Integer turnoverWithVAT;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getAverageOrderAmount() {
        return averageOrderAmount;
    }

    public void setAverageOrderAmount(Integer averageOrderAmount) {
        this.averageOrderAmount = averageOrderAmount;
    }

    public Integer getTurnoverWithoutVAT() {
        return turnoverWithoutVAT;
    }

    public void setTurnoverWithoutVAT(Integer turnoverWithoutVAT) {
        this.turnoverWithoutVAT = turnoverWithoutVAT;
    }

    public Integer getTurnoverVAT() {
        return turnoverVAT;
    }

    public void setTurnoverVAT(Integer turnoverVAT) {
        this.turnoverVAT = turnoverVAT;
    }

    public Integer getTurnoverWithVAT() {
        return turnoverWithVAT;
    }

    public void setTurnoverWithVAT(Integer turnoverWithVAT) {
        this.turnoverWithVAT = turnoverWithVAT;
    }
}
