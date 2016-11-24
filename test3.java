package test;

public class test3 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		A a=new A();
		B b=new B();
		C c=new C();
		A a2=new C();
		a2.m1();
		a2.m2();
		a2.m3();
	}
}
class A{
	int ivar=7;
	void m1(){
		System.out.print("Am1, ");
	}
	void m2(){
		System.out.print("Am2, ");
	}
	void m3(){
		System.out.print("Am3, ");
	}
}
class B extends A{
	void m1(){
		System.out.print("Bm1, ");
	}
}
class C extends B{
	void m3(){
		System.out.print("Cm3, "+(ivar+6));
	}
}