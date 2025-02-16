package com.poo.codigo_viejo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
// import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Collection;
// import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import com.poo.Enums.*;
import com.poo.Usuario.*;

public class Sistema {

    // Se crean objetos estaticos para listas de reservas, usuarios, espacios u
    // administradores
    public static Scanner sc = new Scanner(System.in);
    private static ArrayList<Usuario> listaUsuario = new ArrayList<>();
    private static ArrayList<Usuario> listaAdministradores = new ArrayList<>();
    public static ArrayList<Espacio> listaEspacio = new ArrayList<>();
    public static ArrayList<Reserva> listaReserva = new ArrayList<>();
    static Usuario usuario;

    public static void main(String[] args) {
        // Se llaman metodos para poder cargar las listas de objetos
        CargarUsuariosDesdeArchivo("Usuarios.txt");
        cargarEspaciosDesdeArchivo("Espacios.txt"); // Encontrar rutas
        CargarAdministradoresDesdeArchivo("Administradores.txt");
        actualizarEstudiantes();
        actualizarProfesores();
        // Proceso de login

        System.out.println("Bienvendio al sistema , Ingrese su usario y contraseña: ");
        System.out.println("Usuario: ");
        String usuario = sc.nextLine();
        System.out.println("Contraseña: ");
        String contraseña = sc.nextLine();
        // Se recorre la lista para poder encontrar al usuario que se esta intentado
        // entrar
        for (Usuario u : listaUsuario) {
            if(u.getRol().equals(Rol.ESTUDIANTE) || u.getRol().equals(Rol.ESTUDIANTE)){
                if (usuario.equals(u.getUsuario()) && contraseña.equals(u.getContrasena())) {
                    Sistema.usuario = u;
                    System.out.println("Bienvenido al sistema");
                    System.out.println(Sistema.usuario);
                    
                }
                if (Sistema.usuario == null) {
                    System.out.println("No existe tu usuario");
                }
                mostrar_menu(Sistema.usuario);
            }else{
                mostrar_menu_administrador(u);
            }
        }
            
            
    }

    /**
     * Muestra el menú de opciones para un usuario y permite realizar reservas o
     * consultar reservas,
     * dependiendo del rol del usuario (Estudiante o Profesor).
     * 
     * @param u El usuario que interactúa con el sistema. El usuario puede ser de
     *          tipo {@link Estudiante} o {@link Profesor}.
     */

    public static void mostrar_menu(Usuario u) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        // Imprime las opciones para Usuario
        do {
            System.out.println("Seleccione una de las opciones: ");
            System.out.println("0. Salir");
            System.out.println("1. Reservar");
            System.out.println("2. Consultar reserva");
            opcion = scanner.nextInt();
            // Por medio de un switch se desarollan llamadas a cada opcion
            switch (opcion) {
                case 1:
                    if (u.getRol() == Rol.ESTUDIANTE) {
                        Estudiante e = (Estudiante) u;
                        System.out.println("Ingrese la fecha: (28/11/2024) "); // SE pide fecha
                        Date date = null;
                        try {
                            date = getDateFromString(sc.nextLine()); // Se procesa la fecha
                        } catch (Exception a) {
                            a.printStackTrace();
                        }
                        e.reservar(date);
                        Reserva linea = listaReserva.get(listaReserva.size() - 1);

                        Sistema.EscribirArchivo("reserva.txt", linea);
                    } else if (u.getRol() == Rol.PROFESOR) {
                        Profesor p = (Profesor) u;
                        for (String materia : p.getListaMaterias()) {
                            System.out.println(materia);
                        }
                        System.out.println("Escoge la materia que va a dar");
                        String materia_reserva = sc.nextLine();
                        p.reservar(materia_reserva);
                    }
                    break;
                case 2:
                    System.out.println("Ingrese la fecha: (28/11/2024) "); // SE pide fecha
                    Date date = null;
                    try {
                        date = getDateFromString(sc.nextLine()); // Se procesa la fecha
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    usuario.ConsultarReserva(date);// Se llama el metodo de consultar reservas
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }

            System.out.println(); // Espaciado para legibilidad
        } while (opcion != 0);

        scanner.close();
    }

    /**
     * Convierte una cadena de texto en un objeto {@link Date}.
     * 
     * @param dateStr La cadena de texto que representa la fecha en formato
     *                "dd/MM/yyyy".
     * @return Un objeto {@link Date} correspondiente a la cadena proporcionada.
     */

    public static Date getDateFromString(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.parse(dateStr);
    }

    public static void mostrar_menu_administrador(Usuario us) {
        int opcion;
        Administrador ad = (Administrador) us;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido");
        do {
            System.out.println("Seleccione una de las opciones: ");
            System.out.println("0...Salir");
            System.out.println("1...Gestinar Reserva");
            System.out.println("2...Consultar reserva");
            opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    ad.CambiarReserva();
                    break;
                case 2:
                    ad.ConsultarReserva();
                    break;
                default:
                    break;
            }

        } while (opcion != 0);
        scanner.close();
    }

    /**
     * Carga los espacios desde un archivo de texto y los agrega a la lista de
     * espacios del sistema.
     * 
     * @param nombreArchivo El nombre del archivo que contiene la información de los
     *                      espacios.
     *                      Este archivo debe estar ubicado en la carpeta
     *                      `Archivos/` dentro de los recursos del proyecto.
     */

    public static void cargarEspaciosDesdeArchivo(String nombreArchivo) {
        try (InputStream inputStream = Sistema.class.getClassLoader().getResourceAsStream("Archivos/" + nombreArchivo);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.out.println("El archivo no fue encontrado.");
                return;
            }
            // Se lee el archvio de espacios y se cargan en la lista necesaria
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                int codigo = Integer.parseInt(datos[0].trim());
                Tipo tipo = Tipo.valueOf(datos[1].trim());
                String nombre = datos[2].trim();
                int capacidad = Integer.parseInt(datos[3].trim());
                Estado estado = Estado.valueOf(datos[4].trim());
                Rol rol = Rol.valueOf(datos[5].trim());

                // Crear y agregar espacio a la lista
                Espacio espacio = new Espacio(codigo, tipo, nombre, capacidad, estado, rol);
                listaEspacio.add(espacio);
            }
            System.out.println("Espacios cargados exitosamente desde el archivo.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga los usuarios desde un archivo de texto y los agrega a la lista de
     * usuarios del sistema.
     * 
     * @param nombreArchivo El nombre del archivo que contiene la información de los
     *                      usuarios.
     *                      Este archivo debe estar ubicado en la carpeta
     *                      `Archivos/` dentro de los recursos del proyecto.
     */

    // Se repite la logica para usuarios
    public static void CargarUsuariosDesdeArchivo(String nombreArchivo) {
        try (InputStream inputStream = Sistema.class.getClassLoader().getResourceAsStream("Archivos/" + nombreArchivo);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.out.println("El archivo no fue encontrado.");
                return;
            }

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                int codigo = Integer.parseInt(datos[0].trim());
                String cedula = datos[1].trim();
                String nombre = datos[2].trim();
                String apellido = datos[3].trim();
                String usuario = datos[4].trim();
                String contrasena = datos[5].trim();
                String correo = datos[6].trim();
                Rol rol = Rol.valueOf(datos[7].trim());
                // Crear y agregar espacio a la lista
                if (rol.equals(Rol.ESTUDIANTE))
                    listaUsuario
                            .add(new Estudiante(codigo, cedula, nombre, apellido, usuario, contrasena, correo, 0, ""));
                if (rol.equals(Rol.PROFESOR))
                    listaUsuario.add(new Profesor(codigo, cedula, nombre, apellido, usuario, contrasena, correo, "",
                            new ArrayList<>()));
            }
            System.out.println("Usuarios cargados exitosamente desde el archivo.");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en el formato del archivo: " + e.getMessage());
        }
    }

    /**
     * Carga los usuarios desde un archivo de texto y los agrega a la lista de
     * usuarios del sistema.
     * 
     * @param nombreArchivo El nombre del archivo que contiene la información de los
     *                      usuarios.
     *                      Este archivo debe estar ubicado en la carpeta
     *                      `Archivos/` dentro de los recursos del proyecto.
     */
    public static void CargarAdministradoresDesdeArchivo(String nombreArchivo) {
        try (InputStream inputStream = Sistema.class.getClassLoader().getResourceAsStream("Archivos/" + nombreArchivo);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                int codigo = Integer.parseInt(datos[0].trim());
                String cedula = datos[1].trim();
                String nombre = datos[2].trim();
                String apellido = datos[3].trim();
                String cargo = datos[4].trim();

                listaAdministradores.add(new Administrador(codigo, cedula, nombre, apellido, "", "", "",
                        Rol.ADMINISTRADOR, Cargo.valueOf(cargo)));
            }
            System.out.println("Administradores cargados");

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en el formato del archivo: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de los estudiantes en la lista de usuarios desde un
     * archivo de texto.
     * El archivo debe contener los datos del estudiante con el siguiente formato:
     * "codigo|cedula|nombre|apellido|numMatricula|carrera".
     */

    // Actualiza campos de los estudiantes dependiendo de sus campos respectivos
    public static void actualizarEstudiantes() {
        try (InputStream inputStream = Sistema.class.getClassLoader()
                .getResourceAsStream("Archivos/" + "Estudiante.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                int codigo = Integer.parseInt(datos[0].trim());
                for (Usuario u : listaUsuario) {
                    if (codigo == u.getCodigo()) {
                        // Sse hace downcasting y se actualiza los datos
                        Estudiante e = (Estudiante) u;
                        e.setNumMatricula(Integer.valueOf(datos[4].trim()));
                        e.setCarrera(datos[5].trim());
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la información de los profesores en la lista de usuarios desde un
     * archivo de texto.
     * El archivo debe contener los datos del profesor con el siguiente formato:
     * "codigo|cedula|nombre|apellido|usuario|facultad|materias".
     */

    // Lee el archivo de profesores
    public static void actualizarProfesores() {
        try (InputStream inputStream = Sistema.class.getClassLoader()
                .getResourceAsStream("Archivos/" + "Profesores.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            // Itera cada profesor
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                int codigo = Integer.parseInt(datos[0].trim());
                for (Usuario u : listaUsuario) {
                    if (codigo == u.getCodigo()) {
                        // Se hace dowcasting y se actualiza lois datos del profesor
                        Profesor temp = (Profesor) u;
                        temp.setFacultad(datos[4].trim());
                        String[] amaterias = datos[5].trim().split(",");
                        ArrayList<String> materias = new ArrayList<>(Arrays.asList(amaterias));
                        temp.setListaMaterias(materias);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    /**
     * Muestra una lista de los espacios disponibles en el sistema.
     */

    public void mostrar_espacios_disponibles() {
        System.out.println("----- Espacios Disponibles -----");
        boolean hayDisponibles = false;
        // Se iteran los espacios en lista Espacio para mostrar a los disponibles
        for (Espacio espacio : listaEspacio) {
            if (espacio.getEstado() == Estado.DISPONIBLE) { // Se usa el if para filtrar
                System.out.println("Código: " + espacio.getCodigo() +
                        ", Tipo: " + espacio.getTipo() +
                        ", Nombre: " + espacio.getNombre() +
                        ", Capacidad: " + espacio.getCapacidad() +
                        ", Rol: " + espacio.getRol());
                hayDisponibles = true;// Se imprimen los espacios correspondientes
            }
        }
        if (!hayDisponibles) {
            System.out.println("No hay espacios disponibles en este momento.");
        }
        System.out.println("---------------------------------");
    }

    public static void EscribirArchivo(String nombreArchivo, Reserva linea) {
        FileWriter fichero = null;
        BufferedWriter bw = null;

        try {
            File carpeta = new File("data");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            fichero = new FileWriter("data/" + nombreArchivo, true);
            bw = new BufferedWriter(fichero);

            if (linea != null) {

                bw.write(linea.toString() + "\n");
                System.out.println("Reserva escrita en el archivo");
            } else {
                System.out.println("La reserva está vacía y no se ha escrito nada");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fichero != null) {
                    fichero.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

}
