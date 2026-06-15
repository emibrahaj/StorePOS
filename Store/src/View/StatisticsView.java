package View;

import Controller.ManagerController;
import Model.Bill;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class StatisticsView {
    private Stage stage;
    private ManagerController controller;

    public StatisticsView(Stage stage, ManagerController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        LocalDate today = LocalDate.now();

        int dailyBillsCount = controller.getTotalBillsByDate(today);
        int dailyItemsSold = controller.getTotalItemsSoldByDate(today);
        double dailyRevenue = controller.getTotalRevenueByDate(today);

        int monthlyBillsCount = controller.getTotalBillsForPeriod(today.withDayOfMonth(1), today);
        int monthlyItemsSold = controller.getTotalItemsSoldForPeriod(today.withDayOfMonth(1), today);
        double monthlyRevenue = controller.getTotalRevenueForPeriod(today.withDayOfMonth(1), today);

        // Get all bills directly from the manager
        List<Bill> allBills = controller.getAllBills();
        int totalBillsCount = allBills.size();
        double totalRevenue = 0.0;
        int totalItemsSold = 0;

        for (Bill bill : allBills) {
            totalRevenue += bill.getTotalAmount();
            totalItemsSold += bill.getTotalItemsSold();
        }

        Label dailyBills = new Label("Total Bills Today: " + dailyBillsCount);
        Label dailyItems = new Label("Total Items Sold Today: " + dailyItemsSold);
        Label dailyRevenueLabel = new Label("Total Revenue Today: $" + dailyRevenue);

        Label monthlyBills = new Label("Total Bills This Month: " + monthlyBillsCount);
        Label monthlyItems = new Label("Total Items Sold This Month: " + monthlyItemsSold);
        Label monthlyRevenueLabel = new Label("Total Revenue This Month: $" + monthlyRevenue);

        Label totalBillsLabel = new Label("Total Bills: " + totalBillsCount);
        Label totalRevenueLabel = new Label("Total Revenue: $" + totalRevenue);
        Label totalItemsLabel = new Label("Total Items Sold: " + totalItemsSold);

        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new ManagerDashboardView(stage, controller).start(stage));

        layout.getChildren().addAll(dailyBills, dailyItems, dailyRevenueLabel, monthlyBills, monthlyItems, monthlyRevenueLabel, totalBillsLabel, totalRevenueLabel, totalItemsLabel, backButton);

        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Manager Statistics");
        stage.show();
    }
}
