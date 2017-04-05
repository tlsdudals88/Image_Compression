import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class MyProgram {

    JFrame frame;
    JLabel lbIm1;
    JLabel lbIm2;
    BufferedImage original_img;
    BufferedImage result_img;

    public double[][][][][] RGBtoYPrPb(double[][][][][] RGB_block) {

        double[][][][][] YPrPb_block = new double[3][36][44][8][8];

        for(int i = 0; i < 36; i++) {
            for (int j = 0; j < 44; j++) {
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        YPrPb_block[0][i][j][k][l] =
                                0.299*RGB_block[0][i][j][k][l]
                                        + 0.587*RGB_block[1][i][j][k][l]
                                            + 0.114*RGB_block[2][i][j][k][l];
                        YPrPb_block[1][i][j][k][l] =
                                -0.169*RGB_block[0][i][j][k][l]
                                        + -0.331*RGB_block[1][i][j][k][l]
                                            + 0.500*RGB_block[2][i][j][k][l];
                        YPrPb_block[2][i][j][k][l] =
                                0.500*RGB_block[0][i][j][k][l]
                                        + -0.419*RGB_block[1][i][j][k][l]
                                            + -0.081*RGB_block[2][i][j][k][l];

                    }
                }
            }
        }

        return YPrPb_block;
    }

    public double[][][][][] YPrPbtoRGB(double[][][][][] YPrPb_block) {

        double[][][][][] RGB_block = new double[3][36][44][8][8];

        for(int i = 0; i < 36; i++) {
            for (int j = 0; j < 44; j++) {
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        RGB_block[0][i][j][k][l] =
                                1.000*YPrPb_block[0][i][j][k][l]
                                        + 0.000*YPrPb_block[1][i][j][k][l]
                                            + 1.402*YPrPb_block[2][i][j][k][l];
                        RGB_block[1][i][j][k][l] =
                                1.000*YPrPb_block[0][i][j][k][l]
                                        + -0.344*YPrPb_block[1][i][j][k][l]
                                            + -0.714*YPrPb_block[2][i][j][k][l];
                        RGB_block[2][i][j][k][l] =
                                1.000*YPrPb_block[0][i][j][k][l]
                                        + 1.772*YPrPb_block[1][i][j][k][l]
                                            + 0.000*YPrPb_block[2][i][j][k][l];

                    }
                }
            }
        }

        return RGB_block;
    }

    public double[][][][][] DCT(double[][][][][] f)
    {
        double C[] = new double[8];
        for(int i=1; i<8; i++) {
            C[i] = 1;
        }
        C[0] = 1 / Math.sqrt(2.0);

        double[][][][][] F = new double[3][36][44][8][8];

        for(int a=0; a<3; a++) {
            for(int b=0; b<36; b++) {
                for (int c = 0; c < 44; c++) {

                    for (int u = 0; u < 8; u++) {
                        for (int v = 0; v < 8; v++) {
                            double sum = 0.0;
                            for (int x = 0; x < 8; x++) {
                                for (int y = 0; y < 8; y++) {
                                    sum += f[a][b][c][x][y]*Math.cos(((2 * x + 1) / 16.0) * u * Math.PI) *
                                            Math.cos(((2 * y + 1) / 16.0) * v * Math.PI);
                                }
                            }
                            sum *= C[u] * C[v] / 4.0;
                            F[a][b][c][u][v] = sum;
                        }
                    }

                }
            }
        }
        return F;
    }

    public double[][][][][] inverseDCT(double[][][][][] F)
    {
        double C[] = new double[8];
        for(int i=1; i<8; i++) {
            C[i] = 1;
        }
        C[0] = 1 / Math.sqrt(2.0);

        double[][][][][] f = new double[3][36][44][8][8];

        for(int a=0; a<3; a++) {
            for (int b = 0; b < 36; b++) {
                for (int c = 0; c < 44; c++) {

                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            double sum = 0.0;
                            for (int u = 0; u < 8; u++) {
                                for (int v = 0; v < 8; v++) {
                                    sum += C[u] * C[v] * F[a][b][c][u][v] * Math.cos(((2 * x + 1) / 16.0) * u * Math.PI) *
                                            Math.cos(((2 * y + 1) / 16.0) * v * Math.PI);
                                }
                            }
                            sum /= 4.0;
                            f[a][b][c][x][y] = sum;
                        }
                    }
                }
            }
        }
        return f;
    }

    public double[][][][][] ZigZagScan(double[][][][][] DCT_block, int num_diagonal) {

        int m = 7;
        int n = 7;
        double[][][][][] quantized_DCT_block = new double[3][36][44][8][8];

        for(int a=0; a<3; a++) {
            for (int b = 0; b < 36; b++) {
                for (int c = 0; c < 44; c++) {

                    for (int index_diagonal = 0; index_diagonal < 15; index_diagonal++) {
                        if (index_diagonal % 2 == 0) {
                            for (int x = index_diagonal; x >= 0; x--) {
                                if ((x <= m) && (index_diagonal - x <= n)) {
                                    if (index_diagonal < num_diagonal)
                                        quantized_DCT_block[a][b][c][x][index_diagonal - x]
                                                = DCT_block[a][b][c][x][index_diagonal - x];
                                    else
                                        quantized_DCT_block[a][b][c][x][index_diagonal - x] = 0;
                                }
                            }
                        } else {
                            for (int x = 0; x <= index_diagonal; x++) {
                                if ((x <= m) && (index_diagonal - x <= n)) {
                                    if (index_diagonal < num_diagonal)
                                        quantized_DCT_block[a][b][c][x][index_diagonal - x]
                                                = DCT_block[a][b][c][x][index_diagonal - x];
                                    else
                                        quantized_DCT_block[a][b][c][x][index_diagonal - x] = 0;
                                }
                            }
                        }
                    }

                }
            }
        }

        return quantized_DCT_block;
    }

    public void showIms(String[] args){
        int width = 352;
        int height = 288;
        int number_of_diagonal = Integer.parseInt(args[1]);
        // int number_of_diagonal = 8;

        original_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        try {
            File file = new File(args[0]);
            // File file = new File("Image1.rgb");
            InputStream is = new FileInputStream(file);

            long len = file.length();
            byte[] bytes = new byte[(int)len];

            double[][][][][] original_RGB_block = new double[3][36][44][8][8];
            double[][][][][] result_RGB_block = new double[3][36][44][8][8];
            double[][][][][] YPbPr_block = new double[3][36][44][8][8];
            double[][][][][] DCT_block = new double[3][36][44][8][8];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }

            // 1. Break all channels into 8x8 blocks
            int ind = 0, i = 0, j = 0, k = 0, l = 0;
            for(int y = 0; y < height; y++){
                l = 0;
                j = 0;
                if(y % 8 == 0 && y != 0) {
                    k = 0;
                    i++;
                }

                for(int x = 0; x < width; x++){
                    if(x % 8 == 0 && x != 0) {
                        l = 0;
                        j++;
                    }
                    // byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    original_RGB_block[0][i][j][k][l] = r;
                    original_RGB_block[1][i][j][k][l] = g;
                    original_RGB_block[2][i][j][k][l] = b;

                    // System.out.println(r);

                    int pix = 0xff000000 | (((byte) Math.round(original_RGB_block[0][i][j][k][l]) & 0xff) << 16)
                            | (((byte) Math.round(original_RGB_block[1][i][j][k][l]) & 0xff) << 8)
                            | ((byte) Math.round(original_RGB_block[2][i][j][k][l]) & 0xff);
                    original_img.setRGB(x,y,pix);
                    ind++;
                    l++;
                }
                k++;
            }

            // 2. Convert the RGB image to YPrPb image
            System.out.println("RGB => YPrPb Processing...");
            YPbPr_block = RGBtoYPrPb(original_RGB_block);


            // 3. For each block, do a DCT on the blocks to compute the DC and AC coefficients.
            System.out.println();
            System.out.println("YPrPb => DCT Processing...");
            DCT_block = DCT(YPbPr_block);


            // 4. Keep the first n diagonal rows of coefficients as suggested by the input parameter
            // and zero out the rest of the coefficients
            System.out.println();
            System.out.println("Zig zag scanning (quantizing AC and DC coefficient)...");
            DCT_block = ZigZagScan(DCT_block, number_of_diagonal);


            // 5. Do an Inverse DCT on the quantized AC and DC coefficients to recover the image
            // signal in the YPrPb space.
            System.out.println();
            System.out.println("DCT => YPrPb Processing...");
            YPbPr_block = inverseDCT(DCT_block);


            // 6. Convert the YPrPb image to RGB image
            System.out.println();
            System.out.println("YPrPb => RGB Processing...");
            result_RGB_block = YPrPbtoRGB(YPbPr_block);


            // 7. Merge all channels of 8x8 blocks
            int i1 = 0, j1 = 0, k1 = 0, l1 = 0;
            for(int y = 0; y < height; y++){
                l1 = 0;
                j1 = 0;

                if(y % 8 == 0 && y != 0) {
                    k1 = 0;
                    i1++;
                }

                for(int x = 0; x < width; x++){
                    if(x % 8 == 0 && x != 0) {
                        l1 = 0;
                        j1++;
                    }

                    // System.out.println(((byte) Math.round(result_RGB_block[0][i1][j1][k1][l1])));

                    int pix = 0xff000000 | (((byte) Math.round(result_RGB_block[0][i1][j1][k1][l1]) & 0xff) << 16)
                            | (((byte) Math.round(result_RGB_block[1][i1][j1][k1][l1]) & 0xff) << 8)
                            | ((byte) Math.round(result_RGB_block[2][i1][j1][k1][l1]) & 0xff);
                    result_img.setRGB(x,y,pix);

                    l1++;
                }
                k1++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use labels to display the images
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        JLabel lbText1 = new JLabel("Original image (Left)");
        lbText1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbText2 = new JLabel("Image after modification (Right)");
        lbText2.setHorizontalAlignment(SwingConstants.CENTER);
        lbIm1 = new JLabel(new ImageIcon(original_img));
        lbIm2 = new JLabel(new ImageIcon(result_img));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        frame.getContentPane().add(lbText1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        frame.getContentPane().add(lbText2, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbIm2, c);

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        MyProgram ren = new MyProgram();
        ren.showIms(args);
        // ren.showIms();
    }

}