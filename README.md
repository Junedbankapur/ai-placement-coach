# 🤖 AI Placement Coach & Recruiter Matching Agent

An interactive full-stack web application designed to help software engineering graduates prepare for placements, audit their resumes, and apply for tailored corporate opportunities. 

---

## 📝 About the Project (In Simple Words)

Preparing for placements can be stressful. This application acts as a personal **AI Career Assistant** and **Corporate Job Board** to make your preparation seamless. 

Here is what the application does:
*   **📊 Performance Dashboard**: Displays your interview preparation progress over time using interactive visual charts.
*   **📄 Resume Analyzer**: Allows you to upload your resume, extracts your technical skills and projects, and lists your strengths and weaknesses.
*   **💼 Placement Board**: Scans available corporate job openings at top firms (like Oracle, Capgemini, and Accenture) tailored to your CV, calculates your ATS Match Score (%), highlights missing keywords, and automatically drafts personalized cover letters.
*   **⏱️ Mock Interview Arena**: Lets you take simulated placement interviews under a timed countdown. Once submitted, the AI recruiter grades all your answers and gives you detailed feedback on how to improve.
*   **🔑 Secure Accounts**: Secure login and registration using stateless JWT security to keep your profile protected.
*   **🛡️ Smart Offline Mode**: The application is built to run smoothly in any environment. If the AI key is offline, it transparently loads local high-fidelity mock questions and jobs so the app stays 100% interactive.

---

## 🛠️ Technologies Used

*   **Backend**: Spring Boot, Java, Spring Security, Hibernate (JPA), MySQL, Maven
*   **Frontend**: React.js, Vite, Chart.js, HTML5, CSS3 (Glassmorphism)
*   **AI Engine**: Google Gemini 1.5 Flash (via REST Integration)

---

## 🚀 How to Run the Project (Installation Process)

### 1. Database & Config Setup (Backend)
1.  Navigate to the `backend` directory:
    ```bash
    cd backend
    ```
2.  Open `src/main/resources/application.properties` and add your local MySQL database details:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/ai_interview_coach?useSSL=false
    spring.datasource.username=YOUR_MYSQL_USERNAME
    spring.datasource.password=YOUR_MYSQL_PASSWORD
    gemini.api.key=YOUR_GEMINI_API_KEY
    ```
3.  Start your MySQL database server.
4.  Run the application inside **STS (Spring Tool Suite)** by right-clicking the backend folder and choosing **Run As** ──> **Spring Boot App**.

### 2. Frontend Setup (React)
1.  Open a terminal and navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  Install the required packages:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
4.  Open `http://localhost:3000` in your web browser and start preparing!
