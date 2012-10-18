public class Test {
	public static void main(String[] args) {
		Test test = new Test();
		B b = test.new B();
		b.test();
	}

	class A {
		public void test() {
			a();
		}

		public void a() {
			System.out.println("a");
		}
	}

	class B extends A {
		public void a() {
			System.out.println("b");
		}
	}
}
