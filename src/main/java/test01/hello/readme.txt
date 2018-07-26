
1. 是啥？
	
	规则引擎是由推理引擎发展而来，是一种嵌入在应用程序中的组件，实现了将业务决策从应用程序代码中分离出来，并使用预定义的
	语义模块编写业务决策。接受数据输入，解释业务规则，并根据业务规则做出业务决策。大多数规则引擎都支持规则的次序和规则冲
	突检验，支持简单脚本语言的规则实现，支持通用开发语言的嵌入开发。目前业内有多个规则引擎可供使用，其中包括商业和开放源
	码选择。开源的代表是Drools，商业的代表是Visual Rules ,I Log。
	
	KIE（Knowledge Is Everything），知识就是一切的简称。JBoss一系列项目的总称。它们之间有一定的关联，通用一些API。
	比如涉及到构建（building）、部署（deploying）和加载（loading）等方面都会以KIE作为前缀来表示这些是通用的API。 
 	无论是Drools还是JBPM，生命周期都包含以下部分：
 	编写：编写规则文件，比如：DRL，BPMN2、决策表、实体类等。
	构建：构建一个可以发布部署的组件，对于KIE来说是JAR文件。
	测试：部署之前对规则进行测试。
	部署：利用Maven仓库将jar部署到应用程序。
	使用：程序加载jar文件，通过KieContainer对其进行解析创建KieSession。
	执行：通过KieSession对象的API与Drools引擎进行交互，执行规则。
	交互：用户通过命令行或者UI与引擎进行交互。
	管理：管理KieSession或者KieContainer对象。
	
	Drools（JBoss Rules ）具有一个易于访问企业策略、易于调整以及易于管理的开源业务规则引擎，符合业内标准，速度快、效
	率高。业务分析师或审核人员可以利用它轻松查看业务规则，从而检验是否已编码的规则执行了所需的业务规则。JBoss Rules的
	前身是Codehaus的一个开源项目叫Drools。现在被纳入JBoss 门下，更名为JBoss Rules，成为了JBoss应用服务器的规则引
	擎。Drools是为Java量身定制的基于Charles Forgy的RETE算法的规则引擎的实现。具有了OO接口的RETE,使得商业规则有了
	更自然的表达。

	Drools项目的基本目录结构：
	src/main/java
		|--xxx.xxx.XXX 				Fact对象集合及执行API
	src/main/resources
		|--xxx.xxx.xxrules.drl		规则文件集合
		|--META-INF/kmodule.xml		kmodule.xml文件
	
	项目基本依赖包：
	<dependency>
		<groupId>org.kie</groupId>
		<artifactId>kie-api</artifactId>
		<version>7.8.0.Final</version>
	</dependency>
	<dependency>
		<groupId>org.drools</groupId>
		<artifactId>drools-core</artifactId>
		<version>7.8.0.Final</version>
	</dependency>
	<dependency>
		<groupId>org.drools</groupId>
		<artifactId>drools-compiler</artifactId>
		<version>7.8.0.Final</version>
	</dependency>
	
	[注] kmodule.xml文件介绍：
	---------------------------------------------------------------------------------------
	a.kmodule.xml 文件的位置默认在 src/main/resources/MATA-INF 目录下（参考KieModuleModelImpl类）
	b.可以包含一个或多个kbase节点，分别对应drl的规则文件。kbase需要一个全局唯一的name。
	c.packages为drl文件所在的resource目录下的路径（注意区分drl文件中的package与此处的packages意义不同），
	  多个包之间用 "," 分隔（默认情况下会扫描resources目录及其子目录下的所有规则文件）
	d.kbase的default属性，表示当前 KieBase 是否是默认的，如果是默认的则不用名称就可以查到，每个kmodule
	  最多只能有一个默认的 KieBase
	e.kbase下面可以有一个或多个ksession，并且其name属性必须指定且唯一。
	
	kbase的属性：
	|name: KieBase的名称，这个属性是强制的，必须设置！
	|includes: 逗号分隔的KieBase名称列表，意为本KieBase将会包含所有include的KieBase的rule、process定义制品文件。
	|packages: 逗号分隔的字符串列表，默认情况下将包含resources目录下面（子目录）的所有规则文件。也可以指定具体目录下面的规则文件。
	|default: 默认为false。表示当前KieBase是不是默认的，如果是默认的话，不用名称就可以查找到该KieBase，但是每一个module最多只能有一个KieBase。
	|equalsBehavior: 默认为identity。名称中的”equals“是针对Fact而言的，当插入一个Fact到Working Memory中的时候，
	|               	Drools引擎会检查该Fact是否已经存在，如果存在的话就使用已有的FactHandle，否则就创建新的，而判
	|                断Fact是否存在的依据通过该属性定义的方式来进行的。设置成 identity，可以理解为用==判断，而如果
	|                设置为equality的话，就是通过Fact对象的equals方法来判断。
	|eventProcessingMode: 默认为cloud。当以云（cloud）模式编译时，KieBase将事件视为正常事实，而在流模式（stream）
	|                下允许对其进行时间推理。
	|declarativeAgenda: 默认为disabled。这是一个高级功能开关，打开后规则将可以控制一些规则的执行与否（disabled|enabled）。
	
	ksession的属性：
	|name: KieSession的名称，该值必须唯一，必须设置。
	|type: 默认为stateful（合法值stateful|stateless）。定义该session到底是有状态（stateful）的还是无状态（stateless）的，
	|      有状态的session可以利用Working Memory执行多次，而无状态的则只能执行一次。
	|default: 默认值为false（合法值true|false）。定义该session是否是默认的，如果是默认的话则可以不用通过session的name来创建
	|      session，在同一个module中最多只能有一个默认的session。
	|clockType: 默认值为realtime（合法值realtime,pseudo）。定义时钟类型，用在事件处理上面，在复合事件处理上会用到，
	|      其中realtime表示用的是系统时钟，而pseudo则是用在单元测试时模拟用的。
	|beliefSystem: 默认值为simple（合法值simple|defeasible|jtms）。定义KieSession使用的belief System的类型。
	
	示例：
	<?xml version="1.0" encoding="UTF-8"?>
	<kmodule xmlns="http://www.drools.org/xsd/kmodule">
	    <kbase name="all-rules-kbase" default="true">
	        <ksession name="all-rules-ksession"/>
	    </kbase>
	    <kbase name="test01-rules-kbase" packages="myrules">
	        <ksession name="test01-rules-ksession"/>
	        <ksession name="test01-rules-stateless-ksession" type="stateless"/>
	    </kbase>
	</kmodule>
	
	
2. FACT对象
	
	a.Fact对象是指在使用Drools 规则时，将一个普通的JavaBean对象插入到规则引擎的 WorkingMemory当中的对象。规则可以对
	  Fact对象进行任意的读写操作（通过getter和setter方法）。Fact对象不是对原来的JavaBean对象进行Clone，而是使用传入
	  的JavaBean对象的引用。规则在进行计算时需要的应用系统数据设置在Fact对象当中，这样规则就可以通过对Fact对象数据的读
	  写实现对应用数据的读写操作。
	b.Fact对象通常是一个具有getter和setter方法的POJO对象，通过getter和setter方法可以方便的实现对Fact对象的读写操作，
	  所以我们可以简单的把 Fact 对象理解为规则与应用系统数据交互的桥梁或通道。 
	c.当Fact对象插入到WorkingMemory当中后，会与当前WorkingMemory当中所有的规则进行匹配，同时返回一个FactHandler对
	  象。FactHandler对象是插入到WorkingMemory当中Fact对象的引用句柄，通过FactHandler对象可以实现对Fact对象的删除
	  及修改等操作。
	
	
	
3. KIE API解析
	
	a.KieServices
	  它就是一个中心，通过它来获取的各种对象来完成规则构建、管理和执行等操作
	  1) 获取KieContainer，再利用KieContainer来访问KBase和KSession
	  		KieServices kieServices = KieServices.Factory.get()
	  		KieContainer kieContainer = kieServices.getKieClasspathContainer();
	  2) 获取KieRepository（单例的）对象，利用KieRepository来管理KieModule等
	  		KieRepository kieRepository = kieServices.getRepository();
	  
	b.KieContainer
	  它就是KieBase的容器，提供了获取KieBase的方法和创建KieSession的方法
	  其中获取KieSession的方法内部依旧通过KieBase来创建KieSession
	  KieBase kieBase = kieContainer.getKieBase();
	  KieSession kieSession = kieContainer.newKieSession("xxxkieSessionName");
	  
	c.KieBase
	  它就是一个知识仓库，包含了若干的规则、流程、方法等。在Drools中主要就是规则和方法。
	  KieBase本身并不包含运行时的数据，如果需要执行规则KieBase中的规则的话，就需要根据
	  KieBase创建KieSession。
	  KieBase kieBase = kieContainer.getKieBase();
	  KieSession kieSession = kieBase.newKieSession();
	  StatelessKieSession statelessKieSession = kieBase.newStatelessKieSession();
	  
	d.KieSession
	  它就是一个跟Drools引擎打交道的会话，其基于KieBase创建，它会包含运行时数据，包含“事实Fact”，
	  并对运行时数据实时进行规则运算。通过KieContainer创建KieSession是一种较为方便的做法，其本质
	  上是从KieBase中创建出来的。KieSession就是应用程序跟规则引擎进行交互的会话通道。创建KieBase
	  是一个成本非常高的事情，KieBase会建立知识（规则、流程）仓库，而创建KieSession则是一个成本非
	  常低的事情，所以KieBase会建立缓存，而KieSession则不必。 
	   
	e.KieRepository
	  KieRepository是一个单例对象，它是存放KieModule的仓库，KieModule由kmodule.xml文件
	  定义（当然不仅仅只是用它来定义）
	  KieRepository kieRepository = kieServices.getRepository();
	  
	f.KieProject
	  KieContainer通过KieProject来初始化、构造KieModule，并将KieModule存放到KieRepository中，
	  然后KieContainer可以通过KieProject来查找KieModule定义的信息，并根据这些信息构造KieBase和
	  KieSession。 
	  
	g.ClasspathKieProject
	  ClasspathKieProject实现了KieProject接口，它提供了根据类路径中的META-INF/kmodule.xml文件
	  构造KieModule的能力，是基于Maven构造Drools组件的基本保障之一。意味着只要按照前面提到过的Maven
	  工程结构组织我们的规则文件或流程文件，只用很少的代码完成模型的加载和构建。 
	
	
	
	
	
	
	
	
	
	
	
	