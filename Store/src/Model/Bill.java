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
    private double refundedAmount;
    private boolean fullyRefunded;

    public Bill(String billNumber, Date date) {
        this.billNumber = billNumber;
        this.date = date;
        this.items = new ArrayList<>();
        this.totalAmount = 0;
        this.refundedAmount = 0;
        this.fullyRefunded = false;
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

    public double getRefundedAmount() {
        return refundedAmount;
    }

    public boolean isFullyRefunded() {
        return fullyRefunded;
    }

    public double returnItem(String itemName, int quantity) {
        for (Item item : new ArrayList<>(items)) {
            if (item.getItemName().equalsIgnoreCase(itemName)) {
                if (quantity <= 0 || quantity > item.getQuantity()) {
                    return 0;
                }

                double refundValue = item.getSellingPrice() * quantity;
                int remainingQuantity = item.getQuantity() - quantity;
                item.setQuantity(remainingQuantity);
                this.totalAmount -= refundValue;
                this.refundedAmount += refundValue;

                if (remainingQuantity == 0) {
                    items.remove(item);
                }

                if (items.isEmpty()) {
                    fullyRefunded = true;
                }

                return refundValue;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Bill Number: " + billNumber + "\nDate: " + date + "\nTotal Amount: " + totalAmount + "\nRefunded: " + refundedAmount;
    }
}
