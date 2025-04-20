# Digital Wallet System

A Java Swing-based digital wallet application with user and admin interfaces, built with MySQL database.

## Features

- User registration and login
- Secure password hashing
- Add money to wallet
- Transfer funds between users
- View transaction history
- Admin panel for user management
- Basic fraud detection (coming soon)
- Bill payment system (coming soon)

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
│   ├── TransactionDAO.java
│   └── UserDAO.java
├── gui/
│   ├── AdminDashboard.java
│   ├── LoginFrame.java
│   ├── RegisterFrame.java
│   └── UserDashboard.java
├── model/
│   ├── Transaction.java
│   └── User.java
└── util/
    └── DatabaseUtil.java
```

## Security Features

- Passwords are hashed using SHA-256
- SQL injection prevention using prepared statements
- Input validation for all user inputs
- Role-based access control

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 