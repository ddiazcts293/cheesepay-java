package com.axolutions.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;

import com.axolutions.db.query.payment.*;
import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;

/**
 * Reprenta la clase que se encarga de manejar la conexión con la base de datos
 * y la realización de consultas para obtener o manipular información.
 */
public class DbContext
{
    private DbConnectionWrapper wrapper;

    /**
     * Crea un uno objeto DbContext
     * @param dbConnectionWrapper Envoltorio de la conexión a la base de datos.
     */
    public DbContext(DbConnectionWrapper dbConnectionWrapper)
    {
        this.wrapper = dbConnectionWrapper;
    }

    /**
     * Obtiene un valor que indica si el sistema está conectado a la base de 
     * datos.
     * @return
     */
    public boolean isConnected()
    {
        return getConnection() != null; //&& dbConnection.isValid(0);
    }

    /**
     * Obtiene el objeto de conexión con la base de datos.
     * @return Objeto Connection
     */
    private Connection getConnection()
    {
        return wrapper.getConnection();
    }

    /* Consultas relacionas con un alumno */

    public Student getStudent(String studentId) throws SQLException
    {
        Student studentFound = null;

        String sqlQuery = "SELECT " +
            "a.matricula AS studentId, " +
            "a.nombre AS name, " +
            "a.primerApellido AS firstSurname, " +
            "a.segundoApellido AS lastSurname, " +
            "a.genero AS gender, " +
            "a.edad AS age, " +
            "a.fechaNacimiento AS dateOfBirth, " +
            "a.domicilioCalle AS addressStreet, " +
            "a.domicilioNumero AS addressNumber, " +
            "a.domicilioColonia AS addressDistrict, " +
            "a.domicilioCP AS addressPostalCode, " +
            "a.curp AS curp, " +
            "a.nss AS ssn " +
            "FROM alumnos AS a " +
            "WHERE matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        if (resultSet.next())
        {
            studentFound = new Student();
            studentFound.studentId = resultSet.getString("studentId");
            studentFound.name = resultSet.getString("name");
            studentFound.firstSurname = resultSet.getString("firstSurname");
            studentFound.lastSurname = resultSet.getString("lastSurname");
            studentFound.gender = resultSet.getString("gender");
            studentFound.age = resultSet.getInt("age");
            studentFound.dateOfBirth = resultSet.getDate("dateOfBirth").toLocalDate();
            studentFound.addressStreet = resultSet.getString("addressStreet");
            studentFound.addressNumber = resultSet.getString("addressNumber");
            studentFound.addressDistrict = resultSet.getString("addressDistrict");
            studentFound.addressPostalCode = resultSet.getString("addressPostalCode");
            studentFound.curp = resultSet.getString("curp");
            studentFound.ssn = resultSet.getString("ssn");
        }

        return studentFound;
    }

    public Tutor[] getStudentTutors(String studentId) throws SQLException
    {
        ArrayList<Tutor> list = new ArrayList<>();

        String sqlQuery = "SELECT  " +
            "t.numero AS number, " +
            "t.nombre AS name, " +
            "t.primerApellido AS firstSurname, " +
            "t.segundoApellido AS lastSurname, " +
            "t.parentesco AS kinship, " +
            "t.correoElectronico AS email, " +
            "t.rfc AS rfc " +
            "FROM tutores AS t " +
            "INNER JOIN tutores_alumnos AS ta ON ta.tutor = t.numero " +
            "INNER JOIN alumnos AS a ON ta.alumno = a.matricula " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var tutorFound = new Tutor();
            tutorFound.number = resultSet.getInt("number");
            tutorFound.name = resultSet.getString("name");
            tutorFound.firstSurname = resultSet.getString("firstSurname");
            tutorFound.lastSurname = resultSet.getString("lastSurname");
            tutorFound.kinship = resultSet.getString("kinship");
            tutorFound.email = resultSet.getString("email");
            tutorFound.rfc = resultSet.getString("rfc");

            var phones = getTutorPhones(tutorFound.number);
            for (var phone : phones)
            {
                tutorFound.phones.add(phone);
            }

            list.add(tutorFound);
        }

        Tutor[] array = new Tutor[list.size()];
        list.toArray(array);
        return array;
    }

    public Group[] getStudentGroups(String studentId) throws SQLException
    {
        ArrayList<Group> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "g.numero AS number, " +
            "g.grado AS grade, " +
            "g.letra AS letter, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS description " +
            "FROM grupos AS g " +
            "INNER JOIN ciclos_escolares AS ce ON g.ciclo = ce.codigo " +
            "INNER JOIN grupos_alumnos AS ga ON g.numero = ga.grupo " +
            "INNER JOIN alumnos AS a ON ga.alumno = a.matricula " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "WHERE a.matricula = ? " +
            "ORDER BY g.grado DESC";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var groupFound = new Group();
            groupFound.number = resultSet.getInt("number");
            groupFound.grade = resultSet.getInt("grade");
            groupFound.letter = resultSet.getString("letter");
            groupFound.period.code = resultSet.getString("period");
            groupFound.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            groupFound.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            groupFound.level.code = resultSet.getString("level");
            groupFound.level.description = resultSet.getString("description");

            list.add(groupFound);
        }

        Group[] array = new Group[list.size()];
        list.toArray(array);
        return array;
    }

    public TutorPhone[] getTutorPhones(int tutorNumber) throws SQLException
    {
        ArrayList<TutorPhone> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "tt.numero AS id, " +
            "tt.numeroTelefono AS phone " +
            "FROM tutores AS t " +
            "INNER JOIN tutor_telefonos AS tt ON tt.tutor = t.numero " +
            "WHERE t.numero = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setInt(1, tutorNumber);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var phone = new TutorPhone();
            phone.id = resultSet.getInt("id");
            phone.phone = resultSet.getString("phone");
            list.add(phone);
        }

        TutorPhone[] array = new TutorPhone[list.size()];
        list.toArray(array);
        return array;
    }

    public Payment[] getStudentPayments(String studentId) throws SQLException
    {
        ArrayList<Payment> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "p.folio AS folio, " +
            "p.fecha AS date, " +
            "p.montoTotal AS totalAmount, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate " +
            "FROM pagos AS p " +
            "INNER JOIN alumnos AS a ON p.alumno = a.matricula " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "WHERE a.matricula = ? " +
            "GROUP BY p.folio";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var payment = new Payment();
            payment.folio = resultSet.getInt("folio");
            payment.date = resultSet.getDate("date").toLocalDate();
            payment.totalAmount = resultSet.getFloat("totalAmount");
            payment.period.code = resultSet.getString("period");
            payment.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            payment.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            list.add(payment);
        }

        Payment[] array = new Payment[list.size()];
        list.toArray(array);
        return array;
    }

    public Student[] getGroupStudents(Group group) throws SQLException
    {
        ArrayList<Student> list = new ArrayList<>();
        String sqlString = "SELECT " +
            "a.matricula AS studentId, " +
            "a.nombre AS name, " +
            "a.primerApellido AS firstSurname, " +
            "a.segundoApellido AS lastSurname, " +
            "a.genero AS gender, " +
            "a.edad AS age, " +
            "a.fechaNacimiento AS dateOfBirth, " +
            "a.domicilioCalle AS addressStreet, " +
            "a.domicilioNumero AS addressNumber, " +
            "a.domicilioColonia AS addressDistrict, " +
            "a.domicilioCP AS addressPostalCode, " +
            "a.curp AS curp, " +
            "a.nss AS ssn, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM alumnos AS a " +
            "INNER JOIN grupos_alumnos AS ga ON ga.alumno = a.matricula " +
            "INNER JOIN grupos AS g ON ga.grupo = g.numero " +
            "INNER JOIN ciclos_escolares AS ce ON ce.codigo = g.ciclo " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "WHERE g.numero = ?";

        var statement = getConnection().prepareStatement(sqlString);
        statement.setInt(1, group.number);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            Student student = new Student();
            student.studentId = resultSet.getString("studentId");
            student.name = resultSet.getString("name");
            student.firstSurname = resultSet.getString("firstSurname");
            student.lastSurname = resultSet.getString("lastSurname");
            student.gender = resultSet.getString("gender");
            student.age = resultSet.getInt("age");
            student.dateOfBirth = resultSet.getDate("dateOfBirth").toLocalDate();
            student.addressStreet = resultSet.getString("addressStreet");
            student.addressNumber = resultSet.getString("addressNumber");
            student.addressDistrict = resultSet.getString("addressDistrict");
            student.addressPostalCode = resultSet.getString("addressPostalCode");
            student.curp = resultSet.getString("curp");
            student.ssn = resultSet.getString("ssn");
            student.period.code = resultSet.getString("period");
            student.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            student.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            student.level.code = resultSet.getString("level");
            student.level.description = resultSet.getString("levelDescription");

            list.add(student);
        }

        Student[] array = new Student[list.size()];
        list.toArray(array);
        return array;
    }

    /* Consultas generales */

    public EducationLevel[] getEducationLevels() throws SQLException
    {
        ArrayList<EducationLevel> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "ne.codigo AS code, " +
            "ne.descripcion AS description " +
            "FROM niveles_educativos AS ne";

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next())
        {
            EducationLevel level = new EducationLevel();
            level.code = resultSet.getString("code");
            level.description = resultSet.getString("description");

            list.add(level);
        }

        EducationLevel[] array = new EducationLevel[list.size()];
        list.toArray(array);
        return array;
    }

    public Group[] getGroups(String period, String level) throws SQLException
    {
        ArrayList<Group> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "g.numero as number, " +
            "g.grado AS grade, " +
            "g.letra AS letter, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "ne.codigo AS level, " +
            "ne.descripcion as description, " +
            "COUNT(ga.alumno) AS studentCount " +
            "FROM grupos AS g " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON g.ciclo = ce.codigo " +
            "INNER JOIN grupos_alumnos AS ga ON ga.grupo = g.numero " +
            "WHERE ne.codigo = ? AND ce.codigo = ? " +
            "GROUP BY g.ciclo, g.numero";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, level);
        statement.setString(2, period);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            Group group = new Group();
            group.number = resultSet.getInt("number");
            group.grade = resultSet.getInt("grade");
            group.letter = resultSet.getString("letter");
            group.period.code = resultSet.getString("period");
            group.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            group.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            group.level.code = resultSet.getString("level");
            group.level.description = resultSet.getString("description");
            group.studentCount = resultSet.getInt("studentCount");

            list.add(group);
        }

        Group[] array = new Group[list.size()];
        list.toArray(array);
        return array;
    }

    public SchoolPeriod[] getScholarPeriods() throws SQLException
    {
        ArrayList<SchoolPeriod> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "ce.codigo AS code, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate " +
            "FROM ciclos_escolares AS ce";

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next())
        {
            SchoolPeriod period = new SchoolPeriod();
            period.code = resultSet.getString("code");
            period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            period.endingDate = resultSet.getDate("endingDate").toLocalDate();

            list.add(period);
        }

        SchoolPeriod[] array = new SchoolPeriod[list.size()];
        list.toArray(array);
        return array;
    }

    /* Consultas de búsqueda */

    public Tutor[] searchForTutors(String string) throws SQLException
    {
        ArrayList<Tutor> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "t.numero as number, " +
            "t.nombre AS name, " +
            "t.primerApellido AS firstSurname, " +
            "t.segundoApellido AS lastSurname, " +
            "t.parentesco AS kinship, " +
            "t.correoElectronico AS email, " +
            "t.rfc AS rfc " +
            "FROM tutores AS t " +
            "WHERE " +
            "t.rfc LIKE ? OR " +
            "t.nombre LIKE ? OR " +
            "t.primerApellido LIKE ? OR " +
            "t.correoElectronico LIKE ? ";

        string = "%" + string + "%";
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, string);
        statement.setString(2, string);
        statement.setString(3, string);
        statement.setString(4, string);

        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var tutorFound = new Tutor();
            tutorFound.number = resultSet.getInt("number");
            tutorFound.name = resultSet.getString("name");
            tutorFound.firstSurname = resultSet.getString("firstSurname");
            tutorFound.lastSurname = resultSet.getString("lastSurname");
            tutorFound.kinship = resultSet.getString("kinship");
            tutorFound.email = resultSet.getString("email");
            tutorFound.rfc = resultSet.getString("rfc");

            var phones = getTutorPhones(tutorFound.number);
            for (TutorPhone phone : phones)
            {
                tutorFound.phones.add(phone);
            }

            list.add(tutorFound);
        }

        Tutor[] array = new Tutor[list.size()];
        list.toArray(array);
        return array;
    }

    public Student[] searchForStudents(String string) throws SQLException
    {
        ArrayList<Student> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "a.matricula AS studentId, " +
            "a.nombre AS name, " +
            "a.primerApellido AS firstSurname, " +
            "a.segundoApellido AS lastSurname, " +
            "a.genero AS gender, " +
            "a.edad AS age, " +
            "a.fechaNacimiento AS dateOfBirth, " +
            "a.domicilioCalle AS addressStreet, " +
            "a.domicilioNumero AS addressNumber, " +
            "a.domicilioColonia AS addressDistrict, " +
            "a.domicilioCP AS addressPostalCode, " +
            "a.curp AS curp, " +
            "a.nss AS ssn " +
            "FROM alumnos AS a WHERE " +
            "a.nombre LIKE ? or " +
            "a.primerApellido LIKE ? or " +
            "a.segundoApellido LIKE ? OR " +
            "a.curp LIKE ?";

        string = "%" + string + "%";
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, string);
        statement.setString(2, string);
        statement.setString(3, string);
        statement.setString(4, string);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var studentFound = new Student();
            studentFound.studentId = resultSet.getString("studentId");
            studentFound.name = resultSet.getString("name");
            studentFound.firstSurname = resultSet.getString("firstSurname");
            studentFound.lastSurname = resultSet.getString("lastSurname");
            studentFound.gender = resultSet.getString("gender");
            studentFound.age = resultSet.getInt("age");
            studentFound.dateOfBirth = resultSet.getDate("dateOfBirth").toLocalDate();
            studentFound.addressStreet = resultSet.getString("addressStreet");
            studentFound.addressNumber = resultSet.getString("addressNumber");
            studentFound.addressDistrict = resultSet.getString("addressDistrict");
            studentFound.addressPostalCode = resultSet.getString("addressPostalCode");
            studentFound.curp = resultSet.getString("curp");
            studentFound.ssn = resultSet.getString("ssn");

            list.add(studentFound);
        }

        Student[] array = new Student[list.size()];
        list.toArray(array);
        return array;
    }

    /* Consultas de registro/actualización de tutores y alumnos */

    /**
     * Realiza el registro de un alumno.
     * @param student Objeto con la información de alumno
     * @throws SQLException
     */
    public void registerStudent(Student student) throws SQLException
    {
        // Consulta 1. Obtiene el valor máximo asignado para una matricula
        String sqlQuery1 = "SELECT MAX(CAST(a.matricula AS INT)) " +
            "FROM alumnos AS a";

        // Consulta 2. Inserta registro de alumno
        String sqlQuery2 = "INSERT INTO alumnos " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        // Paso 1: Generar una nueva matricula para el alumno
        var statement1 = getConnection().createStatement();
        var resultSet = statement1.executeQuery(sqlQuery1);
        if (resultSet.next())
        {
            // Formatea el valor obtenido y lo asigna como la matricula
            student.studentId = String.format("%05d", resultSet.getInt(1) + 1);
        }

        // Paso 2: Registrar un nuevo alumno añadiendo toda la información
        var statement2 = getConnection().prepareStatement(sqlQuery2);
        statement2.setString(1, student.studentId);
        statement2.setString(2, student.name);
        statement2.setString(3, student.firstSurname);
        statement2.setString(4, student.lastSurname);
        statement2.setString(5, student.gender);
        statement2.setInt(6, student.age);
        statement2.setDate(7, Date.valueOf(student.dateOfBirth));
        statement2.setString(8, student.addressStreet);
        statement2.setString(9, student.addressNumber);
        statement2.setString(10, student.addressDistrict);
        statement2.setString(11, student.addressPostalCode);
        statement2.setString(12, student.curp);

        // Verifica si se estableció un número de seguro social
        if (student.ssn != null && !student.ssn.isBlank())
        {
            statement2.setString(13,  student.studentId);
        }
        else
        {
            statement2.setNull(13, java.sql.Types.NULL);
        }

        // Ejecuta la consulta
        statement2.executeUpdate();
    }

    /**
     * Realiza la asociación de un alumno y un tutor.
     * @param student Objeto con la información de un alumno
     * @param tutor Objeto con la información de un tutor
     * @throws SQLException
     */
    public void registerStudentWithTutor(Student student, Tutor tutor)
        throws SQLException
    {
        // Define una consulta para insertar un registro
        String sqlQuery = "INSERT INTO tutores_alumnos VALUES (?,?)";

        // Realiza la consulta para asociar un tutor con un alumno
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setInt(1, tutor.number);
        statement.setString(2, student.studentId);
        statement.executeUpdate();
    }

    public void registerTutor(Tutor tutor) throws SQLException
    {
        String sqlQuery = "INSERT INTO tutores VALUES (DEFAULT,?,?,?,?,?,?)";
        var statement = getConnection().prepareStatement(
            sqlQuery,
            java.sql.Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, tutor.name);
        statement.setString(2, tutor.firstSurname);
        statement.setString(3, tutor.lastSurname);
        statement.setString(4, tutor.email);
        statement.setString(5, tutor.rfc);
        statement.setString(6, tutor.kinship);

        statement.executeUpdate();
        var resultSet = statement.getGeneratedKeys();

        if (resultSet.next())
        {
            tutor.number = resultSet.getInt(1);
        }
    }

    public void updateStudentInfo(Student student) throws SQLException
    {
        String sqlQuery = "UPDATE alumnos " +
            "SET genero = ?, domicilioCalle = ?, domicilioNumero = ?, " +
            "domicilioColonia = ?, domicilioCP = ? " +
            "WHERE matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, student.gender);
        statement.setString(2, student.addressStreet);
        statement.setString(3, student.addressNumber);
        statement.setString(4, student.addressDistrict);
        statement.setString(5, student.addressPostalCode);
        statement.setString(6, student.studentId);

        statement.executeUpdate();
    }

    public void updateTutorEmail(Tutor tutor) throws SQLException
    {
        String sqlQuery = "UPDATE tutores " +
            "SET correoElectronico = ? " +
            "WHERE numero = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, tutor.email);
        statement.setInt(2, tutor.number);

        statement.executeUpdate();
    }

    public void addTutorPhone(Tutor tutor, TutorPhone phone) throws SQLException
    {
        String sqlQuery = "INSERT INTO tutor_telefonos VALUES (DEFAULT, ?, ?)";
        var statement = getConnection().prepareStatement(
            sqlQuery,
            java.sql.Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, phone.phone);
        statement.setInt(2, tutor.number);

        statement.executeUpdate();
        var resultSet = statement.getGeneratedKeys();

        if (resultSet.next())
        {
            phone.id = resultSet.getInt(1);
        }
    }

    public void deleteTutorPhone(TutorPhone phone) throws SQLException
    {
        String sqlQuery = "DELETE FROM tutor_telefonos WHERE numero = ?";
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setInt(1, phone.id);

        statement.executeUpdate();
    }

    /* Consultas de cobros */

    public EnrollmentFee getEnrollmentFee(String periodCode, String levelCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "i.codigo AS enrollmentId, " +
            "i.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN inscripciones AS i ON c.inscripcion = i.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON i.nivel = ne.codigo " +
            "WHERE ce.codigo = ? AND ne.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        statement.setString(2, levelCode);
        var resultSet = statement.executeQuery();
        EnrollmentFee fee = null;

        if (resultSet.next())
        {
            fee = new EnrollmentFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.enrollmentCode = resultSet.getString("enrollmentId");
            fee.cost = resultSet.getFloat("cost");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");
        }

        return fee;
    }

    public MonthlyFee getMonthlyFee(String periodCode, String levelCode, LocalDate date) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "m.codigo AS monthlyCode, " +
            "m.fechaLimite AS dueDate, " +
            "m.mesVacacional AS isVacationMonth, " +
            "m.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN mensualidades AS m ON c.mensualidad = m.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON m.nivel = ne.codigo " +
            "WHERE ce.codigo = ? AND ne.codigo = ? AND m.fechaLimite >= ? " +
            "ORDER BY dueDate LIMIT 1";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        statement.setString(2, levelCode);
        statement.setDate(3, Date.valueOf(date));

        var resultSet = statement.executeQuery();
        MonthlyFee fee = null;

        if (resultSet.next())
        {
            fee = new MonthlyFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.monthlyCode = resultSet.getString("monthlyCode");
            fee.dueDate = resultSet.getDate("dueDate").toLocalDate();
            fee.isVacationMonth = resultSet.getBoolean("isVacationMonth");
            fee.cost = resultSet.getFloat("cost");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");
        }

        return fee;
    }

    public StationeryFee getStationeryFee(String periodCode, String levelCode, int grade) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "p.codigo AS stationeryCode, " +
            "p.concepto AS concept, " +
            "p.grado AS grade, " +
            "p.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN papeleria AS p ON c.papeleria = p.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON p.nivel = ne.codigo " +
            "WHERE ce.codigo = ? AND ne.codigo = ? AND p.grado = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        statement.setString(2, levelCode);
        statement.setInt(3, grade);

        var resultSet = statement.executeQuery();
        StationeryFee fee = null;

        while (resultSet.next())
        {
            fee = new StationeryFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.starioneryCode = resultSet.getString("stationeryCode");
            fee.concept = resultSet.getString("concept");
            fee.grade = resultSet.getInt("grade");
            fee.cost = resultSet.getFloat("cost");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");
        }

        return fee;
    }

    public EnrollmentFee[] getEnrollmentFees(String periodCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "i.codigo AS enrollmentId, " +
            "i.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN inscripciones AS i ON c.inscripcion = i.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON i.nivel = ne.codigo " +
            "WHERE ce.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        var resultSet = statement.executeQuery();
        ArrayList<EnrollmentFee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new EnrollmentFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.enrollmentCode = resultSet.getString("enrollmentId");
            fee.cost = resultSet.getFloat("cost");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        // Convierte la lista de elementos a un arreglo
        EnrollmentFee[] array = new EnrollmentFee[list.size()];
        list.toArray(array);
        return array;
    }

    public MonthlyFee[] getMonthlyFees(String periodCode, String levelCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "m.codigo AS monthlyCode, " +
            "m.fechaLimite AS dueDate, " +
            "m.mesVacacional AS isVacationMonth, " +
            "m.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN mensualidades AS m ON c.mensualidad = m.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON m.nivel = ne.codigo " +
            "WHERE ce.codigo = ? AND ne.codigo = ? " +
            "ORDER BY dueDate";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        statement.setString(2, levelCode);

        var resultSet = statement.executeQuery();
        ArrayList<MonthlyFee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new MonthlyFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.monthlyCode = resultSet.getString("monthlyCode");
            fee.dueDate = resultSet.getDate("dueDate").toLocalDate();
            fee.isVacationMonth = resultSet.getBoolean("isVacationMonth");
            fee.cost = resultSet.getFloat("cost");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        MonthlyFee[] array = new MonthlyFee[list.size()];
        list.toArray(array);
        return array;
    }

    public StationeryFee[] getStationeryFees(String periodCode, String levelCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "p.codigo AS stationeryCode, " +
            "p.concepto AS concept, " +
            "p.grado AS grade, " +
            "p.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN papeleria AS p ON c.papeleria = p.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON p.nivel = ne.codigo " +
            "WHERE ce.codigo = ? AND ne.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        statement.setString(2, levelCode);

        var resultSet = statement.executeQuery();
        ArrayList<StationeryFee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new StationeryFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.starioneryCode = resultSet.getString("stationeryCode");
            fee.concept = resultSet.getString("concept");
            fee.grade = resultSet.getInt("grade");
            fee.cost = resultSet.getFloat("cost");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        StationeryFee[] array = new StationeryFee[list.size()];
        list.toArray(array);
        return array;
    }

    public UniformFee[] getUniformFees(String periodCode, String levelCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "u.codigo AS uniformCode, " +
            "u.concepto AS concept, " +
            "u.talla AS size, " +
            "u.costo AS cost, " +
            "tu.numero AS uniformTypeId, " +
            "tu.descripcion AS uniformType, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN uniformes AS u ON c.uniforme = u.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON u.nivel = ne.codigo " +
            "INNER JOIN tipos_uniforme AS tu ON u.tipo = tu.numero " +
            "WHERE ce.codigo = ? AND ne.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);
        statement.setString(2, levelCode);

        var resultSet = statement.executeQuery();
        ArrayList<UniformFee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new UniformFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.uniformCode = resultSet.getString("uniformCode");
            fee.concept = resultSet.getString("concept");
            fee.size = resultSet.getString("size");
            fee.cost = resultSet.getFloat("cost");
            fee.type.number = resultSet.getInt("uniformTypeId");
            fee.type.description = resultSet.getString("uniformType");
            fee.level.code = resultSet.getString("level");
            fee.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        UniformFee[] array = new UniformFee[list.size()];
        list.toArray(array);
        return array;
    }

    public SpecialEventFee[] getSpecialEventFees(String periodCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "ee.codigo AS specialEventCode, " +
            "ee.concepto AS concept, " +
            "ee.fechaProgramada AS scheduledDate, " +
            "ee.costo AS cost " +
            "FROM cobros AS c " +
            "INNER JOIN eventos_especiales AS ee ON c.eventoEspecial = ee.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "WHERE ce.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);

        var resultSet = statement.executeQuery();
        ArrayList<SpecialEventFee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new SpecialEventFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.specialEventCode = resultSet.getString("specialEventCode");
            fee.concept = resultSet.getString("concept");
            fee.scheduledDate = resultSet.getDate("scheduledDate").toLocalDate();
            fee.cost = resultSet.getFloat("cost");
            list.add(fee);
        }

        SpecialEventFee[] array = new SpecialEventFee[list.size()];
        list.toArray(array);
        return array;
    }

    public MaintenanceFee getMaintenanceFee(String periodCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS code, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "m.numero AS maintenanceNumber, " +
            "m.concepto AS concept, " +
            "m.costo AS cost " +
            "FROM cobros AS c " +
            "INNER JOIN mantenimiento AS m ON c.mantenimiento = m.numero " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "WHERE ce.codigo = ? " +
            "LIMIT 1"; // Solo se requiere de un elemento

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, periodCode);

        var resultSet = statement.executeQuery();
        MaintenanceFee fee = null;

        if (resultSet.next())
        {
            fee = new MaintenanceFee();
            fee.code = resultSet.getString("code");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.maintenanceNumber = resultSet.getInt("maintenanceNumber");
            fee.concept = resultSet.getString("concept");
            fee.cost = resultSet.getFloat("cost");
        }

        return fee;
    }

    /* Consultas de pagos */

    /**
     * Obtiene las inscripciones pagadas por un alumno.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public PaidEnrollment[] getPaidEnrollmentFees(String studentId) throws SQLException
    {
        ArrayList<PaidEnrollment> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "i.costo AS cost, " +
            "ce.codigo AS periodCode, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName, " +
            "t.numero AS tutorNumber, " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName, " +
            "c.codigo AS code, " +
            "ne.codigo AS educationLevelCode, " +
            "ne.descripcion AS educationLevelDescription " +
            "FROM alumnos AS a " +
            "INNER JOIN pagos AS p ON a.matricula = p.alumno " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN inscripciones AS i ON i.codigo = c.inscripcion " +
            "INNER JOIN niveles_educativos AS ne ON i.nivel = ne.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN tutores AS t ON p.tutor = t.numero " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var paidFee = new PaidEnrollment();
            paidFee.paymentFolio = resultSet.getInt("paymentFolio");
            paidFee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            paidFee.cost = resultSet.getFloat("cost");
            paidFee.period.code = resultSet.getString("periodCode");
            paidFee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            paidFee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            paidFee.studentId = resultSet.getString("studentId");
            paidFee.studentName = resultSet.getString("studentName");
            paidFee.tutorNumber = resultSet.getInt("tutorNumber");
            paidFee.tutorName = resultSet.getString("tutorName");
            paidFee.level.code = resultSet.getString("educationLevelCode");
            paidFee.level.description = resultSet.getString("educationLevelDescription");

            list.add(paidFee);
        }

        PaidEnrollment[] array = new PaidEnrollment[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Obtiene los uniformes pagados por un alumno.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public PaidUniform[] getPaidUniformFees(String studentId) throws SQLException
    {
        ArrayList<PaidUniform> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "u.costo AS cost, " +
            "ce.codigo AS periodCode, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName, " +
            "t.numero AS tutorNumber, " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName, " +
            "c.codigo AS code," +
            "u.concepto AS concept,    " +
            "u.talla AS size," +
            "tu.descripcion AS uniformType, " +
            "ne.codigo AS educationLevelCode, " +
            "ne.descripcion AS educationLevelDescription " +
            "FROM alumnos AS a " +
            "INNER JOIN pagos AS p ON a.matricula = p.alumno " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN uniformes AS u ON u.codigo = c.uniforme " +
            "INNER JOIN tipos_uniforme AS tu ON u.tipo = tu.numero " +
            "INNER JOIN niveles_educativos AS ne ON u.nivel = ne.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN tutores AS t ON p.tutor = t.numero " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            PaidUniform paidFee = new PaidUniform();
            paidFee.paymentFolio = resultSet.getInt("paymentFolio");
            paidFee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            paidFee.cost = resultSet.getFloat("cost");
            paidFee.period.code = resultSet.getString("periodCode");
            paidFee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            paidFee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            paidFee.studentId = resultSet.getString("studentId");
            paidFee.studentName = resultSet.getString("studentName");
            paidFee.tutorNumber = resultSet.getInt("tutorNumber");
            paidFee.tutorName = resultSet.getString("tutorName");
            paidFee.code = resultSet.getString("code");
            paidFee.concept = resultSet.getString("concept");
            paidFee.size = resultSet.getString("size");
            paidFee.uniformType = resultSet.getString("uniformType");
            paidFee.level.code = resultSet.getString("educationLevelCode");
            paidFee.level.description = resultSet.getString("educationLevelDescription");

            list.add(paidFee);
        }

        PaidUniform[] array = new PaidUniform[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Obtiene los cobros de papelería pagados por un alumno.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public PaidStationery[] getPaidStationeryFees(String studentId) throws SQLException
    {
        ArrayList<PaidStationery> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "pa.costo AS cost, " +
            "ce.codigo AS periodCode, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName, " +
            "t.numero AS tutorNumber, " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName, " +
            "c.codigo AS code, " +
            "pa.concepto AS concept, " +
            "ne.codigo AS educationLevelCode, " +
            "ne.descripcion AS educationLevelDescription " +
            "FROM alumnos AS a " +
            "INNER JOIN pagos AS p ON a.matricula = p.alumno " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN papeleria AS pa ON pa.codigo = c.papeleria " +
            "INNER JOIN niveles_educativos AS ne ON pa.nivel = ne.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN tutores AS t ON p.tutor = t.numero " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            PaidStationery paidFee = new PaidStationery();
            paidFee.paymentFolio = resultSet.getInt("paymentFolio");
            paidFee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            paidFee.cost = resultSet.getFloat("cost");
            paidFee.period.code = resultSet.getString("periodCode");
            paidFee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            paidFee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            paidFee.studentId = resultSet.getString("studentId");
            paidFee.studentName = resultSet.getString("studentName");
            paidFee.tutorNumber = resultSet.getInt("tutorNumber");
            paidFee.tutorName = resultSet.getString("tutorName");
            paidFee.code = resultSet.getString("code");
            paidFee.concept = resultSet.getString("concept");
            paidFee.level.code = resultSet.getString("educationLevelCode");
            paidFee.level.description = resultSet.getString("educationLevelDescription");

            list.add(paidFee);
        }

        PaidStationery[] array = new PaidStationery[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Obtiene los cobros de mantenimiento pagados por un alumno.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public PaidMaintenance[] getPaidMaintenanceFees(String studentId) throws SQLException
    {
        ArrayList<PaidMaintenance> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "m.costo AS cost, " +
            "ce.codigo AS periodCode, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName, " +
            "t.numero AS tutorNumber, " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName, " +
            "c.codigo AS code, " +
            "m.concepto AS concept " +
            "FROM alumnos AS a " +
            "INNER JOIN pagos AS p ON a.matricula = p.alumno " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN mantenimiento AS m ON m.numero = c.mantenimiento " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN tutores AS t ON p.tutor = t.numero " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            PaidMaintenance paidFee = new PaidMaintenance();
            paidFee.paymentFolio = resultSet.getInt("paymentFolio");
            paidFee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            paidFee.cost = resultSet.getFloat("cost");
            paidFee.period.code = resultSet.getString("periodCode");
            paidFee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            paidFee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            paidFee.studentId = resultSet.getString("studentId");
            paidFee.studentName = resultSet.getString("studentName");
            paidFee.tutorNumber = resultSet.getInt("tutorNumber");
            paidFee.tutorName = resultSet.getString("tutorName");
            paidFee.code = resultSet.getString("code");
            paidFee.concept = resultSet.getString("concept");

            list.add(paidFee);
        }

        PaidMaintenance[] array = new PaidMaintenance[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Obtiene los eventos especiales pagados por un alumno.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public PaidSpecialEvent[] getPaidSpecialEventFees(String studentId) throws SQLException
    {
        ArrayList<PaidSpecialEvent> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "ee.costo AS cost, " +
            "ce.codigo AS periodCode, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName, " +
            "t.numero AS tutorNumber, " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName, " +
            "c.codigo AS code, " +
            "ee.concepto AS concept, " +
            "ee.fechaProgramada AS scheduledDate " +
            "FROM alumnos AS a " +
            "INNER JOIN pagos AS p ON a.matricula = p.alumno " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN eventos_especiales AS ee ON ee.codigo = c.eventoEspecial " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN tutores AS t ON p.tutor = t.numero " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            PaidSpecialEvent paidFee = new PaidSpecialEvent();
            paidFee.paymentFolio = resultSet.getInt("paymentFolio");
            paidFee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            paidFee.cost = resultSet.getFloat("cost");
            paidFee.period.code = resultSet.getString("periodCode");
            paidFee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            paidFee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            paidFee.studentId = resultSet.getString("studentId");
            paidFee.studentName = resultSet.getString("studentName");
            paidFee.tutorNumber = resultSet.getInt("tutorNumber");
            paidFee.tutorName = resultSet.getString("tutorName");
            paidFee.code = resultSet.getString("code");
            paidFee.concept = resultSet.getString("concept");
            paidFee.scheduledDate = resultSet.getDate("scheduledDate").toLocalDate();

            list.add(paidFee);
        }

        PaidSpecialEvent[] array = new PaidSpecialEvent[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Obtiene las mensualidades pagadas por un alumno en un ciclo escolar.
     * @param studentId
     * @param periodCode
     * @return
     * @throws SQLException
     */
    public PaidMonthly[] getPaidMonthlyFees(String studentId, String periodCode) throws SQLException
    {
        ArrayList<PaidMonthly> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "m.costo AS cost, " +
            "ce.codigo AS periodCode, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName, " +
            "t.numero AS tutorNumber, " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName, " +
            "c.codigo AS code, " +
            "m.fechaLimite AS paidMonth, " +
            "m.mesVacacional AS isVacationMonth," +
            "ne.codigo AS educationLevelCode, " +
            "ne.descripcion AS educationLevelDescription " +
            "FROM alumnos AS a " +
            "INNER JOIN pagos AS p ON a.matricula = p.alumno " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN mensualidades AS m ON m.codigo = c.mensualidad " +
            "INNER JOIN niveles_educativos AS ne ON m.nivel = ne.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN tutores AS t ON p.tutor = t.numero " +
            "WHERE a.matricula = ? AND ce.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        statement.setString(2, periodCode);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            PaidMonthly paidFee = new PaidMonthly();
            paidFee.paymentFolio = resultSet.getInt("paymentFolio");
            paidFee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            paidFee.cost = resultSet.getFloat("cost");
            paidFee.period.code = resultSet.getString("periodCode");
            paidFee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            paidFee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            paidFee.studentId = resultSet.getString("studentId");
            paidFee.studentName = resultSet.getString("studentName");
            paidFee.tutorNumber = resultSet.getInt("tutorNumber");
            paidFee.tutorName = resultSet.getString("tutorName");
            paidFee.code = resultSet.getString("code");
            paidFee.paidMonth = resultSet.getDate("paidMonth").toLocalDate();
            paidFee.isVacationMonth = resultSet.getBoolean("isVacationMonth");
            paidFee.level.code = resultSet.getString("educationLevelCode");
            paidFee.level.description = resultSet.getString("educationLevelDescription");

            list.add(paidFee);
        }

        PaidMonthly[] array = new PaidMonthly[list.size()];
        list.toArray(array);
        return array;
    }

    /* Otras consultas */

    /**
     * Obtiene los ciclos escolares en los que un alumno ha estado.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public SchoolPeriod[] getScholarPeriods(String studentId) throws SQLException
    {
        ArrayList<SchoolPeriod> list = new ArrayList<>();
        String sqlQuery = "SELECT  " +
            "ce.codigo AS code, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate " +
            "FROM grupos AS g " +
            "INNER JOIN ciclos_escolares AS ce ON g.ciclo = ce.codigo " +
            "INNER JOIN grupos_alumnos AS ga ON g.numero = ga.grupo " +
            "INNER JOIN alumnos AS a ON ga.alumno = a.matricula " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        while (resultSet.next()) 
        {
            var period = new SchoolPeriod();
            period.code = resultSet.getString("code");
            period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            period.endingDate = resultSet.getDate("endingDate").toLocalDate();

            list.add(period);
        }

        SchoolPeriod[] array = new SchoolPeriod[list.size()];
        list.toArray(array);
        return array;
    }

    public StudentPayment[] getStudentsWhoHaveAFee(String feeCode) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "p.folio AS paymentFolio, " +
            "p.fecha AS paymentDate, " +
            "c.codigo AS code, " +
            "a.matricula AS studentId, " +
            "CONCAT(a.nombre, ' ', a.primerApellido, IFNULL(CONCAT(' ',a.segundoApellido), '')) AS studentName,  " +
            "g.grado AS grade, " +
            "g.letra AS letter, " +
            "ne.codigo AS educationLevel, " +
            "ne.descripcion AS educationLevelDescription, " +
            "t.numero AS tutorNumber,  " +
            "CONCAT(t.nombre, ' ', t.primerApellido, IFNULL(CONCAT(' ',t.segundoApellido), '')) AS tutorName " +
            "FROM cobros AS c " +
            "INNER JOIN detalles_pago AS dp ON c.codigo = dp.codigoCobro " +
            "INNER JOIN pagos AS p ON p.folio = dp.folioPago " +
            "INNER JOIN alumnos AS a ON p.alumno = a.matricula " +
            "INNER JOIN grupos_alumnos AS ga on ga.alumno = a.matricula " +
            "INNER JOIN grupos AS g on ga.grupo = g.numero " +
            "INNER JOIN niveles_educativos AS ne on g.nivel = ne.codigo " +
            "INNER JOIN tutores AS t ON t.numero = p.tutor " +
            "WHERE c.codigo = ? " +
            "GROUP BY a.matricula";

        ArrayList<StudentPayment> list = new ArrayList<>();        
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, feeCode);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var fee = new StudentPayment();
            fee.paymentFolio = resultSet.getInt("paymentFolio");
            fee.paymentDate = resultSet.getDate("paymentDate").toLocalDate();
            fee.code = resultSet.getString("code");
            fee.studentId = resultSet.getString("studentId");
            fee.studentName = resultSet.getString("studentName");
            fee.grade = resultSet.getInt("grade");
            fee.letter = resultSet.getString("letter");
            fee.level.code = resultSet.getString("educationLevel");
            fee.level.description = resultSet.getString("educationLevelDescription");
            fee.tutorNumber = resultSet.getInt("tutorNumber");
            fee.tutorName = resultSet.getString("tutorName");

            list.add(fee);
        }

        StudentPayment[] array = new StudentPayment[list.size()];
        list.toArray(array);
        return array;
    }

    // Nuevas consultas

    /**
     * Verifica si un estudiante es de nuevo ingreso.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public boolean isNewStudent(String studentId) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "COUNT(ga.grupo) = 0 AS isNewStudent " +
            "FROM alumnos AS a " +
            "INNER JOIN grupos_alumnos AS ga ON a.matricula = ga.alumno " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();

        if (resultSet.next())
        {
            return resultSet.getBoolean("isNewStudent");
        }

        // Se supone que nunca debería llegar aquí
        return false;
    }

    /**
     * Obtiene el grado máximo para un nivel educativo.
     * @param educationLevel
     * @return
     * @throws SQLException
     */
    public int getMaxGrade(String educationLevel) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "MAX(g.grado) AS maxGrade " +
            "FROM grupos AS g " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "WHERE ne.codigo = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, educationLevel);
        var resultSet = statement.executeQuery();

        if (resultSet.next())
        {
            return resultSet.getInt("maxGrade");
        }

        // Se supone que nunca debería llegar aquí
        return 0;
    }

    /**
     * Obtiene el ciclo escolar actual.
     * @return
     */
    public SchoolPeriod getCurrentPeriod() throws SQLException
    {
        // Busca el ciclo escolar cuyo intervalo de fechas sea válido para la
        // fecha actual
        String sqlQuery = "SELECT " +
            "ce.codigo AS code, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate " +
            "FROM ciclos_escolares AS ce " +
            "WHERE ce.fechaInicio <= CURRENT_DATE() AND " +
            "ce.fechaFin > CURRENT_DATE() " +
            "LIMIT 1"; // Limita a una sola tupla devuelta

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);
        SchoolPeriod result = null;

        if (resultSet.next())
        {
            result = new SchoolPeriod();
            result.code = resultSet.getString("code");
            result.startingDate = resultSet.getDate("startingDate").toLocalDate();
            result.endingDate = resultSet.getDate("endingDate").toLocalDate();
        }

        return result;
    }

    /**
     * Obtiene el próximo ciclo escolar.
     * @return
     */
    public SchoolPeriod getNextPeriod() throws SQLException
    {
        // Busca el ciclo escolar cuya fecha de inicio aun no llega
        String sqlQuery = "SELECT " +
            "ce.codigo AS code, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate " +
            "FROM ciclos_escolares AS ce " +
            "WHERE ce.fechaInicio > CURRENT_DATE() " +
            "ORDER BY ce.fechaInicio " +
            "LIMIT 1";

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);
        SchoolPeriod result = null;

        if (resultSet.next())
        {
            result = new SchoolPeriod();
            result.code = resultSet.getString("code");
            result.startingDate = resultSet.getDate("startingDate").toLocalDate();
            result.endingDate = resultSet.getDate("endingDate").toLocalDate();
        }

        return result;
    }

    /**
     * Obtiene el grupo actual de un alumno.
     * @param studentId
     * @return
     * @throws SQLException
     */
    public Group getStudentCurrentGroup(String studentId) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "g.numero AS number, " +
            "g.grado AS grade, " +
            "g.letra AS letter, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS description " +
            "FROM alumnos AS a " +
            "INNER JOIN grupos_alumnos AS ga ON a.matricula = ga.alumno " +
            "INNER JOIN grupos AS g ON ga.grupo = g.numero " +
            "INNER JOIN ciclos_escolares AS ce ON g.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "WHERE a.matricula = ? " +
            "AND ce.fechaInicio <= CURRENT_DATE() " +
            "AND ce.fechaFin > CURRENT_DATE()";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentId);
        var resultSet = statement.executeQuery();
        Group group = null;

        if (resultSet.next())
        {
            group = new Group();
            group.number = resultSet.getInt("number");
            group.grade = resultSet.getInt("grade");
            group.letter = resultSet.getString("letter");
            group.period.code = resultSet.getString("period");
            group.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            group.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            group.level.code = resultSet.getString("level");
            group.level.description = resultSet.getString("description");
        }

        return group;
    }

    /**
     * Obtiene el grupo para un grado en un nivel y ciclo escolar especifico.
     * @param levelCode
     * @param periodCode
     * @param grade
     * @return
     * @throws SQLException
     */
    public Group getGroup(String levelCode, String periodCode, int grade) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "g.numero AS number, " +
            "g.grado AS grade, " +
            "g.letra AS letter, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS description " +
            "FROM grupos AS g " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON g.ciclo = ce.codigo " +
            "WHERE g.nivel = ? AND ce.codigo = ? AND g.grado = ?";    
        
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, levelCode);
        statement.setString(2, periodCode);
        statement.setInt(3, grade);
        var resultSet = statement.executeQuery();
        Group group = null;

        if (resultSet.next())
        {
            group = new Group();
            group.number = resultSet.getInt("number");
            group.grade = resultSet.getInt("grade");
            group.letter = resultSet.getString("letter");
            group.period.code = resultSet.getString("period");
            group.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            group.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            group.level.code = resultSet.getString("level");
            group.level.description = resultSet.getString("description");
        }

        return group;
    }

    public int registerPayment(
        String studentId,
        int tutorId,
        LocalDate date,
        float totalAmount,
        Fee[] fees) throws SQLException
    {
        int paymentFolio;

        // Paso 1. Registrar un pago
        String sqlQuery = "INSERT INTO pagos VALUES (DEFAULT,?,?,?,?)";

        var statement1 = getConnection().prepareStatement(
            sqlQuery,
            java.sql.Statement.RETURN_GENERATED_KEYS);

        statement1.setDate(1, Date.valueOf(date));
        statement1.setFloat(2, totalAmount);
        statement1.setString(3, studentId);
        statement1.setInt(4, tutorId);

        statement1.executeUpdate();
        var resultSet = statement1.getGeneratedKeys();

        resultSet.next();
        paymentFolio = resultSet.getInt(1);

        // Paso 2. Registrar los cobros de un pago
        sqlQuery = "INSERT INTO detalles_pago VALUES (?,?)";
        var statement2 = getConnection().prepareStatement(sqlQuery);

        for (int i = 0; i < fees.length; i++)
        {
            var item = fees[i];
            statement2.setInt(1, paymentFolio);
            statement2.setString(2, item.code);
            statement2.addBatch();
/*
            sqlQuery += String.format("(%d,'%s')\n", paymentFolio, item.code);

            if (i != fees.length - 1)
            {
                sqlQuery += ",";
            } */
        }

        statement2.executeBatch();

        return paymentFolio;
    }

    public void updateDbSchema() throws SQLException
    {
        String sqlQuery;
        boolean mesColumnExists = false;
        boolean fechaLimiteColumnExists = false;
        
        // Paso 1
        System.out.println("Actualizando fechas de ciclos escolares");
        var statement = getConnection().createStatement();

        statement.addBatch("UPDATE ciclos_escolares " +
            "SET fechaFin = '2022-08-22' " +
            "WHERE ciclos_escolares.codigo = '02122'");
        statement.addBatch("UPDATE ciclos_escolares " +
            "SET fechaFin = '2023-08-21' " +
            "WHERE ciclos_escolares.codigo = '02223'");
        statement.addBatch("UPDATE ciclos_escolares " +
            "SET fechaFin = '2024-08-19' " +
            "WHERE ciclos_escolares.codigo = '02324'");

        statement.executeBatch();

        // Paso 2
        System.out.println("Borrando columna mes en Mensualidades");

        statement = getConnection().createStatement();
        sqlQuery = "SELECT COUNT(*) > 0 " +
            "FROM information_schema.columns " +
            "WHERE table_schema = 'SistemaEscolar' " +
            "AND table_name = 'mensualidades' " + 
            "AND column_name = 'mes';";

        var resultSet = statement.executeQuery(sqlQuery);
        if (resultSet.next())
        {
            mesColumnExists = resultSet.getBoolean(1);
        }

        if (mesColumnExists)
        {
            sqlQuery = "ALTER TABLE mensualidades DROP `mes`;";
            statement = getConnection().createStatement();
            statement.execute(sqlQuery);
        }
        else
        {
            System.out.println("La columna no existe");
        }

        // Paso 3
        System.out.println("Creando columna fechaLimite en mensualidades");

        statement = getConnection().createStatement();
        sqlQuery = "SELECT COUNT(*) > 0 " +
            "FROM information_schema.columns " +
            "WHERE table_schema = 'SistemaEscolar' " +
            "AND table_name = 'mensualidades' " + 
            "AND column_name = 'fechaLimite';";

        resultSet = statement.executeQuery(sqlQuery);
        if (resultSet.next())
        {
            fechaLimiteColumnExists = resultSet.getBoolean(1);
        }

        if (!fechaLimiteColumnExists)
        {
            sqlQuery = "ALTER TABLE `mensualidades` \n" +
                "ADD `fechaLimite` DATE NOT NULL AFTER `codigo`;";
            
            statement = getConnection().createStatement();
            statement.execute(sqlQuery);
        }
        else
        {
            System.out.println("La columna existe");
        }

        // Paso 4
        System.out.println("Insertando datos nuevos");
        statement = getConnection().createStatement();
        
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-01-01'\n" + //
            "WHERE codigo = 'PRE-21-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-01-01'\n" + //
            "WHERE codigo = 'PRE-22-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-01-01'\n" + //
            "WHERE codigo = 'SEC-22-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-01-01'\n" + //
            "WHERE codigo = 'PRE-23-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-01-01'\n" + //
            "WHERE codigo = 'PRI-23-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-01-01'\n" + //
            "WHERE codigo = 'PRI-21-5';");
        statement.addBatch("UPDATE mensualidades\n" +
            "SET fechaLimite = '2024-01-01'\n" + //
            "WHERE codigo = 'SEC-23-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-01-01'\n" + //
            "WHERE codigo = 'SEC-21-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-01-01'\n" + //
            "WHERE codigo = 'PRI-22-5';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-02-01'\n" + //
            "WHERE codigo = 'PRI-23-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-02-01'\n" + //
            "WHERE codigo = 'PRE-22-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-02-01'\n" + //
            "WHERE codigo = 'PRI-22-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-02-01'\n" + //
            "WHERE codigo = 'SEC-22-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-02-01'\n" + //
            "WHERE codigo = 'SEC-21-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-02-01'\n" + //
            "WHERE codigo = 'PRE-21-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-02-01'\n" + //
            "WHERE codigo = 'PRE-23-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-02-01'\n" + //
            "WHERE codigo = 'SEC-23-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-02-01'\n" + //
            "WHERE codigo = 'PRI-21-6';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-03-01'\n" + //
            "WHERE codigo = 'PRE-23-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-03-01'\n" + //
            "WHERE codigo = 'PRI-23-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-03-01'\n" + //
            "WHERE codigo = 'PRI-22-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-03-01'\n" + //
            "WHERE codigo = 'SEC-21-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-03-01'\n" + //
            "WHERE codigo = 'PRI-21-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-03-01'\n" + //
            "WHERE codigo = 'SEC-22-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-03-01'\n" + //
            "WHERE codigo = 'PRE-21-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-03-01'\n" + //
            "WHERE codigo = 'SEC-23-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-03-01'\n" + //
            "WHERE codigo = 'PRE-22-7';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-04-01'\n" + //
            "WHERE codigo = 'PRI-21-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-04-01'\n" + //
            "WHERE codigo = 'PRE-23-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-04-01'\n" + //
            "WHERE codigo = 'SEC-22-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-04-01'\n" + //
            "WHERE codigo = 'PRI-23-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-04-01'\n" + //
            "WHERE codigo = 'SEC-21-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-04-01'\n" + //
            "WHERE codigo = 'PRE-21-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-04-01'\n" + //
            "WHERE codigo = 'PRI-22-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-04-01'\n" + //
            "WHERE codigo = 'SEC-23-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-04-01'\n" + //
            "WHERE codigo = 'PRE-22-8';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-05-01'\n" + //
            "WHERE codigo = 'PRE-21-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-05-01'\n" + //
            "WHERE codigo = 'PRI-21-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-05-01'\n" + //
            "WHERE codigo = 'PRI-22-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-05-01'\n" + //
            "WHERE codigo = 'SEC-23-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-05-01'\n" + //
            "WHERE codigo = 'PRE-22-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-05-01'\n" + //
            "WHERE codigo = 'PRI-23-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-05-01'\n" + //
            "WHERE codigo = 'SEC-21-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-05-01'\n" + //
            "WHERE codigo = 'PRE-23-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-05-01'\n" + //
            "WHERE codigo = 'SEC-22-9';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-06-01'\n" + //
            "WHERE codigo = 'PRE-21-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-06-01'\n" + //
            "WHERE codigo = 'PRE-23-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-06-01'\n" + //
            "WHERE codigo = 'SEC-22-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-06-01'\n" + //
            "WHERE codigo = 'PRI-23-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-06-01'\n" + //
            "WHERE codigo = 'PRE-22-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-06-01'\n" + //
            "WHERE codigo = 'PRI-22-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-06-01'\n" + //
            "WHERE codigo = 'SEC-23-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-06-01'\n" + //
            "WHERE codigo = 'PRI-21-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-06-01'\n" + //
            "WHERE codigo = 'SEC-21-10';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-07-01'\n" + //
            "WHERE codigo = 'PRI-23-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-07-01'\n" + //
            "WHERE codigo = 'SEC-21-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-07-01'\n" + //
            "WHERE codigo = 'SEC-22-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-07-01'\n" + //
            "WHERE codigo = 'SEC-23-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-07-01'\n" + //
            "WHERE codigo = 'PRE-23-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-07-01'\n" + //
            "WHERE codigo = 'PRE-21-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-07-01'\n" + //
            "WHERE codigo = 'PRE-22-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-07-01'\n" + //
            "WHERE codigo = 'PRI-22-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-07-01'\n" + //
            "WHERE codigo = 'PRI-21-11';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-08-01'\n" + //
            "WHERE codigo = 'PRI-23-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-08-01'\n" + //
            "WHERE codigo = 'PRI-21-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-08-01'\n" + //
            "WHERE codigo = 'SEC-21-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-08-01'\n" + //
            "WHERE codigo = 'PRE-21-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-08-01'\n" + //
            "WHERE codigo = 'PRE-23-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-08-01'\n" + //
            "WHERE codigo = 'PRI-22-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-08-01'\n" + //
            "WHERE codigo = 'SEC-22-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-08-01'\n" + //
            "WHERE codigo = 'PRE-22-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2024-08-01'\n" + //
            "WHERE codigo = 'SEC-23-12';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-09-01'\n" + //
            "WHERE codigo = 'PRE-23-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-09-01'\n" + //
            "WHERE codigo = 'PRI-21-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-09-01'\n" + //
            "WHERE codigo = 'PRE-21-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-09-01'\n" + //
            "WHERE codigo = 'SEC-21-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-09-01'\n" + //
            "WHERE codigo = 'PRI-23-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-09-01'\n" + //
            "WHERE codigo = 'SEC-22-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-09-01'\n" + //
            "WHERE codigo = 'PRE-22-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-09-01'\n" + //
            "WHERE codigo = 'PRI-22-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-09-01'\n" + //
            "WHERE codigo = 'SEC-23-1';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-10-01'\n" + //
            "WHERE codigo = 'PRE-22-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-10-01'\n" + //
            "WHERE codigo = 'SEC-23-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-10-01'\n" + //
            "WHERE codigo = 'PRE-21-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-10-01'\n" + //
            "WHERE codigo = 'SEC-22-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-10-01'\n" + //
            "WHERE codigo = 'PRI-23-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-10-01'\n" + //
            "WHERE codigo = 'PRI-22-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-10-01'\n" + //
            "WHERE codigo = 'PRI-21-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-10-01'\n" + //
            "WHERE codigo = 'SEC-21-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-10-01'\n" + //
            "WHERE codigo = 'PRE-23-2';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-11-01'\n" + //
            "WHERE codigo = 'SEC-22-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-11-01'\n" + //
            "WHERE codigo = 'PRI-22-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-11-01'\n" + //
            "WHERE codigo = 'PRE-21-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-11-01'\n" + //
            "WHERE codigo = 'SEC-21-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-11-01'\n" + //
            "WHERE codigo = 'PRI-23-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-11-01'\n" + //
            "WHERE codigo = 'SEC-23-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-11-01'\n" + //
            "WHERE codigo = 'PRE-23-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-11-01'\n" + //
            "WHERE codigo = 'PRI-21-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-11-01'\n" + //
            "WHERE codigo = 'PRE-22-3';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-12-01'\n" + //
            "WHERE codigo = 'PRI-21-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-12-01'\n" + //
            "WHERE codigo = 'SEC-22-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-12-01'\n" + //
            "WHERE codigo = 'PRE-22-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-12-01'\n" + //
            "WHERE codigo = 'PRE-21-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-12-01'\n" + //
            "WHERE codigo = 'SEC-23-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2021-12-01'\n" + //
            "WHERE codigo = 'SEC-21-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-12-01'\n" + //
            "WHERE codigo = 'PRI-23-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2023-12-01'\n" + //
            "WHERE codigo = 'PRE-23-4';");
        statement.addBatch("UPDATE mensualidades\n" + //
            "SET fechaLimite = '2022-12-01'\n" + //
            "WHERE codigo = 'PRI-22-4';");
        
        statement.executeBatch();
    }
}
