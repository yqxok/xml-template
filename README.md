#### 场景举例
```json
//某学校详情数据
{
  "meta_data":{
    "school":{
      "class": [
        {"name": "终极一班",
          "type": "理科班" ,
          "grade": "B",
          "students": [
            {"name": "李华","scored": 555},
            {"name": "袁华","scored": 576}
          ]
        },{
          "name": "文雅班",
          "type": "文科班",
          "grade": "A",
          "students": [
            {"name": "小美","scored": 601},
            {"name": "雨琪","scored": 611}
          ]
        },{
          "name": "清北班",
          "type": "理科班",
          "grade": "A",
          "students": [
            {"name": "张三","scored": 666},
            {"name": "李四","scored": 654}
          ]
        }
      ],
      "name": "**市第一中学",
      "averageScore": 589,
      "address": "茂名市茂南区",
      "grade": 1
    }
  },
  "message":"学校数据"
}
```

```xml
//教育系统保存某学校的数据格式
<data>
    <high_chool>
        <class>
            <field name="班名" value=""></field>
            <field name="排名" value=""></field>
            <field name="等级" value=""></field
             <student>
                <name value=""></name>
                <scored value=""></scored>
            </student>
        </class>
    </high_chool>
    <msg>
        <field name="学校名称" value=""></field>
        <field name="学校高考平均分" value=""></field>
    </msg>
</data>
```

假设当前学校内的系统需要对接教育局的系统，需要在双方系统之间搭建一个中转站，通过调用学校管理系统的开发api查询学校详情数据，然后根据学校详情数据构造出教育局所需数据再发送给教育局系统，由于双方传输数据格式不同，字段不同，需要确认双方字段的映射关系

解决方案：

1. 获取学校详情数据后，反序列化json数据转化成javabean对象，再将该javabean对象通过get、set方式转化成教育局系统对应的javabean对象，再将该对象序列化为xml传给教育局系统

   > 优点：实现简单，缺点：不同学校之间的字段可能也不同，需要按照不同学校创建各自的javabean，创建大量的javabean增加了人工成本，以及大量get、set这种硬编码方式使得代码变得十分臃肿、不利于后期维护；

2. 参考前端框架vue的模型视图设计架构和语法，将json作为模型数据，xml看作是视图，使用“{{}}”插值将json数据绑定到xml中，对于json中不存在的字段数据，支持在插值中使用“$[默认值]”作为默认值，同时也支持使用“for”语法动态创建xml节点，减轻重复创建列表xml节点的带来的繁琐，如下：

   > 优点：减少创建javabean和get、set代码，使系统代码变得干净简洁；可维护性高，通过修改模板xml即可改变字段之间的映射关系

   ```xml
   <!--xml模板-->
   <data>
       <high_chool >
           <class y-for="res in meta_data.school.class">
               <field  name="班名" value="{{res.name$[某某班]}}"></field>
               <field  name="排名" value="{{res.grade}}"></field>
               <field  name="等级" value="{{res.type}}"></field>
               <student y-for="stu in res.students">
                   <name value="{{stu.name}}"></name>
                   <scored value="{{stu.scored}}"></scored>
               </student>
           </class>
       </high_chool>
       <msg>
           <field  name="学校名称" value="{{meta_data.school.name}}"></field>
           <field  name="学校高考平均分" value="{{meta_data.school.dd$[111]}}"></field>
       </msg>
   </data>
   ```
   
   ```xml
   <!--通过json原数据和xml模板生成的xml数据-->
   <?xml version="1.0" encoding="UTF-8" standalone="no"?>
   <data>
   	<high_chool>
   		<class>
   			<field name="班名"value="终极一班"/>
   			<field name="排名"value="B"/>
   			<field name="等级"value="理科班"/>
   			<student>
   				<name value="李华"/>
   				<scored value="555"/>
   			</student>
   			<student>
   				<name value="袁华"/>
   				<scored value="576"/>
   			</student>
   		</class>
   		<class>
   			<field name="班名"value="文雅班"/>
   			<field name="排名"value="A"/>
   			<field name="等级"value="文科班"/>
   			<student>
   				<name value="小美"/>
   				<scored value="601"/>
   			</student>
   			<student>
   				<name value="雨琪"/>
   				<scored value="611"/>
   			</student>
   		</class>
   		<class>
   			<field name="班名"value="清北班"/>
   			<field name="排名"value="A"/>
   			<field name="等级"value="理科班"/>
   			<student>
   				<name value="张三"/>
   				<scored value="666"/>
   			</student>
   			<student>
   				<name value="李四"/>
   				<scored value="654"/>
   			</student>
   		</class>
   	</high_chool>
   	<msg>
   		<field name="学校名称"value="**市第一中学"/>
   		<field name="学校高考平均分"value="111"/>
   	</msg>
   </data>
   ```
   
   ```java
   public class Main {
       public static void main(String[] args) throws Exception {
           //数据转化过程的代码量非常少
           Document document = FileUtil.readXml("xml/test.xml");
           String s = FileUtil.readJson("xml/test.json");
           JsonMapNode jsonMapNode = JsonMapNode.newInstance(s);
           JsonToXmConvertor convertor = new JsonToXmConvertor(jsonMapNode, document);
           convertor.startParse(document);
           System.out.println(FileUtil.documentToString(document));
       }
   }
   ```
   
#### 实现原理

   将json数据转化为JsonNode对象，xml模板转化为document对象，将使用一系列的XmlProceesor对document对象进行处理，XmlProceesor的实现类有不同的处理优先级，如ForAttrProcessor首先识别element节点的“y-for”属性后将按照json对应的list的长度进行节点复制后，再由ValueInsertProcessor识别节点属性是否包含插值“{{}}”，使用JsonInserValueResolver去json查找其映射后赋值回该节点属性，最后ClearProcessor将特殊属性清除，处理器都完成后，最后序列化document对象得到需要的xml数据

   #### 核心接口

   **XmlProceesor**：识别“y”开头的特殊属性，对该属性及其节点进行处理；如XmlForProceesor识别":for"属性，对带有“:for”属性的节点进行深度拷贝后追加到当前父节点，XmlReflectProceesor识别“:reflect”属性，将该属性的值赋值到value属性上，EndProceesor识别所有特殊属性并将该属性从当前节点删除

   **InsertValueIResolver**：插值解析器，可以自定义添加自己的插值解析器，支持对不同插值采用不同的解析方式，如自定义插值解析器对插值内容进行数学运算

   **JsonNode**：能够操作json节点的接口，与hutool的JSONObject没什么两样，但不同的一点是，它能够通过特殊表达式直接获得获得字段值，这对插值解析器十分重要，如：

   ```java
   JsonMapNode jsonMapNode = JsonMapNode.newInstance(s);//s是上述学校详情的json数据
   String schoolName=jsonMapNode.getNode("meta_data.school.class[1].name",String.class);//schooleName等于文雅班
   ```

   #### 设计模式

   **模板方法模式**：jsonNode接口有两种实现类JsonMapNode和JsonListNode，它们同时继承AbstractJsonNode，AbstractJsonNode抽象实现了通用功能，如：添加节点、使用表达式获取节点、获取扁平化数据等，实现类则负责实现一些特定方法和步骤，提高了代码的复用性

   **责任链模式**：XmlProceesor的实现类被存放到列表中，按照优先级顺序依次地对xml节点进行解析处理

   