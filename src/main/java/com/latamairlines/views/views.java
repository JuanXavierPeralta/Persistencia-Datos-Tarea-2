package com.latamairlines.views;

import com.latamairlines.dao.ReservaDAO;
import com.latamairlines.entities.Reserva;
import javax.swing.JOptionPane;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import com.latamairlines.dao.ClienteDAO;
import com.latamairlines.dao.VueloDAO;
import com.latamairlines.entities.Cliente;
import com.latamairlines.entities.Vuelo;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

public class views extends javax.swing.JFrame {

    // ACCESO A DATOS
    private final ReservaDAO dao = new ReservaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VueloDAO vueloDAO = new VueloDAO();

    private static final java.util.logging.Logger logger
            = java.util.logging.Logger.getLogger(views.class.getName());

    public views() {

        initComponents();

        configurarTabla();
        configurarCombos();
        setLocationRelativeTo(null);

        try {
            cargarOpciones();
            mostrarReservas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Error al cargar los datos: " + e.getMessage()
            );
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dao.cerrar();
                clienteDAO.cerrar();
                vueloDAO.cerrar();
            }
        });
    }

    // MOSTRAR RESERVAS
    private void mostrarReservas() {

        List<Reserva> reservas = dao.listarReservas();

        DefaultTableModel modelo
                = (DefaultTableModel) tblReservas.getModel();

        modelo.setRowCount(0);

        for (Reserva reserva : reservas) {
            modelo.addRow(new Object[]{
                reserva.getId(),
                reserva.getCliente().getNombre(),
                reserva.getVuelo().getCodigo(),
                reserva.getVuelo().getOrigen(),
                reserva.getVuelo().getDestino(),
                reserva.getNumeroPasajeros(),
                reserva.getPago() == null
                ? "Pendiente"
                : reserva.getPago().getMonto().toPlainString()
            });
        }
    }

    // CONFIGURAR TABLA
    private void centrarTabla() {

        // DATOS CENTRADOS SIN NEGRITA
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        centro.setFont(new java.awt.Font("Bahnschrift", java.awt.Font.PLAIN, 18));

        for (int i = 0; i < tblReservas.getColumnCount(); i++) {
            tblReservas.getColumnModel()
                    .getColumn(i).setCellRenderer(centro);
        }

        // ENCABEZADOS CENTRADOS EN NEGRITA
        javax.swing.table.JTableHeader encabezado = tblReservas.getTableHeader();
        encabezado.setFont(new java.awt.Font("Bahnschrift", java.awt.Font.BOLD, 14));

        DefaultTableCellRenderer renderer
                = (DefaultTableCellRenderer) encabezado.getDefaultRenderer();

        renderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    }

    private void configurarCombos() {

        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Cliente) {
                    Cliente cliente = (Cliente) value;
                    setText(cliente.getNombre()
                            + " | " + cliente.getIdentificacion());
                } else {
                    setText("Seleccione un cliente");
                }

                return this;
            }
        });

        cbVuelo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Vuelo) {
                    Vuelo vuelo = (Vuelo) value;

                    String fecha = vuelo.getFechaSalida().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    );

                    setText(vuelo.getCodigo()
                            + " | " + vuelo.getOrigen()
                            + " → " + vuelo.getDestino()
                            + " | " + fecha);
                } else {
                    setText("Seleccione un vuelo");
                }

                return this;
            }
        });
    }

    private void cargarOpciones() {

        // CONSULTAR ANTES DE MODIFICAR LOS COMBOS
        List<Cliente> clientes = clienteDAO.listarClientes();
        List<Vuelo> vuelos = vueloDAO.listarVuelos();

        cbCliente.removeAllItems();
        for (Cliente cliente : clientes) {
            cbCliente.addItem(cliente);
        }

        cbVuelo.removeAllItems();
        for (Vuelo vuelo : vuelos) {
            cbVuelo.addItem(vuelo);
        }

        cbCliente.setSelectedIndex(-1);
        cbVuelo.setSelectedIndex(-1);
    }

    private void configurarTabla() {

        tblReservas.setModel(new DefaultTableModel(
                new Object[]{
                    "ID", "Cliente", "Vuelo",
                    "Origen", "Destino", "Pasajeros", "Pago"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 5) {
                    return Integer.class;
                }
                return String.class;
            }
        });

        tblReservas.setRowHeight(28);
        centrarTabla();
    }

    private void limpiarCampos() {
        tfID.setText("");
        tfNumPasajeros.setText("");
        cbCliente.setSelectedIndex(-1);
        cbVuelo.setSelectedIndex(-1);
    }

    private int leerEnteroPositivo(String texto, String campo) {

        try {
            int valor = Integer.parseInt(texto.trim());

            if (valor <= 0) {
                throw new NumberFormatException();
            }

            return valor;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    campo + " debe ser un número entero mayor a cero"
            );
        }
    }

    private void seleccionarCliente(Integer id) {

        cbCliente.setSelectedIndex(-1);

        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getId().equals(id)) {
                cbCliente.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarVuelo(Integer id) {

        cbVuelo.setSelectedIndex(-1);

        for (int i = 0; i < cbVuelo.getItemCount(); i++) {
            if (cbVuelo.getItemAt(i).getId().equals(id)) {
                cbVuelo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void refrescarTabla() {
        try {
            mostrarReservas();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "La operación se completó, pero no se pudo "
                    + "actualizar la tabla: " + e.getMessage()
            );
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelLATAMAIRLINES = new javax.swing.JLabel();
        labelVUELO = new javax.swing.JLabel();
        labelNUMPASAJEROS = new javax.swing.JLabel();
        labelCLIENTE = new javax.swing.JLabel();
        labelID = new javax.swing.JLabel();
        tfID = new javax.swing.JTextField();
        tfNumPasajeros = new javax.swing.JTextField();
        btnActualizar = new javax.swing.JButton();
        btnCrear = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnMostrar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservas = new javax.swing.JTable();
        labelREGISTRORESERVAS = new javax.swing.JLabel();
        cbCliente = new javax.swing.JComboBox<>();
        cbVuelo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        labelLATAMAIRLINES.setBackground(new java.awt.Color(51, 102, 255));
        labelLATAMAIRLINES.setFont(new java.awt.Font("Bahnschrift", 1, 24)); // NOI18N
        labelLATAMAIRLINES.setForeground(new java.awt.Color(0, 0, 0));
        labelLATAMAIRLINES.setText("LATAM Airlines | Gestión de reservas");

        labelVUELO.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        labelVUELO.setText("Vuelo");

        labelNUMPASAJEROS.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        labelNUMPASAJEROS.setText("Número de Pasajeros");

        labelCLIENTE.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        labelCLIENTE.setText("Cliente");

        labelID.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        labelID.setText("ID de Reserva");

        tfID.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N

        tfNumPasajeros.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N

        btnActualizar.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        btnCrear.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnCrear.setText("Crear");
        btnCrear.addActionListener(this::btnCrearActionPerformed);

        btnBuscar.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(this::btnBuscarActionPerformed);

        btnMostrar.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnMostrar.setText("Mostrar");
        btnMostrar.addActionListener(this::btnMostrarActionPerformed);

        btnEliminar.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        tblReservas.setFont(new java.awt.Font("Bahnschrift", 0, 18)); // NOI18N
        tblReservas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Cliente", "Vuelo", "Origen", "Destino", "Pasajeros", "Pago"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblReservas.setToolTipText("");
        jScrollPane1.setViewportView(tblReservas);

        labelREGISTRORESERVAS.setFont(new java.awt.Font("Bahnschrift", 1, 24)); // NOI18N
        labelREGISTRORESERVAS.setForeground(new java.awt.Color(0, 0, 0));
        labelREGISTRORESERVAS.setText("Registro de Reservas");

        cbCliente.addActionListener(this::cbClienteActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(labelREGISTRORESERVAS, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLATAMAIRLINES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(183, 183, 183)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelNUMPASAJEROS, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelCLIENTE, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfNumPasajeros, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(51, 51, 51)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelVUELO, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(labelID, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tfID, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cbVuelo, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(21, 21, 21))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(btnCrear)
                        .addGap(39, 39, 39)
                        .addComponent(btnBuscar)
                        .addGap(31, 31, 31)
                        .addComponent(btnActualizar)
                        .addGap(43, 43, 43)
                        .addComponent(btnEliminar)
                        .addGap(56, 56, 56)
                        .addComponent(btnMostrar)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(labelLATAMAIRLINES, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelVUELO)
                    .addComponent(labelCLIENTE, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbVuelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(labelNUMPASAJEROS, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfNumPasajeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBuscar)
                    .addComponent(btnActualizar)
                    .addComponent(btnCrear)
                    .addComponent(btnMostrar)
                    .addComponent(btnEliminar))
                .addGap(36, 36, 36)
                .addComponent(labelREGISTRORESERVAS, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearActionPerformed

        try {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vuelo vuelo = (Vuelo) cbVuelo.getSelectedItem();

            if (cliente == null || vuelo == null) {
                JOptionPane.showMessageDialog(
                        this, "Seleccione un cliente y un vuelo"
                );
                return;
            }

            int pasajeros = leerEnteroPositivo(
                    tfNumPasajeros.getText(), "El número de pasajeros"
            );

            Reserva reserva = new Reserva(cliente, vuelo, pasajeros);
            dao.crearReserva(reserva);

            limpiarCampos();

            JOptionPane.showMessageDialog(
                    this, "Reserva creada correctamente"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Error al crear reserva: " + e.getMessage()
            );
            return;
        }

        refrescarTabla();

    }//GEN-LAST:event_btnCrearActionPerformed

    private void btnMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarActionPerformed
        try {
            cargarOpciones();
            mostrarReservas();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Error al cargar los datos: " + e.getMessage()
            );
}    }//GEN-LAST:event_btnMostrarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed

        try {
            int id = leerEnteroPositivo(
                    tfID.getText(), "El ID de reserva"
            );

            Reserva reserva = dao.buscarPorId(id);

            if (reserva == null) {
                JOptionPane.showMessageDialog(
                        this, "Reserva no encontrada"
                );
                return;
            }

            cargarOpciones();

            seleccionarCliente(reserva.getCliente().getId());
            seleccionarVuelo(reserva.getVuelo().getId());

            tfNumPasajeros.setText(
                    String.valueOf(reserva.getNumeroPasajeros())
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Error al buscar reserva: " + e.getMessage()
            );
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed

        try {
            int id = leerEnteroPositivo(
                    tfID.getText(), "El ID de reserva"
            );

            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            Vuelo vuelo = (Vuelo) cbVuelo.getSelectedItem();

            if (cliente == null || vuelo == null) {
                JOptionPane.showMessageDialog(
                        this, "Seleccione un cliente y un vuelo"
                );
                return;
            }

            int pasajeros = leerEnteroPositivo(
                    tfNumPasajeros.getText(), "El número de pasajeros"
            );

            dao.actualizarReserva(id, cliente, vuelo, pasajeros);

            JOptionPane.showMessageDialog(
                    this, "Reserva actualizada correctamente"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Error al actualizar reserva: " + e.getMessage()
            );
            return;
        }

        refrescarTabla();

    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed

        try {
            int id = leerEnteroPositivo(
                    tfID.getText(), "El ID de reserva"
            );

            Reserva reserva = dao.buscarPorId(id);

            if (reserva == null) {
                JOptionPane.showMessageDialog(
                        this, "Reserva no encontrada"
                );
                return;
            }

            if (reserva.getPago() != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "La reserva tiene un pago registrado. "
                        + "Elimine primero el pago."
                );
                return;
            }

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea eliminar la reserva " + id + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            dao.eliminarReserva(id);
            limpiarCampos();

            JOptionPane.showMessageDialog(
                    this, "Reserva eliminada correctamente"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Error al eliminar reserva: " + e.getMessage()
            );
            return;
        }

        refrescarTabla();

    }//GEN-LAST:event_btnEliminarActionPerformed

    private void cbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbClienteActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new views().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnMostrar;
    private javax.swing.JComboBox<Cliente> cbCliente;
    private javax.swing.JComboBox<Vuelo> cbVuelo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelCLIENTE;
    private javax.swing.JLabel labelID;
    private javax.swing.JLabel labelLATAMAIRLINES;
    private javax.swing.JLabel labelNUMPASAJEROS;
    private javax.swing.JLabel labelREGISTRORESERVAS;
    private javax.swing.JLabel labelVUELO;
    private javax.swing.JTable tblReservas;
    private javax.swing.JTextField tfID;
    private javax.swing.JTextField tfNumPasajeros;
    // End of variables declaration//GEN-END:variables
}
