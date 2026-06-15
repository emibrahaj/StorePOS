# StorePOS - Retail Management System

A desktop-based Point-of-Sale (POS) and Inventory Management System built with **Java** and **JavaFX**. Designed for retail stores to manage inventory, process bills, track suppliers, and generate sales statistics with role-based access control.

## Features

✅ **Multi-Role Access Control**
- Admin: Full system management and user administration
- Manager: Inventory and supplier oversight
- Cashier: Sales transactions and billing
- User roles with authentication via credentials file

✅ **Inventory Management**
- Track items by category, supplier, purchase/selling price
- Real-time stock level monitoring
- Add/update/remove inventory items
- Automated inventory persistence

✅ **Billing & Sales**
- Generate bills with itemized transactions
- Calculate totals with selling prices
- Bill history and tracking
- Print-friendly bill format support

✅ **Supplier Management**
- Maintain supplier records
- Track purchase relationships
- Supplier-based inventory filtering

✅ **Analytics Dashboard**
- Sales statistics and reporting
- Inventory analytics
- Real-time data visualization

✅ **Data Persistence**
- Serialized data storage (DAT files)
- Project-relative path management (works from any directory)
- Automatic data backup in structured format

## System Requirements

- **Java**: 22 or higher
- **JavaFX**: 21 or higher
- **OS**: Windows, macOS, or Linux
- **RAM**: 512 MB minimum
- **Disk Space**: 100 MB for application + data

## Project Structure

```
Store/
├── src/                          # Source code
│   ├── Controller/               # Business logic & event handlers
│   │   ├── AdminController.java
│   │   ├── LoginController.java
│   │   ├── CashierController.java
│   │   └── ManagerController.java
│   ├── DAO/                      # Data Access Objects
│   │   ├── AppPaths.java         # Centralized path management
│   │   ├── BillDAO.java
│   │   ├── InventoryDAO.java
│   │   ├── SupplierDAO.java
│   │   └── passwords.txt         # User credentials (DO NOT COMMIT)
│   ├── Model/                    # Data models
│   │   ├── User.java
│   │   ├── Admin.java
│   │   ├── Manager.java
│   │   ├── Cashier.java
│   │   ├── Item.java
│   │   ├── Bill.java
│   │   ├── Inventory.java
│   │   ├── Supplier.java
│   │   └── Role.java
│   ├── View/                     # JavaFX UI components
│   │   ├── LoginView.java
│   │   ├── AdminDashboardView.java
│   │   ├── ManagerDashboardView.java
│   │   ├── CashierDashboardView.java
│   │   └── StatisticsView.java
│   ├── Main/
│   │   └── Main.java             # Application entry point
│   └── Database/                 # Generated data files (auto-created)
│       ├── bills.dat
│       ├── inventory.dat
│       ├── suppliers.dat
│       └── PrintableBills/
├── bin/                          # Compiled bytecode
├── build.fxbuild                 # JavaFX build configuration
└── .project / .classpath         # Eclipse project files

```

## Getting Started

### 1. Prerequisites

Ensure Java 22+ and JavaFX 21+ are installed:

```powershell
java -version
```

### 2. Running the Application

**Option A: From the Store directory (Recommended)**

```powershell
cd Store
java -p "path\to\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp "bin" Main.Main
```

**Option B: Using IDE (Eclipse/IntelliJ)**

1. Import the `Store` folder as an Eclipse project
2. Right-click project → Run As → Java Application
3. Select `Main.Main` as the main class

### 3. First Time Setup

- The app automatically creates required data files in `Store/src/Database/`
- **Credentials**: Edit `Store/src/DAO/passwords.txt` to add users in format:
  ```
  username,password,email,phonenumber,role
  admin,admin123,admin@store.com,1234567890,admin
  manager,mgr123,manager@store.com,0987654321,manager
  cashier,cash123,cashier@store.com,5555555555,cashier
  ```

## Usage

### Login
1. Start the application
2. Enter credentials (username, password, email, phone) and select role
3. Click "Login"

### Admin Dashboard
- Manage user accounts
- View system statistics
- Access full application controls

### Manager Dashboard
- Manage inventory (add/edit/delete items)
- Manage suppliers
- Monitor stock levels

### Cashier Dashboard
- Process sales transactions
- Generate and print bills
- View recent transaction history

### Statistics View
- View sales analytics
- Monitor inventory trends
- Generate reports

## Data Files Location

All data is stored in project-relative paths (works from any directory):

- **Bills**: `Store/src/Database/bills.dat`
- **Inventory**: `Store/src/Database/inventory.dat`
- **Suppliers**: `Store/src/Database/suppliers.dat`
- **Credentials**: `Store/src/DAO/passwords.txt`
- **Printable Bills**: `Store/src/Database/PrintableBills/`

## Building from Source

If using Eclipse/Maven:

```powershell
cd Store
javac -d bin -sourcepath src src/Main/Main.java
```

Or use your IDE's build function (right-click → Build).

## Troubleshooting

### "File not found" errors
- Ensure you're running from the `Store/` directory or its parent
- Check that `passwords.txt` exists in `Store/src/DAO/`
- Verify JavaFX path is correctly set

### "Module not found: javafx"
- Ensure JavaFX SDK is in your CLASSPATH
- Add `--add-modules javafx.controls,javafx.fxml` to JVM args

### Data not persisting
- Check file permissions in `Store/src/Database/`
- Ensure sufficient disk space available

## Technologies Used

- **Java 22** - Core language
- **JavaFX 21** - Desktop UI framework
- **Serialization** - Data persistence
- **Git** - Version control

## Contributors

This project was developed as a collaborative effort at **Epoka University**:

- **Emi Brahaj**
- **Era Malaj**
- **Erla Luzi**

## Future Enhancements

- Database integration (MySQL/PostgreSQL)
- PDF report generation
- Barcode scanning support
- Multi-location support
- Email receipt functionality
- Advanced analytics & forecasting

## License

MIT License - Feel free to use and modify for personal or commercial use.

## Support

For issues or questions, refer to the project documentation or contact the development team.