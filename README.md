

# Internet Technology Project – Multi-Client Chat & File Transfer System

This project is a **multi-client chat and file transfer system** implemented in Java, featuring:

 **Real-time messaging** (public broadcasts and private messages)
 **Mini-games** (Rock-Paper-Scissors between users)
 **Secure file exchange** between clients
 **Client-Server architecture** with multi-threading
**Protocol-based communication** using JSON (Jackson library)

##  Features

* **User authentication:**

  * Log in & log out
  * Username validation
* **Chat functionality:**

  * Public chat (broadcast)
  * Private messaging (user to user)
* **Heartbeat:**

  * Responds to server PING messages with PONG (keeps connection alive)
* **File transfer:**

  * Upload and download files between clients securely
* **Gaming:**

  * Play Rock-Paper-Scissors within the chat system
* **Robust error handling:**

  * Server responds with detailed error codes
  * Handles connection loss gracefully

## Technologies

* **Language:** Java
* **Libraries:**

  * [Jackson](https://github.com/FasterXML/jackson) (for JSON parsing)
* **Concurrency:** Multi-threaded server & client handling
* **Networking:** Sockets (TCP)




## ⚠️ Notes

* The `.idea/` and `out/` folders are IDE and build artifacts and **should not be tracked** (added to `.gitignore`).
* This project was cleaned to have a **single clean commit** with full ownership.

## 👤 Author

**Emad Sawan**

