
1. DRL文件简介：

	从架构角度来讲，一般将同一业务的规则放置在同一规则文件，也可以根据不同类型处理操作放置在不同规则文件当中。不建议将所有
	的规则放置与一个规则文件当中。分开放置，当规则变动时不至于影响到不相干的业务。读取构建规则的成本业务会相应减少。
	
	标准规则文件的结构如下:
	---------------------------
	package xxx 	必须配置，放置在第一行，是逻辑上的区分，不必对应物理路径。同一的package下定义的function和query等可以直接使用
	imports xxx		导入规则文件需要的外部变量，使用方法跟java相同
	globals xxx 	定义一个全局变量，主要作用是在代码和规则之间进行数据的传输，全局变量是全局唯一的，不同的session之间可以访问到相
					同的一个对象。全局变量并不会插入到working memory中，因此除非作为常量值，否则不应将全局变量用于规则约束的判断中
	functions xxx	定义一个函数，封装相同的规则执行逻辑
	queries xxx		定义一个查询，Query提供了一种查询working memory中符合约束条件的FACT对象的简单方法
	rules xxx   	定义一条规则，包含 属性|条件|结果 三部分
					属性部分：定义当前规则执行的一些属性等，比如是否可被重复执行、过期时间、生效时间等
					条件部分：简称LHS，即Left Hand Side。定义当前规则的条件，处于when和then之间。如when Message();
							判断当前workingMemory中是否存在Message对象。LHS中，可包含0~n个条件，如果没有条件，默认为
							eval(true)，也就是始终返回 true。
					结果部分：简称RHS，即Right Hand Side，处于then和end之间，用于处理满足条件之后的业务逻辑。可以使用
							LHS部分定义的变量名、设置的全局变量、或者是直接编写Java 代码。
							RHS部分，提供了一些对当前Working Memory实现快速操作的宏函数或对象，比如 insert/insertLogical、
							update/modify和retract等。利用这些函数可以实现对当前Working Memory中的Fact对象进行新增、修改
							或删除操作；如果还要使用Drools提供的其它方法，可以使用另一个外宏对象drools，通过该对象可以使用更多
							的方法；同时Drools 还提供了一个名为kcontext的宏对象，可以通过该对象直接访问当前Working Memory的
							KnowledgeRuntime。
		
		
	LHS(left hand side)的语法示例：
	---------------------------
	//等价于什么也不写
	eval(true)
	  
	//只要当前Working Memory中含有Account对象（或其子类对象）即可触发规则
	$a:Account()  
	
	//必须同时满足这两个条件规则才能被触发(此处省略了 and)，注意括号中实质是一个条件表达式（结果为true或false）
	$a1:Account(balance>100)
	$a2:Account(type=="zhaoshang")  
	
	//只能绑定balance==100的Account对象
	$a: Account(balance==100) and Account(balance==200) 
	
	//满足其中一个条件即可触发，条件连接中 "&&" 等价于 "," 
	$a1:Account(user.age>30 && user.type=="VIP") 
	or
	$u1:User(getName()=="Kinglyjn") 
	
	//变量的绑定不仅可以在外部，内部的条件表达式也可以进行变量绑定
	$a:Account($myvalue:balance) 
	
	//非空校验，只有person的address属性不为空的情况下才把person的street属性赋值给$streetName变量
	$p:Person($streetName: address!.street)
	
	//正则表达式
	$p:Person(email matches "\\w+@\\w+\.com")
	
	//集合元素判断
	$a:Account(user memberOf $mylist)
	
	//条件的分组写法
	$c:Car(discount<8, person(name=="xxx",age>20))  
	
	//类型的强制转换，此处将person对象强制转换为Emp对象		
	$c:Car( person#Emp.id=="110" )  
	
	//Drools日期类型可以直接进行比较，日期的格式可以通过 System.setProperty("drools.dateformat", "yyyy-MM-dd")设置
	$u:User( birthday > "1999-09-09")  
	
	//map 和 list
	$p:Person( mymap["key1"]==1 )
	$mymap:Map() 
	$mymap:Map(this["key1"]==1)  包含键key1值1的map
	$mylist:List(this[0]==1) 首元素为1的列表
	$person: Person(age==20) from $mylist  从$mylist列表中获取年龄为20的person
	
	
	RHS(right hand side)的语法示例：
	---------------------------
	RHS主要用于处理结果，因此不建议在此部分再进行业务判断。如果必须要业务判断需要考虑规则设计的合理性，
	是否能将判断部分放置于LHS，那里才是判断条件应该在的地方。同时，应当保持RHS的精简和可读性。如果在
	使用的过程中发现需要在RHS中使用AND或OR来进行操作，那么应该考虑将一根规则拆分成多个规则。 RHS的主
	要功能是对working memory中的数据进行insert、update、delete或modify操作，Drools提供了相应的
	内置方法来帮助实现这些功能（参考 KnowledgeHelper接口）。	
	
	a.update(object,handle)：执行此操作更新对象（LHS绑定对象）之后，会告知引擎，并重新触发规则匹配。 
	b.update(object)：效果与上面方法类似，引擎会默认查找对象对应的handle。效果同 StatefulSession
	  的update方法。查看KnowledgeHelper接口中的update方法可以发现，update函数有多种参数组合的使用
	  方法。在实际使用中更多的会传入FACT对象来进行更新操作。
	c.modify是基于结构化的更新操作，它将更新操作与设置属性相结合，用来更改FACT对象的属性。语法格式如下：
	  modify ( <fact-expression> ) {
	  	  <expression> [ , <expression> ]*
	  }
	  其中fact-expression必须是FACT对象的引用，expression中的属性必须提供setter方法。在调用
	  setter方法时，不必再写FACT对象的引用，编译器会自动添加。例如：
	  rule "modify stilton"
		when
		    $stilton : Cheese(type == "stilton")
		then
		    modify( $stilton ){
		        setPrice( 20 ),
		        setAge( "overripe" )
		    }
		end
	  使用属性监听器，来监听JavaBean对象的属性变更，并插入到引擎中，可以避免在对象更改之后调用update
	  方法。当一个字段被更改之后，必须在再次改变之前调用update方法，否则可能导致引擎中的索引问题。而
	  modify关键字避免了这个问题。
	d.insert(newSomething())：创建一个新对象放置到working memory中。效果同 KieSession#insert，
	  调用insert之后，规则会进行重新匹配，如果没有设置no-loop为true或lock-on-active为true的规则，
	  如果条件满足则会重新执行。update、modify、delete都具有同样的特性，因此在使用时需特别谨慎，防止
	  出现死循环。
	e.insertLogical(newSomething())：功能类似于insert，但当创建的对象不再被引用时，将会被销毁。
	f.delete(handle)：从working memory中删除fact对象。效果同 KieSession#delete 或 retract，
	  同时delete函数和retract效果也相同，但后者已经被废弃。
	
	其实这些宏函数是KnowledgeHelper接口中方法对应的快捷操作，通过它们可以在规则文件中访问Working Memory
	中的数据。预定义变量drools的真实类型就是KnowledgeHelper，因此可以通过drools来调用相关的方法。具体每
	个方法的使用说明可以参考类中方法的说明。
	通过预定义的变量kcontext可以访问完整的Knowledge Runtime API，而kcontext对应的接口为KieContext。
	查看KieContext类会发现提供了一个getKieRuntime()方法，该方法返回KieRuntime接口类，该接口中提供了更
	多的操作方法，对RHS编码逻辑有很大作用。
	
	在Java中，如果有重复的代码我们会考虑进行重构，抽取公共方法或继承父类，以减少相同的代码在多处出现，
	达到代码的最优管理和不必要的麻烦。Drools同样提供了类似的功能。下面进行示例说明：
	rule "conditional1:Give 10% discount to customers older than 60"
    	agenda-group "conditional1"
		when
		    $customer : Customer( age > 60 )
		then
		    modify($customer) { setDiscount( 0.1 ) };
		    System.out.println("Give 10% discount to customers older than 60");
		end
	rule "conditional1:Give free parking to customers older than 60"
		agenda-group "conditional1"
		when
		    $customer : Customer( age > 60 )
		    $car : Car ( owner == $customer )
		then
		    modify($car) { setFreeParking( true ) };
		    System.out.println("Give free parking to customers older than 60");
		end
	现在Drools提供了extends特性，也就是一个规则可以继承另外一个规则，并获得其约束条件。改写之后执行效果相同：
	rule "conditional2:Give 10% discount to customers older than 60"
   		agenda-group "conditional2"
		when
		    $customer : Customer( age > 60 )
		then
		    modify($customer) { setDiscount( 0.1 ) };
		    System.out.println("conditional2:Give 10% discount to customers older than 60");
		end
	rule "conditional2:Give free parking to customers older than 60"
		extends "conditional2:Give 10% discount to customers older than 60"
		agenda-group "conditional2"
		when
		    $car : Car ( owner == $customer )
		then
		    modify($car) { setFreeParking( true ) };
		    System.out.println("conditional2:Give free parking to customers older than 60");
		end
	我们可以看到上面使用了extends，后面紧跟的是另外一条规则的名称。这样，第二条规则同时拥有了第一条规则的约束条件。
	只需要单独写此条规则自身额外需要的约束条件即可。那么，现在是否是最优的写法吗？当然不是，还可以将两条规则合并成
	一条来规则。这就用到了do和标记。
	rule "conditional3:Give 10% discount to customers older than 60"
	agenda-group "conditional3"
	when
	    $customer : Customer( age > 60 )
	    do[giveDiscount]
	    $car : Car(owner == $customer)
	then
	    modify($car) { setFreeParking(true) };
	        System.out.println("conditional3:Give free parking to customers older than 60");
	then[giveDiscount]
	    modify($customer){
	        setDiscount(0.1)
	    };
	    System.out.println("conditional3:Give 10% discount to customers older than 60");
	end
	在then中标记了giveDiscount处理操作，在when中用do来调用标记的操作。这样也当第一个约束条件判断完成之后，
	就执行标记giveDiscount中的操作，然后继续执行Car的约束判断，通过之后执行默认的操作。
	在then中还可以添加一些判断来执行标记的操作，这样就不必每次都执行do操作，而是每当满足if条件之后才执行，
	同时，还可以通过break来中断后续的判断。
	rule "conditional5:Give 10% discount to customers older than 60"
		agenda-group "conditional5"
		when
		    $customer : Customer( age > 60 )
		    if(type == "Golden") do[giveDiscount10]
		    else if (type == "Silver") break[giveDiscount5]
		    $car : Car(owner == $customer)
		then
		    modify($car) { setFreeParking(true) };
	        System.out.println("conditional5:Give free parking to customers older than 60");
		then[giveDiscount10]
		    modify($customer){
		        setDiscount(0.1)
		    };
		    System.out.println("giveDiscount10:Give 10% discount to customers older than 60");
		then[giveDiscount5]
		    modify($customer){
		        setDiscount(0.05)
		    };
		    System.out.println("giveDiscount5:Give 10% discount to customers older than 60");
		end
	
	
	[注意]
	---------------------------
	对于规则的执行的控制，还可以使用org.kie.api.runtime.rule. AgendaFilter来实现。
	用户可以实现该接口的accept方法，通过规则当中的属性值来控制是否执行规则。示例如下：
	
	public class MyAgendaFilter implements AgendaFilter {
		private String ruleName;
		public MyAgendaFilter(String ruleName) {
	        this.ruleName = ruleName;
	    }
		public String getRuleName() {
			return ruleName;
		}
		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}
		@Override
		public boolean accept(Match match) {
			System.out.println("rule name: " + match.getRule().getName());
			return match.getRule().getName().equals(ruleName);
		}
		
		public static void main(String[] args) {
			...
			AgendaFilter agendaFilter = new MyAgendaFilter("test02-agenda-filter-01");
			kieSession.fireAllRules(agendaFilter);
			...	
		}
	}
	
						

2. no-loop属性
	
	定义当前的规则是否不允许多次循环执行，默认是 false，也就是当前的规则只要满足条件，可以无限次执行。请看如下示例：
	
	rule "updateDistcount"
    no-loop false
    when
        productObj:Product(discount > 0);
    then
        productObj.setDiscount(productObj.getDiscount() + 1);
        System.out.println(productObj.getDiscount());
        update(productObj);
    end
    
    其中Product对象的discount属性值默认为1。执行此条规则时就会发现程序进入了死循环。也就是说对传入当前workingMemory中
    的FACT对象的属性进行修改，并调用update方法就会重新触发规则。从打印的结果来看，update之后被修改的数据已经生效，在重新
    执行规则时并未被重置。当然对Fact对象数据的修改并不是一定需要调用update才可以生效，简单的使用 set 方法设置就可以完成，
    但仅仅调用set方法时并不会重新触发规则。所以，对insert、retract、update方法使用时一定要慎重，否则极可能会造成死循环。
    可以通过设置no-loop为true来避免规则的重新触发，同时，如果本身的RHS部分有insert、retract、update等触发规则重新执行
    的操作，也不会再次执行当前规则。上面的设置虽然解决了当前规则的不会被重复执行，但其他规则还是会收到影响，比如下面的例子：
    
    rule updateDistcount
    	no-loop true
	    when
	        productObj:Product(discount > 0);
	    then
	        productObj.setDiscount(productObj.getDiscount() + 1);
	        System.out.println(productObj.getDiscount());
	        update(productObj);
	    end
	
	rule otherRule
	    when
	    	productObj : Product(discount > 1);
	    then
	    	System.out.println("被触发了" + productObj.getDiscount());
	    end
	此时执行会发现，当第一个规则执行update方法之后，规则otherRule也会被触发执行（如果是no-loop=false，则otherRule不会被执行）。
	如果注释掉update方法，规则otherRule则不会被触发。想要解决这个问题，就需要引入 lock-on-active=true 属性。
	
	

3. lock-on-active属性

	当在规则上使用ruleflow-group属性或agenda-group属性的时候，将lock-on-active 属性的值设置为true，可避免因
	某些Fact对象被修改而使已经执行过的规则再次被激活执行。可以看出该属性与no-loop属性有相似之处，no-loop属性是为
	了避免Fact被修改或调用了insert、retract、update之类的方法而导致规则再次激活执行，这里的lock-on-active 属
	性起同样的作用，lock-on-active是no-loop的增强版属性，它主要作用在使用ruleflow-group属性或agenda-group属
	性的时候。lock-on-active属性默认值为false。与no-loop不同的是lock-on-active可以避免其他规则修改FACT对象导
	致规则的重新执行。
	[注]
	lock-on-active 和 no-loop 的触发都是针对同一个获取焦点的分组规则集合而言的！
	
	

4. agenda-group 或 ruleflow-group
	agenda-group:
	规则的调用与执行是通过StatelessKieSession或KieSession来实现的，一般的顺序是创建一个StatelessKieSession
	或KieSession，将各种经过编译的规则添加到session当中，然后将规则当中可能用到的Global对象和Fact对象插入到Session
	当中，最后调用fireAllRules 方法来触发、执行规则。在没有调用fireAllRules方法之前，所有的规则及插入的Fact对象都存
	放在一个Agenda表的对象当中，这个Agenda表中每一个规则及与其匹配相关业务数据叫做Activation，在调用fireAllRules方
	法后，这些Activation会依次执行，执行顺序在没有设置相关控制顺序属性时（比如salience属性），它的执行顺序是随机的。
	Agenda Group是用来在Agenda基础上对规则进行再次分组，可通过为规则添加agenda-group属性来实现。agenda-group属性
	的值是一个字符串，通过这个字符串，可以将规则分为若干个Agenda Group。引擎在调用设置了agenda-group属性的规则时需要
	显示的指定某个Agenda Group得到Focus（焦点），否则将不执行该Agenda Group当中的规则。
	ruleflow-group:
	在使用规则流的时候要用到ruleflow-group属性，该属性的值为一个字符串，作用是将规则划分为一个个的组，然后在规则流当中
	通过使用ruleflow-group属性的值，从而使用对应的规则。该属性会通过流程的走向确定要执行哪一条规则。在规则流中有具体的
	说明。 
	
	[注意]
	a.如果想要触发某个分组的规则，首先需要ksession获取该分组的焦点
	b.可以设置规则的 auto-focus=true 使kiesession自动获取焦点，这样就会简化业务代码的编写
	c.一个kiesession某分组的焦点只能被执行一次，即使该分组再次获取焦点，执行触发任务，该分组的规则也不会被执行
	d.没有分组的规则，无论相应的kiesession有没有获取焦点，都会被触发
	e.设置规则rule的分组时agenda-group和ruleflow-group的作用是相同的
	


5. salience

	用来设置规则执行的优先级，salience属性的值是一个数字，数字越大执行优先级越高，同时它的值可以是一个负数。默认情况下，
	规则的salience默认值为0。如果不设置规则的salience属性，那么执行顺序是随机的。Drools还支持动态saline，可以使用绑
	定绑定变量表达式来作为salience的值。

	
6. activation-group
	
	该属性将若干个规则划分成一个组，统一命名。在执行的时候，具有相同activation-group 属性的规则中只要有一个被执行，
	其它的规则都不再执行。可以用类似salience之类属性来实现规则的执行优先级（同一activation-group优先级高的被执行，
	其他规则不会再被执行）。该属性以前也被称为异或（Xor）组，但技术上并不是这样实现的，当提到此概念，知道是该属性即可。 

	
7. auto-focus
	
	在agenda-group中，我们知道想要让AgendaGroup下的规则被执行，需要在代码中显式的设置group获得焦点。而此属性可
	配合agenda-group使用，代替代码中的显式调用。默认值为false，即不会自动获取焦点。设置为true，则可自动获取焦点。
	
	
8. dialect
	
	该属性用来定义规则（LHS、RHS）当中要使用的语言类型，可选值为“java”或“mvel”。默认情况下使用java语言。当在包级
	别指定方言时，这个属性可以在具体的规则中覆盖掉包级别的指定。


9. date-effective
	
	该属性是用来控制规则只有在到达指定时间后才会触发。在规则运行时，引擎会拿当前操作系统的时间与date-effective设置
	的时间值进行比对，只有当系统时间大于等于date-effective设置的时间值时，规则才会触发执行，否则执行将不执行。在没
	有设置该属性的情况下，规则随时可以触发。
	date-effective的值为一个日期型的字符串，默认情况下，date-effective可接受的日期格式为“dd-MMM-yyyy”。
	例如2017年7月20日，在设置为date-effective值时，如果操作系统为英文的，那么应该写成“20-Jul-2017”；
	如果是中文操作系统则为“20-七月-2017”。  
	如果需要精确到时分秒改如何使用呢？可以通过设置drools的日期格式化来完成任意格式的时间设定，而不是使用默认的格式。
	在调用代码之前设置日期格式化格式：
	System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm");
	在规则文件中就可以按照上面设定的格式来传入日期：
	date-effective "2017-07-20 16:31"


10. date-expires

	此属性与date-effective的作用相反，用来设置规则的过期时间。时间格式可完全参考date-effective的时间格式。引擎在执
	行规则时会检查属性是否设置，如果设置则比较当前系统时间与设置时间，如果设置时间大于系统时间，则执行规则，否则不执行。
	
	
11. enabled

	设置规则是否可用。true：表示该规则可用；false：表示该规则不可用。
	

	
	
	
	



	
	
	
	
	
	
	
	
	
	
	
						
