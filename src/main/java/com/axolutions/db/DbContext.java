package com.axolutions.db;

import java.sql.Connection;
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
        String sqlQuery = "SELECT * FROM alumnos WHERE " +
            "matricula = '" + enrollment + "'";
    
        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

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
            "WHERE a.matricula = '" + studentEnrollment +"'";
    
        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

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
            for (String phone : phones) 
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
            "WHERE a.matricula = " + enrollment;

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) 
        {
            var groupFound = new Group();
            groupFound.number = resultSet.getInt(1);
            groupFound.grade = resultSet.getInt(2);
            groupFound.letter = resultSet.getString(3);
            
            groupFound.period = new ScholarPeriod();
            groupFound.period.code = resultSet.getString(4);
            groupFound.period.startingDate = resultSet.getDate(5).toLocalDate();
            groupFound.period.endingDate = resultSet.getDate(6).toLocalDate();
            
            groupFound.level = new EducationLevel();
            groupFound.level.code = resultSet.getString(7);
            groupFound.level.description = resultSet.getString(8);

            list.add(groupFound);
        }
        
        Group[] array = new Group[list.size()];
        list.toArray(array);
        return array;
    }

    public String[] getTutorPhones(int tutorNumber) throws SQLException
    {
        ArrayList<String> list = new ArrayList<>();
        String sqlQuery = "SELECT " +
            "tt.numeroTelefono AS numeroTelefono " +
            "FROM tutores AS t " +
            "INNER JOIN tutor_telefonos AS tt ON tt.tutor = t.numero " +
            "WHERE t.numero = " + tutorNumber;
        
        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) 
        {
            list.add(resultSet.getString(1));
        }
        
        String[] array = new String[list.size()];
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
            "WHERE a.matricula = '" + enrollment +"' " +
            "GROUP BY p.folio ";

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

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

    public Tutor[] searchTutors(String data) throws SQLException
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
            "INNER JOIN tutor_telefonos AS tt ON t.numero = tt.tutor " +
            "WHERE " +
            "t.rfc LIKE '%" + data + "%' OR " +
            "t.nombre LIKE '%" + data + "%' OR " +
            "t.primerApellido LIKE '%" + data + "%' OR " +
            "t.correoElectronico LIKE '%" + data + "%' OR " +
            "tt.numeroTelefono LIKE '%" + data + "%' " +
            "GROUP BY t.numero";

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

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
            for (String phone : phones) 
            {
                tutorFound.phones.add(phone);
            }

            list.add(tutorFound);
        }
        
        Tutor[] array = new Tutor[list.size()];
        list.toArray(array);
        return array;
    }

    public Student[] searchStudents(String data) throws SQLException
    {
        ArrayList<Student> list = new ArrayList<>();

        String sqlQuery = "SELECT * FROM alumnos WHERE " +
            "nombre LIKE '%" + data + "%' or " +
            "primerApellido LIKE '%" + data + "%' or " +
            "curp LIKE '%" + data + "%'";

        var statement = getConnection().createStatement();
        var resultSet = statement.executeQuery(sqlQuery);

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

    public void registerTutor(Tutor tutor) throws SQLException
    {
        String sqlQuery = "INSERT INTO tutores " +
            "VALUES (DEFAULT,?,?,?,?,?)";
        
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

    // INSERT INTO alumnos VALUES
    //('00350','Xavier','Lopez','Rodriguez','Masculino','6','2018-02-17','Av. de las aguas negras','666','Mala Vista','22666','MJAI180306MSRTNR887',NULL)

    //public 
    
    private Connection getConnection()
    {
        return wrapper.getConnection();
    }
}
