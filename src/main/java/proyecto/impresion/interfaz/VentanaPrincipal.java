package proyecto.impresion.interfaz;

import proyecto.impresion.estructuras.ListaEnlazada;
import proyecto.impresion.modelo.Documento;
import proyecto.impresion.modelo.RegistroImpresion;
import proyecto.impresion.modelo.RegistroUsuarioCola;
import proyecto.impresion.modelo.TipoUsuario;
import proyecto.impresion.modelo.Usuario;
import proyecto.impresion.servicio.GestorImpresion;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

/**
 * Ventana principal de la simulación.
 * Permite gestionar usuarios, documentos y la cola de impresión.
 */
public class VentanaPrincipal extends JFrame {
    private final GestorImpresion gestorImpresion;

    private final JLabel etiquetaReloj;
    private final DefaultListModel<String> modeloListaUsuarios;
    private final DefaultListModel<String> modeloListaDocumentos;
    private final JList<String> listaUsuarios;
    private final JList<String> listaDocumentos;
    private final JTextArea areaColaLineal;
    private final JTree arbolMonticulo;

    private final JTextField campoNuevoUsuario;
    private final JComboBox<TipoUsuario> comboTipoUsuario;

    private final JTextField campoNombreDocumento;
    private final javax.swing.JSpinner spinnerTamanoDocumento;
    private final JTextField campoTipoDocumento;
    private final JCheckBox checkPrioritario;

    private final JComboBox<String> comboUsuarioEliminarEnCola;
    private final JComboBox<String> comboDocumentoEliminarEnCola;

    /**
     * Crea la ventana y deja todos los componentes listos para usar.
     */
    public VentanaPrincipal() {
        super("Proyecto 2 EDD - Cola de Impresión");
        this.gestorImpresion = new GestorImpresion();

        this.etiquetaReloj = new JLabel("Tiempo simulación: 0 s", SwingConstants.LEFT);
        this.modeloListaUsuarios = new DefaultListModel<>();
        this.modeloListaDocumentos = new DefaultListModel<>();
        this.listaUsuarios = new JList<>(modeloListaUsuarios);
        this.listaDocumentos = new JList<>(modeloListaDocumentos);
        this.areaColaLineal = new JTextArea();
        this.areaColaLineal.setEditable(false);
        this.arbolMonticulo = new JTree(new DefaultMutableTreeNode("Cola vacía"));

        this.campoNuevoUsuario = new JTextField();
        this.comboTipoUsuario = new JComboBox<>(TipoUsuario.values());
        this.campoNuevoUsuario.setToolTipText("Nombre del usuario");

        this.campoNombreDocumento = new JTextField();
        this.spinnerTamanoDocumento = new javax.swing.JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        this.campoTipoDocumento = new JTextField();
        this.checkPrioritario = new JCheckBox("Enviar como prioritario");

        this.campoNombreDocumento.setToolTipText("Nombre del documento");
        this.spinnerTamanoDocumento.setToolTipText("Cantidad de páginas");
        this.campoTipoDocumento.setToolTipText("Tipo o extensión del documento");

        this.comboUsuarioEliminarEnCola = new JComboBox<>();
        this.comboDocumentoEliminarEnCola = new JComboBox<>();

        configurarVentana();
        construirInterfaz();
        inicializarEventos();
        iniciarRelojVisual();
        refrescarTodo();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 740));
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel contenedorPrincipal = new JPanel(new BorderLayout(10, 10));
        contenedorPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenedorPrincipal.add(etiquetaReloj, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.add("Usuarios y Documentos", construirPanelUsuariosDocumentos());
        pestanas.add("Cola de Impresión", construirPanelColaImpresion());

        contenedorPrincipal.add(pestanas, BorderLayout.CENTER);
        setContentPane(contenedorPrincipal);
    }

    private JPanel construirPanelUsuariosDocumentos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel panelUsuarios = new JPanel(new GridLayout(3, 1, 8, 8));
        panelUsuarios.setBorder(BorderFactory.createTitledBorder("Gestión de usuarios"));

        JPanel filaEtiquetasUsuario = new JPanel(new GridLayout(1, 4, 8, 8));
        filaEtiquetasUsuario.add(new JLabel("Nombre del usuario"));
        filaEtiquetasUsuario.add(new JLabel("Tipo de usuario"));
        filaEtiquetasUsuario.add(new JLabel(""));
        filaEtiquetasUsuario.add(new JLabel(""));

        JPanel filaAgregar = new JPanel(new GridLayout(1, 4, 8, 8));
        filaAgregar.add(campoNuevoUsuario);
        filaAgregar.add(comboTipoUsuario);
        JButton botonAgregarUsuario = new JButton("Agregar usuario");
        botonAgregarUsuario.setActionCommand("agregar_usuario");
        JButton botonEliminarUsuario = new JButton("Eliminar usuario");
        botonEliminarUsuario.setActionCommand("eliminar_usuario");
        filaAgregar.add(botonAgregarUsuario);
        filaAgregar.add(botonEliminarUsuario);

        JButton botonCargarCsv = new JButton("Cargar usuarios CSV");
        botonCargarCsv.setActionCommand("cargar_csv");

        panelUsuarios.add(filaEtiquetasUsuario);
        panelUsuarios.add(filaAgregar);
        panelUsuarios.add(botonCargarCsv);

        JPanel panelListas = new JPanel(new GridLayout(1, 2, 10, 10));
        JScrollPane scrollUsuarios = new JScrollPane(listaUsuarios);
        scrollUsuarios.setBorder(BorderFactory.createTitledBorder("Usuarios"));
        JScrollPane scrollDocumentos = new JScrollPane(listaDocumentos);
        scrollDocumentos.setBorder(BorderFactory.createTitledBorder("Documentos del usuario seleccionado"));
        panelListas.add(scrollUsuarios);
        panelListas.add(scrollDocumentos);

        JPanel panelDocumentos = new JPanel(new GridLayout(4, 1, 8, 8));
        panelDocumentos.setBorder(BorderFactory.createTitledBorder("Operaciones con documentos"));

        JPanel filaDocEtiquetas = new JPanel(new GridLayout(1, 3, 8, 8));
        filaDocEtiquetas.add(new JLabel("Nombre del documento"));
        filaDocEtiquetas.add(new JLabel("Tamaño (páginas)"));
        filaDocEtiquetas.add(new JLabel("Tipo de documento"));

        JPanel filaDocDatos = new JPanel(new GridLayout(1, 3, 8, 8));
        filaDocDatos.add(campoNombreDocumento);
        filaDocDatos.add(spinnerTamanoDocumento);
        filaDocDatos.add(campoTipoDocumento);

        JPanel filaDocBotones = new JPanel(new GridLayout(1, 3, 8, 8));
        JButton botonCrearDoc = new JButton("Crear documento");
        botonCrearDoc.setActionCommand("crear_documento");
        JButton botonEliminarDoc = new JButton("Eliminar documento creado");
        botonEliminarDoc.setActionCommand("eliminar_documento_creado");
        JButton botonEnviarDoc = new JButton("Enviar a cola");
        botonEnviarDoc.setActionCommand("enviar_documento");
        filaDocBotones.add(botonCrearDoc);
        filaDocBotones.add(botonEliminarDoc);
        filaDocBotones.add(botonEnviarDoc);

        panelDocumentos.add(filaDocEtiquetas);
        panelDocumentos.add(filaDocDatos);
        panelDocumentos.add(checkPrioritario);
        panelDocumentos.add(filaDocBotones);

        panel.add(panelUsuarios, BorderLayout.NORTH);
        panel.add(panelListas, BorderLayout.CENTER);
        panel.add(panelDocumentos, BorderLayout.SOUTH);

        botonAgregarUsuario.addActionListener(e -> agregarUsuario());
        botonEliminarUsuario.addActionListener(e -> eliminarUsuario());
        botonCargarCsv.addActionListener(e -> cargarCsvUsuarios());
        botonCrearDoc.addActionListener(e -> crearDocumento());
        botonEliminarDoc.addActionListener(e -> eliminarDocumentoCreado());
        botonEnviarDoc.addActionListener(e -> enviarDocumento());

        return panel;
    }

    private JPanel construirPanelColaImpresion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel barraAcciones = new JPanel(new GridLayout(1, 2, 8, 8));
        JButton botonLiberar = new JButton("Liberar impresora (eliminar_min)");
        JButton botonRefrescar = new JButton("Refrescar vista de cola");
        barraAcciones.add(botonLiberar);
        barraAcciones.add(botonRefrescar);

        JScrollPane scrollColaLineal = new JScrollPane(areaColaLineal);
        scrollColaLineal.setBorder(BorderFactory.createTitledBorder("Vista lineal de la cola"));

        JScrollPane scrollArbol = new JScrollPane(arbolMonticulo);
        scrollArbol.setBorder(BorderFactory.createTitledBorder("Vista de árbol (montículo binario)"));

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollColaLineal, scrollArbol);
        division.setResizeWeight(0.5);

        JPanel panelEliminar = new JPanel(new GridLayout(2, 3, 8, 8));
        panelEliminar.setBorder(BorderFactory.createTitledBorder("Eliminar documento en cola por usuario"));
        panelEliminar.add(new JLabel("Usuario"));
        panelEliminar.add(new JLabel("Documento en cola"));
        panelEliminar.add(new JLabel("Acción"));
        panelEliminar.add(comboUsuarioEliminarEnCola);
        panelEliminar.add(comboDocumentoEliminarEnCola);
        JButton botonEliminarCola = new JButton("Eliminar de cola");
        panelEliminar.add(botonEliminarCola);

        panel.add(barraAcciones, BorderLayout.NORTH);
        panel.add(division, BorderLayout.CENTER);
        panel.add(panelEliminar, BorderLayout.SOUTH);

        botonLiberar.addActionListener(e -> liberarImpresora());
        botonRefrescar.addActionListener(e -> refrescarTodo());
        botonEliminarCola.addActionListener(e -> eliminarDocumentoEnColaPorUsuario());
        comboUsuarioEliminarEnCola.addActionListener(e -> refrescarComboDocumentosEnCola());

        return panel;
    }

    private void inicializarEventos() {
        listaUsuarios.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refrescarListaDocumentos();
            }
        });

        listaDocumentos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String seleccionado = listaDocumentos.getSelectedValue();
                if (seleccionado != null && seleccionado.contains("|")) {
                    String nombre = seleccionado.split("\\|")[0].trim();
                    campoNombreDocumento.setText(nombre);
                }
            }
        });
    }

    private void iniciarRelojVisual() {
        Timer temporizador = new Timer(500, e ->
            etiquetaReloj.setText("Tiempo simulación: "
                + formatearDuracion(gestorImpresion.obtenerTiempoSimulacion())));
        temporizador.start();
    }

    private void agregarUsuario() {
        try {
            gestorImpresion.agregarUsuario(campoNuevoUsuario.getText(), (TipoUsuario) comboTipoUsuario.getSelectedItem());
            campoNuevoUsuario.setText("");
            refrescarTodo();
            mostrarInfo("Usuario agregado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarUsuario() {
        String usuarioId = obtenerUsuarioSeleccionado();
        if (usuarioId == null) {
            mostrarError("Seleccione un usuario para eliminar.");
            return;
        }
        try {
            gestorImpresion.eliminarUsuario(usuarioId);
            refrescarTodo();
            mostrarInfo("Usuario eliminado. Los documentos en cola se conservaron.");
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarCsvUsuarios() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Seleccionar archivo CSV de usuarios");
        int resultado = selector.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        try {
            int agregados = gestorImpresion.cargarUsuariosDesdeCsv(archivo);
            refrescarTodo();
            mostrarInfo("Carga completada. Usuarios agregados: " + agregados);
        } catch (Exception ex) {
            mostrarError("No se pudo cargar el archivo: " + ex.getMessage());
        }
    }

    private void crearDocumento() {
        String usuarioId = obtenerUsuarioSeleccionado();
        if (usuarioId == null) {
            mostrarError("Seleccione un usuario para crear el documento.");
            return;
        }
        try {
            int paginas = (int) spinnerTamanoDocumento.getValue();
            gestorImpresion.crearDocumento(usuarioId, campoNombreDocumento.getText(), paginas, campoTipoDocumento.getText());
            campoNombreDocumento.setText("");
            campoTipoDocumento.setText("");
            refrescarTodo();
            mostrarInfo("Documento creado correctamente.");
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarDocumentoCreado() {
        String usuarioId = obtenerUsuarioSeleccionado();
        if (usuarioId == null) {
            mostrarError("Seleccione un usuario.");
            return;
        }
        String nombreDocumento = obtenerDocumentoSeleccionado();
        if (nombreDocumento == null) {
            mostrarError("Seleccione un documento.");
            return;
        }
        try {
            gestorImpresion.eliminarDocumentoCreado(usuarioId, nombreDocumento);
            refrescarTodo();
            mostrarInfo("Documento eliminado.");
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void enviarDocumento() {
        String usuarioId = obtenerUsuarioSeleccionado();
        if (usuarioId == null) {
            mostrarError("Seleccione un usuario.");
            return;
        }
        String nombreDocumento = obtenerDocumentoSeleccionado();
        if (nombreDocumento == null) {
            mostrarError("Seleccione un documento.");
            return;
        }
        try {
            RegistroImpresion registro = gestorImpresion.enviarDocumentoACola(
                    usuarioId,
                    nombreDocumento,
                    checkPrioritario.isSelected()
            );
            refrescarTodo();
            mostrarInfo("Documento encolado con etiqueta de tiempo: "
                    + formatearDuracion(registro.obtenerEtiquetaTiempo()));
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void liberarImpresora() {
        RegistroImpresion impreso = gestorImpresion.liberarImpresora();
        if (impreso == null) {
            mostrarInfo("No hay documentos en cola para imprimir.");
            return;
        }
        refrescarTodo();
        mostrarInfo("Documento impreso: " + impreso.obtenerNombreDocumento());
    }

    private void eliminarDocumentoEnColaPorUsuario() {
        String usuario = (String) comboUsuarioEliminarEnCola.getSelectedItem();
        String documento = (String) comboDocumentoEliminarEnCola.getSelectedItem();
        if (usuario == null || documento == null) {
            mostrarError("Seleccione usuario y documento en cola.");
            return;
        }

        boolean eliminado = gestorImpresion.eliminarDocumentoEnColaPorUsuario(usuario, documento);
        if (!eliminado) {
            mostrarError("No fue posible eliminar el documento de la cola.");
            return;
        }
        refrescarTodo();
        mostrarInfo("Documento eliminado de la cola sin imprimirse.");
    }

    /**
     * Actualiza todos los paneles visibles de la interfaz.
     */
    private void refrescarTodo() {
        refrescarListaUsuarios();
        refrescarListaDocumentos();
        refrescarVistaColaLineal();
        refrescarVistaArbol();
        refrescarCombosEliminacionCola();
    }

    private void refrescarListaUsuarios() {
        String usuarioPrevio = obtenerUsuarioSeleccionado();
        modeloListaUsuarios.clear();
        for (Usuario usuario : gestorImpresion.obtenerUsuarios()) {
            modeloListaUsuarios.addElement(usuario.obtenerIdentificador() + " | " + usuario.obtenerTipoUsuario().obtenerEtiquetaCsv());
        }
        if (usuarioPrevio != null) {
            seleccionarUsuarioPorId(usuarioPrevio);
        }
    }

    private void refrescarListaDocumentos() {
        modeloListaDocumentos.clear();
        String usuarioId = obtenerUsuarioSeleccionado();
        if (usuarioId == null) {
            return;
        }
        Usuario usuario = gestorImpresion.obtenerUsuario(usuarioId);
        if (usuario == null) {
            return;
        }
        for (Documento documento : usuario.obtenerDocumentos()) {
            modeloListaDocumentos.addElement(documento.toString());
        }
    }

    private void refrescarVistaColaLineal() {
        RegistroImpresion[] cola = gestorImpresion.obtenerColaLineal();
        if (cola.length == 0) {
            areaColaLineal.setText("Cola vacía");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cola.length; i++) {
            RegistroImpresion registro = cola[i];
            builder.append(i)
                    .append(" -> ")
                    .append("[id=").append(registro.obtenerIdRegistro()).append("] ")
                    .append(registro.obtenerNombreDocumento())
                    .append(" | ").append(registro.obtenerTamanoPaginas()).append(" pág. | ")
                    .append(registro.obtenerTipoDocumento())
                    .append(" | t=").append(formatearDuracion(registro.obtenerEtiquetaTiempo()))
                    .append(" | ").append(registro.esPrioritario() ? "PRIORITARIO" : "NORMAL")
                    .append(System.lineSeparator());
        }
        areaColaLineal.setText(builder.toString());
    }

    private void refrescarVistaArbol() {
        RegistroImpresion[] cola = gestorImpresion.obtenerColaLineal();
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Montículo (" + cola.length + " nodos)");
        if (cola.length == 0) {
            raiz.add(new DefaultMutableTreeNode("vacío"));
        } else {
            construirNodosArbol(cola, 0, raiz);
        }
        arbolMonticulo.setModel(new DefaultTreeModel(raiz));
        for (int i = 0; i < arbolMonticulo.getRowCount(); i++) {
            arbolMonticulo.expandRow(i);
        }
    }

    private void construirNodosArbol(RegistroImpresion[] cola, int indice, DefaultMutableTreeNode padre) {
        if (indice >= cola.length) {
            return;
        }
        RegistroImpresion registro = cola[indice];
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(
                "id=" + registro.obtenerIdRegistro() + " | " + registro.obtenerNombreDocumento() + " | t="
                + formatearDuracion(registro.obtenerEtiquetaTiempo()));
        padre.add(nodo);

        construirNodosArbol(cola, indice * 2 + 1, nodo);
        construirNodosArbol(cola, indice * 2 + 2, nodo);
    }

    private void refrescarCombosEliminacionCola() {
        String usuarioPrevio = (String) comboUsuarioEliminarEnCola.getSelectedItem();
        DefaultComboBoxModel<String> modeloUsuarios = new DefaultComboBoxModel<>();

        ListaEnlazada<String> usuariosConCola = gestorImpresion.obtenerUsuariosConRegistrosEnCola();
        for (String usuario : usuariosConCola) {
            modeloUsuarios.addElement(usuario);
        }
        comboUsuarioEliminarEnCola.setModel(modeloUsuarios);

        if (usuarioPrevio != null) {
            comboUsuarioEliminarEnCola.setSelectedItem(usuarioPrevio);
        }
        refrescarComboDocumentosEnCola();
    }

    private void refrescarComboDocumentosEnCola() {
        String usuario = (String) comboUsuarioEliminarEnCola.getSelectedItem();
        DefaultComboBoxModel<String> modeloDocumentos = new DefaultComboBoxModel<>();

        if (usuario != null) {
            ListaEnlazada<RegistroUsuarioCola> registros = gestorImpresion.obtenerRegistrosActivosPorUsuario(usuario);
            for (RegistroUsuarioCola registro : registros) {
                modeloDocumentos.addElement(registro.obtenerNombreDocumento());
            }
        }

        comboDocumentoEliminarEnCola.setModel(modeloDocumentos);
    }

    private String obtenerUsuarioSeleccionado() {
        String seleccionado = listaUsuarios.getSelectedValue();
        if (seleccionado == null || !seleccionado.contains("|")) {
            return null;
        }
        return seleccionado.split("\\|")[0].trim();
    }

    private String obtenerDocumentoSeleccionado() {
        String seleccionado = listaDocumentos.getSelectedValue();
        if (seleccionado == null || !seleccionado.contains("|")) {
            return null;
        }
        return seleccionado.split("\\|")[0].trim();
    }

    private void seleccionarUsuarioPorId(String usuarioId) {
        for (int i = 0; i < modeloListaUsuarios.getSize(); i++) {
            String valor = modeloListaUsuarios.get(i);
            if (valor.startsWith(usuarioId + " |")) {
                listaUsuarios.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Muestra una alerta de error al usuario.
     *
     * @param mensaje texto a mostrar.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje informativo al usuario.
     *
     * @param mensaje texto a mostrar.
     */
    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Formatea una duración en milisegundos a un texto fácil de leer.
     *
     * @param milisegundos tiempo en milisegundos.
     * @return tiempo en formato s, min/s o h/min/s.
     */
    private String formatearDuracion(long milisegundos) {
        boolean negativo = milisegundos < 0;
        long segundosTotales = Math.abs(milisegundos) / 1000;

        String valor;
        if (segundosTotales < 60) {
            valor = segundosTotales + " s";
        } else if (segundosTotales < 3600) {
            long minutos = segundosTotales / 60;
            long segundos = segundosTotales % 60;
            valor = minutos + " min " + segundos + " s";
        } else {
            long horas = segundosTotales / 3600;
            long minutos = (segundosTotales % 3600) / 60;
            long segundos = segundosTotales % 60;
            valor = horas + " h " + minutos + " min " + segundos + " s";
        }
        return negativo ? "-" + valor : valor;
    }
}
