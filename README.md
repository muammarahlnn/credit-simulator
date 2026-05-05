# Credit Simulator

A Java CLI application designed to simulate vehicle loan installments with dynamic interest rates based on vehicle type, condition, and tenure.

---

## Features

**Dual Mode Execution**
- **Interactive Mode** — A full CLI experience with real-time command processing
- **File/Batch Mode** — Directly process a JSON/Text file via command-line arguments

**Data Integration**
- **REST API Integration** — Loads external loan data from a Web Service (Mocky)
- **File System Integration** — Reads and parses local JSON files using Jackson

**In-Memory State Management** — Features a "Switch Sheet" system to store and revisit previous calculations during a session

**Professional Architecture** — Implements Clean Architecture with N-Tier separation, Factory Pattern, and Repository Pattern

---

## Tech Stack

| | |
|---|---|
| Language | Java 21+ (Compatible with OpenJDK 26) |
| Build Tool | Maven |
| JSON Parser | Jackson Databind |
| Testing | JUnit 5 & Mockito |
| Version Control | Git |

---

## Prerequisites

- Java JDK **version 21 or higher**
- Maven installed and added to your system `PATH`

---

## Setup & Installation

**1. Clone the repository**

```bash
git clone https://github.com/muammarahlnn/credit-simulator
cd credit-simulator
```

**2. Build the project**

Compile the code and run unit tests to ensure everything is working:

```bash
mvn clean package
```

This generates a Fat JAR in the `target/` directory:

```
target/credit-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## How to Run

### Interactive Mode

Run without arguments to enter the interactive shell:

```bash
./credit_simulator
```

Available commands:

| Command | Description |
|---|---|
| `show` | List all available commands |
| `manual` | Start the step-by-step manual input flow |
| `load` | Fetch data from the remote Web Service |
| `history` | List all calculations performed in this session |
| `switch <id>` | View the detailed breakdown of a previous calculation |
| `exit` | Safely close the application |

### File/Batch Mode

Process a specific file immediately and exit:

```bash
./credit_simulator docs/file_inputs.txt
```

---

## Architecture

The application follows Clean Architecture principles to ensure maintainability and testability.

```
┌─────────────────────────────────────────────────────┐
│                  Controller Layer                   │
│         CLI I/O · Input trapping · Validation       │
├─────────────────────────────────────────────────────┤
│                   Service Layer                     │
│     Business flow · Rule coordination · Routing     │
├─────────────────────────────────────────────────────┤
│                Domain / Model Layer                 │
│    LoanRequest.validate() · Entity definitions      │
├─────────────────────────────────────────────────────┤
│                  Repository Layer                   │
│  ApiLoanRepository · FileLoanRepository             │
│  InMemoryHistoryRepository (interface-abstract)    │
├─────────────────────────────────────────────────────┤
│                 CalculatorFactory                   │
│      Car engine · Motorcycle engine (runtime)       │
└─────────────────────────────────────────────────────┘
```

1. **Controller Layer** — Handles CLI I/O and user input trapping to ensure only valid data formats reach the core
2. **Service Layer** — Orchestrates the business flow, validating business rules and coordinating between repositories and calculators
3. **Domain/Model Layer** — Contains the source of truth for business rules (`LoanRequest.validate()`) and entity definitions
4. **Repository Layer** — Abstracted via interfaces; the app can switch between `ApiLoanRepository`, `FileLoanRepository`, and `InMemoryHistoryRepository` seamlessly
5. **Factory Pattern** — Used in `CalculatorFactory` to instantiate the correct math engine (Car vs. Motorcycle) at runtime based on vehicle type

---

## Testing

Run the automated test suite to verify business logic and math calculations:

```bash
mvn test
```

Tests follow the **GWT (Given-When-Then)** pattern for maximum readability.

---

## 📄 Example Input File

`docs/file_inputs.txt`

```json
{
  "vehicleType": "Motor",
  "vehicleCondition": "Bekas",
  "vehicleYear": 2022,
  "totalLoanAmount": 20000000,
  "loanTenure": 3,
  "downPayment": 5000000
}
```