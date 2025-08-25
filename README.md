# jjbank
Jordan Joseph Bank (JJB) - A demo banking system built with Spring Boot, PostgreSQL and Docker. It features user accounts, and transactions in Jordan Dollars ($J), a custom currency.

---

## 🚀 Project Status
📌 Work in progress (MVP under development - August 2025)
The goal is to build a simple but realistic banking system to demonstrate backend development skills.

---

## 🛠 Tech Stack
**Backend:** Java 21, Spring Boot, Spring Web, Spring Security, Spring Data JPA
**Database:** PostgreSQL
**Containerization:** Docker & Docker Compose
**Testing:** JUnit (Planned)
**API Documentation:** Swagger/OpenAI (Planned)

## 📌 Planned Features
- User registration and authentication (JWT)
- Accounts with balances in **Jordan Dollar ($J)**
- Deposit, withdraw, and transfer operation
- Transaction history with pagination
- Dockerized setup (App + PostgreSQL)
- Swagger UI for API documentation

## 📂 Project Structure (planned)
jjbank/
├─ backend/ # Spring Boot API
├─ frontend/ # (future) React client
├─ .gitignore
├─ LICENSE
└─ README.md

## 🏗 Roadmap
- [ ] Initialize Spring Boot project & DB connection
- [ ] Implement user registration and authentication (JWT)
- [ ] Add account + transaction management
- [ ] Setup SMTP service for register and transfer operations
- [ ] Dockerize application
- [ ] API docs with Swagger
- [ ] Write integration tests
- [ ] Create minimal frontend (optional)