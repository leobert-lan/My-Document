---
title: LiteOrm 使用文档&性能测试
---

# LiteOrm 使用文档&性能测试
author : leobert<p>
time : 6/15/2016 1:36:10 PM 

## What is LiteOrm

It is an object-relational-mapping framework for the
sqlite , a database module uesd in Android applications for data persistence。 <p>
## How to use LiteOrm ##

### 1.library support : ###

.jar extensioned library project can be used in both eclipse and android-studio.

### 2.better use singleton mode ###
```
//modify
LiteOrm liteOrm;

//....

//get singleton when need
if (liteOrm == null) {
	//provide context and dbname,db will be created when not exists
     liteOrm = LiteOrm.newSingleInstance(this,"liteorm.db");
}
```

if need debug : 

```
liteOrm.setDebugged(true);
```

To keep singleton in the whole application,you may have three ways : 

 - just use **LiteOrm#newSingleInstance()** when you need it
 - **initialize the instance in application class**,the application is public for Android-compnent
 -  modify a superunmerary Helper-typed or Utils-typed class

### 3.Model <==> Table ###
In java code and the java runtime,we use a model class to describe a table,an instance of the model represent a row in table. For example : 

```
@Table("test_model")
public class TestModel {

    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    // 非空字段
    @NotNull
    private String name;

    //忽略字段，将不存储到数据库
    @Ignore
    private String password;

    // 默认为true，指定列名
    @Default("true")
    @Column("login")
    private Boolean isLogin;
}

```

The TestModel describes a table named "test_model" with four columns.


### 4.basic annotation ###

 - @check
to make a rule.

```
/**
 * 校验
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {
	public String value();
}
```

example : 

```
    @Check("age > 0 ") // 值需>0
    private int age;
```

 - @Collate

> collate可应用于数据库定义或列定义以定义排序规则，或应用于字符串表达式以应用排序规则投影。<p>
When SQLite compares two strings, it uses a collating sequence or collating function (two words for the same thing) to determine which string is greater or if the two strings are equal. SQLite has three built-in collating functions :  BINARY, NOCASE, and RTRIM.<p>
BINARY - Compares string data using memcmp(), regardless of text encoding.<p>
NOCASE - The same as binary, except the 26 upper case characters of ASCII are folded to their lower case equivalents before the comparison is performed. Note that only ASCII characters are case folded. SQLite does not attempt to do full UTF case folding due to the size of the tables required.<p>
RTRIM - The same as binary, except that trailing space characters are ignored.

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collate {
    public String value();
}
```

 - @Column

> 为属性命名“列名”，如果没有设置，将以属性名字命名它在表中的“列名”；

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	/**
	 * Table Name
	 */
	public String value();
}
```

 - @Conflict

> 冲突策略

```
@Retention(RetentionPolicy.RUNTIME)
public @interface Conflict {
    public Strategy value();
}
```

 - @Default

> 设置默认值,如果是其他基础类型，可用String.valueOf(ooxx)<p>
**with caution** :    **never use "/"**,it will cause error when modify sql


```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
	public String value();
}
```
 - @Ignore

> 忽略字段，如果属性被设置忽略，那么将在增、改的时候忽略它。<p>
this makes your model be more flexible,means it can use as Bean or PO

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {}
```

 - @MapCollection

*used in cascade*

```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapCollection {
    Class<?> value();
}
```

 - @Mapping
*used in cascade*

> 关系映射

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
	Relation value();
}
```

 - @Notnull

> 非空约束

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {}
```

 - @PrimaryKey

> 主键，这是一个模型里必须有的,是对象的唯一标识。
没有主键将会报错，一个表只有一个主关键字，它有两种类型：
 * 1.主键值自定义，适用于已有唯一ID的对象。
 * 2.主键值系统定义，适用于没有唯一ID的对象，将使用递增的数字作为值賦予它。

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    AssignType value();
}
```

```
public enum AssignType {
    /**
     * 主键值自己来指定。
     */
    BY_MYSELF,
    /**
     * 主键值由系统分配，系统将使用自动递增整数赋值给主键。
     * 系统将从1开始递增分配，每次在上一条最大ID上+1 。
     */
    AUTO_INCREMENT
}
```

 - @Table

> 为对象命名“表名”，如果没有设置，将以对象类名命名表。

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * Table Name
	 */
	public String value();
}
```

 - @Temporary

>临时性
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Temporary {
}
```

 - @Unique

> 唯一性约束

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {}
```

 - @UniqueCombine

> 联合唯一性约束

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCombine {
    /**
     * 使用相同int值将归为一组[联合唯一]属性
     */
    public int value();
}
```

example：

```
   // 联合唯一
    @UniqueCombine(1)// 和其他UniqueCombine value=1的字段（who）联合唯一
    private int mark;
    // 联合唯一
    @UniqueCombine(1)// 和其他UniqueCombine value=1的字段（mark）联合唯一
    private String who;
```



### 5.CRUD operations ###

 - Save （Insert or Update）

```
School school = new School("hello");
liteOrm.save(school);
```


- Insert

```
Book book = new Book("good");
liteOrm.insert(book, ConflictAlgorithm.Abort);
```


- Update

```
book.setIndex(1988);
book.setAuthor("hehe");
liteOrm.update(book);
```


- Update Column *更新指定列*

```
// 把所有书的author强制批量改为liter
HashMap<String, Object> bookIdMap = new HashMap<String, Object>();
bookIdMap.put(Book.COL_AUTHOR, "liter");
liteOrm.update(bookList, new ColumnsValue(bookIdMap), ConflictAlgorithm.Fail);
```

```
// 仅 author 这一列更新为该对象的最新值。
//liteOrm.update(bookList, new ColumnsValue(new String[]{Book.COL_AUTHOR}, null), ConflictAlgorithm.Fail);
```


- Query

```
List list = liteOrm.query(Book.class);
OrmLog.i(TAG, list);
```


- Query with where rule,
**WhereBuilder shoud be used**

```
List<Student> list = liteOrm.query(new QueryBuilder<Student>(Student.class)
        .where(Person.COL_NAME + " LIKE ?", new String[]{"%0"})
        .whereAppendAnd()
        .whereAppend(Person.COL_NAME + " LIKE ?", new String[]{"%s%"}));
OrmLog.i(TAG, list);
```


- Query by the ID/PrimaryKey

```
Student student = liteOrm.queryById(student1.getId(), Student.class);
```

- Query with any support rules

```
List<Book> books = liteOrm.query(new QueryBuilder<Book>(Book.class)
        .columns(new String[]{"id", "author", Book.COL_INDEX})
        .distinct(true)
        .whereGreaterThan("id", 0)
        .whereAppendAnd()
        .whereLessThan("id", 10000)
        .limit(6, 9)
        .appendOrderAscBy(Book.COL_INDEX));
OrmLog.i(TAG, books);
```


- Delete use bean-entity

```
// 删除 student-0
liteOrm.delete(student0);
```


- Delete appoint size *删除 指定数量*

```
// 按id升序，删除[2, size-1]，结果：仅保留第一个和最后一个
// 最后一个参数可为null，默认按 id 升序排列
liteOrm.delete(Book.class, 2, bookList.size() - 1, "id");
```


- Delete WhereBuilder

```
// 删除 student-1
liteOrm.delete(new WhereBuilder(Student.class)
        .where(Person.COL_NAME + " LIKE ?", new String[]{"%1%"})
        .and()
        .greaterThan("id", 0)
        .and()
        .lessThan("id", 10000));
```

- Delete all

```
// 连同其关联的classes，classes关联的其他对象一带删除
liteOrm.deleteAll(School.class);
liteOrm.deleteAll(Book.class);
```

- Delete DB

```
liteOrm.deleteDatabase();
```

### Performance Test ###

TestCase：

- Insert 100,000 
- query all 100,000
- query top 10 in 100,000
- delete 100,000

<p>
compare with Native SQLiteUtils and use transaction.

the SQLiteUtils has many functions, so we do not format SQL manually

<p>

model :

    @Table("boss")
    public class Boss extends Person {
    public String address;
    public String phone;
    
    @Mapping(Relation.ManyToMany)
    private ArrayList<Man> list;
    
    public Boss() {
    
    }
    
    public Boss(String name, ArrayList<Man> list) {
    this.name = name;
    this.list = list;
    }
    
    public long getId() {
    return id;
    }
    
    public void setId(long id) {
    this.id = id;
    }
    
    public String getName() {
    return name;
    }
    
    public void setName(String name) {
    this.name = name;
    }
    
    public ArrayList<Man> getList() {
    return list;
    }
    
    public void setList(ArrayList<Man> list) {
    this.list = list;
    }
    
    public String getAddress() {
    return address;
    }
    
    public Boss setAddress(String address) {
    this.address = address;
    return this;
    }
    
    public String getPhone() {
    return phone;
    }
    
    public Boss setPhone(String phone) {
    this.phone = phone;
    return this;
    }
    
    @Override
    public String toString() {
    StringBuilder sb = new StringBuilder("Boss [id=" + id + ", name=" + name + ", phone=" + phone + ", " +
     "address=" + address);
    if (list != null) {
    sb.append(", list=");
    for (Man m  :  list) {
    sb.append(m.getName() + ", ");
    }
    }
    return sb.toString();
    }
    }
    
    
    public class Person extends  BaseModel{
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    protected long id;
    
    @NotNull
    @Conflict(Strategy.FAIL)
    public String name;
    
    @Override
    public String toString() {
    return "Person{" +
    "id=" + id +
    ", name='" + name + '\'' +
    "} " + super.toString();
    }
    }    

LiteOrm 测试代码：

    public static boolean testLargeScaleUseLiteOrm(LiteOrm liteOrm, int max) {
    boolean logPrint = OrmLog.isPrint;
    OrmLog.isPrint = false;
    
    // 1. 初始化数据
    List<Boss> list = new ArrayList<Boss>();
    for (int i = 0; i < max; i++) {
    Boss boss = new Boss();
    boss.setAddress("ZheJiang Xihu " + i);
    boss.setPhone("1860000" + i);
    boss.setName("boss" + i);
    list.add(boss);
    }
    
    // 2. 全部插入测试
    long start = System.currentTimeMillis();
    int num = liteOrm.insert(list);
    long end = System.currentTimeMillis();
    Log.i(TAG, " lite-orm insert boss model num :  " + num + " , use time :  " + (end - start) + " MS");
    
    // 3. 查询数量测试
    start = System.currentTimeMillis();
    long count = liteOrm.queryCount(Boss.class);
    end = System.currentTimeMillis();
    Log.i(TAG, " lite-orm query all boss model num :  " + count + " , use time :  " + (end - start) + " MS");
    
    // 4. 查询最后10条测试
    start = System.currentTimeMillis();
    ArrayList subList = liteOrm.query(
    new QueryBuilder<Boss>(Boss.class).appendOrderDescBy("_id").limit(0, 9));
    end = System.currentTimeMillis();
    Log.i(TAG,
    " lite-orm select top 10 boss model num :  " + subList.size() + " , use time :  " + (end - start) + " MS");
    Log.i(TAG, String.valueOf(subList));
    
    // 5. 删除全部测试
    start = System.currentTimeMillis();
    // direct delete all faster
    num = liteOrm.deleteAll(Boss.class);
    // delete list
    //num = liteOrm.delete(list);
    end = System.currentTimeMillis();
    Log.i(TAG, " lite-orm delete boss model num :  " + num + " , use time :  " + (end - start) + " MS");
    
    // 6. 再次查询数量测试
    start = System.currentTimeMillis();
    count = liteOrm.queryCount(Boss.class);
    end = System.currentTimeMillis();
    
    Log.i(TAG, " lite-orm query all boss model num :  " + count + " , use time :  " + (end - start) + " MS");
    
    OrmLog.isPrint = logPrint;
    return true;
    }
   

 <p>  
Native SqliteUtils test code : <p>

    public static boolean testLargeScaleUseDefault(Context context, int max) {
    
    // 1. 初始化数据，先创建实例并建表，为公平性该时间不计入统计。
    final List<Boss> list = new ArrayList<Boss>();
    for (int i = 0; i < max; i++) {
    Boss boss = new Boss();
    boss.setAddress("ZheJiang Xihu " + i);
    boss.setPhone("1860000" + i);
    boss.setName("boss" + i);
    list.add(boss);
    }
    if (helper == null) {
    helper = new SQLiteHelper(context, "mydata", null, 1, null);
    }
    final SQLiteDatabase wdb = helper.getWritableDatabase();
    final SQLiteDatabase rdb = helper.getReadableDatabase();
    wdb.execSQL(
    "CREATE TABLE IF NOT EXISTS boss (id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT, phone TEXT, address TEXT)");
    
    // 2. 全部插入
    long start = System.currentTimeMillis();
    wdb.beginTransaction();
    try {
    for (int i = 0; i < max; i++) {
    Boss boss = list.get(i);
    ContentValues values = new ContentValues();
    values.put("name", boss.getName());
    values.put("address", boss.getAddress());
    values.put("phone", boss.getPhone());
    long id = wdb.insert("boss", "", values);
    // 注意，非常重要：insert要回执ID给对象
    boss.setId(id);
    //wdb.execSQL("insert into boss (name, address, phone) values (?,?,?)", new String[]{boss.getName(), boss.getAddress(), boss.getPhone()});
    }
    wdb.setTransactionSuccessful();
    } finally {
    wdb.endTransaction();
    }
    long end = System.currentTimeMillis();
    Log.i(TAG, " android-api insert boss model num :  " + list.size() + " , use time :  " + (end - start) + " MS");
    
    // 3. 查询数量测试
    start = System.currentTimeMillis();
    Cursor cursor = rdb.rawQuery("SELECT COUNT(*) FROM boss", null);
    long count = 0;
    if (cursor.moveToFirst()) {
    count = cursor.getInt(0);
    }
    cursor.close();
    end = System.currentTimeMillis();
    Log.i(TAG, " android-api query all boss model num :  " + count + " , use time :  " + (end - start) + " MS");
    
    
    // 4. 查询最后10条测试
    start = System.currentTimeMillis();
    List<Boss> subList = new ArrayList<Boss>();
    cursor = rdb.rawQuery("select * from boss order by id desc limit 0,10", null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
    Boss boss = new Boss();
    boss.setAddress(cursor.getString(cursor.getColumnIndex("address")));
    boss.setName(cursor.getString(cursor.getColumnIndex("name")));
    boss.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
    boss.setId(cursor.getLong(cursor.getColumnIndex("id")));
    subList.add(boss);
    cursor.moveToNext();
    }
    cursor.close();
    end = System.currentTimeMillis();
    Log.i(TAG, " android-api select top 10 boss model num :  " + subList
    .size() + " , use time :  " + (end - start) + " MS");
    Log.i(TAG, String.valueOf(subList));
    
    // 5. 删除全部测试
    start = System.currentTimeMillis();
    long num = wdb.delete("boss", null, null);
    end = System.currentTimeMillis();
    Log.i(TAG, " android-api delete boss model num :  " + num + " , use time :  " + (end - start) + " MS");
    
    // 6. 再次查询数量测试
    start = System.currentTimeMillis();
    cursor = rdb.rawQuery("SELECT COUNT(*) FROM boss", null);
    count = 0;
    if (cursor.moveToFirst()) {
    count = cursor.getInt(0);
    }
    cursor.close();
    end = System.currentTimeMillis();
    Log.i(TAG, " android-api query all boss model num :  " + count + " , use time :  " + (end - start) + " MS");
    return true;
    }
    ```

100,000条记录测试：

 liteorm : 

 - insert :  5601 ms
 - query all :  10 ms
 - query top 10 :  3 ms
 - delete all : 57ms

native ways : 

 - insert :  6239 ms
 - query all :  11ms
 - query top 10  :  4 ms
 - delete all  :  76 ms

compare to liteorm,native ways may be modest.but native ways needs more memory and frequently GC.

![liteorm 测试截图](http://img.blog.csdn.net/20160615153652338)

![native way 测试截图](http://img.blog.csdn.net/20160615153732155)


