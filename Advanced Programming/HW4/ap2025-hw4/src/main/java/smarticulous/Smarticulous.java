package smarticulous;

import smarticulous.db.Exercise;
import smarticulous.db.Submission;
import smarticulous.db.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Smarticulous class, implementing a grading system.
 */
public class Smarticulous {

    /**
     * The connection to the underlying DB.
     * <p>
     * null if the db has not yet been opened.
     */
    Connection db;

    /**
     * Open the {@link Smarticulous} SQLite database.
     * <p>
     * This should open the database, creating a new one if necessary, and set the {@link #db} field
     * to the new connection.
     * <p>
     * The open method should make sure the database contains the following tables, creating them if necessary:
     *
     * <table>
     *   <caption><em>Table name: <strong>User</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>UserId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Username</td><td>Text</td></tr>
     *   <tr><td>Firstname</td><td>Text</td></tr>
     *   <tr><td>Lastname</td><td>Text</td></tr>
     *   <tr><td>Password</td><td>Text</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Exercise</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>DueDate</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Question</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>Desc</td><td>Text</td></tr>
     *   <tr><td>Points</td><td>Integer</td></tr>
     * </table>
     * In this table the combination of ExerciseId and QuestionId together comprise the primary key.
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Submission</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>UserId</td><td>Integer</td></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>SubmissionTime</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>QuestionGrade</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Grade</td><td>Real</td></tr>
     * </table>
     * In this table the combination of SubmissionId and QuestionId together comprise the primary key.
     *
     * @param dburl The JDBC url of the database to open (will be of the form "jdbc:sqlite:...")
     * @return the new connection
     * @throws SQLException
     */
    public Connection openDB(String dburl) throws SQLException {
        db = DriverManager.getConnection(dburl); //connect to the db with the filepath recieved.
        Statement st = db.createStatement();  // create the following tables if they don't exist already:
        st.executeUpdate("CREATE TABLE IF NOT EXISTS User (UserId INTEGER PRIMARY KEY, Username TEXT UNIQUE, Firstname TEXT, Lastname TEXT, Password TEXT);");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS Exercise (ExerciseId INTEGER PRIMARY KEY, Name TEXT, DueDate INTEGER);");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS Question (ExerciseId INTEGER, QuestionId INTEGER, Name TEXT, Desc TEXT, Points INTEGER, PRIMARY KEY (ExerciseId, QuestionId));");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS Submission (SubmissionId INTEGER PRIMARY KEY, UserId INTEGER, ExerciseId INTEGER, SubmissionTime INTEGER);");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS QuestionGrade (SubmissionId INTEGER, QuestionId INTEGER, Grade REAL, PRIMARY KEY (SubmissionId, QuestionId));");
        return db;
    }


    /**
     * Close the DB if it is open.
     *
     * @throws SQLException
     */
    public void closeDB() throws SQLException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    // =========== User Management =============

    /**
     * Add a user to the database / modify an existing user.
     * <p>
     * Add the user to the database if they don't exist. If a user with user.username does exist,
     * update their password and firstname/lastname in the database.
     *
     * @param user
     * @param password
     * @return the userid.
     * @throws SQLException
     */
    public int addOrUpdateUser(User user, String password) throws SQLException {
        PreparedStatement prst = db.prepareStatement("SELECT UserId FROM User WHERE Username = ?");
        prst.setString(1, user.username);  // adding to prepared statment for handling sql injection efforts
        ResultSet rs = prst.executeQuery();
        if(rs.next()){ // If the result set has a row, it means the name exists
            int userId = rs.getInt("UserId");
            PreparedStatement prstUpd = db.prepareStatement("UPDATE User SET Firstname = ?, Lastname = ?, Password = ? WHERE UserId = ?");
            prstUpd.setString(1, user.firstname); //handling sql injection 
            prstUpd.setString(2, user.lastname);
            prstUpd.setString(3, password);
            prstUpd.setInt(4, userId);
            prstUpd.executeUpdate();
            return userId;
        }else{  //if user not in table, create.
            PreparedStatement prstIn = db.prepareStatement("INSERT INTO User (Username, Firstname, Lastname, Password) VALUES (?, ?, ?, ?)");
            prstIn.setString(1, user.username); //handling sql injection 
            prstIn.setString(2, user.firstname);
            prstIn.setString(3, user.lastname); 
            prstIn.setString(4, password); 
            prstIn.executeUpdate();
            ResultSet genKey = prstIn.getGeneratedKeys(); // get the user id which was automaticaly generated
            return genKey.getInt(1);
        }
    }


    /**
     * Verify a user's login credentials.
     *
     * @param username
     * @param password
     * @return true if the user exists in the database and the password matches; false otherwise.
     * @throws SQLException
     * <p>
     * Note: this is totally insecure. For real-life password checking, it's important to store only
     * a password hash
     * @see <a href="https://crackstation.net/hashing-security.htm">How to Hash Passwords Properly</a>
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        PreparedStatement prst = db.prepareStatement("SELECT Password FROM User WHERE Username = ?");  // get the password stord on the name.
        prst.setString(1, username);
        ResultSet rs = prst.executeQuery();
        if (rs.next()) { // if the user has a password
            String userPassoword = rs.getString("Password");  //  keep the password.
            return userPassoword.equals(password);  // true if password saved and entered match.
        }
        return false; //return false if the username doesn't exist
    }

    // =========== Exercise Management =============

    /**
     * Add an exercise to the database.
     *
     * @param exercise
     * @return the new exercise id, or -1 if an exercise with this id already existed in the database.
     * @throws SQLException
     */
    public int addExercise(Exercise exercise) throws SQLException {
        PreparedStatement prst = db.prepareStatement("SELECT ExerciseId FROM Exercise WHERE ExerciseId = ?");
        prst.setInt(1, exercise.id);
        if (prst.executeQuery().next()) {   //if an exercise with the same ID exists
            return -1; // Exercise with this ID already exists
        }
        //get to here if exercise doesn't exist
        // we need to add it to exercise table and question table
        PreparedStatement prstex = db.prepareStatement("INSERT INTO Exercise (ExerciseId, Name, DueDate) VALUES (?, ?, ?)");
        prstex.setInt(1, exercise.id);
        prstex.setString(2, exercise.name);
        prstex.setLong(3, exercise.dueDate.getTime());
        prstex.executeUpdate(); // add to exercise table

        PreparedStatement prstqu = db.prepareStatement("INSERT INTO Question (ExerciseId, QuestionId, Name, Desc, Points) VALUES (?, ?, ?, ?, ?)");
        for (int i = 0 ; i < exercise.questions.size(); i++) { // run on all questions in the exercise
            Exercise.Question question = exercise.questions.get(i); // get questions from list of questions.
            prstqu.setInt(1, exercise.id);
            prstqu.setInt(2, i+1); // index the questions in the exercise 
            prstqu.setString(3, question.name);
            prstqu.setString(4, question.desc);
            prstqu.setInt(5, question.points);
            prstqu.addBatch(); // Adds the current prepared statement
        }
            prstqu.executeBatch(); // add all questions to question table.
        
        return exercise.id;
    }


    /**
     * Return a list of all the exercises in the database.
     * <p>
     * The list should be sorted by exercise id.
     *
     * @return list of all exercises.
     * @throws SQLException
     */
    public List<Exercise> loadExercises() throws SQLException {
        List<Exercise> exercises = new ArrayList<>(); //the list we return
        Statement st = db.createStatement();  // regular statment, no need prepared : only recieving data without editing queries
        ResultSet rs = st.executeQuery("SELECT ExerciseId, Name, DueDate FROM Exercise ORDER BY ExerciseId");
        while (rs.next()) { // while there are more exercises
            int exerciseId = rs.getInt("ExerciseId"); 
            Exercise exercise = new Exercise(exerciseId, rs.getString("Name"), new Date(rs.getLong("DueDate"))); // create exercise with the values from table
            Statement stqu = db.createStatement();
            ResultSet rsqu = stqu.executeQuery("SELECT QuestionId, Name, Desc, Points FROM Question WHERE ExerciseId = " + exerciseId + " ORDER BY QuestionId"); // get current exercises questions
            while (rsqu.next()) { // while there is another question
                exercise.addQuestion(rsqu.getString("Name"),rsqu.getString("Desc"),rsqu.getInt("Points")); //add the current question in current exercise
            }
            exercises.add(exercise);
        }
    
        return exercises;
    }

    // ========== Submission Storage ===============

    /**
     * Store a submission in the database.
     * The id field of the submission will be ignored if it is -1.
     * <p>
     * Return -1 if the corresponding user doesn't exist in the database.
     *
     * @param submission
     * @return the submission id.
     * @throws SQLException
     */
    public int storeSubmission(Submission submission) throws SQLException {
        PreparedStatement prst = db.prepareStatement("SELECT UserId FROM User WHERE Username = ?"); //for recieving userId with name
        prst.setString(1, submission.user.username); //get submission user id
        ResultSet rs = prst.executeQuery(); 
        if (!rs.next()) return -1; // User not found
        int userId = rs.getInt("UserId");
        prst = db.prepareStatement("INSERT INTO Submission (UserId, ExerciseId, SubmissionTime) VALUES (?, ?, ?)");
        prst.setInt(1, userId);
        prst.setInt(2, submission.exercise.id);
        prst.setLong(3, submission.submissionTime.getTime());
        prst.executeUpdate();
        rs = prst.getGeneratedKeys(); //get the submission.id generated automaticallty if new 
        int submissionId = rs.next() ? rs.getInt(1) : -1; // if there is an id
        prst = db.prepareStatement("INSERT INTO QuestionGrade (SubmissionId, QuestionId, Grade) VALUES (?, ?, ?)");
        for (int i = 0; i < submission.questionGrades.length; i++) {
            prst.setInt(1, submissionId);
            prst.setInt(2, i + 1);  // Assuming question IDs are 1-based
            prst.setFloat(3, submission.questionGrades[i]);
            prst.addBatch();
        }
        prst.executeBatch();
        return submissionId;
    }


    // ============= Submission Query ===============


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the latest submission for the given exercise by the given user.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getLastSubmission(User, Exercise)}
     *
     * @return
     */
    PreparedStatement getLastSubmissionGradesStatement() throws SQLException {

        String lastSubmissionGrade = "SELECT s.SubmissionId, qg.QuestionId, qg.Grade, s.SubmissionTime " + //take the parameters from submission table and question grade table joined as requested.
        "FROM Submission s JOIN QuestionGrade qg ON s.SubmissionId = qg.SubmissionId " +  // submission and questiongrade tables joined by the same id
        "WHERE s.UserId = (SELECT UserId FROM User WHERE Username = ?) AND s.ExerciseId = ? " +  // check for a user given in the prepared statement, get the id by entering username (par1) and select submissions that belong to a specific exercise (par 2)
        "ORDER BY s.SubmissionTime DESC, qg.QuestionId LIMIT ?"; //get the most recent by time, and only a number of questions (par3)

        return db.prepareStatement(lastSubmissionGrade);
    }

    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the <i>best</i> submission for the given exercise by the given user.
     * The best submission is the one whose point total is maximal.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getBestSubmission(User, Exercise)}
     *
     */
    PreparedStatement getBestSubmissionGradesStatement() throws SQLException {
    return db.prepareStatement("");
    }

    /**
     * Return a submission for the given exercise by the given user that satisfies
     * some condition (as defined by an SQL prepared statement).
     * <p>
     * The prepared statement should accept the user name as parameter 1, the exercise id as parameter 2 and a limit on the
     * number of rows returned as parameter 3, and return a row for each question corresponding to the submission, sorted by questionId.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @param stmt
     * @return
     * @throws SQLException
     */
    Submission getSubmission(User user, Exercise exercise, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, user.username);
        stmt.setInt(2, exercise.id);
        stmt.setInt(3, exercise.questions.size());

        ResultSet res = stmt.executeQuery();

        boolean hasNext = res.next();
        if (!hasNext)
            return null;

        int sid = res.getInt("SubmissionId");
        Date submissionTime = new Date(res.getLong("SubmissionTime"));

        float[] grades = new float[exercise.questions.size()];

        for (int i = 0; hasNext; ++i, hasNext = res.next()) {
            grades[i] = res.getFloat("Grade");
        }

        return new Submission(sid, user, exercise, submissionTime, (float[]) grades);
    }

    /**
     * Return the latest submission for the given exercise by the given user.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     * @throws SQLException
     */
    public Submission getLastSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getLastSubmissionGradesStatement());
    }


    /**
     * Return the submission with the highest total grade
     *
     * @param user the user for which we retrieve the best submission
     * @param exercise the exercise for which we retrieve the best submission
     * @return
     * @throws SQLException
     */
    public Submission getBestSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getBestSubmissionGradesStatement());
    }
}
