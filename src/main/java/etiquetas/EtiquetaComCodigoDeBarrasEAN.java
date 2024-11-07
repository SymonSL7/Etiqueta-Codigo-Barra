package etiquetas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EtiquetaComCodigoDeBarrasEAN {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> criarInterfaceGrafica());
    }

    // Criar a interface gráfica com Swing
    private static void criarInterfaceGrafica() {
        // Criar o frame (janela)
        JFrame frame = new JFrame("Gerador de Etiquetas EAN-13");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null); // Centralizar na tela

        // Layout da interface
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Label e campo de texto para entrada do código de barras
        JLabel label = new JLabel("Informe o código EAN-13 (13 dígitos):");
        JTextField codigoBarrasField = new JTextField();
        JButton gerarEtiquetaButton = new JButton("Gerar Etiqueta");

        // Adicionar os componentes no painel
        panel.add(label, BorderLayout.NORTH);
        panel.add(codigoBarrasField, BorderLayout.CENTER);
        panel.add(gerarEtiquetaButton, BorderLayout.SOUTH);

        // Adicionar o painel no frame
        frame.add(panel);
        frame.setVisible(true);

        // Ação para o botão gerar etiqueta
        gerarEtiquetaButton.addActionListener(e -> {
            String textoCodigoDeBarras = codigoBarrasField.getText().trim();

            // Validar o código EAN-13
            if (!textoCodigoDeBarras.matches("\\d{13}")) {
                JOptionPane.showMessageDialog(frame, "Erro: O código de barras precisa ter exatamente 13 dígitos numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Definindo o nome do arquivo com base no texto do código de barras
            String nomeArquivo = textoCodigoDeBarras + ".png";
            String caminhoPasta = "etiquetas";  // A pasta "etiquetas" dentro do diretório do projeto
            File pastaEtiquetas = new File(caminhoPasta);

            // Se a pasta não existir, criar a pasta
            if (!pastaEtiquetas.exists()) {
                if (pastaEtiquetas.mkdir()) {
                    System.out.println("Pasta 'etiquetas' criada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Erro ao criar a pasta 'etiquetas'.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                // Gerar o código de barras como imagem
                BufferedImage imagemCodigoDeBarras = gerarCodigoDeBarrasEAN(textoCodigoDeBarras);

                // Caminho completo para salvar a etiqueta
                String caminhoArquivo = caminhoPasta + File.separator + nomeArquivo;

                // Criar a etiqueta com o código de barras
                gerarEtiquetaComCodigoDeBarras(imagemCodigoDeBarras, textoCodigoDeBarras, caminhoArquivo);

                // Mostrar mensagem de sucesso
                JOptionPane.showMessageDialog(frame, "Etiqueta gerada com sucesso em: " + caminhoArquivo, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao gerar a etiqueta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Método para gerar o código de barras EAN-13 como imagem
    private static BufferedImage gerarCodigoDeBarrasEAN(String texto) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);  // Margem ao redor do código de barras

        // Gerar a matriz do código de barras EAN-13
        BitMatrix matrix = new MultiFormatWriter().encode(texto, BarcodeFormat.EAN_13, 300, 100, hints);

        // Converter a matriz para uma imagem BufferedImage
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    // Método para criar a etiqueta com o código de barras
    private static void gerarEtiquetaComCodigoDeBarras(BufferedImage imagemCodigoDeBarras, String textoCodigoDeBarras, String caminhoArquivo) throws IOException {
        // Definindo o tamanho da etiqueta
        int larguraEtiqueta = 400;
        int alturaEtiqueta = 200;

        // Criando a imagem da etiqueta
        BufferedImage etiqueta = new BufferedImage(larguraEtiqueta, alturaEtiqueta, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = etiqueta.createGraphics();

        // Definir o fundo branco e configurar a fonte
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, larguraEtiqueta, alturaEtiqueta);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));

        // Adicionar o código de barras à etiqueta (centralizado)
        int xPosCodigoBarras = (larguraEtiqueta - imagemCodigoDeBarras.getWidth()) / 2;
        int yPosCodigoBarras = 40;
        g2d.drawImage(imagemCodigoDeBarras, xPosCodigoBarras, yPosCodigoBarras, null);

        // Adicionar texto abaixo do código de barras (dinâmico)
        g2d.drawString(textoCodigoDeBarras, (larguraEtiqueta - 150) / 2, 180);

        // Finalizar a imagem da etiqueta
        g2d.dispose();

        // Salvar a imagem no caminho especificado
        File arquivoEtiqueta = new File(caminhoArquivo);
        javax.imageio.ImageIO.write(etiqueta, "PNG", arquivoEtiqueta);
    }
}
