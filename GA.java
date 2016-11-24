package test;
/*A given function is as follows: 
Use genetic algorithm to find a near-maximal value in f=xsin(10*pi*x)+2  
x的定义域为[-1,2].  
In addition, the required precision is six places after the decimal point. 
 */  
import java.util.Random;  
  
public class GA {  
    public static final double A = -1;                 // 定义域下界  
    public static final double B = 2;                  // 定义域上界  
    public static Best best;                           // 记录最佳答案的对象  
    public static final int POP_SIZE = 30;             // 种群大小（本题的种群由30个个体（即x）组成）  
    public static String[] pop = new String[POP_SIZE];    // String型数组，存放种群中每个个体（即x）的编码  
    public static double[] result = new double[POP_SIZE]; // double型数组，经过进化后的种群中的每个个体（即x）  
    public static double[] fitness = new double[POP_SIZE];//double型，存放种群中每个个体（即x）的适应值  
    public static final int LENGTH = 22; // x的编码长度，因为要精确到小数点后六位，所以十进制的整数部分（1位）与小数部分（6位）共有7位。这需要用22位的二进制数表示。  
    public static final int conversionFactor = 4194303;//转换因子，22位二进制数所能表示最大的十进制数为2^22-1  
    public static Random random = new Random();     // 用于产生随机数的工具  
    public static final double PM = 0.05;           // 变异率  
    public static double[] p = new double[POP_SIZE];// 轮盘赌方法个体适应度概率  
    public static double[] q = new double[POP_SIZE];// q[i]是前n项p之和  
    int k1,k2;  //被选出来杂交的两个个体     
  
  
    /* 
     * 构造方法，初始化种群 
     */  
    public GA(double d[]) {  
        for (int i = 0; i < d.length; i++) {  
            result[i] = d[i];  
        }  
    }  
      
    /* 
     * 适应度函数，在本例中，使函数值f(x)越大，x的适应值越高。 
     */  
    public void fitness() {  
        for (int i = 0; i < result.length; i++) {  
            fitness[i] = result[i] * (Math.sin(10 * Math.PI * result[i])) + 2;  
            // System.out.println(fitness[i]);  
        }  
    }  
      
    /* 
     * 编码方法，将解值表示为二进制字节码形式 
     */  
    public void encoding()  
    {  
        for (int i = 0; i < result.length; i++) {  
            //放大result[i]以利于编码。  
            //d1的值:(|result[i]与A点的距离|/(定义域的长度))*转换因子  
            double d1 = ((result[i] - A) / (B - A)) * (conversionFactor);  
            //将d1强制类型转换成int型，并将其转换成二进制的字符串，并放入pop数组中。  
            pop[i] = Integer.toBinaryString((int) d1);  
        }  
        //依次检验pop数组的每一项，如果它的长度!=22,则在前面补“0”使其达到22位  
        for (int i = 0; i < pop.length; i++) {  
            if (pop[i].length() < LENGTH) {  
                int k = LENGTH - pop[i].length();  
                for (int j = 0; j < k; j++) {  
                    pop[i] = "0" + pop[i];  
                }  
            }  
        }  
    }  
      
    /* 
     * 选择操作,依据轮盘赌算法从种群中选出两个个体进行杂交。 
     */  
    public void selection()  
    {  
        int k1;  
        int k2;  
        do {  
            k1 = roulettewheel();  
            k2 = roulettewheel();  
        } while (k1 != k2);  
    }  
      
    /* 
     * 轮盘赌算法，适应值大的个体被选中的机率更大些。 
     */  
    int roulettewheel()  
    {  
        double m = 0;  
        double r =random.nextDouble(); //r为0至1的随机数  
        int i = 0;  
        for(i=0;i<=LENGTH; i++)  
        {  
                /* 产生的随机数在m~m+P[i]间则认为选中了i 
                 *  因此i被选中的概率是P[i] 
                 */  
                 m = m + fitness[i];  
                 if(r<=m)  
                     break;  
        }  
        return i;  
    }  
      
    /* 
     * 解码方法，将二进制字节码还原 
     */  
    public void decoding() {  
        for (int i = 0; i < pop.length; i++) {  
            int k = Integer.parseInt(pop[i], 2);  
            result[i] = A + k * (B - A) / (conversionFactor);  
        }  
    }  
  
    /* 
     * 交叉操作 
     */  
    public void crossover() {  
        //随机产生是染色体发生交叉操作的位置  
        int position = random.nextInt(LENGTH);  
        //s1字符串被切成s11和s12，s2字符串被切成s21，s22  
        String s11 = null, s12 = null, s21 = null, s22 = null;  
        s11 = pop[k1].substring(0, position);  
        s12 = pop[k1].substring(position, LENGTH);  
        s21 = pop[k2].substring(0, position);  
        s22 = pop[k2].substring(position, LENGTH);  
        //重新拼接字符串，并放入种群  
        pop[k1] = s11 + s22;  
        pop[k2] = s21 + s12;  
    }  
  
    /* 
     * 变异操作，变异在染色体上的每个基因都可能发生 
     */  
    public void mutation() {  
        //第i个个体（即染色体）  
        for (int i = 0; i < pop.length; i++) {  
            //第i个个体第j号基因  
            for (int j = 0; j < LENGTH; j++) {  
                double k = random.nextDouble();  
                //如果产生的随机数ｋ小于变异率，则进行变异操作。  
                if (PM > k) {  
                    mutation(i, j);  
                }  
            }  
        }  
    }  
    //如果基因的位置是“1”则换为“0”。如果基因的位置是“0”则换为“1”。  
    public void mutation(int i, int j) {  
        String s = pop[i];  
        StringBuffer sb = new StringBuffer(s);  
        if (sb.charAt(j) == '0')  
            sb.setCharAt(j, '1');  
        else  
            sb.setCharAt(j, '0');  
        pop[i] = sb.toString();  
  
    }  
      
    /* 
     * 一次进化 
     */  
    public void evolution() {  
        fitness();          //计算适应度  
        encoding();         //编码  
        selection();        //选择  
        crossover();        //交叉  
        mutation();         //变异  
        decoding();         //解码  
    }  
  
    /* 
     * 整个进化过程，n 表示进化多少代 
     */  
    public void dispose(int n) {  
        for (int i = 0; i < n; i++) {  
            evolution();  
        }  
    }  
  
    /* 
     * 取得结果 
     */  
    public double findBestOne() {  
        if (best == null)  
            best = new Best();  
        double max = 0;  
        for (int i=0;i<fitness.length; i++) {  
            if (fitness[i] > max) {  
                max = fitness[i];  
                best.fitness = max;  
                best.x = result[i];  
                best.str = pop[i];  
            }  
        }  
        return max;  
    }  
      
    /* 
     * 保存最佳个体的对象 
     */  
    class Best {   
        public int generations;  
        public String str;  
        public double fitness;  
        public double x;  
    }  
  
    public static void main(String[] args) {  
        // d为初始数据  
        double d[] = { -0.953181, -0.851234, -0.749723, -0.645386, -0.551234,  
                -0.451644, -0.351534, -0.239566, -0.151234, 0.145445, 0.245445,  
                0.285174, 0.345445, 0.445445, 0.542445, 0.645445, 0.786445,  
                0.845445, 0.923238, 1.245445, 1.383453, 1.454245, 1.584566,  
                1.644345, 1.741545, 1.845445, 1.981254, -0.012853, 0.083413,  
                1.801231 };  
        // 初始化初始种群及其他数据  
        GA ga = new GA(d);  
        System.out.println("种群进化中....");  
        // 进化，这里进化10000次  
        ga.dispose(10000);  
        ga.findBestOne();       //取得结果  
        System.out.println("+++++++++++++++++++++++++++结果为：");  
        System.out.println("x=" + best.x);  
        System.out.println("f=" + best.fitness);  
        //for(double i :result)  
        //{  
        //  System.out.println(i);  
        //}  
    }  
}  
