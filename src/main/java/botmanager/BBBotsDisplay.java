package botmanager;

import botmanager.bulletbot.BulletBot;
import botmanager.jigsaw.Jigsaw;
import botmanager.nsfwpolice.NSFWPolice;
import botmanager.suggestionbox.SuggestionBox;
import botmanager.generic.BotBase;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author MC_2018 <mc2018.git@gmail.com>
 */
public class BBBotsDisplay extends javax.swing.JFrame {

    BotBase[] bots;
    boolean active = false;
    
    public BBBotsDisplay() {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            this.setIconImage(ImageIO.read(getClass().getClassLoader().getResource("images/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        pressLabel = new javax.swing.JLabel();
        lastLabel = new javax.swing.JLabel();
        dragPanel = new MotionPanel(this);
        minimizeLabel = new javax.swing.JLabel();
        closeLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(73, 0, 110));

        try {
            pressLabel.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("images/press.png"))));
        } catch (Exception e) {

        }
        pressLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressLabelMousePressed(evt);
            }
        });

        lastLabel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        lastLabel.setForeground(new java.awt.Color(167, 14, 255));
        lastLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lastLabel.setText("<html><div style=\"text-align: center\">The name and time of the person who last attempted to add NSFW tag will appear here.</div></html>");

        dragPanel.setBackground(new java.awt.Color(43, 0, 66));

        try {
            minimizeLabel.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("images/min.png"))));
        } catch (Exception e) {

        };
        minimizeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                minimizeLabelMousePressed(evt);
            }
        });

        try {
            closeLabel.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("images/x.png"))));
        } catch (Exception e) {

        }
        closeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                closeLabelMousePressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(167, 14, 255));
        jLabel1.setText("BulletBarry Bot Manager");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout dragPanelLayout = new javax.swing.GroupLayout(dragPanel);
        dragPanel.setLayout(dragPanelLayout);
        dragPanelLayout.setHorizontalGroup(
            dragPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dragPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minimizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        dragPanelLayout.setVerticalGroup(
            dragPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dragPanelLayout.createSequentialGroup()
                .addGroup(dragPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minimizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dragPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pressLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(lastLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(dragPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(pressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pressLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressLabelMousePressed
        TimerTask task;
        Timer timer;
        
        if (active) {
            return;
        }
        
        active = true;
        
        try {
            pressLabel.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("images/press_hard.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            List<String> tokens = IOUtils.readLines(new File("data/bbbots_tokens.txt"));
            
            bots = new BotBase[] {
                new NSFWPolice(tokens.get(0), "NSFW Police"),
                new SuggestionBox(tokens.get(1), "Suggestion Box"),
                new BulletBot(tokens.get(2), "Bullet Bot"),
                new Jigsaw(tokens.get(3), "Jigsaw")
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        task = new TimerTask() {
            @Override
            public void run() {
                for (BotBase bot : bots) {
                    if (bot instanceof NSFWPolice) {
                        lastLabel.setText(((NSFWPolice) bot).getLastMemberCaught());
                    }
                }
            }
        };
        
        timer = new Timer();
        timer.schedule(task, 0, 2000);
    }//GEN-LAST:event_pressLabelMousePressed

    private void closeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeLabelMousePressed
        System.exit(0);
    }//GEN-LAST:event_closeLabelMousePressed

    private void minimizeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeLabelMousePressed
        setState(Frame.ICONIFIED);
    }//GEN-LAST:event_minimizeLabelMousePressed

    /**
     * @param args the command line arguments
     */
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BBBotsDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BBBotsDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BBBotsDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BBBotsDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BBBotsDisplay().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel closeLabel;
    private javax.swing.JPanel dragPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lastLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel minimizeLabel;
    private javax.swing.JLabel pressLabel;
    // End of variables declaration//GEN-END:variables
}
