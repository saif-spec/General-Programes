import java.util.Random;

public class MiniAES{

    public static void main(String[] args) {

        
        String PlainText = "3223";
        String Key = "4335";

    
        System.out.println("Text  : "+PlainText);
        System.out.println("Key  :  "+Key);

        String CypherText = Mini_AES_2rounds(PlainText, Key);
        
        System.out.println("Encrypted Text With Mini AES 16bit : "+CypherText);
    }
   
    
    public static String Mini_AES_2rounds(String PlainText,String Key){

        String res = "";


        int Round = 0;
        
        String Round_Key = Key;

        String[][] NibbleMatrix;
        String[][] ShiftMatrix;
        String[][] MixMatrix;
        String[][] Text_XOR;

        // Round 0
        
        Text_XOR = XOR( StringToMatrix_2x2(PlainText), StringToMatrix_2x2(Round_Key));
        


        // Round 1

        Round++;
        Round_Key = RoundKeyGenerator(Round, Round_Key);  // K1 (w4,..,w7)

        NibbleMatrix = NibbleSub(Text_XOR);

        ShiftMatrix = ShiftRows( NibbleMatrix );

        MixMatrix = MixColums( ShiftMatrix );

        Text_XOR = XOR(MixMatrix, StringToMatrix_2x2(Round_Key));


        // Round 2

        Round++;
        Round_Key = RoundKeyGenerator(Round, Round_Key);  // K2 (w8,..,w12)
        
        
        NibbleMatrix = NibbleSub(Text_XOR);
        
        ShiftMatrix = ShiftRows( NibbleMatrix );
        
        Text_XOR = XOR(ShiftMatrix, StringToMatrix_2x2(Round_Key));

        for (int i = 0; i < 2; i++)
        for (int j = 0; j < 2; j++) {
            res += Text_XOR[i][j];
        }

        return res;
    }

    
    public static String[][] XOR(String[][] Matrix1,String[][] Matrix2){

        String [][] res = {{"",""},{"",""}};
        String temp = "";
        for (int i=0;i<2;i++)
            for(int j=0;j<2;j++){
                
                for(int k=0;k<4;k++){

                    if(HexaToBi(Matrix1[i][j]).charAt(k) == HexaToBi(Matrix2[i][j]).charAt(k)){
                        
                        temp += "0";
                    }
                    else{
                        temp += "1";
                    }
                }
                res[i][j] += BiToHexa(temp);
                temp = "";
            }

        return res;
    }

    public static String XOR4Bits(String val1, String val2){

        String res;

            res = DecToHexa(HexaToDec(val1) ^ HexaToDec(val2));

        return res;
    } 

    public static String[][] NibbleSub(String[][] Val){

        String [] Input  = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        char [] OutPut =   {'E','4','D','1','2','F','B','8','3','A','6','C','5','9','0','7'};
        
        String[][] res = {{"",""},{"",""}};


        for(int i=0;i<2;i++)
        for(int j=0;j<2;j++)
            for(int k=0;k<16;k++){
            
                if(Input[k].equals(Val[i][j])){
                
                    res[i][j] += OutPut[k];

                }

        }
        return res;
    }
   
    public static String[][] ShiftRows(String[][] val){

        String[][] res = {val[0],{val[1][1],val[1][0]}};
        
       
        return res;
    }
 
    public static String[][] MixColums(String[][] M1){

        String [][] res = {{"",""},{"",""}};
        String[][] M2 = {{"3","2"},{"2","3"}};
        
        
        
        
        
        for(int i=0;i<2;i++)
            for(int j=0;j<2;j++){
                
                
                //res[i][j] += DecToHexa(((HexaToDec(M1[0][j])*HexaToDec(M2[i][0])) + (HexaToDec(M1[1][j])*HexaToDec(M2[i][1])))%16);
                res[i][j] += DecToHexa(MultiplyWithModulo(HexaToBi(M1[0][j]),HexaToBi(M2[i][0])) ^ MultiplyWithModulo(HexaToBi(M1[1][j]),HexaToBi(M2[i][1])));
            }
   


        return res;
    }

    public static int MultiplyWithModulo(String val1,String val2){

        int [] res = {0,0,0,0,0,0,0}; // max  100000

        //polynomyal multiply with modulo 2

        for (int i = val1.length() - 1 ; i>= 0; i--)
            for(int j= val2.length()-1 ; j>= 0; j--){

                if(val1.charAt(i) == '1' && val2.charAt(j) == '1'){

                    if(res[i+j] == 1) res[i+j] = 0;
                    else res[i+j] = 1;

                }
            }
        
        // modulo with the polynomyal x4 + x + 1  (work of art by saifGTA)

        for(int i=0; i<3;i++){

            if(res[i] == 1){

                res[i] = 0;
                if(res[i+3] == 1) res[i+3] = 0;
                else res[i+3] = 1;

                if(res[i+4] == 1) res[i+4] = 0;
                else res[i+4] = 1;
            }
        }

        int Som = 0;
        for (int i = 0; i<7;i++){
            
            if(res[6-i] == 1){

            Som += Math.pow(2,i);
            }
        }

        return Som;
    }
 

    public static String RoundKeyGenerator(int RoundN,String PrevKeyX){

        String res [] = {"","","",""}; 
        String[] RConst = {"","1","2"};
        String [] PrevKey = { String.valueOf(PrevKeyX.charAt(0)),String.valueOf(PrevKeyX.charAt(1)),
                              String.valueOf(PrevKeyX.charAt(2)),String.valueOf(PrevKeyX.charAt(3))};
        
        if(RoundN == 0){
            return PrevKeyX;
        }
        else{

            for(int i = 0;i<4;i++){
                if(i == 0){
                    String[][] tempxxx = {{PrevKey[3],"0"},{"0","0"}};
                    String tempyyy = NibbleSub(tempxxx)[0][0];
                    res[i] = XOR4Bits(XOR4Bits(PrevKey[0], tempyyy), RConst[RoundN]);
                }
                else{

                    res[i] = XOR4Bits(PrevKey[i], res[i-1]);
                }
                 
            }

        }
        String temp = "";
        for (int i = 0; i < 4; i++) {
            temp += res[i];
        }
        return temp;
    }


    public static String HexaToBi(String val){

        switch(val) {
            case ("0") -> { return ("0000");}
            case ("1") -> { return ("0001");}
            case ("2") -> { return ("0010");}
            case ("3") -> { return ("0011");}
            case ("4") -> { return ("0100");}
            case ("5") -> { return ("0101");}
            case ("6") -> { return ("0110");}
            case ("7") -> { return ("0111");}
            case ("8") -> { return ("1000");}
            case ("9") -> { return ("1001");}
            case ("A") -> { return ("1010");}
            case ("B") -> { return ("1011");}
            case ("C") -> { return ("1100");}
            case ("D") -> { return ("1101");}
            case ("E") -> { return ("1110");}
            case ("F") -> { return ("1111");}
            default -> throw new AssertionError();
        }
    }

    public static String BiToHexa(String val){

        switch(val) {
            case ("0000") -> { return ("0");}
            case ("0001") -> { return ("1");}
            case ("0010") -> { return ("2");}
            case ("0011") -> { return ("3");}
            case ("0100") -> { return ("4");}
            case ("0101") -> { return ("5");}
            case ("0110") -> { return ("6");}
            case ("0111") -> { return ("7");}
            case ("1000") -> { return ("8");}
            case ("1001") -> { return ("9");}
            case ("1010") -> { return ("A");}
            case ("1011") -> { return ("B");}
            case ("1100") -> { return ("C");}
            case ("1101") -> { return ("D");}
            case ("1110") -> { return ("E");}
            case ("1111") -> { return ("F");}
            
            default -> throw new AssertionError();
        }
    }

    public static int HexaToDec(String val){

        String BiVal = HexaToBi(val);
        int res = 0;
        int tempxx;
        for(int i=0;i<4;i++){
            tempxx = BiVal.charAt(3-i) - '0';
            res += tempxx*(int)(Math.pow(2,i));
        }
        return res;
    }

    public static String DecToHexa(int val){

        String res = "";
        
        if(val >= 0 && val < 10)
            res = String.valueOf(val);
        else{

            switch (val) {
                case 10 -> res += "A";
                case 11 -> res += "B";
                case 12 -> res += "C";
                case 13 -> res += "D";
                case 14 -> res += "E";
                case 15 -> res += "F";
                default -> throw new AssertionError();
            }
        }

        return res;
    }


    public static  String [][] StringToMatrix_2x2(String Text){
        int counter = 0;
        String[][] TextMatrix = {{"",""},{"",""}};
        for(int i=0;i<2;i++)
            for(int j=0;j<2;j++){
                TextMatrix[j][i] += Text.charAt(counter);
                counter++;
            }
        return TextMatrix;
    }

    public static  void Matrix_2x2_Display(String [][] Matrix){

        for (int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                System.out.print(Matrix[i][j]+" ");
            }
            System.out.print("\n");
        }
        
        //System.out.println("9 in Binary is : "+HexaToBi('9'));
        

    }
    
    public static String RandomKey16bit(){

        Random random = new Random(); 
        
        
        int RN1 = random.nextInt(15) + 1;
        int RN2 = random.nextInt(15) + 1;
        int RN3 = random.nextInt(15) + 1;
        int RN4 = random.nextInt(15) + 1;

        String res = "";
        res += DecToHexa(RN1);
        res += DecToHexa(RN2);
        res += DecToHexa(RN3);
        res += DecToHexa(RN4);

        return res;
    }

    

}