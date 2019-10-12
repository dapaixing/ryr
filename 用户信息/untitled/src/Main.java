public class Main {
    public static void main(String[] args){
        // a();
        b();
    }

//     3.构造方法,禁止递归
//     编译报错:构造方法是创建对象使用的,不能让对象一直创建下去
//
//    public Demo01DiGui()
//    {
//        //Demo01DiGui();
//    }
//2.在递归中虽然有限定条件，但是递归次数不能太多。否则也会发生栈内存溢出。
// 4993
// Exception in thread "main" java.lang.StackOverflowError
    private static void b(int i){
        System.out.println(i);
        //添加一个递归结束的条件,i==5000的时候结束
        if(i==5000){
            return;//结束方法
        }
        b(++i);
    }
    //1.递归一定要有条件限定，保证递归能够停止下来，否则会发生栈内存溢出。Exception in thread "main"
    //java.lang.StackOverflowError
    private static void a(){
        System.out.println("a方法");
        a();
    }
}
 
public class DiGuiDemo {
    public static void main(String[] args) {
        //计算1~num的和，使用递归完成
        int num = 5;
        // 调用求和的方法
        int sum = getSum(num);
        // 输出结果
        System.out.println(sum);
    }

    /*
    通过递归算法实现.
    参数列表:int
    返回值类型: int
    */
    public static int getSum(int num) {
        /*
        num为1时,方法返回1,
        相当于是方法的出口,num总有是1的情况
        */
        if (num == 1) {
            return 1;
        }
        /*
        num不为1时,方法返回 num +(num‐1)的累和
        递归调用getSum方法
         */
        return num + getSum(num-1);
    }
}

