class Test {
    public static void main(String[] args) {
        
    }
    public static void doItAll(ActualDoer doer) {
        I obj1 = new A();
        I obj2 = new B();
 
        obj1.doThat(doer);
        obj2.doThis(doer);
        obj1.doThis(doer);
        obj2.doThat(doer);
     }
  }