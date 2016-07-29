package foo.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class CreateUserLibraries {
    public static void main(String[] args) throws IOException {
	createLib("ePo", "ORION_HOME");
    }

    public static void createLib(String nameLib, String envVar) throws IOException {
	String intro = "\n";
	String start = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + intro + "<eclipse-userlibraries version=\"2\">"
		+ intro + "    <library name=\"" + nameLib + "\" systemlibrary=\"false\">";
	String end = "    </library>" + intro + "</eclipse-userlibraries>";
	String startPath = "        <archive path=\"";
	String endPath = "\"/>";
	LinkedList<File> lines = readFile(envVar);
	writeFile(envVar, lines, start, end, startPath, endPath, nameLib);
    }

    private static void writeFile(String envVar, LinkedList<File> ficherosJava, String start, String end, String startPath, String endPath, String nameFileOutput) throws IOException {
	String directorySeparator = directorySeparator();
	String path = System.getenv(envVar);
	OutputStream outputStream = new FileOutputStream(path + directorySeparator + nameFileOutput + ".userlibraries");
	Writer writer = new OutputStreamWriter(outputStream);
	writer.flush();
	
	StringBuffer out = new StringBuffer();
	if (ficherosJava.size() > 0) {
	    out.append(start);
	    for (int i = 0; i < ficherosJava.size(); i++)
		out.append(startPath + ficherosJava.get(i).getAbsolutePath() + endPath);
	    out.append(end);
	}
	writer.write(out.toString());
	writer.close();
    }

    public static LinkedList<File> readFile(String envVar) {
	String path = "";
	try {
	    path = System.getenv(envVar);
	    LinkedList<File> ficherosJava = new LinkedList<File>();
	    dameFicheros(path, dameRegex("*.jar"), ficherosJava, true);
	    return ficherosJava;

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Busca todos los ficheros que cumplen la m�scara que se le pasa y los mete
     * en la listaFicheros que se le pasa.
     * 
     * @param pathInicial
     *            Path inicial de b�squeda. Debe ser un directorio que exista y
     *            con permisos de lectura.
     * @param mascara
     *            Una m�scara v�lida para la clase Pattern de java.
     * @param listaFicheros
     *            Una lista de ficheros a la que se a�adir�n los File que
     *            cumplan la m�scara. No puede ser null. El m�todo no la vac�a.
     * @param busquedaRecursiva
     *            Si la b�squeda debe ser recursiva en todos los subdirectorios
     *            por debajo del pathInicial.
     */
    public static void dameFicheros(String pathInicial, String mascara, LinkedList<File> listaFicheros, boolean busquedaRecursiva) {
	File directorioInicial = new File(pathInicial);
	if (directorioInicial.isDirectory()) {
	    File[] ficheros = directorioInicial.listFiles();
	    for (int i = 0; i < ficheros.length; i++) {
		if (ficheros[i].isDirectory() && busquedaRecursiva)
		    dameFicheros(ficheros[i].getAbsolutePath(), mascara, listaFicheros, busquedaRecursiva);
		else if (Pattern.matches(mascara, ficheros[i].getName()))
		    listaFicheros.add(ficheros[i]);
	    }
	}
    }

    /**
     * Se le pasa una m�scara de fichero t�pica de ficheros con * e ? y devuelve
     * la cadena regex que entiende la clase Pattern.
     * 
     * @param mascara
     *            Un String que no sea null.
     * @return Una m�scara regex v�lida para Pattern.
     */
    public static String dameRegex(String mascara) {
	mascara = mascara.replace(".", "\\.");
	mascara = mascara.replace("*", ".*");
	mascara = mascara.replace("?", ".");
	return mascara;
    }
    
    private static String directorySeparator() {
	String osName = System.getProperty("os.name").toLowerCase();
	String directorySeparator;
	if (osName.contains("win")) {
	    directorySeparator = "\\";
	} else {
	    directorySeparator = "/";
	}
	return directorySeparator;
    }
}
