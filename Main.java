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