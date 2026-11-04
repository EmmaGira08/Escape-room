import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * ESCAPE: LA HABITACION - Motor del juego.
 *
 * <p>El juego completo son TRES habitaciones. Despiertas encerrado y debes
 * escapar de cada una resolviendo su puzzle. Antes de jugar se elige el modo
 * (historia, habitacion unica o contrarreloj), el nombre y la dificultad.</p>
 *
 * <p>Fuentes de verdad de cada habitacion N:</p>
 * <ul>
 *   <li><code>habitacionN.txt</code> - la matriz VISUAL (el dibujo).</li>
 *   <li><code>zonasN.txt</code> - la matriz de MOVIMIENTO (que ES cada casilla).</li>
 *   <li><code>objetosN.csv</code> - nombre, arte y detalle de cada objeto.</li>
 *   <li><code>arte/*.txt</code> y <code>musica/*.mid</code> - compartidos.</li>
 * </ul>
 *
 * <p>Los metodos con el banner <code>PLACEHOLDER</code> son los que el curso
 * pidio completar al equipo. Lo que sigue pendiente se marca con
 * <code>// TODO equipo:</code> (hoy: el bonus de habitaciones externas y el
 * easter egg de la tecla X). En VS Code se encuentran con Ctrl+Shift+F.</p>
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

    /** Habitacion 2: el codigo de 3 digitos del baul (libros: 3 rojos, 1 azul, 5 amarillos). */
    public static final String CODIGO_BAUL_2 = "315";

    /** Habitacion 2: la clave fija de la puerta (esta en la nota secreta del librero). */
    public static final String CLAVE_HABITACION_2 = "8420";

    /** Habitacion 2: true cuando el jugador ya vio el BUHO en el cuadro con luz UV. */
    public static boolean buhoDescubierto = false;

    /** Habitacion 3: la secuencia del cofre de hierro (U arriba, R derecha, L izquierda, D abajo). */
    public static final String SECUENCIA_COFRE_3 = "URUL";

    /** Habitacion 3: el codigo de 3 digitos del escudo de la armadura (lo da la balanza). */
    public static final String CODIGO_ESCUDO_3 = "137";

    /** Habitacion 3: true cuando la balanza ya dio el codigo 1-3-7. */
    public static boolean balanzaResuelta = false;

    /** Habitacion 3: true cuando el altar recibio la gema y solto el pasador de la puerta. */
    public static boolean mecanismoActivo = false;

    /** El nombre del jugador; lo pide configurarJugador() (PLACEHOLDER 2). */
    public static String nombreJugador = "aventurero";

    /** La dificultad elegida: 1 = facil, 2 = normal, 3 = dificil (PLACEHOLDER 2). */
    public static int dificultad = 1;

    /**
     * El modo elegido en seleccionarTipoJuego() (PLACEHOLDER 1): 1 = historia
     * (las 3 habitaciones una tras otra), 2 = habitacion unica, 3 = contrarreloj.
     */
    public static int tipoJuego = 1;

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

    /** El indice (0 a 4) de la operacion sorteada para esta habitacion. */
    public static int operacionElegida;

    /** La clave de la puerta: la operacion aplicada al numero del cofre. */
    public static String clave;

    /**
     * El ciclo de vida del juego: configuracion inicial (los placeholders),
     * carga de la habitacion 1 y el ciclo principal de comandos.
     */
    public static void main(String[] args) {
        Sonido.iniciar();

        // ---- Configuracion inicial (placeholders 1, 2 y 3) ----
        configurarArte();
        tipoJuego = seleccionarTipoJuego(); // en HABITACION UNICA deja la elegida en habitacionActual
        configurarJugador();

        if (!cargarMundo(habitacionActual)) {
            return; // sin datos no hay juego
        }
        mostrarEntradaHabitacion(habitacionActual, "");
        iniciarMusicaHabitacion(habitacionActual);

        boolean escapo = false;
        boolean jugando = true;
        while (jugando) {
            dibujarPantalla(panelEstado());
            System.out.print(" > ");
            String comando = lector.nextLine().trim().toUpperCase();
            if (comando.equals("Q")) {
                jugando = false;
            } else if (comando.equals("H")) {
                mensaje = pedirPista(); // respeta el limite de pistas de la dificultad
            } else if (comando.equals("M")) {
                Sonido.alternarMusica();
                mensaje = "Musica de fondo: " + (Sonido.musicaActiva ? "ACTIVADA" : "APAGADA");
            } else if (comando.equals("E")) {
                Sonido.alternarEfectos();
                mensaje = "Efectos de sonido: " + (Sonido.efectosActivos ? "ACTIVADOS" : "APAGADOS");
            } else if (comando.equals("X")) {
                easterEgg(); // la tecla secreta (no aparece en el panel)
            } else {
                escapo = mover(comando);
                if (!escapo) {
                    revisarMovimientos(); // CONTRARRELOJ: se acabaron los movimientos?
                }
                if (escapo || derrota) {
                    jugando = false;
                }
            }
        }

        if (!escapo) {
            System.out.println();
            if (derrota) {
                System.out.println(" " + mensajeDerrota);
            } else {
                System.out.println(" Te rindes. La habitacion gana esta vez...");
            }
        }
        Sonido.apagar(); // sin esto el programa no termina
    }

    /* ====================================================================
     *                    LOS PLACEHOLDERS DEL EQUIPO
     *
     * El motor YA llama a estos metodos. Completarlos es obligatorio para
     * terminar el juego. Cada uno explica su contrato en el javadoc.
     * ==================================================================== */

    /**
     * PLACEHOLDER 1 - EL TIPO DE JUEGO.
     *
     * <p>Debe mostrar un menu inicial y leer la opcion del jugador (validada,
     * como leerOpcion()). Modos sugeridos: 1 = HISTORIA (las 3 habitaciones),
     * 2 = HABITACION UNICA (elegir cual y escapar solo de esa),
     * 3 = CONTRARRELOJ (historia con movimientos limitados). El modo elegido
     * se guarda en tipoJuego y el resto del motor debe respetarlo.</p>
     *
     * @return el modo elegido por el jugador (1, 2 o 3).
     */
    public static int seleccionarTipoJuego() {
        System.out.println(" ============ MODO DE JUEGO ============");
        System.out.println("   1. HISTORIA          las " + TOTAL_HABITACIONES + " habitaciones, una tras otra");
        System.out.println("   2. HABITACION UNICA  eliges una y escapas solo de esa");
        System.out.println("   3. CONTRARRELOJ      la historia con movimientos limitados");
        System.out.println();
        int modo = leerEntero(" Elige el modo (1-3): ", 1, 3);

        habitacionActual = 1; // por defecto se empieza en la primera
        if (modo == MODO_UNICA) {
            for (int i = 1; i <= TOTAL_HABITACIONES; i++) {
                System.out.println("   " + i + ". " + NOMBRES_HABITACION[i]);
            }
            habitacionActual = leerEntero(" Habitacion (1-" + TOTAL_HABITACIONES + "): ", 1, TOTAL_HABITACIONES);
        }
        System.out.println();
        return modo;
    }

    /**
     * Cuantos movimientos tiene el jugador en CONTRARRELOJ segun la dificultad.
     * La ruta perfecta de las 3 habitaciones cuesta 227 movimientos, asi que
     * incluso en DIFICIL (330) queda algo de margen para equivocarse.
     *
     * @return el limite de movimientos de la partida.
     */
    public static int limiteMovimientos() {
        if (dificultad == 1) {
            return 600;
        }
        if (dificultad == 2) {
            return 450;
        }
        return 330;
    }

    /**
     * Cuantas pistas permite la dificultad actual.
     *
     * @return -1 si son ilimitadas (FACIL); si no, el maximo de la partida.
     */
    public static int limitePistas() {
        if (dificultad == 1) {
            return -1;
        }
        if (dificultad == 2) {
            return PISTAS_NORMAL;
        }
        return PISTAS_DIFICIL;
    }

    /**
     * La tecla H: entrega la pista de la habitacion solo si la dificultad aun
     * permite pistas, y cuenta la que se gasta.
     *
     * @return la pista, o el aviso de que ya no quedan.
     */
    public static String pedirPista() {
        int limite = limitePistas();
        if (limite >= 0 && pistasUsadas >= limite) {
            return "Ya gastaste tus " + limite + " pistas. Ahora estas solo, " + nombreJugador + ".";
        }
        pistasUsadas++;
        String pista = darPista();
        if (limite >= 0) {
            pista = pista + " (" + (limite - pistasUsadas) + " restantes)";
        }
        return pista;
    }

    /** CONTRARRELOJ: si no quedan movimientos, el jugador pierde. */
    public static void revisarMovimientos() {
        if (tipoJuego == MODO_CONTRARRELOJ && movimientosRestantes <= 0) {
            derrota = true;
            mensajeDerrota = "Se acabo el tiempo: te quedaste sin movimientos, " + nombreJugador
                    + ". La habitacion gana esta vez...";
        }
    }

    /**
     * PLACEHOLDER 2 - LA TERMINAL INICIAL DEL JUGADOR.
     *
     * <p>Debe configurar la partida conversando con el jugador: preguntar su
     * nombre (guardarlo en nombreJugador y usarlo en los mensajes del juego),
     * elegir la dificultad 1-3 (guardarla en dificultad) y aplicarla: por
     * ejemplo en FACIL hay pistas ilimitadas, en NORMAL cuestan y en DIFICIL
     * la puerta admite maximo 3 intentos de clave. Tambien pueden ofrecer
     * aqui el estado inicial del sonido (M/E).</p>
     */
    public static void configurarJugador() {
        System.out.println(" ========== TERMINAL DEL JUGADOR ==========");
        nombreJugador = leerNombre();

        System.out.println();
        System.out.println("   1. FACIL    pistas ilimitadas");
        System.out.println("   2. NORMAL   " + PISTAS_NORMAL + " pistas en toda la partida");
        System.out.println("   3. DIFICIL  " + PISTAS_DIFICIL + " pistas y solo " + MAX_INTENTOS_DIFICIL
                + " intentos de clave por puerta");
        dificultad = leerEntero(" Dificultad (1-3): ", 1, 3);

        System.out.println();
        Sonido.musicaActiva = leerSiNo(" Musica de fondo? (S/N, ENTER = S): ");
        Sonido.efectosActivos = leerSiNo(" Efectos de sonido? (S/N, ENTER = S): ");

        // Se aplica la configuracion: contadores en cero y, en CONTRARRELOJ, el limite.
        pistasUsadas = 0;
        movimientos = 0;
        movimientosRestantes = (tipoJuego == MODO_CONTRARRELOJ) ? limiteMovimientos() : 0;

        System.out.println();
        System.out.println(" Bienvenido, " + nombreJugador + ". Modo " + NOMBRES_MODO[tipoJuego]
                + " | Dificultad " + NOMBRES_DIFICULTAD[dificultad]);
        if (tipoJuego == MODO_CONTRARRELOJ) {
            System.out.println(" Tienes " + movimientosRestantes + " movimientos. Cada W, A, S o D cuenta.");
        }
        System.out.println();
    }

    /**
     * Pide el nombre del jugador hasta que sea valido: maximo 15 caracteres,
     * solo letras, numeros y espacios. ENTER solo deja el nombre por defecto.
     *
     * @return el nombre elegido.
     */
    public static String leerNombre() {
        while (true) {
            System.out.print(" Tu nombre (ENTER = aventurero): ");
            String nombre = lector.nextLine().trim();
            if (nombre.isEmpty()) {
                return "aventurero";
            }
            boolean valido = nombre.length() <= 15;
            for (int i = 0; valido && i < nombre.length(); i++) {
                char c = nombre.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != ' ') {
                    valido = false;
                }
            }
            if (valido) {
                return nombre;
            }
            System.out.println(" Nombre invalido: maximo 15 letras, numeros o espacios.");
        }
    }

    /**
     * Lee un numero entero entre min y max, repitiendo la pregunta hasta que
     * la entrada sea valida. Nunca rompe el programa con letras o simbolos.
     *
     * @param pregunta el texto que se muestra antes de leer.
     * @param min      el valor minimo aceptado.
     * @param max      el valor maximo aceptado.
     * @return el numero elegido, dentro del rango.
     */
    public static int leerEntero(String pregunta, int min, int max) {
        while (true) {
            System.out.print(pregunta);
            String entrada = lector.nextLine().trim();
            boolean soloDigitos = !entrada.isEmpty() && entrada.length() <= 6;
            for (int i = 0; soloDigitos && i < entrada.length(); i++) {
                if (!Character.isDigit(entrada.charAt(i))) {
                    soloDigitos = false;
                }
            }
            if (soloDigitos) {
                int valor = Integer.parseInt(entrada);
                if (valor >= min && valor <= max) {
                    return valor;
                }
            }
            System.out.println(" Entrada invalida. Escribe un numero de " + min + " a " + max + ".");
        }
    }

    /**
     * Lee una respuesta si/no. ENTER cuenta como SI.
     *
     * @param pregunta el texto que se muestra antes de leer.
     * @return true para S o SI (o ENTER), false para N o NO.
     */
    public static boolean leerSiNo(String pregunta) {
        while (true) {
            System.out.print(pregunta);
            String entrada = lector.nextLine().trim().toUpperCase();
            if (entrada.isEmpty() || entrada.equals("S") || entrada.equals("SI")) {
                return true;
            }
            if (entrada.equals("N") || entrada.equals("NO")) {
                return false;
            }
            System.out.println(" Entrada invalida. Responde S o N.");
        }
    }

    /**
     * PLACEHOLDER 3 - EL ASCII ART DEL JUEGO.
     *
     * <p>Debe configurar la identidad visual: un titulo propio del equipo
     * (reemplacen arte/titulo.txt o elijan el archivo segun el tipo de juego
     * o la dificultad), y la decoracion de cada habitacion (por ejemplo un
     * arte distinto al entrar a la 2 y a la 3). Aqui se imprime el titulo del
     * equipo; el arte de cada habitacion lo muestra mostrarEntradaHabitacion().</p>
     */
    public static void configurarArte() {
        // El titulo propio del equipo; si falta el archivo, se usa el del profesor.
        String ruta = "arte/titulo_equipo.txt";
        if (!new File(ruta).exists()) {
            ruta = "arte/titulo.txt";
        }
        imprimirArte(ruta);
        System.out.println();
    }

    /**
     * La pantalla de entrada de una habitacion: su nombre, su arte
     * (arte/entrada_hN.txt), una frase de bienvenida y los datos de la partida.
     * Se muestra al empezar y en cada transicion entre habitaciones.
     *
     * @param numero la habitacion a la que entra el jugador.
     * @param saludo una frase previa (por ejemplo el escape de la anterior) o "".
     */
    public static void mostrarEntradaHabitacion(int numero, String saludo) {
        limpiarPantalla();
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        if (!saludo.isEmpty()) {
            System.out.println(centrar(saludo, ANCHO_ESCENA));
            System.out.println();
        }
        System.out.println(centrar("HABITACION " + numero + " DE " + TOTAL_HABITACIONES + ": "
                + NOMBRES_HABITACION[numero].toUpperCase(), ANCHO_ESCENA));
        System.out.println();
        imprimirArte("arte/entrada_h" + numero + ".txt");
        System.out.println();
        String[] lineas = partirTexto(TEXTOS_ENTRADA[numero], 48);
        for (int i = 0; i < lineas.length; i++) {
            System.out.println(centrar(lineas[i], ANCHO_ESCENA));
        }
        System.out.println();
        System.out.println(centrar(nombreJugador + " | " + NOMBRES_MODO[tipoJuego] + " | "
                + NOMBRES_DIFICULTAD[dificultad], ANCHO_ESCENA));
        System.out.println();
        System.out.println(bordeEscena());
        System.out.print(centrar("[ ENTER para entrar ]", ANCHO_ESCENA));
        lector.nextLine();
    }

    /**
     * PLACEHOLDER 4 - LA NAVEGACION COMPLETA DEL JUEGO.
     *
     * <p>Se llama cuando el jugador abre la puerta de una habitacion que NO
     * es la ultima. Debe llevarlo a la siguiente: sumar 1 a habitacionActual,
     * llamar cargarMundo(habitacionActual) con los archivos que ustedes crean
     * (habitacion2.txt, zonas2.txt, objetos2.csv... y los de la 3), mostrar
     * una transicion (arte + mensaje) y retornar false para que el ciclo siga
     * jugando. El inventario se conserva entre habitaciones: usenlo para que
     * un objeto de la habitacion 1 sirva en la 3.</p>
     *
     * @return true si el juego debe terminar; false para seguir jugando en la
     *         nueva habitacion.
     */
    public static boolean siguienteHabitacion() {
        int anterior = habitacionActual;
        Sonido.detenerMusica(); // al cruzar la puerta calla la musica de la habitacion que dejas
        habitacionActual++;
        if (!cargarMundo(habitacionActual)) {
            System.out.println(" No se pudieron cargar los archivos de la habitacion "
                    + habitacionActual + ". Fin del juego.");
            return true; // sin datos no hay juego
        }
        mostrarEntradaHabitacion(habitacionActual,
                "Escapaste de la habitacion " + anterior + ". La puerta se cierra tras de ti.");
        mensaje = "Entras a la habitacion " + habitacionActual + ". Tu inventario sigue contigo.";
        iniciarMusicaHabitacion(habitacionActual);
        return false; // el ciclo sigue jugando en la nueva habitacion
    }

    /**
     * Pone la musica de fondo de una habitacion: la 1 usa musica/ambiente.mid
     * y las demas musica/ambienteN.mid (la 2 usa ambiente2.mid). Si el archivo
     * de esa habitacion no existe, usa musica/ambiente.mid. Siempre suena UNA
     * sola cancion: Sonido.musicaFondo calla la anterior antes de empezar.
     *
     * @param numero el numero de la habitacion a la que entra el jugador.
     */
    public static void iniciarMusicaHabitacion(int numero) {
        String ruta = (numero == 1) ? "musica/ambiente.mid" : "musica/ambiente" + numero + ".mid";
        if (!new File(ruta).exists()) {
            ruta = "musica/ambiente.mid";
        }
        Sonido.musicaFondo(ruta);
    }

    /**
     * PLACEHOLDER BONUS - HABITACIONES DE OTROS EQUIPOS (+10%).
     *
     * <p>Como todas las habitaciones del salon usan el mismo formato
     * (habitacionN.txt + zonasN.txt + objetosN.csv + su arte), es posible
     * armar un juego GIGANTE conectando habitaciones de varios equipos.
     * Solo queda indicado: si lo intentan, el plan es:</p>
     * <ol>
     *   <li>Acordar con otro equipo el intercambio de la carpeta de una
     *       habitacion (sus 3 archivos + los arte/ que use).</li>
     *   <li>Copiar los archivos con un numero nuevo (ej. habitacion4.txt) y
     *       sus artes a arte/ sin pisar los propios.</li>
     *   <li>Llamar este metodo desde siguienteHabitacion() cuando el jugador
     *       cruce la ultima puerta propia, y subir TOTAL_HABITACIONES.</li>
     *   <li>Verificar que los simbolos y claves del otro equipo no choquen
     *       con los suyos (cada CSV trae los suyos, asi que basta cargarlo).</li>
     * </ol>
     *
     * @param numero el numero con el que se copiaron los archivos del otro equipo.
     * @return true si la habitacion externa quedo cargada y lista para jugar.
     */
    public static boolean conectarHabitacionExterna(int numero) {
        // TODO equipo (bonus): cargar y jugar la habitacion de otro equipo.
        return false;
    }

    /* ==================== CARGA DEL MUNDO ==================== */

    /**
     * Carga todos los datos de una habitacion, reinicia su estado y sortea
     * su puzzle. Sirve para cualquiera de las habitaciones (1, 2 o 3).
     *
     * @param numero el numero de la habitacion (sus archivos llevan el sufijo).
     * @return true si todos los archivos cargaron bien.
     */
    public static boolean cargarMundo(int numero) {
        visual = cargarMatriz("habitacion" + numero + ".txt");
        zonas = cargarMatriz("zonas" + numero + ".txt");
        objetos = cargarObjetos("objetos" + numero + ".csv");
        if (visual == null || zonas == null || objetos == null) {
            return false;
        }
        calcularAnchoMapa();
        buscarInicio();
        reiniciarEstadoHabitacion();
        sortearPuzzle();
        if (numero == 2) {
            clave = CLAVE_HABITACION_2; // la habitacion 2 tiene su propio puzzle: clave fija
        }
        return true;
    }

    /** Cada habitacion empieza limpia: sin objetos mirados, intentos ni mecanismos activados. */
    public static void reiniciarEstadoHabitacion() {
        mirados = "";
        intentosClave = 0;
        buhoDescubierto = false;
        balanzaResuelta = false;
        mecanismoActivo = false;
    }

    /** Sortea el numero del cofre y la operacion, y calcula la clave de la puerta. */
    public static void sortearPuzzle() {
        // El numero del cofre: decena 1-9 y unidad par distinta de cero,
        // asi la MITAD es exacta y el numero AL REVES no empieza por cero.
        int decena = 1 + (int) (Math.random() * 9);
        int[] unidadesPares = {2, 4, 6, 8};
        int unidad = unidadesPares[(int) (Math.random() * unidadesPares.length)];
        numeroCofre = decena * 10 + unidad;

        operacionElegida = (int) (Math.random() * operaciones.length);
        clave = calcularClave(operacionElegida, numeroCofre);
    }

    /** El ancho del mapa es el de la fila mas larga de la matriz visual. */
    public static void calcularAnchoMapa() {
        anchoMapa = 0;
        for (int i = 0; i < visual.length; i++) {
            if (visual[i].length > anchoMapa) {
                anchoMapa = visual[i].length;
            }
        }
    }

    /**
     * Encuentra la @ en la matriz visual: ahi arranca el jugador.
     * Se borra del dibujo porque el @ se superpone al dibujar.
     */
    public static void buscarInicio() {
        fila = 1;
        columna = 1; // posicion de emergencia si olvidan la @
        for (int i = 0; i < visual.length; i++) {
            for (int j = 0; j < visual[i].length; j++) {
                if (visual[i][j] == '@') {
                    fila = i;
                    columna = j;
                    visual[i][j] = ' ';
                }
            }
        }
    }

    /* ==================== PANTALLA 1: LA HABITACION ==================== */

    /**
     * La pantalla principal: la habitacion (matriz visual con el @ del
     * jugador superpuesto) a la izquierda y el panel a la derecha.
     *
     * @param panel las lineas del panel derecho (estado o preview).
     */
    public static void dibujarPantalla(String[] panel) {
        limpiarPantalla(); // la habitacion se redibuja en el mismo lugar, sin apilar cuadros
        System.out.println();
        System.out.println(" +============ ESCAPE: LA HABITACION ============+");
        System.out.println();
        int total = Math.max(visual.length, panel.length);
        for (int i = 0; i < total; i++) {
            String izquierda = (i < visual.length) ? filaVisual(i) : repetir(' ', anchoMapa);
            String derecha = (i < panel.length) ? panel[i] : "";
            System.out.println(" " + izquierda + "   " + derecha);
        }
        System.out.println();
        System.out.println(" " + mensaje);
    }

    /** Si es false, el juego no borra la consola (util para depurar con todo el historial). */
    public static boolean limpiarConsola = true;

    /**
     * Borra la consola y deja el cursor arriba, para que cada pantalla
     * REEMPLACE a la anterior en vez de apilarse debajo. En Windows usa
     * "cls"; en Linux, Mac y la terminal de VS Code usa el codigo ANSI.
     * Si la consola no soporta ninguno, el juego sigue igual (solo se apila).
     */
    public static void limpiarPantalla() {
        if (!limpiarConsola) {
            return;
        }
        try {
            String sistema = System.getProperty("os.name").toLowerCase();
            if (sistema.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // sin limpieza: la pantalla simplemente se imprime debajo
        }
    }

    /** Una fila de la matriz visual como texto, con el jugador (@) encima. */
    public static String filaVisual(int i) {
        String linea = "";
        for (int j = 0; j < anchoMapa; j++) {
            if (i == fila && j == columna) {
                linea = linea + '@';
            } else if (j < visual[i].length) {
                linea = linea + visual[i][j];
            } else {
                linea = linea + ' '; // relleno si la linea del txt quedo corta
            }
        }
        return linea;
    }

    /** El panel cuando no hay objeto cerca: progreso, inventario y comandos. */
    public static String[] panelEstado() {
        int lineasInventario = Math.max(1, totalInventario);
        int extra = (tipoJuego == MODO_CONTRARRELOJ) ? 1 : 0; // la linea de movimientos
        String[] contenido = new String[7 + extra + lineasInventario];
        int k = 0;
        contenido[k++] = "Jugador: " + nombreJugador;
        contenido[k++] = "Dificultad: " + NOMBRES_DIFICULTAD[dificultad];
        contenido[k++] = "Habitacion " + habitacionActual + " de " + TOTAL_HABITACIONES;
        contenido[k++] = "Objetos mirados: " + mirados.length() + " de " + objetos.length;
        if (limitePistas() < 0) {
            contenido[k++] = "Pistas: ilimitadas";
        } else {
            contenido[k++] = "Pistas: " + (limitePistas() - pistasUsadas) + " de " + limitePistas();
        }
        if (extra == 1) {
            contenido[k++] = "Movimientos: " + movimientosRestantes;
        }
        contenido[k++] = "";
        contenido[k++] = "Inventario:";
        if (totalInventario == 0) {
            contenido[k++] = "  (vacio)";
        } else {
            for (int i = 0; i < totalInventario; i++) {
                contenido[k++] = "  - " + inventario[i];
            }
        }

        return panelConPie(contenido, new String[] {
            "", " Moverse: W A S D", " Pista: H | Rendirse: Q", " Musica: M | Efectos: E"
        });
    }

    /** Enmarca el contenido en una caja y le pone debajo las lineas de ayuda (el pie). */
    public static String[] panelConPie(String[] contenido, String[] pie) {
        String[] caja = enmarcar(contenido);
        String[] panel = new String[caja.length + pie.length];
        for (int i = 0; i < caja.length; i++) {
            panel[i] = caja[i];
        }
        for (int i = 0; i < pie.length; i++) {
            panel[caja.length + i] = pie[i];
        }
        return panel;
    }

    /**
     * Intenta mover al jugador. La matriz de ZONAS decide que pasa:
     * '.' piso (avanza), letra A-Z objeto (pregunta), otro solido (bloquea).
     *
     * @param comando W, A, S o D en mayuscula.
     * @return true solo si el jugador escapo y el juego termina.
     */
    public static boolean mover(String comando) {
        int nuevaFila = fila;
        int nuevaColumna = columna;
        if (comando.equals("W")) {
            nuevaFila--;        // arriba = una fila menos
        } else if (comando.equals("S")) {
            nuevaFila++;        // abajo = una fila mas
        } else if (comando.equals("D")) {
            nuevaColumna++;     // derecha = una columna mas
        } else if (comando.equals("A")) {
            nuevaColumna--;     // izquierda = una columna menos
        } else {
            mensaje = "No entiendo '" + comando + "'. Usa W A S D (H = pista, Q = rendirse).";
            return false;
        }

        movimientos++; // cada W, A, S o D cuenta (tambien chocar con un muro)
        if (tipoJuego == MODO_CONTRARRELOJ) {
            movimientosRestantes--;
        }

        char casilla = casillaZona(nuevaFila, nuevaColumna);
        if (casilla == '.') {
            fila = nuevaFila;
            columna = nuevaColumna;
            mensaje = "Caminas hacia " + nombreDireccion(comando) + ".";
        } else if (casilla >= 'A' && casilla <= 'Z') {
            return preguntarObjeto(casilla); // chocaste con un objeto
        } else {
            Sonido.golpe();
            mensaje = "Algo solido te bloquea el paso.";
        }
        return false;
    }

    /** La zona de una casilla; fuera de la matriz todo es solido. */
    public static char casillaZona(int f, int c) {
        if (f < 0 || f >= zonas.length) {
            return '#';
        }
        if (c < 0 || c >= zonas[f].length) {
            return '#';
        }
        return zonas[f][c];
    }

    /** El nombre en palabras de una tecla de movimiento. */
    public static String nombreDireccion(String comando) {
        if (comando.equals("W")) return "arriba";
        if (comando.equals("S")) return "abajo";
        if (comando.equals("D")) return "la derecha";
        return "la izquierda";
    }

    /* ==================== PANTALLA 2: LA PREGUNTA ==================== */

    /**
     * El jugador choco con un objeto: se redibuja la pantalla con el preview
     * en el panel y se pregunta que hacer. Aqui viven los comportamientos
     * especiales (la alfombra, el cofre y la nota son los moldes a imitar).
     *
     * @param simbolo la letra del objeto en la matriz de zonas.
     * @return true si el objeto era la puerta final y el jugador escapo.
     */
    public static boolean preguntarObjeto(char simbolo) {
        String[] objeto = buscarObjeto(simbolo);
        if (objeto == null) {
            mensaje = "Hay un objeto sin registrar ('" + simbolo + "'). Revisen el CSV.";
            return false;
        }

        dibujarPantalla(panelObjeto(objeto));
        int opcion = leerOpcion();
        if (opcion == 2) {
            mensaje = "Decides seguir explorando.";
            return false;
        }

        // COMPORTAMIENTOS ESPECIALES por objeto van aqui.
        // Cada habitacion tiene sus propios simbolos: se despacha por habitacion
        // para que una 'C' de la habitacion 2 no se comporte como el cofre de la 1.
        if (habitacionActual == 2) {
            return objetoHabitacion2(simbolo, objeto);
        }
        if (habitacionActual == 3) {
            return objetoHabitacion3(simbolo, objeto);
        }

        // La alfombra entrega la llave al inventario la primera vez.
        if (simbolo == 'A') {
            abrirEscena(objeto);
            recordarMirado(simbolo);
            if (!tieneObjeto("llave oxidada")) {
                guardarObjeto("llave oxidada");
                Sonido.recoger();
                mensaje = "Guardaste la LLAVE OXIDADA en tu inventario.";
            } else {
                mensaje = "Bajo la alfombra ya solo queda polvo.";
            }
            return false;
        }

        // El cofre exige la llave: sin ella no revela su numero.
        if (simbolo == 'C') {
            if (!tieneObjeto("llave oxidada")) {
                abrirEscena(objeto); // el cofre cerrado (arte y detalle del CSV)
                Sonido.error();
                mensaje = "El candado del cofre no cede. Algo debe abrirlo...";
            } else {
                Sonido.exito();
                escenaConEnter("el cofre abierto", "cofre_abierto",
                        "Usas la llave oxidada y el candado cae. CLIC. Grabado en la tapa esta el numero "
                        + numeroCofre + ".");
                recordarMirado(simbolo);
                mensaje = "El cofre revelo su numero: " + numeroCofre + ".";
            }
            return false;
        }

        // La nota revela la operacion sorteada en esta habitacion.
        if (simbolo == 'N') {
            escenaConEnter(objeto[1], objeto[2],
                    objeto[3] + " Hoy la operacion es: " + operaciones[operacionElegida] + ".");
            recordarMirado(simbolo);
            mensaje = "Miraste " + objeto[1] + ".";
            return false;
        }

        if (simbolo == 'P') {
            return abrirPuerta(objeto);
        }
        return mirarObjeto(simbolo, objeto);
    }

    /* ==================== HABITACION 2: EL ESTUDIO ==================== */

    /**
     * Los comportamientos de la habitacion 2. Cadena del puzzle:
     * perchero (nota: contar libros) -> librero (3 rojos, 1 azul, 5 amarillos)
     * -> baul (315) -> linterna UV -> cuadro (BUHO) -> caja del buho -> llave
     * dorada -> cajon del librero -> nota secreta (8420) -> puerta.
     *
     * @param simbolo la letra del objeto (R, C, F, N, A o P).
     * @param objeto  la fila del objetos2.csv de ese objeto.
     * @return true si el juego termina (la puerta de la ultima habitacion).
     */
    public static boolean objetoHabitacion2(char simbolo, String[] objeto) {
        if (simbolo == 'P') {
            return abrirPuerta(objeto);
        }

        // El perchero: la pista inicial (la nota del bolsillo del saco).
        if (simbolo == 'R') {
            abrirEscena(objeto);
            recordarMirado(simbolo);
            mensaje = "La nota del saco te manda a contar los libros de la repisa.";
            return false;
        }

        // El baul: pide el codigo de 3 digitos y entrega la linterna UV.
        if (simbolo == 'F') {
            return abrirBaul(objeto);
        }

        // El cuadro: con la linterna UV revela el BUHO.
        if (simbolo == 'N') {
            recordarMirado(simbolo);
            if (tieneObjeto("linterna UV")) {
                Sonido.exito();
                buhoDescubierto = true;
                escenaConEnter("el cuadro bajo la luz UV", "h2_cuadro_uv",
                        "Enciendes la linterna UV y el lienzo brilla: aparece el dibujo fluorescente de un BUHO.");
                mensaje = "El cuadro escondia un BUHO.";
            } else {
                abrirEscena(objeto);
                mensaje = "Hay algo en el cuadro, pero no logras verlo. Necesitas otra luz.";
            }
            return false;
        }

        // La caja mecanica: el dial se gira a la figura del buho y da la llave dorada.
        if (simbolo == 'A') {
            return abrirCajaBuho(objeto);
        }

        // El librero: el conteo de libros y, con la llave dorada, el cajon secreto.
        if (simbolo == 'C') {
            recordarMirado(simbolo);
            if (tieneObjeto("llave dorada")) {
                Sonido.exito();
                escenaConEnter("el cajon abierto", "h2_librero_abierto",
                        "La llave dorada gira. CLIC. Dentro del cajon hay una nota secreta con un codigo grabado: "
                        + CLAVE_HABITACION_2 + ".");
                guardarObjeto("nota secreta");
                mensaje = "Guardaste la NOTA SECRETA: el codigo es " + CLAVE_HABITACION_2 + ".";
            } else {
                abrirEscena(objeto);
                Sonido.error();
                mensaje = "El cajon secreto del librero tiene cerradura dorada. Esta cerrado.";
            }
            return false;
        }

        return mirarObjeto(simbolo, objeto);
    }

    /**
     * El baul del rincon: candado de 3 digitos (315). Al abrirlo entrega la
     * linterna UV; si ya estaba abierto, solo se muestra vacio.
     */
    public static boolean abrirBaul(String[] objeto) {
        recordarMirado('F');
        if (tieneObjeto("linterna UV")) {
            escenaConEnter("el baul abierto", "h2_baul_abierto", "El baul ya esta abierto y vacio.");
            mensaje = "El baul ya no guarda nada mas.";
            return false;
        }
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        String intento = pedirDato("Codigo (solo ENTER para alejarte): ");
        if (intento.equals(CODIGO_BAUL_2)) {
            entregarObjeto("linterna UV", "el baul abierto", "h2_baul_abierto",
                    "CLIC. El candado cede. Dentro hay una LINTERNA DE LUZ UV. La guardas en tu inventario.",
                    "Guardaste la LINTERNA UV en tu inventario.");
        } else if (intento.isEmpty()) {
            mensaje = "Te alejas del baul sin intentar nada.";
        } else {
            Sonido.error();
            mensaje = "CODIGO INCORRECTO. El candado no se mueve.";
        }
        return false;
    }

    /**
     * La caja mecanica del buho: un dial de animales. Solo la figura del
     * BUHO la abre y entrega la llave dorada.
     */
    public static boolean abrirCajaBuho(String[] objeto) {
        recordarMirado('A');
        if (tieneObjeto("llave dorada")) {
            escenaConEnter("la caja abierta", "h2_caja_abierta", "La caja ya esta abierta y vacia.");
            mensaje = "La caja ya no guarda nada mas.";
            return false;
        }
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        String animal = pedirDato("Girar el dial a (solo ENTER para alejarte): ").toLowerCase();
        if (animal.equals("buho") || animal.equals("b\u00faho")) {
            entregarObjeto("llave dorada", "la caja abierta", "h2_caja_abierta",
                    "El dial se alinea con el BUHO. CLIC. Dentro brilla una LLAVE DORADA. La guardas en tu inventario.",
                    "Guardaste la LLAVE DORADA en tu inventario.");
        } else if (animal.isEmpty()) {
            mensaje = "Te alejas de la caja sin tocar el dial.";
        } else if (animal.equals("gato") || animal.equals("pez")
                || animal.equals("rana") || animal.equals("zorro")) {
            Sonido.error();
            mensaje = "El dial gira a " + animal.toUpperCase() + " pero la caja no cede.";
        } else {
            Sonido.error();
            mensaje = "Esa figura no esta en el dial (GATO, PEZ, BUHO, RANA, ZORRO).";
        }
        return false;
    }

    /** La pista de la tecla H segun el avance del puzzle de la habitacion 2. */
    public static String darPistaHabitacion2() {
        if (!tieneObjeto("linterna UV")) {
            if (mirados.indexOf('R') < 0) {
                return "PISTA: el saco del perchero guarda una nota en el bolsillo.";
            }
            if (mirados.indexOf('C') < 0) {
                return "PISTA: la nota habla de contar libros. Mira el librero.";
            }
            return "PISTA: junta las cifras (rojos, azul, amarillos) y pruebalas en el baul del rincon.";
        }
        if (!buhoDescubierto) {
            return "PISTA: la linterna UV revela lo que el ojo no ve. Prueba con el cuadro.";
        }
        if (!tieneObjeto("llave dorada")) {
            return "PISTA: ya viste el animal oculto. La caja mecanica tiene un dial de animales.";
        }
        if (!tieneObjeto("nota secreta")) {
            return "PISTA: la llave dorada abre el cajon secreto del librero.";
        }
        return "PISTA: la nota secreta tiene el codigo de 4 digitos de la puerta.";
    }

    /* ==================== HABITACION 3: EL LABORATORIO ==================== */

    /**
     * Los comportamientos de la habitacion 3. Cadena del puzzle:
     * esqueleto (nota: ARRIBA-DERECHA-ARRIBA-IZQUIERDA) -> cofre de hierro
     * (cruz direccional) -> pociones (rojo 3, amarillo 7, azul 1) -> balanza
     * (orden AZUL-ROJO-AMARILLO = 1-3-7) -> escudo de la armadura (137) ->
     * gema verde -> altar -> el pasador suelta la puerta.
     *
     * @param simbolo la letra del objeto (E, F, M, C, A o P).
     * @param objeto  la fila del objetos3.csv de ese objeto.
     * @return true si el juego termina (la puerta de la ultima habitacion).
     */
    public static boolean objetoHabitacion3(char simbolo, String[] objeto) {
        if (simbolo == 'P') {
            return abrirPuertaAltar(objeto);
        }
        if (simbolo == 'F') {
            return abrirCofreHierro(objeto);
        }
        if (simbolo == 'M') {
            return usarBalanza(objeto);
        }
        if (simbolo == 'C') {
            return abrirEscudo(objeto);
        }
        if (simbolo == 'A') {
            return usarAltar(objeto);
        }
        // El esqueleto y cualquier otro objeto: solo se miran (la nota esta en el CSV).
        return mirarObjeto(simbolo, objeto);
    }

    /**
     * Convierte lo que escribe el jugador en letras de direccion. Acepta
     * palabras (arriba, abajo, izquierda, derecha) o teclas (W, S, A, D),
     * separadas por espacios, comas o guiones.
     *
     * @param texto la entrada del jugador.
     * @return por ejemplo "URUL", "" si no escribio nada, o null si algo no es una direccion.
     */
    public static String leerDirecciones(String texto) {
        String limpio = texto.toLowerCase().replace('-', ' ').replace(',', ' ').trim();
        if (limpio.isEmpty()) {
            return "";
        }
        String[] partes = limpio.split("\\s+");
        String letras = "";
        for (int i = 0; i < partes.length; i++) {
            if (partes[i].equals("arriba") || partes[i].equals("w")) {
                letras = letras + "U";
            } else if (partes[i].equals("derecha") || partes[i].equals("d")) {
                letras = letras + "R";
            } else if (partes[i].equals("abajo") || partes[i].equals("s")) {
                letras = letras + "D";
            } else if (partes[i].equals("izquierda") || partes[i].equals("a")) {
                letras = letras + "L";
            } else {
                return null;
            }
        }
        return letras;
    }

    /** El cofre de hierro: candado de cruz direccional; entrega las pociones. */
    public static boolean abrirCofreHierro(String[] objeto) {
        recordarMirado('F');
        if (tieneObjeto("pociones de colores")) {
            escenaConEnter("el cofre abierto", "h3_cofre_abierto", "El cofre ya esta abierto y vacio.");
            mensaje = "El cofre ya no guarda nada mas.";
            return false;
        }
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        String entrada = pedirDato("Direcciones (solo ENTER para alejarte): ");
        String letras = leerDirecciones(entrada);
        if (letras != null && letras.equals(SECUENCIA_COFRE_3)) {
            entregarObjeto("pociones de colores", "el cofre abierto", "h3_cofre_abierto",
                    "CLIC. El candado cede. Dentro hay 3 pociones: ROJA (3 oz) - AMARILLA (7 oz) - AZUL (1 oz). Las guardas en tu inventario.",
                    "Guardaste las POCIONES DE COLORES en tu inventario.");
        } else if (letras != null && letras.isEmpty()) {
            mensaje = "Te alejas del cofre sin intentar nada.";
        } else if (letras == null) {
            Sonido.error();
            mensaje = "Solo valen direcciones: arriba, abajo, izquierda, derecha (o W, S, A, D).";
        } else {
            Sonido.error();
            mensaje = "SECUENCIA INCORRECTA. El candado no se mueve.";
        }
        return false;
    }

    /** El peso en onzas de una pocion segun su color, o 0 si no es un color valido. */
    public static int pesoPocion(String color) {
        if (color.equals("rojo") || color.equals("roja")) {
            return 3;
        }
        if (color.equals("amarillo") || color.equals("amarilla")) {
            return 7;
        }
        if (color.equals("azul")) {
            return 1;
        }
        return 0;
    }

    /**
     * La balanza del mueble: el jugador pone las 3 pociones en orden. Si sigue
     * la secuencia grabada en la base (AZUL - ROJO - AMARILLO) la balanza marca
     * 1 - 3 - 7, el codigo del escudo.
     */
    public static boolean usarBalanza(String[] objeto) {
        recordarMirado('M');
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        if (!tieneObjeto("pociones de colores")) {
            esperarEnter();
            Sonido.error();
            mensaje = "La balanza esta vacia. Necesitas algo que pesar.";
            return false;
        }
        String entrada = pedirDato("Pociones en orden, ej: rojo azul amarillo (ENTER = alejarte): ");
        if (entrada.isEmpty()) {
            mensaje = "Te alejas de la balanza sin pesar nada.";
            return false;
        }
        String[] colores = entrada.toLowerCase().replace(',', ' ').replace('-', ' ').trim().split("\\s+");
        boolean valido = colores.length == 3;
        for (int i = 0; valido && i < 3; i++) {
            if (pesoPocion(colores[i]) == 0) {
                valido = false;
            }
        }
        if (valido && (colores[0].equals(colores[1]) || colores[0].equals(colores[2]) || colores[1].equals(colores[2]))) {
            valido = false;
        }
        if (!valido) {
            Sonido.error();
            mensaje = "Pon las 3 pociones, cada una una vez: rojo, amarillo y azul.";
            return false;
        }
        boolean enOrden = pesoPocion(colores[0]) == 1 && pesoPocion(colores[1]) == 3 && pesoPocion(colores[2]) == 7;
        String marca = pesoPocion(colores[0]) + " - " + pesoPocion(colores[1]) + " - " + pesoPocion(colores[2]);
        if (enOrden) {
            Sonido.exito();
            balanzaResuelta = true;
            escenaConEnter("la balanza", "h3_balanza",
                    "Pones las pociones en el orden de la base y la balanza marca " + marca
                    + ". Anotas el numero: " + CODIGO_ESCUDO_3 + ".");
            mensaje = "La balanza marco 1 - 3 - 7. Codigo: " + CODIGO_ESCUDO_3 + ".";
        } else {
            Sonido.error();
            mensaje = "La balanza marca " + marca + " pero no coincide con la secuencia grabada en la base.";
        }
        return false;
    }

    /** La armadura: el escudo tiene un candado de 3 digitos; detras esta la gema verde. */
    public static boolean abrirEscudo(String[] objeto) {
        recordarMirado('C');
        if (tieneObjeto("gema verde") || mecanismoActivo) {
            escenaConEnter("la armadura", "h3_armadura_abierta", "El escudo ya esta abierto y no queda nada detras.");
            mensaje = "La armadura ya no guarda nada mas.";
            return false;
        }
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        String codigo = pedirDato("Codigo de 3 digitos (solo ENTER para alejarte): ");
        if (codigo.equals(CODIGO_ESCUDO_3)) {
            entregarObjeto("gema verde", "el escudo abierto", "h3_armadura_abierta",
                    "CLIC. El escudo se abre y detras brilla la GEMA MISTICA VERDE. La guardas en tu inventario.",
                    "Guardaste la GEMA VERDE en tu inventario.");
        } else if (codigo.isEmpty()) {
            mensaje = "Te alejas de la armadura sin intentar nada.";
        } else {
            Sonido.error();
            mensaje = "CODIGO INCORRECTO. El candado del escudo no cede.";
        }
        return false;
    }

    /** El altar: al encajar la gema verde activa el mecanismo que suelta la puerta. */
    public static boolean usarAltar(String[] objeto) {
        recordarMirado('A');
        if (mecanismoActivo) {
            escenaConEnter("el altar activado", "h3_altar_activo", "La gema brilla en el altar. El mecanismo ya esta activo.");
            mensaje = "El altar ya esta activo. Ve a la puerta de salida.";
        } else if (tieneObjeto("gema verde")) {
            quitarObjeto("gema verde");
            mecanismoActivo = true;
            Sonido.exito();
            escenaConEnter("el altar activado", "h3_altar_activo",
                    "Encajas la gema verde en la ranura. El contrapeso baja y se oye un CLANK: el pasador de la puerta quedo libre.");
            mensaje = "El altar solto el pasador de la puerta.";
        } else {
            abrirEscena(objeto);
            Sonido.error();
            mensaje = "La ranura del altar esta vacia. Falta algo que encaje.";
        }
        return false;
    }

    /**
     * La puerta de la habitacion 3 no tiene teclado: solo se abre cuando el
     * altar activo el mecanismo. Al ser la ultima puerta, lleva a la victoria.
     */
    public static boolean abrirPuertaAltar(String[] objeto) {
        recordarMirado('P');
        if (!mecanismoActivo) {
            abrirEscena(objeto);
            Sonido.error();
            mensaje = "El pasador de hierro no se mueve. Algo debe soltarlo...";
            return false;
        }
        Sonido.exito();
        mostrarEscena("la puerta abierta", "h3_puerta_abierta",
                "El pasador se retira solo y la puerta se abre con un largo chirrido.");
        System.out.println(centrar("[ ENTER para cruzar ]", ANCHO_ESCENA));
        System.out.println(bordeEscena());
        lector.nextLine();
        return cruzarPuerta();
    }

    /** La pista de la tecla H segun el avance del puzzle de la habitacion 3. */
    public static String darPistaHabitacion3() {
        if (mirados.indexOf('E') < 0) {
            return "PISTA: el esqueleto encadenado sostiene una nota.";
        }
        if (!tieneObjeto("pociones de colores") && !balanzaResuelta && !tieneObjeto("gema verde") && !mecanismoActivo) {
            return "PISTA: la nota da una secuencia de direcciones. Pruebala en el cofre de hierro.";
        }
        if (!balanzaResuelta && !tieneObjeto("gema verde") && !mecanismoActivo) {
            return "PISTA: las pociones se pesan en la balanza del mueble. Sigue el orden de colores de la base.";
        }
        if (!tieneObjeto("gema verde") && !mecanismoActivo) {
            return "PISTA: el escudo de la armadura tiene un candado de 3 digitos. Usa lo que marco la balanza.";
        }
        if (!mecanismoActivo) {
            return "PISTA: la gema verde encaja en la ranura del altar de piedra.";
        }
        return "PISTA: el altar solto el pasador. Cruza la puerta de salida.";
    }

    /** Saca un objeto del inventario (por ejemplo la gema ya encajada en el altar). */
    public static void quitarObjeto(String nombre) {
        for (int i = 0; i < totalInventario; i++) {
            if (inventario[i].equals(nombre)) {
                for (int j = i; j < totalInventario - 1; j++) {
                    inventario[j] = inventario[j + 1];
                }
                inventario[totalInventario - 1] = null;
                totalInventario--;
                return;
            }
        }
    }

    /** Muestra un texto sin salto de linea y lee la respuesta del jugador (sin espacios). */
    public static String leerTexto(String texto) {
        System.out.print(texto);
        return lector.nextLine().trim();
    }

    /** El panel con el preview (mini arte) del objeto y la pregunta. */
    public static String[] panelObjeto(String[] objeto) {
        String[] mini = leerLineas("arte/mini_" + objeto[2] + ".txt");
        if (mini == null) {
            mini = new String[] { "( sin dibujo )" };
        }

        String[] contenido = new String[mini.length + 2];
        for (int i = 0; i < mini.length; i++) {
            contenido[i] = mini[i];
        }
        contenido[mini.length] = "";
        contenido[mini.length + 1] = centrar(objeto[1].toUpperCase(), ANCHO_PANEL - 2);

        return panelConPie(contenido, new String[] {
            "", " Que haces?", "   1. Mirar de cerca", "   2. Seguir explorando"
        });
    }

    /** Busca la fila del CSV cuyo simbolo coincide, o null si no existe. */
    public static String[] buscarObjeto(char simbolo) {
        for (int i = 0; i < objetos.length; i++) {
            if (objetos[i][0].charAt(0) == simbolo) {
                return objetos[i];
            }
        }
        return null;
    }

    /* ==================== PANTALLA 3: LA ESCENA ==================== */

    /** Abre la escena de un objeto del CSV y espera el ENTER. */
    public static void abrirEscena(String[] objeto) {
        escenaConEnter(objeto[1], objeto[2], objeto[3]);
    }

    /** Muestra una escena con su propio texto y espera el ENTER. */
    public static void escenaConEnter(String titulo, String archivo, String texto) {
        mostrarEscena(titulo, archivo, texto);
        esperarEnter();
    }

    /** Mira de cerca un objeto normal: abre su escena y lo recuerda (nunca termina el juego). */
    public static boolean mirarObjeto(char simbolo, String[] objeto) {
        abrirEscena(objeto);
        recordarMirado(simbolo);
        mensaje = "Miraste " + objeto[1] + ". Sigues frente al objeto.";
        return false;
    }

    /** Pide un dato dentro de una escena (codigo, secuencia...) y la cierra con el borde. */
    public static String pedirDato(String pregunta) {
        String dato = leerTexto(centrar(pregunta, ANCHO_ESCENA));
        System.out.println(bordeEscena());
        return dato;
    }

    /** Un candado que cede: suena el exito, el objeto va al inventario y se muestra la escena. */
    public static void entregarObjeto(String objeto, String titulo, String arte, String texto, String aviso) {
        Sonido.exito();
        guardarObjeto(objeto);
        escenaConEnter(titulo, arte, texto);
        Sonido.recoger();
        mensaje = aviso;
    }

    /** Pausa la escena hasta que el jugador presione ENTER. */
    public static void esperarEnter() {
        System.out.println(centrar("[ ENTER para volver a la habitacion ]", ANCHO_ESCENA));
        System.out.println(bordeEscena());
        lector.nextLine();
    }

    /**
     * Imprime una escena generica: titulo, arte grande y texto.
     * Los comportamientos especiales la reutilizan con sus propios textos.
     *
     * @param titulo  el nombre que encabeza la escena.
     * @param archivo el nombre base del arte (arte/archivo.txt).
     * @param texto   el detalle, partido automaticamente en lineas.
     */
    public static void mostrarEscena(String titulo, String archivo, String texto) {
        limpiarPantalla();
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        System.out.println(centrar("~ " + titulo.toUpperCase() + " ~", ANCHO_ESCENA));
        System.out.println();
        imprimirArte("arte/" + archivo + ".txt");
        System.out.println();
        String[] lineas = partirTexto(texto, 48);
        for (int i = 0; i < lineas.length; i++) {
            System.out.println(centrar(lineas[i], ANCHO_ESCENA));
        }
        System.out.println();
    }

    /** Se abrio una puerta: victoria si era la ultima (o modo UNICA); si no, la siguiente habitacion. */
    public static boolean cruzarPuerta() {
        if (tipoJuego == MODO_UNICA || habitacionActual >= TOTAL_HABITACIONES) {
            mostrarVictoria();
            return true;
        }
        return siguienteHabitacion(); // PLACEHOLDER 4: el viaje continua
    }

    /**
     * LA PUERTA pide la clave del puzzle. Si es la ultima habitacion el
     * jugador gana; si no, el viaje continua en siguienteHabitacion().
     *
     * @param objeto la fila del CSV de la puerta.
     * @return true si el juego termina.
     */
    public static boolean abrirPuerta(String[] objeto) {
        mostrarEscena(objeto[1], objeto[2], objeto[3]);
        recordarMirado('P');
        System.out.print(centrar("Clave (solo ENTER para alejarte): ", ANCHO_ESCENA));
        String intento = lector.nextLine().trim();
        if (intento.equals(clave)) {
            Sonido.exito();
            return cruzarPuerta();
        }
        if (intento.isEmpty()) {
            mensaje = "Te alejas de la puerta sin intentar nada.";
        } else {
            Sonido.error();
            intentosClave++;
            if (dificultad == 3) {
                // DIFICIL: la puerta solo admite MAX_INTENTOS_DIFICIL claves incorrectas.
                int restantes = MAX_INTENTOS_DIFICIL - intentosClave;
                if (restantes <= 0) {
                    derrota = true;
                    mensajeDerrota = "El teclado se bloquea tras " + MAX_INTENTOS_DIFICIL
                            + " claves incorrectas. Quedas encerrado para siempre, " + nombreJugador + ".";
                    mensaje = "TECLADO BLOQUEADO.";
                } else {
                    mensaje = "CLAVE INCORRECTA. Te quedan " + restantes
                            + (restantes == 1 ? " intento." : " intentos.");
                }
            } else {
                mensaje = "CLAVE INCORRECTA. El teclado parpadea en silencio.";
            }
        }
        return false;
    }

    /** La pantalla final del juego, con fanfarria y pausa para disfrutarla. */
    public static void mostrarVictoria() {
        limpiarPantalla();
        System.out.println();
        System.out.println(bordeEscena());
        System.out.println();
        imprimirArte("arte/victoria.txt");
        System.out.println();
        if (tipoJuego == MODO_UNICA) {
            System.out.println(centrar("Escapaste de la habitacion " + habitacionActual
                    + ". Eres libre, " + nombreJugador + ".", ANCHO_ESCENA));
        } else {
            System.out.println(centrar("Escapaste de las " + TOTAL_HABITACIONES
                    + " habitaciones. Eres libre, " + nombreJugador + ".", ANCHO_ESCENA));
        }
        System.out.println();
        // Resumen final de la partida.
        System.out.println(centrar("Modo: " + NOMBRES_MODO[tipoJuego] + " | Dificultad: "
                + NOMBRES_DIFICULTAD[dificultad], ANCHO_ESCENA));
        String resumen = "Movimientos: " + movimientos + " | Pistas usadas: " + pistasUsadas;
        if (tipoJuego == MODO_CONTRARRELOJ) {
            resumen = resumen + " | Te sobraron: " + movimientosRestantes;
        }
        System.out.println(centrar(resumen, ANCHO_ESCENA));
        System.out.println();
        System.out.println(bordeEscena());
        Sonido.detenerMusica();
        Sonido.victoria();
        System.out.println(centrar("[ ENTER para salir ]", ANCHO_ESCENA));
        lector.nextLine();
    }

    /* ==================== PUZZLE, PISTAS Y SECRETO ==================== */

    /**
     * Aplica la operacion sorteada al numero del cofre.
     *
     * @param operacion indice en el arreglo operaciones (0 a 4).
     * @param numero    el numero grabado en el cofre.
     * @return la clave resultante, como texto.
     */
    public static String calcularClave(int operacion, int numero) {
        if (operacion == 0) {
            return "" + (numero * 2);
        }
        if (operacion == 1) {
            return "" + (numero / 2);
        }
        if (operacion == 2) {
            return "" + (numero + 10);
        }
        if (operacion == 3) {
            return "" + (numero - 10);
        }
        // operacion 4: el numero escrito al reves (como cadena)
        String texto = "" + numero;
        String alReves = "";
        for (int i = texto.length() - 1; i >= 0; i--) {
            alReves = alReves + texto.charAt(i);
        }
        return alReves;
    }

    /**
     * La pista que toca segun la habitacion y el estado del juego (tecla H).
     * El limite de pistas segun la dificultad lo aplica pedirPista().
     */
    public static String darPista() {
        if (habitacionActual == 2) {
            return darPistaHabitacion2();
        }
        if (habitacionActual == 3) {
            return darPistaHabitacion3();
        }
        if (mirados.length() == 0) {
            return "PISTA: los objetos guardan secretos. Camina hacia uno y miralo de cerca.";
        }
        if (!tieneObjeto("llave oxidada")) {
            return "PISTA: en esta habitacion hay algo escondido DEBAJO de algo.";
        }
        if (mirados.indexOf('C') < 0) {
            return "PISTA: la llave que cargas abre algo... algo que guarda un numero.";
        }
        if (mirados.indexOf('N') < 0) {
            return "PISTA: ya tienes el numero. La nota dice que OPERACION aplicarle.";
        }
        return "PISTA: aplica la operacion de la nota al numero del cofre. El resultado abre la puerta.";
    }

    /**
     * EL EASTER EGG (tecla secreta X): una escena con la ira del dios Yigua.
     * Muestra un dibujo (arte/easter_egg.txt), el mensaje y un retumbar grave.
     * No gasta movimientos ni pistas: es solo un secreto para quien lo descubre.
     */
    public static void easterEgg() {
        mostrarEscena("la ira de Yigua", "easter_egg", "hiciste enojar al lider supremo Yigua");
        esperarEnter();
        mensaje = "Yigua te mira furioso... mejor sigue buscando la salida, " + nombreJugador + ".";
    }

    /* ==================== ESTADO DEL JUGADOR ==================== */

    /** Guarda algo en el inventario si hay espacio y no esta repetido. */
    public static void guardarObjeto(String nombre) {
        if (totalInventario < inventario.length && !tieneObjeto(nombre)) {
            inventario[totalInventario] = nombre;
            totalInventario++;
        }
    }

    /**
     * Recorre el inventario buscando un objeto por su nombre.
     *
     * @param nombre el nombre exacto guardado.
     * @return true si el jugador lo tiene.
     */
    public static boolean tieneObjeto(String nombre) {
        for (int i = 0; i < totalInventario; i++) {
            if (inventario[i].equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    /** Recuerda los objetos ya mirados (sin repetir) en una cadena. */
    public static void recordarMirado(char simbolo) {
        if (mirados.indexOf(simbolo) < 0) {
            mirados = mirados + simbolo;
        }
    }

    /* ==================== UTILIDADES DE TEXTO ==================== */

    /** El borde superior e inferior de las escenas. */
    public static String bordeEscena() {
        return "+" + repetir('=', ANCHO_ESCENA - 2) + "+";
    }

    /** Imprime un archivo de arte centrado como bloque (sin deformarlo). */
    public static void imprimirArte(String ruta) {
        String[] arte = leerLineas(ruta);
        if (arte == null) {
            System.out.println(centrar("( falta el dibujo: " + ruta + " )", ANCHO_ESCENA));
            return;
        }
        int masAncha = 0;
        for (int i = 0; i < arte.length; i++) {
            if (arte[i].length() > masAncha) {
                masAncha = arte[i].length();
            }
        }
        String margen = repetir(' ', (ANCHO_ESCENA - masAncha) / 2);
        for (int i = 0; i < arte.length; i++) {
            System.out.println(margen + arte[i]);
        }
    }

    /** Mete lineas de texto en una caja de ancho fijo. */
    public static String[] enmarcar(String[] lineas) {
        String borde = "+" + repetir('-', ANCHO_PANEL) + "+";
        String[] caja = new String[lineas.length + 2];
        caja[0] = borde;
        for (int i = 0; i < lineas.length; i++) {
            String linea = " " + lineas[i];
            while (linea.length() < ANCHO_PANEL) {
                linea = linea + " ";
            }
            if (linea.length() > ANCHO_PANEL) {
                linea = linea.substring(0, ANCHO_PANEL);
            }
            caja[i + 1] = "|" + linea + "|";
        }
        caja[lineas.length + 1] = borde;
        return caja;
    }

    /** Agrega espacios a la izquierda para centrar un texto en 'ancho'. */
    public static String centrar(String texto, int ancho) {
        int sobrante = ancho - texto.length();
        if (sobrante <= 0) {
            return texto;
        }
        return repetir(' ', sobrante / 2) + texto;
    }

    /** Construye una cadena repitiendo un caracter 'veces' veces. */
    public static String repetir(char c, int veces) {
        String resultado = "";
        for (int i = 0; i < veces; i++) {
            resultado = resultado + c;
        }
        return resultado;
    }

    /**
     * Parte un texto largo en lineas de maximo 'ancho' caracteres,
     * cortando por espacios para no romper las palabras.
     */
    public static String[] partirTexto(String texto, int ancho) {
        String[] palabras = texto.split(" ");
        String[] temporal = new String[palabras.length];
        int total = 0;
        String linea = "";
        for (int i = 0; i < palabras.length; i++) {
            if (linea.isEmpty()) {
                linea = palabras[i];
            } else if (linea.length() + 1 + palabras[i].length() <= ancho) {
                linea = linea + " " + palabras[i];
            } else {
                temporal[total] = linea;
                total++;
                linea = palabras[i];
            }
        }
        temporal[total] = linea;
        total++;

        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) {
            lineas[i] = temporal[i];
        }
        return lineas;
    }

    /* ==================== ENTRADA Y ARCHIVOS ==================== */

    /** Lee 1 o 2 validando la entrada. Nunca rompe el programa. */
    public static int leerOpcion() {
        int opcion = -1;
        do {
            System.out.print(" > Elige (1 o 2): ");
            String entrada = lector.nextLine().trim();
            if (entrada.equals("1") || entrada.equals("2")) {
                opcion = Integer.parseInt(entrada);
            } else {
                System.out.println(" Entrada invalida. Intenta de nuevo.");
            }
        } while (opcion == -1);
        return opcion;
    }

    /** Lee un archivo de texto completo y retorna sus lineas, o null. */
    public static String[] leerLineas(String ruta) {
        String[] temporal = new String[200];
        int total = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = br.readLine()) != null && total < 200) {
                temporal[total] = linea;
                total++;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
            return null;
        }
        String[] lineas = new String[total];
        for (int i = 0; i < total; i++) {
            lineas[i] = temporal[i];
        }
        return lineas;
    }

    /** Convierte un archivo de texto en una matriz de caracteres. */
    public static char[][] cargarMatriz(String ruta) {
        String[] lineas = leerLineas(ruta);
        if (lineas == null) {
            return null;
        }
        char[][] matriz = new char[lineas.length][];
        for (int i = 0; i < lineas.length; i++) {
            matriz[i] = lineas[i].toCharArray();
        }
        return matriz;
    }

    /** Lee un CSV de objetos (saltando el encabezado) a una matriz de String. */
    public static String[][] cargarObjetos(String ruta) {
        String[] lineas = leerLineas(ruta);
        if (lineas == null || lineas.length < 2) {
            System.out.println(ruta + " esta vacio o no existe.");
            return null;
        }
        String[][] tabla = new String[lineas.length - 1][];
        for (int i = 1; i < lineas.length; i++) {
            tabla[i - 1] = lineas[i].split(",");
        }
        return tabla;
    }
}
