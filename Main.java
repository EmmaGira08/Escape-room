import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * ESCAPE: LA HABITACION - Motor del juego (codigo base del profesor).
 *
 * <p>El juego completo son TRES habitaciones. Despiertas encerrado y debes
 * escapar de cada una resolviendo su puzzle. La habitacion 1 ya funciona de
 * punta a punta; las habitaciones 2 y 3 <b>las construye el equipo
 * programando</b>: el motor ya llama a los metodos PLACEHOLDER que ustedes
 * deben completar para poder llegar al final del juego.</p>
 *
 * <p>Fuentes de verdad de cada habitacion N:</p>
 * <ul>
 *   <li><code>habitacionN.txt</code> - la matriz VISUAL (el dibujo).</li>
 *   <li><code>zonasN.txt</code> - la matriz de MOVIMIENTO (que ES cada casilla).</li>
 *   <li><code>objetosN.csv</code> - nombre, arte y detalle de cada objeto.</li>
 *   <li><code>arte/*.txt</code> y <code>musica/*.mid</code> - compartidos.</li>
 * </ul>
 *
 * <p><b>PLACEHOLDERS vs TODOs:</b> un PLACEHOLDER es un metodo que el juego ya
 * llama pero cuyo cuerpo esta incompleto a proposito: es el espacio donde el
 * equipo programa (busquen el banner <code>PLACEHOLDER</code>). Un
 * <code>// TODO equipo:</code> es una tarea puntual dentro de un metodo que ya
 * funciona. En VS Code se encuentran todos con Ctrl+Shift+F buscando
 * <code>TODO equipo</code> o <code>PLACEHOLDER</code>.</p>
 *
 * <p><b>Importante:</b> cada equipo construye SU propia version: claves,
 * habitaciones, arte, textos y melodias distintas. El trabajo se rige por el
 * Codigo de Etica del curso (en el sitio): entreguen solo lo que puedan
 * explicar.</p>
 *
 * <p>Compilar y ejecutar: <code>javac *.java</code> y <code>java Main</code>.
 * Documentacion: <code>javadoc -d docs -charset UTF-8 *.java</code>.</p>
 */
public class Main {

    /** El lector de teclado unico de todo el juego. */
    public static Scanner lector = new Scanner(System.in);

    /** La matriz VISUAL de la habitacion actual: lo que se VE (habitacionN.txt). */
    public static char[][] visual;

    /**
     * La matriz de MOVIMIENTO (zonasN.txt), del mismo tamano que la visual:
     * que ES cada casilla ('.' piso, '#' solido, letra = objeto).
     */
    public static char[][] zonas;

    /** La tabla de objetosN.csv: [0] simbolo | [1] nombre | [2] archivo | [3] detalle. */
    public static String[][] objetos;

    /** En que habitacion esta el jugador: el progreso del juego. */
    public static int habitacionActual = 1;

    /** Cuantas habitaciones tiene el juego: la victoria esta tras la ultima. */
    public static final int TOTAL_HABITACIONES = 3; // subir a medida que se agreguen habitaciones

    /** Habitacion 3: la secuencia del cofre de hierro (U arriba, R derecha, L izquierda, D abajo). */
    public static final String SECUENCIA_COFRE_3 = "URUL";

    /** Habitacion 3: el codigo de 3 digitos del escudo de la armadura (lo da la balanza). */
    public static final String CODIGO_ESCUDO_3 = "137";

    /** Habitacion 3: true cuando la balanza ya dio el codigo 1-3-7. */
    public static boolean balanzaResuelta = false;

    /** Habitacion 3: true cuando el altar recibio la gema y solto el pasador de la puerta. */
    public static boolean mecanismoActivo = false;

    /** El codigo de 3 digitos del baul de la habitacion 2 (libros: 3 rojos, 1 azul, 5 amarillos). */
    public static final String CODIGO_BAUL_2 = "315";

    /** La clave fija de la puerta de la habitacion 2 (esta en la nota secreta del librero). */
    public static final String CLAVE_HABITACION_2 = "8420";

    /** Habitacion 2: true cuando el jugador ya vio el BUHO en el cuadro con luz UV. */
    public static boolean buhoDescubierto = false;

    /** El nombre del jugador; lo pide configurarJugador() (PLACEHOLDER 2). */
    public static String nombreJugador = "aventurero";

    /** La dificultad elegida: 1 = facil, 2 = normal, 3 = dificil (PLACEHOLDER 2). */
    public static int dificultad = 1;

    /** El modo elegido en seleccionarTipoJuego() (PLACEHOLDER 1); 1 = historia. */
    public static int tipoJuego = 1;

    /** Modo 1: las 3 habitaciones una tras otra. */
    public static final int MODO_HISTORIA = 1;

    /** Modo 2: el jugador elige UNA habitacion y escapa solo de esa. */
    public static final int MODO_UNICA = 2;

    /** Modo 3: la historia completa con un limite de movimientos. */
    public static final int MODO_CONTRARRELOJ = 3;

    /** Cuantas pistas (tecla H) se permiten en dificultad NORMAL. */
    public static final int PISTAS_NORMAL = 5;

    /** Cuantas pistas se permiten en dificultad DIFICIL. */
    public static final int PISTAS_DIFICIL = 2;

    /** Cuantas claves incorrectas aguanta una puerta con teclado en dificultad DIFICIL. */
    public static final int MAX_INTENTOS_DIFICIL = 3;

    /** Los nombres de las dificultades (el indice es el numero 1 a 3). */
    public static final String[] NOMBRES_DIFICULTAD = {"", "FACIL", "NORMAL", "DIFICIL"};

    /** Los nombres de los modos (el indice es el numero 1 a 3). */
    public static final String[] NOMBRES_MODO = {"", "HISTORIA", "HABITACION UNICA", "CONTRARRELOJ"};

    /** Los nombres de las habitaciones (el indice es el numero de la habitacion). */
    public static final String[] NOMBRES_HABITACION = {"", "El despertar", "El estudio", "El laboratorio"};

    /** El texto de bienvenida de cada habitacion (el indice es su numero). */
    public static final String[] TEXTOS_ENTRADA = {
        "",
        "Despiertas en un cuarto frio. La unica salida es una puerta con un teclado numerico.",
        "Un estudio polvoriento: libros de colores, un baul, un cuadro y una caja con animales.",
        "Un laboratorio antiguo: frascos, una armadura con escudo y un altar con una ranura."
    };

    /** Cuantas pistas ha gastado el jugador en toda la partida. */
    public static int pistasUsadas = 0;

    /** Cuantos movimientos (W, A, S, D) ha hecho el jugador en toda la partida. */
    public static int movimientos = 0;

    /** En CONTRARRELOJ: cuantos movimientos le quedan al jugador. */
    public static int movimientosRestantes = 0;

    /** Cuantas claves incorrectas lleva en la puerta de la habitacion actual. */
    public static int intentosClave = 0;

    /** true cuando el jugador perdio (sin movimientos o teclado bloqueado). */
    public static boolean derrota = false;

    /** La razon de la derrota, para mostrarla al terminar. */
    public static String mensajeDerrota = "";

    /** La fila de la matriz donde esta parado el jugador. */
    public static int fila;

    /** La columna de la matriz donde esta parado el jugador. */
    public static int columna;

    /** El mensaje que se muestra bajo el mapa en cada turno. */
    public static String mensaje = "Despiertas encerrado y no recuerdas nada. Explora.";

    /** Los simbolos de los objetos ya mirados de cerca (sin repetir). */
    public static String mirados = "";

    /** El inventario del jugador: un arreglo de tamano fijo. */
    public static String[] inventario = new String[10];

    /** Cuantas casillas del inventario estan ocupadas. */
    public static int totalInventario = 0;

    /** El ancho del mapa en caracteres; se calcula de la matriz visual. */
    public static int anchoMapa;

    /** El ancho interior del panel derecho, en caracteres. */
    public static final int ANCHO_PANEL = 26;

    /** El ancho de las escenas y pantallas grandes, en caracteres. */
    public static final int ANCHO_ESCENA = 64;

    /**
     * El numero grabado en el cofre. Se sortea al cargar cada habitacion
     * (dos digitos, par y sin terminar en cero) para que la clave cambie
     * en cada partida.
     */
    public static int numeroCofre;

    /** Las 5 operaciones posibles del puzzle; la nota revela la sorteada. */
    public static String[] operaciones = {
        "el DOBLE del numero",
        "la MITAD del numero",
        "el numero MAS 10",
        "el numero MENOS 10",
        "el numero escrito AL REVES"
    };