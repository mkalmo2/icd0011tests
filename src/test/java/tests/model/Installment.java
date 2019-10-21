package tests.model;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Installment {

    private Integer amount;

    private LocalDate date;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return MessageFormat.format("amount: {0}; date: {1}",
                amount, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}