
1. 是啥？
	
	决策表是一个“精确而紧凑的”表示条件逻辑的方式，非常适合商业级别的规则。 
	目前决策表支持xls格式和csv格式。决策表与现有的drools drl文件使用可以无缝替换。
	
	什么时候使用决策表？
	规则能够被表达为模板+数据的格式，考虑使用决策表
	很少量的规则不建议使用决策表
	不是遵循一组规则模板的规则也不建议使用决策表


2. 咋用？
	格式：
	-------------------------------------------------------
	RuleSet		rules.decition	
	Sequential	TRUE	
	Functions	function void output(String message) { System.out.println(message); }	
			
	RuleTable test-decision-rules		
	CONDITION		CONDITION		ACTION
			
	eval(3<$param)	eval($param<8)	output("$param");
			
	5				3				执行1
	6				4				执行2
	-------------------------------------------------------
	RuleSet 和 drl 文件中的 package 是一样
	Sequential 与 drl 文件中的属性优先级是一样的，只是这边为 true
	Functions 与 drl 文件中的 function 是一样的
	RuleTable 表示 rule name,必添
	CONDITION ACTION 表示 rule 中的 LHS RHS 部分 至少要有一个
	从CONDITION 下面两行则表示 LHS 部分 第三行则为注释行，不计为规则部分，
	从第四行开始，每一行表示一条规则。
	$param 表示占位符会替换下面每一行的值，生成一条规则。
	
	
	


	
	
	
	