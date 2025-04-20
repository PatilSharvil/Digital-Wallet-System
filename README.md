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
<h3>1. Login Page</h3>
<img src="src/main/java/com/digitalwallet/images/1%20login%20page.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>2. Create Account Page</h3>
<img src="src/main/java/com/digitalwallet/images/2%20create%20account%20page.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>3. User Dashboard</h3>
<img src="src/main/java/com/digitalwallet/images/3%20user%20dashboard.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>4. Add Money to Wallet</h3>
<img src="src/main/java/com/digitalwallet/images/4%20add%20money%20to%20wallet.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>5. Transfer Money</h3>
<img src="src/main/java/com/digitalwallet/images/5%20transfer%20money.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>6. Pay Bills Page</h3>
<img src="src/main/java/com/digitalwallet/images/6%20pay%20bills%20page.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>7. Types of Services</h3>
<img src="src/main/java/com/digitalwallet/images/7%20types%20of%20services.png" width="400" style="border-radius: 10px;">
</div>

### Admin Interface
<div align='center'>
<h3>8. Admin Login Page</h3>
<img src="src/main/java/com/digitalwallet/images/8%20admin%20login%20page.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>9. Admin Dashboard and User Tab</h3>
<img src="src/main/java/com/digitalwallet/images/9%20admin%20dashboard%20and%20user%20tab.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>10. Fraud Detection Tab</h3>
<img src="src/main/java/com/digitalwallet/images/10%20fraud%20detection%20tab.png" width="400" style="border-radius: 10px;">
<br><br>

<h3>11. Transaction History in Admin</h3>
<img src="src/main/java/com/digitalwallet/images/11%20transaction%20history%20in%20admin.png" width="400" style="border-radius: 10px;">
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
