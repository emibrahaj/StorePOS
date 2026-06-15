package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    private String billNumber;
    private Date date;
    private List<Item> items;
    private double totalAmount;

    public Bill(String billNumber, Date date) {
        this.billNumber = billNumber;
        this.date = date;
        this.items = new ArrayList<>();
        this.totalAmount = 0;
    }

    public void addItem(Item item) {
        this.items.add(item);
        this.totalAmount += item.getSellingPrice() * item.getQuantity();
    }

    public String getBillNumber() {
        return billNumber;
    }

    public Date getDate() {
        return date;
    }

    public List<Item> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getTotalItemsSold() {
        return items.stream().mapToInt(Item::getQuantity).sum();
    }

    @Override
    public String toString() {
        return "Bill Number: " + billNumber + "\nDate: " + date + "\nTotal Amount: " + totalAmount;
    }
}
