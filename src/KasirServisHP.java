
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Administrator
 */
public class KasirServisHP extends javax.swing.JFrame {
    Connection conn;
    DefaultTableModel model;
    

    /**
     * Creates new form KasirServisHP
     */
    public KasirServisHP(String id) throws SQLException {
        initComponents();
        dimensi();
        timer();
        load_tabel();
        IDTRANS.setText(IDTRX());
        
        try{
            conn = Koneksi.configDB();
            System.out.println("Koneksi Berhasil");
        }catch(SQLException e){
            System.out.println("Koneksi Gagal");
        }
        
        HARGAT.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                hitung();
            }
            public void removeUpdate(DocumentEvent e) {
                hitung();
            }
            public void changedUpdate(DocumentEvent e) {
                hitung();
            }
        });

        SERVIS.addActionListener(e -> hitung());
        HARGAT.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
                hitung();
            }
        });
        
        if(id !=null && !id.isEmpty()){
            IDCUST.setText(id);
        }else{
            JOptionPane.showMessageDialog(this, "ID Konsumen tidak tersedia", "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
        }
        
        jTable1.addMouseListener(new java.awt.event.MouseAdapter(){
        public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    IDTRANS.setText(model.getValueAt(selectedRow, 0).toString());
                    IDCUST.setText(model.getValueAt(selectedRow, 1).toString());
                    SERVIS.setSelectedItem(model.getValueAt(selectedRow, 2).toString());
                    HARGAL.setText(model.getValueAt(selectedRow, 3).toString());
                    TAMBAHAN.setText(model.getValueAt(selectedRow, 4).toString());
                    HARGAT.setText(model.getValueAt(selectedRow, 5).toString());
                    DISKON.setText(model.getValueAt(selectedRow, 6).toString());
                    TOTAL.setText(model.getValueAt(selectedRow, 7).toString());
                }
            }
        });
    }

    public KasirServisHP() throws SQLException {
        this("");
    }
    
    public void setId(String id){
        IDCUST.setText(id);
    }
    
    private void timer(){
        Timer time = new Timer(10, e -> tanggal());
        time.start();
    }
    
    private void tanggal(){
        SimpleDateFormat date = new SimpleDateFormat("dd:MM:yyyy mm.ss.SSS");
        String tgl = date.format(new Date());
        Ltgl.setText(tgl);
    }
    
    private String IDTRX(){
        SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy-mmss");
        String tgl = date.format(new Date());
        return "TR" + tgl;
    }
    
    private void dimensi() {
        Dimension layar = Toolkit.getDefaultToolkit().getScreenSize();
        
        int x = (layar.width - getSize().width) / 2;
        int y = (layar.height - getSize().height) / 2;
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void hitung(){
        String pilih = (String) SERVIS.getSelectedItem();
        int hargal = 0;
        
        switch(pilih){
            case "Ganti Baterai":
                hargal = 50000;
                break;
            case "Ganti LCD":
                hargal = 100000;
                break;
            case "Perbaikan Tombol":
                hargal = 50000;
                break;
            case "Servis Mesin":
                hargal = 300000;
                break;
            case "Lainnya":
                hargal = 100000;
                break;
            default:
                hargal = 0;
        }
        HARGAL.setText(String.valueOf(hargal));
        try{
            int hargat = 0;
            String tambahan = HARGAT.getText();
            if(!tambahan.isEmpty()){
                hargat = Integer.parseInt(tambahan);
            }
            
            int jumlah = hargal + hargat;
            
            int diskon = 0;
            
            if (jumlah >= 1000000) {
                diskon = (int)(jumlah * 0.15);
            } else if (jumlah >= 500000) {
                diskon = (int)(jumlah * 0.10);
            }
            
            int total = jumlah - diskon;
            
            TOTAL.setText(String.valueOf(total));
            DISKON.setText(String.valueOf(diskon));
        }catch(NumberFormatException ex){
            TOTAL.setText("0");
            DISKON.setText("0");
        }
    }
    
    private void load_tabel() throws SQLException{
        model = new DefaultTableModel();
        model.addColumn("ID Trans");
        model.addColumn("ID Cust");
        model.addColumn("Servis");
        model.addColumn("Harga Layanan");
        model.addColumn("Tambahan");
        model.addColumn("Harga Tmbhn");
        model.addColumn("Diskon");
        model.addColumn("Total");
        model.addColumn("Tanggal");
        
        String sql = "SELECT * FROM servis";
        java.sql.Connection conn = (Connection)Koneksi.configDB();
        java.sql.Statement st = conn.createStatement();
        java.sql.ResultSet rs = st.executeQuery(sql);
        while(rs.next()){
            model.addRow(new Object[]{
            rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), 
                rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)
            });
        }
        jTable1.setModel(model);
    }
    
    private void kosong(){
        IDTRANS.setText("");
        IDCUST.setText("");
        SERVIS.setSelectedItem("---Pilih Menu---");
        HARGAL.setText("");
        TAMBAHAN.setText("");
        HARGAT.setText("");
        DISKON.setText("");
        TOTAL.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Ltgl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        IDTRANS = new javax.swing.JTextField();
        HARGAL = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TAMBAHAN = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        HARGAT = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        IDCUST = new javax.swing.JTextField();
        SERVIS = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        DISKON = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        TOTAL = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 153, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SERVIS HP POI");

        Ltgl.setForeground(new java.awt.Color(255, 255, 255));
        Ltgl.setText("Tanggal");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Trans", "ID Cust", "Servis", "Harga Layanan", "Tambahan", "Harga Tmbhn", "Total", "Diskon", "Tanggal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton5.setText("TAMBAH");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("EDIT");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("HAPUS");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("SELESAI");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 153, 153));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Servis", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ID Transaksi");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Jenis Servis");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Harga Layanan");

        IDTRANS.setEditable(false);
        IDTRANS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDTRANSActionPerformed(evt);
            }
        });

        HARGAL.setEditable(false);

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Rp");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Tambahan");

        TAMBAHAN.setColumns(20);
        TAMBAHAN.setRows(5);
        jScrollPane2.setViewportView(TAMBAHAN);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Harga");

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Rp");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("ID Customer");

        IDCUST.setEditable(false);

        SERVIS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "---Pilih Menu---", "Ganti Baterai", "Ganti LCD", "Perbaikan Tombol", "Servis Mesin", "Lainnya" }));
        SERVIS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SERVISActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(HARGAT, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(IDTRANS, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                                    .addComponent(SERVIS, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(HARGAL, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(IDCUST))))
                        .addGap(31, 31, 31))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(IDTRANS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(IDCUST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(SERVIS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(HARGAL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HARGAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 153, 153));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pembayaran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Diskon");

        DISKON.setEditable(false);

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total");

        TOTAL.setEditable(false);

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Rp");

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Rp");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TOTAL, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DISKON, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(DISKON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(TOTAL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jButton1.setText("CETAK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("KEMBALI");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton9.setText("CLEAR");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(361, 361, 361))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(447, 657, Short.MAX_VALUE)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(55, 55, 55))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Ltgl)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(Ltgl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton7)
                            .addComponent(jButton9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8)
                            .addComponent(jButton1)
                            .addComponent(jButton2))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void IDTRANSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDTRANSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDTRANSActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            new Customer().setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(KasirServisHP.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void SERVISActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SERVISActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SERVISActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        kosong();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if(IDCUST.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Mohon pilih customer terlebih dahulu!", "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            return;
        }else if(HARGAL.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Pilih Jenis Servis untuk melanjutkan!", "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try{
            try{
                java.util.Date tgl = new Date();
                java.sql.Date sqlTgl = new java.sql.Date(tgl.getTime());
                
                String sql = "INSERT INTO servis VALUES ('"+IDTRANS.getText()+"', '"+
                        IDCUST.getText()+"', '"+SERVIS.getSelectedItem()+"', '"+
                        HARGAL.getText()+"', '"+TAMBAHAN.getText()+"', '"+HARGAT.getText()+"', '"+
                        DISKON.getText()+"', '"+TOTAL.getText()+"', '"+sqlTgl+"')";
                java.sql.Connection conn = (Connection)Koneksi.configDB();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.execute();
                
                JOptionPane.showMessageDialog(this, "Berhasil tambah data");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(this, "Gagal tambah data: "+e.getMessage(), "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            }
            load_tabel();
            kosong();
        }catch(SQLException ex){
            Logger.getLogger(KasirServisHP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if(IDTRANS.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Mohon pilih data terlebih dahulu!", "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            return;
        }else if(SERVIS.getSelectedItem().equals("---Pilih Menu---")){
            JOptionPane.showMessageDialog(this, "Pilih Jenis Servis untuk melanjutkan!", "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try{
            try{
                String sql = "UPDATE servis SET jenis_servis = '"+SERVIS.getSelectedItem()
                        +"', harga_layanan = '"+HARGAL.getText()+"', tambahan = '"+TAMBAHAN.getText()
                        +"', harga_tambahan = '"+HARGAT.getText()+"', diskon = '"+DISKON.getText()
                        +"', total = '"+TOTAL.getText()+"' WHERE id_trans = '"+IDTRANS.getText()+"'";
                java.sql.Connection conn = (Connection)Koneksi.configDB();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.execute();
                
                JOptionPane.showMessageDialog(this, "Edit data berhasil");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(this, "Edit data gagal: "+e.getMessage(), "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            }
            load_tabel();
            kosong();
        }catch(SQLException ex){
            Logger.getLogger(KasirServisHP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if(IDTRANS.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!", "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try{
            try{
                String sql = "DELETE FROM servis WHERE id_trans = '"+IDTRANS.getText()+"'";
                java.sql.Connection conn = (Connection)Koneksi.configDB();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.execute();
                
                JOptionPane.showMessageDialog(this, "Berhasil hapus data");
            }catch(SQLException | HeadlessException e){
                JOptionPane.showMessageDialog(this, "Gagal hapus data: "+e.getMessage(), "Peringatan: ", JOptionPane.WARNING_MESSAGE);
            }
            load_tabel();
            kosong();
        }catch(SQLException ex){
            Logger.getLogger(KasirServisHP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try{
            String report = "C:\\Users\\Administrator\\JaspersoftWorkspace\\MyReports\\ServisHP.jasper";
            Connection conn = Koneksi.configDB();
            Map<String, Object> param = new HashMap<>();
            JasperPrint jp = JasperFillManager.fillReport(report, param, conn);
            JasperViewer.viewReport(jp, false);
        }catch(SQLException | net.sf.jasperreports.engine.JRException e){
            JOptionPane.showMessageDialog(null, "Gagal mencetak laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new KasirServisHP("").setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField DISKON;
    private javax.swing.JTextField HARGAL;
    private javax.swing.JTextField HARGAT;
    private javax.swing.JTextField IDCUST;
    private javax.swing.JTextField IDTRANS;
    private javax.swing.JLabel Ltgl;
    private javax.swing.JComboBox<String> SERVIS;
    private javax.swing.JTextArea TAMBAHAN;
    private javax.swing.JTextField TOTAL;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
