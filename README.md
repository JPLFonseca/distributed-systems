## Club Management System

This project is a web-based management system for a health club, developed as the second part of a distributed computational infrastructure course. The system allows different types of users—clients, personal trainers (PTs), and managers—to perform specific tasks related to the club's operations.

---

### Key Features

* **User Authentication**: Secure login and logout functionality for clients, PTs, and managers.
* **Client Management**:
    * Clients can update their profile information, including email, phone number, and medical conditions.
    * Clients can view their current recommendations from their PT.
* **Personal Trainer (PT) Functionality**:
    * PTs can view their scheduled sessions.
    * They can also get lists of clients, activities, and equipment.
* **Manager Functionality**:
    * Managers can update club contact information and schedules.
    * They can manage visual media by updating photos and videos for specific rooms.
    * Managers can also view reports on the least-used equipment.
* **Database Integration**: The system connects to a database to manage user data, activities, and club information using a dedicated `Manipula.java` class for data access and a `Configura.java` class for handling database connection settings.

---

### Technical Stack

* **Back-end**: Java Servlets
* **Front-end**: HTML and JSP (JavaServer Pages) for dynamic content.
* **Database**: MySQL (default configuration). The system's database architecture is also detailed in the project documentation.

---

### Project Architecture

The project follows a client-server architecture. It uses servlets to handle requests and interact with a database, which is configured using a separate properties file. The client-side is composed of JSP pages that display dynamic content based on the user's role. A key advantage of this architecture is the low latency, as the server can be hosted on the same machine or local network as the client. A potential disadvantage is that a user is unable to perform other actions while waiting for another user to connect.
