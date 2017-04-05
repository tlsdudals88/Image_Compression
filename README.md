# Image_Compression

javac MyProgram.java

java MyProgram inpuFile.rgb 1: 
This case converts your image to the DCT domain for all channels and uses only the DC coefficients to convert it back to the spatial domain. Consequently, the output should appear very blocky.


MyProgam.exe inpuFile.rgb 15: 
This case converts your image to the DCT domain and uses all coefficients (since there are 15 diagonal rows overall) to convert it back to the spatial domain. Consequently, the output should be the same as input with no loss.


MyProgam.exe inpuFile.rgb 8: 
This case converts your image to the DCT domain and using the upper left triangular set of 36 coefficients (since 8 rows correspond to 1+2+3+4+5+6+7+8 = 36 coefficients) to convert it back to the spatial domain. Consequently, the output should be the same as input with no loss.
