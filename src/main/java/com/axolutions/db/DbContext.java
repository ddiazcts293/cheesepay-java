package com.axolutions.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import com.axolutions.db.type.*;

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

    public Student getStudent(String enrollment) throws SQLException
    {
        Student studentFound = null;
        String sqlQuery = "SELECT * FROM alumnos WHERE matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, enrollment);
        var resultSet = statement.executeQuery();

        if (resultSet.next())
        {
            studentFound = new Student();
            studentFound.enrollment = resultSet.getString(1);
            studentFound.name = resultSet.getString(2);
            studentFound.firstSurname = resultSet.getString(3);
            studentFound.lastSurname = resultSet.getString(4);
            studentFound.gender = resultSet.getString(5);
            studentFound.age = resultSet.getInt(6);
            studentFound.dateOfBirth = resultSet.getDate(7).toLocalDate();
            studentFound.addressStreet = resultSet.getString(8);
            studentFound.addressNumber = resultSet.getString(9);
            studentFound.addressDistrict = resultSet.getString(10);
            studentFound.addressPostalCode = resultSet.getString(11);
            studentFound.curp = resultSet.getString(12);
            studentFound.nss = resultSet.getString(13);
        }

        return studentFound;
    }

    public Tutor[] getStudentTutors(String studentEnrollment) throws SQLException
    {
        ArrayList<Tutor> list = new ArrayList<>();

        String sqlQuery = "SELECT  " +
            "t.numero AS numero, " +
            "t.nombre AS nombre, " +
            "t.primerApellido AS primerApellido, " +
            "t.segundoApellido AS segundoApellido, " +
            "t.parentesco AS parentesco, " +
            "t.correoElectronico AS correoElectronico, " +
            "t.rfc AS rfc " +
            "FROM tutores AS t " +
            "INNER JOIN tutores_alumnos AS ta ON ta.tutor = t.numero " +
            "INNER JOIN alumnos AS a ON ta.alumno = a.matricula " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, studentEnrollment);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var tutorFound = new Tutor();
            tutorFound.number = resultSet.getInt(1);
            tutorFound.name = resultSet.getString(2);
            tutorFound.firstSurname = resultSet.getString(3);
            tutorFound.lastSurname = resultSet.getString(4);
            tutorFound.kinship = resultSet.getString(5);
            tutorFound.email = resultSet.getString(6);
            tutorFound.rfc = resultSet.getString(7);

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

    public Group[] getStudentGroups(String enrollment) throws SQLException
    {
        ArrayList<Group> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "g.numero AS numero, " +
            "g.grado AS grado, " +
            "g.letra AS grupo, " +
            "ce.codigo AS ciclo, " +
            "ce.fechaInicio AS fechaInicio, " +
            "ce.fechaFin AS fechaFin, " +
            "ne.codigo AS nivel, " +
            "ne.descripcion AS descripcion " +
            "FROM grupos AS g " +
            "INNER JOIN ciclos_escolares AS ce ON g.ciclo = ce.codigo " +
            "INNER JOIN grupos_alumnos AS ga ON g.numero = ga.grupo " +
            "INNER JOIN alumnos AS a ON ga.alumno = a.matricula " +
            "INNER JOIN niveles_educativos AS ne ON g.nivel = ne.codigo " +
            "WHERE a.matricula = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, enrollment);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var groupFound = new Group();
            groupFound.number = resultSet.getInt(1);
            groupFound.grade = resultSet.getInt(2);
            groupFound.letter = resultSet.getString(3);
            groupFound.period.code = resultSet.getString(4);
            groupFound.period.startingDate = resultSet.getDate(5).toLocalDate();
            groupFound.period.endingDate = resultSet.getDate(6).toLocalDate();
            groupFound.level.code = resultSet.getString(7);
            groupFound.level.description = resultSet.getString(8);

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
            "tt.numeroTelefono AS numeroTelefono " +
            "FROM tutores AS t " +
            "INNER JOIN tutor_telefonos AS tt ON tt.tutor = t.numero " +
            "WHERE t.numero = ?";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setInt(1, tutorNumber);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var phone = new TutorPhone();
            phone.id = resultSet.getInt(1);
            phone.phone = resultSet.getString(2);
            list.add(phone);
        }

        TutorPhone[] array = new TutorPhone[list.size()];
        list.toArray(array);
        return array;
    }

    public Invoice[] getStudentInvoices(String enrollment) throws SQLException
    {
        ArrayList<Invoice> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "p.folio AS folio, " +
            "p.fecha AS fecha, " +
            "p.montoTotal AS montoTotal, " +
            "ce.codigo AS ciclo, " +
            "ce.fechaInicio AS fechaInicio, " +
            "ce.fechaFin AS fechaFin " +
            "FROM pagos AS p " +
            "INNER JOIN alumnos AS a ON p.alumno = a.matricula " +
            "INNER JOIN detalles_pago AS dp ON p.folio = dp.folioPago " +
            "INNER JOIN cobros AS c ON dp.codigoCobro = c.codigo " +
            "INNER JOIN ciclos_escolares AS ce ON c.ciclo = ce.codigo " +
            "WHERE a.matricula = ? " +
            "GROUP BY p.folio";

        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, enrollment);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var invoice = new Invoice();
            invoice.folio = resultSet.getInt(1);
            invoice.date = resultSet.getDate(2).toLocalDate();
            invoice.totalAmount = resultSet.getFloat(3);

            invoice.period = new ScholarPeriod();
            invoice.period.code = resultSet.getString(4);
            invoice.period.startingDate = resultSet.getDate(5).toLocalDate();
            invoice.period.endingDate = resultSet.getDate(6).toLocalDate();
            list.add(invoice);
        }

        Invoice[] array = new Invoice[list.size()];
        list.toArray(array);
        return array;
    }

    public Tutor[] searchForTutors(String data) throws SQLException
    {
        ArrayList<Tutor> list = new ArrayList<>();

        String sqlQuery = "SELECT " +
            "t.numero as numero, " +
            "t.nombre AS nombre, " +
            "t.primerApellido AS primerApellido, " +
            "t.segundoApellido AS segundoApellido, " +
            "t.parentesco AS parentesco, " +
            "t.correoElectronico AS correoElectronico, " +
            "t.rfc AS rfc " +
            "FROM tutores AS t " +
            "WHERE " +
            "t.rfc LIKE ? OR " +
            "t.nombre LIKE ? OR " +
            "t.primerApellido LIKE ? OR " +
            "t.correoElectronico LIKE ? ";

        data = "%" + data + "%";
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, data);
        statement.setString(2, data);
        statement.setString(3, data);
        statement.setString(4, data);

        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var tutorFound = new Tutor();
            tutorFound.number = resultSet.getInt(1);
            tutorFound.name = resultSet.getString(2);
            tutorFound.firstSurname = resultSet.getString(3);
            tutorFound.lastSurname = resultSet.getString(4);
            tutorFound.kinship = resultSet.getString(5);
            tutorFound.email = resultSet.getString(6);
            tutorFound.rfc = resultSet.getString(7);

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

    public Student[] searchForStudents(String data) throws SQLException
    {
        ArrayList<Student> list = new ArrayList<>();

        String sqlQuery = "SELECT * FROM alumnos WHERE " +
            "nombre LIKE ? or " +
            "primerApellido LIKE ? or " +
            "curp LIKE ?";

        data = "%" + data + "%";
        var statement = getConnection().prepareStatement(sqlQuery);
        statement.setString(1, data);
        statement.setString(2, data);
        statement.setString(3, data);
        var resultSet = statement.executeQuery();

        while (resultSet.next())
        {
            var studentFound = new Student();
            studentFound.enrollment = resultSet.getString(1);
            studentFound.name = resultSet.getString(2);
            studentFound.firstSurname = resultSet.getString(3);
            studentFound.lastSurname = resultSet.getString(4);
            studentFound.gender = resultSet.getString(5);
            studentFound.age = resultSet.getInt(6);
            studentFound.dateOfBirth = resultSet.getDate(7).toLocalDate();
            studentFound.addressStreet = resultSet.getString(8);
            studentFound.addressNumber = resultSet.getString(9);
            studentFound.addressDistrict = resultSet.getString(10);
            studentFound.addressPostalCode = resultSet.getString(11);
            studentFound.curp = resultSet.getString(12);
            studentFound.nss = resultSet.getString(13);

            list.add(studentFound);
        }

        Student[] array = new Student[list.size()];
        list.toArray(array);
        return array;
    }

    public EducationLevel[] getEducationLevels() throws SQLException
    {
        ArrayList<EducationLevel> list = new ArrayList<>();
        String sqlQuery = "SELECT * FROM niveles_educativos";
        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next())
        {
            EducationLevel level = new EducationLevel();
            level.code = resultSet.getString(1);
            level.description = resultSet.getString(2);

            list.add(level);
        }

        EducationLevel[] array = new EducationLevel[list.size()];
        list.toArray(array);
        return array;
    }

    public Group[] getGroups(String period, String level) throws SQLException
    {
        ArrayList<Group> list = new ArrayList<>();
        String sqlQuery = "SELECT g.numero as numero, " +
            "g.grado AS grado, " +
            "g.letra AS letra, " +
            "ce.codigo AS ciclo, " +
            "ce.fechaInicio AS fechaInicio, " +
            "ce.fechaFin AS fechaFin, " +
            "ne.codigo AS nivel, " +
            "ne.descripcion as nivelEducativo, " +
            "COUNT(ga.alumno) AS cantidadAlumnos " +
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
            group.number = resultSet.getInt(1);
            group.grade = resultSet.getInt(2);
            group.letter = resultSet.getString(3);
            group.period.code = resultSet.getString(4);
            group.period.startingDate = resultSet.getDate(5).toLocalDate();
            group.period.endingDate = resultSet.getDate(6).toLocalDate();
            group.level.code = resultSet.getString(7);
            group.level.description = resultSet.getString(8);
            group.studentCount = resultSet.getInt(9);

            list.add(group);
        }

        Group[] array = new Group[list.size()];
        list.toArray(array);
        return array;
    }

    public Student[] getGroupStudents(Group group) throws SQLException
    {
        ArrayList<Student> list = new ArrayList<>();
        String sqlString = "SELECT a.matricula AS matrícula, " +
            "a.nombre AS nombre, " +
            "a.primerApellido AS primerApellido, " +
            "a.segundoApellido AS segundoApellido, " +
            "a.genero AS genero, " +
            "a.edad AS edad, " +
            "a.fechaNacimiento AS fechaNacimiento, " +
            "a.domicilioCalle AS domicilioCalle, " +
            "a.domicilioNumero AS domicilioNumero, " +
            "a.domicilioColonia AS domicilioColonia, " +
            "a.domicilioCP AS domicilioCP, " +
            "a.curp AS curp, " +
            "a.nss AS nss, " +
            "ce.codigo AS ciclo, " +
            "ce.fechaInicio AS fechaInicio, " +
            "ce.fechaFin AS fechaFin, " +
            "ne.codigo AS nivel, " +
            "ne.descripcion AS nivel " +
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
            student.enrollment = resultSet.getString(1);
            student.name = resultSet.getString(2);
            student.firstSurname = resultSet.getString(3);
            student.lastSurname = resultSet.getString(4);
            student.gender = resultSet.getString(5);
            student.age = resultSet.getInt(6);
            student.dateOfBirth = resultSet.getDate(7).toLocalDate();
            student.addressStreet = resultSet.getString(8);
            student.addressNumber = resultSet.getString(9);
            student.addressDistrict = resultSet.getString(10);
            student.addressPostalCode = resultSet.getString(11);
            student.curp = resultSet.getString(12);
            student.nss = resultSet.getString(13);
            student.period.code = resultSet.getString(14);
            student.period.startingDate = resultSet.getDate(15).toLocalDate();
            student.period.endingDate = resultSet.getDate(16).toLocalDate();
            student.level.code = resultSet.getString(17);
            student.level.description = resultSet.getString(18);

            list.add(student);
        }

        Student[] array = new Student[list.size()];
        list.toArray(array);
        return array;
    }

    public ScholarPeriod[] getScholarPeriods() throws SQLException
    {
        ArrayList<ScholarPeriod> list = new ArrayList<>();
        String sqlQuery = "SELECT * FROM ciclos_escolares";
        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next())
        {
            ScholarPeriod period = new ScholarPeriod();
            period.code = resultSet.getString(1);
            period.startingDate = resultSet.getDate(2).toLocalDate();
            period.endingDate = resultSet.getDate(3).toLocalDate();

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
            student.enrollment = String.format("%05d", resultSet.getInt(1) + 1);
        }

        // Paso 2: Registrar un nuevo alumno añadiendo toda la información
        var statement2 = getConnection().prepareStatement(sqlQuery2);
        statement2.setString(1, student.enrollment);
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
        if (student.nss != null && !student.nss.isBlank())
        {
            statement2.setString(13,  student.enrollment);
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
        statement.setString(2, student.enrollment);
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
        statement.setString(6, student.enrollment);

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
        String sqlQuery = "INSERT INTO tutor_telefonos " +
            "VALUES (DEFAULT, ?, ?)";
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

    private Connection getConnection()
    {
        return wrapper.getConnection();
    }
}
