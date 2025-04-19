# Digital Wallet System

A modern Java Swing-based digital wallet application with user and admin interfaces, built with MySQL database. Features a beautiful and intuitive user interface with modern design elements.

## Features

- User registration and login with role-based access
- Secure password hashing
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

## Prerequisites

- Java JDK 11 or higher
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

3. Configure database connection:
   - Open `src/main/resources/database.properties`
   - Update the following properties with your MySQL credentials:
     ```
     db.url=jdbc:mysql://localhost:3306/digital_wallet_db
     db.username=your_username
     db.password=your_password
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

- Email: admin@digitalwallet.com
- Password: admin123

## Project Structure

```
src/main/java/com/digitalwallet/
├── dao/
│   ├── FraudLogDAO.java
│   ├── ServiceDAO.java
│   ├── TransactionDAO.java
│   └── UserDAO.java
├── gui/
│   ├── AdminDashboard.java
│   ├── LoginFrame.java
│   ├── PayBillsDialog.java
│   ├── RegisterFrame.java
│   └── UserDashboard.java
├── model/
│   ├── FraudLog.java
│   ├── Service.java
│   ├── Transaction.java
│   └── User.java
├── service/
│   └── FraudDetectionService.java
└── util/
    └── DatabaseUtil.java
```

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

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
