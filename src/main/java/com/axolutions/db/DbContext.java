package com.axolutions.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import com.axolutions.db.type.*;
import com.axolutions.db.type.fee.*;

public class DbContext
{
    private DbConnectionWrapper wrapper;

    public DbContext(DbConnectionWrapper dbConnectionWrapper)
    {
        this.wrapper = dbConnectionWrapper;
    }

    public boolean isConnected()
    {
        return getConnection() != null; //&& dbConnection.isValid(0);
    }

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
            "WHERE a.matricula = ?";

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

    public ScholarPeriod[] getScholarPeriods() throws SQLException
    {
        ArrayList<ScholarPeriod> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "ce.codigo AS code, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate " +
            "FROM ciclos_escolares AS ce";
        
        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next())
        {
            ScholarPeriod period = new ScholarPeriod();
            period.code = resultSet.getString("code");
            period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            period.endingDate = resultSet.getDate("endingDate").toLocalDate();

            list.add(period);
        }

        ScholarPeriod[] array = new ScholarPeriod[list.size()];
        list.toArray(array);
        return array;
    }

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

    public Fee getEnrollmentFee(ScholarPeriod period, EducationLevel level) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS feeId, " +
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
            "WHERE ce.codigo = ? AND ne.codigo = ?" +
            "LIMIT 1"; // Solo se requiere un elemento

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, period.code);
        statement.setString(2, level.code);

        var resultSet = statement.executeQuery();
        Fee fee = null;

        if (resultSet.next())
        {
            fee = new Fee();
            fee.enrollment = new EnrollmentFee();
            fee.code = resultSet.getString("feeId");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.enrollment.code = resultSet.getString("enrollmentId");
            fee.enrollment.cost = resultSet.getFloat("cost");
            fee.enrollment.level.code = resultSet.getString("level");
            fee.enrollment.level.description = resultSet.getString("levelDescription");
        }

        return fee;
    }

    public Fee[] getMonthlyFees(ScholarPeriod period, EducationLevel level) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS feeId, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "m.codigo AS monthlyId, " +
            "DATE_ADD(DATE(CONCAT(YEAR(ce.fechaInicio), '-01-01')), INTERVAL " +
            "IF(m.mes >= 9, m.mes - 1, m.mes + 11) MONTH) AS month, " +
            "m.mesVacacional AS isVacationMonth, " +
            "m.costo AS cost, " +
            "ne.codigo AS level, " +
            "ne.descripcion AS levelDescription " +
            "FROM cobros AS c " +
            "INNER JOIN mensualidades AS m ON c.mensualidad = m.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "INNER JOIN niveles_educativos AS ne ON m.nivel = ne.codigo " +
            "WHERE ce.codigo = ? AND ne.codigo = ? " +
            "ORDER BY month";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, period.code);
        statement.setString(2, level.code);

        var resultSet = statement.executeQuery();
        ArrayList<Fee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new Fee();
            fee.monthly = new MonthlyFee();
            fee.code = resultSet.getString("feeId");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.monthly.code = resultSet.getString("monthlyId");
            fee.monthly.month = resultSet.getDate("month").toLocalDate().getMonth();
            fee.monthly.isVacationMonth = resultSet.getBoolean("isVacationMonth");
            fee.monthly.cost = resultSet.getFloat("cost");
            fee.monthly.level.code = resultSet.getString("level");
            fee.monthly.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        Fee[] array = new Fee[list.size()];
        list.toArray(array);
        return array;
    }

    public Fee[] getStationeryFees(ScholarPeriod period, EducationLevel level) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS feeId, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "p.codigo AS stationeryId, " +
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
        statement.setString(1, period.code);
        statement.setString(2, level.code);

        var resultSet = statement.executeQuery();
        ArrayList<Fee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new Fee();
            fee.stationery = new StationeryFee();
            fee.code = resultSet.getString("feeId");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.stationery.code = resultSet.getString("stationeryId");
            fee.stationery.concept = resultSet.getString("concept");
            fee.stationery.grade = resultSet.getInt("grade");
            fee.stationery.cost = resultSet.getFloat("cost");
            fee.stationery.level.code = resultSet.getString("level");
            fee.stationery.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        Fee[] array = new Fee[list.size()];
        list.toArray(array);
        return array;
    }

    public Fee[] getUniformFees(ScholarPeriod period, EducationLevel level) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS feeId, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "u.codigo AS uniformId, " +
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
        statement.setString(1, period.code);
        statement.setString(2, level.code);

        var resultSet = statement.executeQuery();
        ArrayList<Fee> list = new ArrayList<>();

        while (resultSet.next())
        {
            var fee = new Fee();
            fee.uniform = new UniformFee();
            fee.code = resultSet.getString("feeId");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.uniform.code = resultSet.getString("uniformId");
            fee.uniform.concept = resultSet.getString("concept");
            fee.uniform.size = resultSet.getString("size");
            fee.uniform.cost = resultSet.getFloat("cost");
            fee.uniform.type.number = resultSet.getInt("uniformTypeId");
            fee.uniform.type.description = resultSet.getString("uniformType");
            fee.uniform.level.code = resultSet.getString("level");
            fee.uniform.level.description = resultSet.getString("levelDescription");

            list.add(fee);
        }

        Fee[] array = new Fee[list.size()];
        list.toArray(array);
        return array;
    }

    public Fee getMaintenanceFee(ScholarPeriod period) throws SQLException
    {
        String sqlQuery = "SELECT " +
            "c.codigo AS feeId, " +
            "ce.codigo AS period, " +
            "ce.fechaInicio AS startingDate, " +
            "ce.fechaFin AS endingDate, " +
            "m.numero AS maintenanceId, " +
            "m.concepto AS concept, " +
            "m.costo AS cost " +
            "FROM cobros AS c " +
            "INNER JOIN mantenimiento AS m ON c.mantenimiento = m.numero " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "WHERE ce.codigo = ? " +
            "LIMIT 1"; // Solo se requiere un elemento

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, period.code);

        var resultSet = statement.executeQuery();
        Fee fee = null;

        if (resultSet.next())
        {
            fee = new Fee();
            fee.maintenance = new MaintenanceFee();
            fee.code = resultSet.getString("feeId");
            fee.period.code = resultSet.getString("period");
            fee.period.startingDate = resultSet.getDate("startingDate").toLocalDate();
            fee.period.endingDate = resultSet.getDate("endingDate").toLocalDate();
            fee.maintenance.number = resultSet.getInt("maintenanceId");
            fee.maintenance.concept = resultSet.getString("concept");
            fee.maintenance.cost = resultSet.getFloat("cost");
        }

        return fee;
    }

    private Connection getConnection()
    {
        return wrapper.getConnection();
    }
}
