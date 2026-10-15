# 🌱 GrowNet — Internship & Career Readiness Platform

GrowNet is a web-based platform designed to help students discover relevant internship opportunities, understand how well their skills match internship requirements, and prepare for their careers.

The platform aims to bring internship discovery, career guidance, placement readiness, and opportunity verification together in one place.

## 🎯 Project Objectives

- Help students discover internships relevant to their skills and interests.
- Provide explainable skill-matching results.
- Track placement readiness through a checklist-based dashboard.
- Offer AI-powered career guidance.
- Support recruiter and internship verification.
- Help students track internship deadlines.
- Allow students to save, search, filter, and compare internships.

## ✨ Planned Features

### 1. Explainable Internship Matching
Calculate a skill-match percentage between a student's skills and an internship's requirements. Display matched skills, missing skills, and a simple explanation.

### 2. Placement Readiness Dashboard
Provide a checklist-based readiness score to help students identify areas they need to improve before applying for internships.

### 3. AI Career Assistant
Provide career guidance on technical skills, learning roadmaps, resumes, internships, and interview preparation using an LLM API.

### 4. Recruiter and Internship Verification
Allow administrators to review and approve or reject recruiter accounts and internship listings.

### 5. Internship Deadline Alerts
Display internship deadline statuses such as upcoming, closing soon, and expired.

### 6. Saved Internships and Smart Search
Allow students to save internships and search or filter opportunities using relevant criteria.

### 7. Internship Comparison
Allow students to compare two or three internships based on available information, including required skills, location, and deadlines.

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| Frontend | React 19, Vite, JavaScript |
| UI and Styling | HTML5, CSS3, Bootstrap 5 |
| Backend | Java 21, Spring Boot |
| Build Tool | Maven |
| Database | MySQL 8 |
| Authentication | Spring Security, JWT |
| AI Integration | LLM API |
| API Testing | Postman |
| Version Control | Git and GitHub |

## 🏗️ Project Structure

```text
GrowNet/
├── backend/
│   ├── pom.xml
│   ├── .env.example
│   └── src/
│       └── main/
│           ├── java/com/grownet/
│           └── resources/
│               └── application.properties
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   └── src/
├── database/
│   └── setup.sql
├── .gitignore
└── README.md
```

*Note: This is the planned project structure. The repository structure may evolve during development.*

## ⚙️ Getting Started

### Prerequisites

Install the following tools before running the application:

- [Java JDK 21](https://adoptium.net/)
- [Node.js LTS](https://nodejs.org/)
- [MySQL 8](https://dev.mysql.com/downloads/installer/)
- [Git](https://git-scm.com/)
- [Apache Maven](https://maven.apache.org/)

### 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd GrowNet
```

Replace `<YOUR_GITHUB_REPOSITORY_URL>` with the actual repository URL.

### 2. Configure the Database

1. Start the MySQL server.
2. Create the GrowNet database using the provided SQL script, when available.
3. Configure the database connection in the backend environment configuration.
4. Ensure the MySQL username and password are correct.

Database name: `grownet`

### 3. Configure Backend Environment Variables

Create a local `.env` file using `backend/.env.example` as a template, once the example file is available.

Configure the required database credentials, JWT secret, and AI provider API key as applicable.

**Important:** Never commit your actual `.env` file, database passwords, JWT secrets, or AI API keys to GitHub.

### 4. Run the Backend

Open a terminal in the backend directory:

```bash
cd backend
mvn spring-boot:run
```

The backend will use the port configured in the application properties.

### 5. Run the Frontend

Open a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the local URL displayed by Vite in your browser.

*These instructions assume the corresponding project files and configurations have been created.*

## 🔐 Security

GrowNet is designed to use:

- JWT-based authentication.
- Role-based access for students, recruiters, and administrators.
- Password hashing.
- Environment variables for sensitive configuration.
- Backend-side authorization for protected operations.

AI provider credentials must remain on the backend and must never be exposed in frontend code.

## 👥 Team Responsibilities

The project is developed collaboratively by three team members.

| Member | Primary Responsibility |
|---|---|
| Member 1 | Authentication, user profiles, database entities, and core internship APIs |
| Member 2 | Internship matching, saved internships, search, readiness, deadlines, and comparison |
| Member 3 | AI Career Assistant, admin verification, and error handling |

Frontend development and integration are shared across the team according to the agreed feature allocation.

## 🧪 Testing

The project will use Postman to test backend endpoints, including:

- User registration and login.
- JWT authentication and role-based access.
- Profile management.
- Internship creation and retrieval.
- Saved internships and search.
- Skill matching and readiness.
- AI assistant responses.
- Administrative verification operations.

Frontend functionality will be tested in a browser as the features are implemented.

## 🚀 Development Status

**Current stage:** Initial project setup and development.

Features will be implemented and tested incrementally. The README will be updated as the application becomes functional.

## 🔮 Future Scope

Potential future improvements include:

- Resume analysis and skill extraction.
- Personalized internship recommendations.
- Email notifications for important deadlines.
- Advanced analytics for career preparation.
- Integration with additional internship data sources.

## 📄 License

This project is developed for educational purposes as part of a college project. A formal open-source license may be added if the project is released for broader use.

---

**GrowNet — Discover opportunities. Build skills. Grow your career.**`