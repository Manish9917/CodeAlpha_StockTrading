# 📈 Stock Trading Platform

A desktop-based Stock Trading Platform developed using **Java Swing** and **SQLite**. The application provides a graphical interface where users can register, log in, manage their individual portfolios, and simulate stock trading transactions.

## ✨ Features

- **User Registration and Login** — Create an account and log in with your credentials.
- **Password Security** — Passwords are stored using salted PBKDF2 hashing instead of plain text.
- **Individual User Portfolios** — Each user has a separate portfolio and cash balance.
- **Stock Market Interface** — Browse and search available stocks.
- **Buy and Sell Stocks** — Simulate stock transactions with balance and quantity validation.
- **Portfolio Management** — Track holdings, cash balance, and portfolio value.
- **Transaction History** — Maintain records of buy and sell transactions.
- **Data Persistence** — Save user portfolio information between application sessions.
- **Desktop GUI** — Interactive graphical interface built with Java Swing.

## 🛠️ Technologies Used

- **Java** — Core application logic and object-oriented programming.
- **Java Swing** — Desktop graphical user interface.
- **SQLite** — Database for user account information.
- **JDBC** — Database connectivity.
- **SQLite JDBC Driver** — Java connectivity to SQLite.
- **File Serialization** — Local storage of individual user portfolios.

## 📂 Project Structure

```text
CodeAlpha_StockTrading/
├── src/
│   ├── Main.java
│   ├── Login.java
│   ├── User.java
│   ├── Stock.java
│   ├── StockMarket.java
│   ├── Portfolio.java
│   ├── Holding.java
│   └── Transaction.java
├── lib/
│   └── sqlite-jdbc-3.53.4.0.jar
├── out/                  # Compiled classes (generated locally)
├── .gitignore
└── README.md
```

## ⚙️ Prerequisites

Before running the application, make sure you have:

- Java Development Kit (JDK) installed.
- A terminal or command prompt.
- The SQLite JDBC driver included in the `lib` directory.
- Git, if you want to clone the project repository.

Check your Java installation:

```bash
java -version
javac -version
```

## 🚀 Getting Started

### 1. Clone the Repository

Replace `<your-github-username>` with your GitHub username.

```bash
git clone https://github.com/<your-github-username>/CodeAlpha_StockTrading.git
```

Move into the project directory:

```bash
cd CodeAlpha_StockTrading
```

### 2. Compile the Application

Run this command from the project root directory:

```bash
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d out src/Main.java src/Login.java src/User.java src/Stock.java src/StockMarket.java src/Portfolio.java src/Holding.java src/Transaction.java
```

### 3. Run the Application

On **Windows PowerShell**:

```powershell
java -cp "out;lib/sqlite-jdbc-3.53.4.0.jar" Main
```

On **Linux or macOS**:

```bash
java -cp "out:lib/sqlite-jdbc-3.53.4.0.jar" Main
```

## 🔐 Data and Security

- User account information is stored in a local SQLite database.
- Passwords are protected using salted PBKDF2 hashing.
- Portfolio files are maintained separately for individual users.
- Local database files and saved portfolio files should not be committed to the public repository.
- Do not store real passwords, API keys, or other secrets in the source code.

**Note:** User database and portfolio files are created locally when the application runs. Each user should use their own account.

## 📊 Project Scope

This application is designed as a desktop-based stock trading simulator for learning and demonstration purposes.

Unless a live market data integration is implemented, stock prices should be treated as application-provided or simulated prices. This project does not execute real stock market orders.

## 🔮 Future Improvements

Potential future enhancements include:

- Live stock market data integration.
- Interactive stock price charts.
- Improved portfolio performance analytics.
- Transaction history search and filtering.
- Migration of portfolio and transaction storage to SQLite.
- Automated unit and integration tests.
- Packaging the application as an executable JAR or desktop installer.

## 👨‍💻 Author

Developed as a Java desktop application project.

---

⭐ If you find this project useful, consider giving the repository a star on GitHub.