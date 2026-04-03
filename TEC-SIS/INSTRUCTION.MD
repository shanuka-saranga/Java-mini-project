Project Initiation Guide: TEC-SIS

To ensure we meet all the requirements of ICT2132, please follow these steps to set up your environment and start coding.

1. Environment Setup
   Java SDK: Ensure you have JDK 21.
   IDE: You must use IntelliJ IDEA.
   Database: Install MySQL Server
   Version Control: Make sure you have Git installed and are added to our GitHub com.fot.system.repository.

2. Database Initialization

    Before coding, we must set up the shared database schema:
    Create a database named tec_sis_db.
    Run the initial SQL script (to be shared) to create tables for Users, Courses, Attendance, and Marks.
    Ensure every table has the correct primary and foreign keys to support Data Integrity.

3. Coding Guidelines (The "Strict" Rules)

    To get full marks for the Individual Evaluation, everyone must follow these:
    Encapsulation: All class variables in the com.fot.system.model package must be private. Use public getters and setters.
    Inheritance: If you are working on User profiles, inherit from the User base class.
    No SQL in UI: Do not write SQL queries inside the com.fot.system.view (GUI) classes. Use the com.fot.system.repository layer for that.
    Exception Handling: Wrap all database and input-parsing code in try-catch blocks to prevent the system from crashing.

4. Workflow for Contributions

   Don't push to com.fot.system.main: Create a new branch for your task (e.g., git checkout -b feature-attendance).
   Pull Requests: Once your task is done, push your branch and create a Pull Request (PR) on GitHub.
   Daily Updates: Please update the group on your progress to ensure we meet the March 1st deadline for the SRS.
