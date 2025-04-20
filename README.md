# Digital Wallet System

A modern Java Swing-based digital wallet application with user and admin interfaces, built with MySQL database. Features a beautiful and intuitive user interface with modern design elements.

## Features

- User registration and login with role-based access
- Secure password hashing using SHA-256
- Add money to wallet
- Transfer funds between users
- View transaction history
- Admin panel for user management
- Advanced fraud detection system
- Bill payment system with multiple service providers
- Modern UI with shadow effects and curved panels
- Responsive design with proper spacing and typography
- Real-time balance updates
- Transaction status tracking
- User blocking/unblocking functionality
- Detailed transaction logs
- Fraud detection logs and analysis

## 📸 GUI Screenshots

### User Interface
<div align='center'>
<h3>Login Page</h3>
<img src="src/main/java/com/digitalwallet/images/login%20page.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Create Account Page</h3>
<img src="src/main/java/com/digitalwallet/images/create%20account%20page.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>User Dashboard</h3>
<img src="src/main/java/com/digitalwallet/images/user%20dashboard.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Add Money to Wallet</h3>
<img src="src/main/java/com/digitalwallet/images/add%20money%20to%20wallet.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Transfer Money</h3>
<img src="src/main/java/com/digitalwallet/images/transfer%20money.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Transaction History</h3>
<img src="src/main/java/com/digitalwallet/images/transaction%20history.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Pay Bills Page</h3>
<img src="src/main/java/com/digitalwallet/images/pay%20bills%20page.png" width="500" style="border-radius: 10px;">
</div>

### Admin Interface
<div align='center'>
<h3>Admin Login Page</h3>
<img src="src/main/java/com/digitalwallet/images/admin%20login%20page.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Admin Dashboard</h3>
<img src="src/main/java/com/digitalwallet/images/admin%20dashboard.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Fraud Detection Tab</h3>
<img src="src/main/java/com/digitalwallet/images/fraud%20detection%20tab.png" width="500" style="border-radius: 10px;">
<br><br>

<h3>Types of Services</h3>
<img src="src/main/java/com/digitalwallet/images/types%20of%20services.png" width="500" style="border-radius: 10px;">
</div>

## Prerequisites

- Java JDK 17 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher

## Setup Instructions

1. Clone the repository:
```bash
git clone <repository-url>
cd digital-wallet-system
```

2. Create the MySQL database and tables:
   - Open MySQL command line or MySQL Workbench
   - Run the SQL script in `src/main/resources/database.sql`
   - This will create the database and all required tables
   - It will also create a default admin user and sample services

3. Configure database connection:
   - Open `src/main/java/com/digitalwallet/util/DatabaseUtil.java`
   - Update the following constants with your MySQL credentials:
     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/digital_wallet_db";
     private static final String USERNAME = "root";
     private static final String PASSWORD = "your_password";
     ```

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn exec:java -Dexec.mainClass="com.digitalwallet.gui.LoginFrame"
```

## Default Admin Account

- Username: admin
- Email: admin@digitalwallet.com
- Password: admin123

## Project Structure

```
src/main/java/com/digitalwallet/
├── dao/
│   ├── FraudLogDAO.java      # Data access for fraud logs
│   ├── ServiceDAO.java       # Data access for services
│   ├── TransactionDAO.java   # Data access for transactions
│   └── UserDAO.java          # Data access for users
├── gui/
│   ├── AdminDashboard.java   # Admin interface
│   ├── LoginFrame.java       # Login screen
│   ├── PayBillsDialog.java   # Bill payment dialog
│   ├── RegisterFrame.java    # Registration screen
│   └── UserDashboard.java    # User interface
├── images/                   # GUI screenshots
│   ├── admin dashboard.png
│   ├── admin login page.png
│   ├── create account page.png
│   ├── fraud detection tab.png
│   ├── login page.png
│   ├── pay bills page.png
│   ├── transaction history.png
│   ├── transfer money.png
│   ├── types of services.png
│   └── user dashboard.png
├── model/
│   ├── FraudLog.java         # Fraud log entity
│   ├── Service.java          # Service entity
│   ├── Transaction.java      # Transaction entity
│   └── User.java             # User entity
├── service/
│   └── FraudDetectionService.java  # Fraud detection logic
└── util/
    └── DatabaseUtil.java     # Database connection utility
```

## Database Schema

The application uses the following tables:
- `users`: Stores user information and balances
- `transactions`: Records all financial transactions
- `services`: Stores available service providers
- `payments`: Tracks bill payments
- `fraud_logs`: Records suspicious activities

## UI Features

- Modern color scheme with white coffee background (#E6E0D4)
- Curved panels with enhanced shadow effects
- Responsive buttons with hover and click animations
- Clean typography using Segoe UI font
- Consistent spacing and alignment
- Intuitive navigation
- Clear visual hierarchy
- Proper error handling and user feedback
- Modal dialogs for important actions
- Styled form inputs and tables

## Security Features

- Passwords are hashed using SHA-256
- SQL injection prevention using prepared statements
- Input validation for all user inputs
- Role-based access control
- Secure transaction verification
- Fraud detection system
- User blocking mechanism
- Transaction logging

## Dependencies

- MySQL Connector/J 8.0.27
- JFreeChart 1.5.3 (for analytics)
- Apache Commons Lang 3.12.0

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
